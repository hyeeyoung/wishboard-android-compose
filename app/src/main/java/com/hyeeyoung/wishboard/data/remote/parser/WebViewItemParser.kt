package com.hyeeyoung.wishboard.data.remote.parser

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Build
import android.os.SystemClock
import android.webkit.CookieManager
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import com.hyeeyoung.wishboard.domain.model.wish.ParsedWishItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.ByteArrayInputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * 아이템 파싱 API가 정보를 내려주지 못한(204) 경우를 위해, 화면에 붙이지 않은(숨긴) WebView로 상품 페이지를 열어
 * 자바스크립트로 og 태그 등을 읽어 아이템 정보를 만든다.
 * - 이름: og:title, 없으면 document.title
 * - 이미지: og:image
 * - 가격: og:price:amount, product:price:amount, JSON-LD의 offers.price 순
 *
 * 앱이 백그라운드에 있거나 다른 앱 위에서 실행되는 경우에도 앱이 죽지 않도록 다음을 지킨다.
 * - 한 번에 하나의 WebView만 만들고(Mutex), 끝나면 항상 destroy 한다. (코루틴 취소 포함)
 * - 이미지/영상/폰트 요청은 막고, 캐시/미디어 자동재생을 끈다.
 * - 전체 타임아웃을 두고, 메모리가 부족하면([isMemoryLow], 시스템 콜백) 즉시 중단한다.
 * - 렌더러 프로세스가 죽어도([WebViewClient.onRenderProcessGone]) 앱 프로세스는 유지한다.
 * - 매 시도마다 쿠키/스토리지를 비워, 이전 시도의 세션이 남지 않게 한다.
 */
@Singleton
class WebViewItemParser @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val mutex = Mutex()
    private val json = Json { ignoreUnknownKeys = true }

    /** [url]의 상품 정보를 파싱한다. 아무 정보도 찾지 못하거나 중단되면 null을 반환한다. */
    suspend fun parse(url: String): ParsedWishItem? = mutex.withLock {
        withContext(Dispatchers.Main) { parseOnMain(url) }
    }

    private class LoadState {
        @Volatile var pageFinishedAt: Long? = null

        @Volatile var abortReason: String? = null

        var bridgeRedirects = 0
    }

    private suspend fun parseOnMain(url: String): ParsedWishItem? {
        Timber.d("$TAG request: $url")
        if (isMemoryLow()) {
            Timber.w("$TAG skipped: low memory")
            return null
        }

        val state = LoadState()
        val memoryCallback = object : ComponentCallbacks2 {
            @Suppress("DEPRECATION")
            override fun onTrimMemory(level: Int) {
                if (level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL ||
                    level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND
                ) {
                    state.abortReason = "onTrimMemory($level)"
                }
            }

            @Suppress("OVERRIDE_DEPRECATION")
            override fun onLowMemory() {
                state.abortReason = "onLowMemory"
            }

            override fun onConfigurationChanged(newConfig: Configuration) = Unit
        }

        var webView: WebView? = null
        try {
            context.registerComponentCallbacks(memoryCallback)
            clearWebData()
            webView = createWebView(state)

            val startUrl = AppBridgeUrlResolver.resolve(url) ?: url
            val latest = loadAndPoll(webView, startUrl, state)

            val item = latest?.let(PageSnapshotMapper::toParsedItem)
            Timber.d("$TAG result: name=${item?.name}, image=${item?.image}, price=${item?.price}")
            if (item == null) Timber.w("$TAG nothing found. snapshot=$latest")
            return item
        } catch (e: Exception) {
            Timber.e(e, "$TAG failed: ${e.javaClass.simpleName}: ${e.message}")
            throw e
        } finally {
            context.unregisterComponentCallbacks(memoryCallback)
            webView?.release()
            clearWebDataWithoutWaiting()
        }
    }

    /** 페이지를 로드하고, 이름/이미지/가격이 모두 채워지거나 시간이 다 될 때까지 주기적으로 DOM을 읽는다. 마지막으로 읽은 비어있지 않은 값을 반환한다. */
    private suspend fun loadAndPoll(webView: WebView, url: String, state: LoadState): PageSnapshot? {
        var latest: PageSnapshot? = null
        val finished = withTimeoutOrNull(TOTAL_TIMEOUT_MILLIS) {
            webView.loadUrl(url)
            while (state.abortReason == null) {
                delay(POLL_INTERVAL_MILLIS)
                if (isMemoryLow()) {
                    state.abortReason = "low memory"
                    break
                }

                val snapshot = webView.extractSnapshot()
                if (snapshot != null && !snapshot.isEmpty) {
                    latest = snapshot
                    if (PageSnapshotMapper.isComplete(snapshot)) return@withTimeoutOrNull true
                }

                // 로딩이 끝난 뒤에도 SPA가 늦게 채우는 경우를 위해 잠시만 더 기다린다.
                val finishedAt = state.pageFinishedAt
                if (finishedAt != null && SystemClock.elapsedRealtime() - finishedAt >= POST_FINISH_GRACE_MILLIS) {
                    return@withTimeoutOrNull true
                }
            }
            false
        }
        when {
            state.abortReason != null -> Timber.w("$TAG aborted: ${state.abortReason}, use latest snapshot")
            finished == null -> Timber.w("$TAG timeout(${TOTAL_TIMEOUT_MILLIS}ms), use latest snapshot")
        }
        return latest
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun createWebView(state: LoadState): WebView = WebView(context).apply {
        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            blockNetworkImage = true
            loadsImagesAutomatically = false
            mediaPlaybackRequiresUserGesture = true
            cacheMode = WebSettings.LOAD_NO_CACHE
            allowFileAccess = false
            allowContentAccess = false
            javaScriptCanOpenWindowsAutomatically = false
            setSupportMultipleWindows(false)
            setGeolocationEnabled(false)
        }
        CookieManager.getInstance().setAcceptThirdPartyCookies(this, false)
        webViewClient = ParsingWebViewClient(state)
    }

    private inner class ParsingWebViewClient(private val state: LoadState) : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val target = request.url.toString()
            if (AppBridgeUrlResolver.isWebUrl(target)) return false

            // 앱 실행용 링크는 외부 앱을 띄우지 않고, 안에 담긴 웹 URL로 다시 로드한다.
            val resolved = AppBridgeUrlResolver.resolve(target)
            if (resolved != null && state.bridgeRedirects < MAX_BRIDGE_REDIRECTS) {
                state.bridgeRedirects++
                Timber.d("$TAG app bridge: $target -> $resolved")
                view.loadUrl(resolved)
            } else {
                Timber.w("$TAG blocked non-web navigation: $target")
            }
            return true
        }

        override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
            if (request.isForMainFrame) return null
            val extension = request.url.lastPathSegment?.substringAfterLast('.', "")?.lowercase()
            if (extension !in BLOCKED_EXTENSIONS) return null
            return WebResourceResponse(
                "text/plain",
                "utf-8",
                404,
                "Blocked",
                emptyMap(),
                ByteArrayInputStream(ByteArray(0)),
            )
        }

        override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
            Timber.d("$TAG page started: $url")
            state.pageFinishedAt = null
        }

        override fun onPageFinished(view: WebView, url: String?) {
            Timber.d("$TAG page finished: $url")
            state.pageFinishedAt = SystemClock.elapsedRealtime()
        }

        override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
            if (request.isForMainFrame) {
                Timber.w("$TAG main frame error: code=${error.errorCode}, ${error.description}, url=${request.url}")
            }
        }

        override fun onReceivedHttpError(
            view: WebView,
            request: WebResourceRequest,
            errorResponse: WebResourceResponse,
        ) {
            if (request.isForMainFrame) {
                Timber.w("$TAG main frame http error: status=${errorResponse.statusCode}, url=${request.url}")
            }
        }

        /** 렌더러 프로세스가 죽거나 메모리 때문에 종료된 경우, true를 반환해 앱 프로세스까지 죽지 않게 한다. */
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
            Timber.e("$TAG render process gone: didCrash=${detail.didCrash()}")
            state.abortReason = "renderProcessGone(didCrash=${detail.didCrash()})"
            return true
        }
    }

    private suspend fun WebView.extractSnapshot(): PageSnapshot? = suspendCancellableCoroutine { continuation ->
        evaluateJavascript(EXTRACT_SCRIPT) { raw ->
            continuation.resume(parseSnapshot(raw))
        }
    }

    /** evaluateJavascript는 문자열 결과를 JSON 문자열 리터럴로 감싸서 주므로 두 번 디코딩한다. */
    private fun parseSnapshot(raw: String?): PageSnapshot? {
        if (raw.isNullOrEmpty() || raw == "null") return null
        return runCatching {
            json.decodeFromString<PageSnapshot>(json.decodeFromString<String>(raw))
        }.onFailure { Timber.w("$TAG snapshot decode failed: ${it.message}") }.getOrNull()
    }

    private fun WebView.release() {
        runCatching {
            stopLoading()
            webViewClient = WebViewClient()
            loadUrl("about:blank")
            clearHistory()
            clearCache(true)
            removeAllViews()
            destroy()
        }.onFailure { Timber.w("$TAG webview release failed: ${it.message}") }
    }

    /** 쿠키/웹 스토리지를 지운다. 쿠팡처럼 이전 시도의 세션으로 재시도를 막는 사이트를 위해 매 시도마다 호출한다. */
    private suspend fun clearWebData() {
        val cookieManager = CookieManager.getInstance()
        suspendCancellableCoroutine { continuation ->
            cookieManager.removeAllCookies { continuation.resume(Unit) }
        }
        cookieManager.flush()
        WebStorage.getInstance().deleteAllData()
    }

    /** 취소된 코루틴의 finally에서도 쓸 수 있도록 완료를 기다리지 않는 버전 */
    private fun clearWebDataWithoutWaiting() {
        runCatching {
            CookieManager.getInstance().apply {
                removeAllCookies(null)
                flush()
            }
            WebStorage.getInstance().deleteAllData()
        }
    }

    private fun isMemoryLow(): Boolean {
        val activityManager = context.getSystemService(ActivityManager::class.java)
        val systemLow = activityManager?.let {
            ActivityManager.MemoryInfo().also(it::getMemoryInfo).lowMemory
        } ?: false

        val runtime = Runtime.getRuntime()
        val freeHeap = runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory())
        return systemLow || freeHeap < MIN_FREE_HEAP_BYTES
    }

    private companion object {
        const val TAG = "[WebViewParser]"
        const val TOTAL_TIMEOUT_MILLIS = 15_000L
        const val POLL_INTERVAL_MILLIS = 500L
        const val POST_FINISH_GRACE_MILLIS = 2_000L
        const val MAX_BRIDGE_REDIRECTS = 5
        const val MIN_FREE_HEAP_BYTES = 24L * 1024 * 1024

        val BLOCKED_EXTENSIONS = setOf(
            "jpg", "jpeg", "png", "gif", "webp", "avif", "svg", "ico",
            "mp4", "webm", "m3u8", "ts", "mov",
            "woff", "woff2", "ttf", "otf",
        )

        val EXTRACT_SCRIPT = """
            (function () {
              try {
                function meta(key) {
                  var e = document.querySelector('meta[property="' + key + '"],meta[name="' + key + '"]');
                  var v = e && e.getAttribute('content');
                  return v ? v.trim() : null;
                }
                function abs(u) {
                  try { return new URL(u, document.baseURI).href; } catch (e) { return u; }
                }
                var image = meta('og:image');
                var priceMeta = [];
                [${PageSnapshotMapper.PRICE_META_KEYS.joinToString(",") {
            "'$it'"
        }}].forEach(function (k) {
                  var v = meta(k);
                  if (v) priceMeta.push(v);
                });
                var jsonLd = [];
                document.querySelectorAll('script[type="application/ld+json"]').forEach(function (s) {
                  if (jsonLd.length < 10 && s.textContent) jsonLd.push(s.textContent.slice(0, 100000));
                });
                var priceTexts = [];
                document.querySelectorAll('[class*="price"]').forEach(function (el) {
                  if (priceTexts.length >= 20) return;
                  var t = (el.textContent || '').trim();
                  if (t && t.length <= 50) priceTexts.push(t);
                });
                return JSON.stringify({
                  url: location.href,
                  title: document.title,
                  ogTitle: meta('og:title'),
                  ogImage: image ? abs(image) : null,
                  priceMeta: priceMeta,
                  jsonLd: jsonLd,
                  priceTexts: priceTexts
                });
              } catch (e) {
                return null;
              }
            })();
        """.trimIndent()
    }
}
