package com.hyeeyoung.wishboard.data.remote.sse

import com.hyeeyoung.wishboard.BuildConfig
import com.hyeeyoung.wishboard.config.GlobalState
import com.hyeeyoung.wishboard.data.local.WishBoardPreference
import com.hyeeyoung.wishboard.data.remote.model.event.ReconnectDto
import com.hyeeyoung.wishboard.data.remote.model.event.UnreadStatusDto
import com.hyeeyoung.wishboard.data.remote.service.GuestAuthService
import com.hyeeyoung.wishboard.domain.model.auth.Token
import com.hyeeyoung.wishboard.domain.model.noti.UnreadStatus
import com.hyeeyoung.wishboard.domain.util.JsonUtil
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.SnackbarMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.random.Random

/**
 * 서버 이벤트(SSE) 연결을 관리한다. 연결 하나로 여러 종류의 이벤트를 받으며, 현재는 `unread-status`만 사용한다.
 * 포그라운드에서만 연결을 유지하고(start/stop), 끊기면 `reconnect` 이벤트의 reason에 따라 재연결 여부를 결정한다.
 *
 * 스펙: https://github.com/hyeeyoung/wishboard-server-v2/blob/develop/docs/api/event-sse-spec.md
 */
@Singleton
class EventSseClient @Inject constructor(
    @Named("Sse") okHttpClient: OkHttpClient,
    private val localStorage: WishBoardPreference,
    private val authService: GuestAuthService,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val eventSourceFactory = EventSources.createFactory(okHttpClient)
    private val lock = Any()

    private val _unreadStatus = MutableStateFlow(UnreadStatus())
    val unreadStatus: StateFlow<UnreadStatus> = _unreadStatus.asStateFlow()

    // 아래 상태는 모두 lock으로 보호한다.
    private var isActive = false // 포그라운드라서 연결을 유지해야 하는 상태인지
    private var connection: Connection? = null // 현재 유효한 연결. 이 값이 아닌 연결의 콜백은 무시한다.
    private var reconnectJob: Job? = null
    private var failureCount = 0

    /** 연결을 시작한다. 이미 연결 중이면 아무 일도 하지 않는다. 로그인 전이면 연결하지 않는다. */
    fun start() {
        synchronized(lock) {
            isActive = true
            if (connection != null) return
            reconnectJob?.cancel()
            reconnectJob = null
            connectLocked()
        }
    }

    /** 연결을 끊고 재연결도 멈춘다. 백그라운드 진입 시 호출한다. */
    fun stop() {
        synchronized(lock) {
            isActive = false
            reconnectJob?.cancel()
            reconnectJob = null
            failureCount = 0
            closeConnectionLocked()
        }
    }

    private fun connectLocked() {
        if (!isActive || localStorage.accessToken.isBlank()) return

        // 재연결 전에 옛 연결을 먼저 닫는다. 옛 연결에서 오는 콜백은 현재 연결 검사로 무시된다.
        closeConnectionLocked()
        val newConnection = Connection()
        connection = newConnection
        Timber.d("SSE 연결 시도")
        newConnection.eventSource = eventSourceFactory.newEventSource(buildRequest(), newConnection)
    }

    private fun closeConnectionLocked() {
        val oldConnection = connection
        connection = null
        oldConnection?.eventSource?.cancel()
    }

    private fun buildRequest(): Request {
        val url = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegments(SUBSCRIBE_PATH)
            .build()
        // Authorization, Device-Info는 AuthInterceptor가, User-Agent는 AddInfoInterceptor가 추가한다.
        return Request.Builder()
            .url(url)
            .header("Accept", "text/event-stream")
            .header(APP_VERSION_HEADER, BuildConfig.VERSION_CODE.toString())
            .build()
    }

    private fun scheduleReconnectLocked(delayMillis: Long) {
        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            delay(delayMillis)
            synchronized(lock) { connectLocked() }
        }
    }

    private fun randomReconnectDelayMillis() = Random.nextLong(
        RECONNECT_MIN_DELAY_MILLIS,
        RECONNECT_MAX_DELAY_MILLIS + 1,
    )

    private fun nextBackoffMillis(): Long = if (failureCount <= 1) {
        randomReconnectDelayMillis()
    } else {
        // 연속 실패하면 지수 백오프 (최대 60초)
        minOf(MAX_BACKOFF_MILLIS, RECONNECT_MAX_DELAY_MILLIS shl (failureCount - 2).coerceAtMost(4))
    }

    private fun onUnreadStatusEvent(data: String) {
        runCatching {
            JsonUtil.json.decodeFromString<UnreadStatusDto>(data).toDomain()
        }.onSuccess {
            _unreadStatus.value = it
        }.onFailure {
            Timber.e(it, "unread-status 파싱 실패: $data")
        }
    }

    private fun onReconnectEvent(source: Connection, data: String) {
        val reason = runCatching { JsonUtil.json.decodeFromString<ReconnectDto>(data).reason }.getOrDefault("")
        synchronized(lock) {
            if (connection !== source) return

            // 서버가 곧 연결을 닫는다. 이어서 오는 끊김 콜백으로 또 재연결하지 않도록 먼저 현재 연결을 정리한다.
            closeConnectionLocked()
            when (reason) {
                REASON_TOKEN_EXPIRING -> {
                    reconnectJob?.cancel()
                    reconnectJob = scope.launch { refreshTokenAndReconnect() }
                }

                // 같은 기기의 새 연결 또는 기기 수 초과로 닫힘. 다음 포그라운드 진입 때 다시 연결한다.
                REASON_REPLACED -> Unit

                // 이후 API 호출의 401 처리로 로그인 화면으로 이동한다.
                REASON_LOGGED_OUT -> _unreadStatus.value = UnreadStatus()

                // SERVER_SHUTDOWN 및 모르는 reason
                else -> scheduleReconnectLocked(randomReconnectDelayMillis())
            }
        }
    }

    private fun onDisconnected(source: Connection, response: Response?, throwable: Throwable? = null) {
        synchronized(lock) {
            // 앱이 직접 끊은(stop/재연결) 옛 연결의 콜백은 무시한다.
            if (connection !== source) return
            closeConnectionLocked()
            Timber.e(throwable, "SSE 연결 끊김 (code: ${response?.code})")

            val code = response?.code
            if (code != null && code in NON_RETRYABLE_STATUS_CODES) {
                Timber.e("SSE 연결 실패($code). 재시도하지 않는다.")
                return
            }
            failureCount++
            scheduleReconnectLocked(nextBackoffMillis())
        }
    }

    /** TOKEN_EXPIRING: access token을 갱신한 뒤 재연결한다. 같은 토큰으로 붙으면 곧바로 같은 이벤트가 반복된다. */
    private suspend fun refreshTokenAndReconnect() {
        try {
            val tokens = authService.refreshToken(
                deviceInfo = localStorage.deviceId,
                token = Token(accessToken = localStorage.accessToken, refreshToken = localStorage.refreshToken),
            ).data
            localStorage.updateToken(accessToken = tokens.accessToken, refreshToken = tokens.refreshToken)
            synchronized(lock) { connectLocked() }
        } catch (e: HttpException) {
            Timber.e(e, "SSE 토큰 갱신 실패. 로그인 화면으로 이동한다.")
            localStorage.clear()
            GlobalState.autoLoginExpiryInfo.value = true to SnackbarMessage.AUTO_LOGIN
        } catch (e: Exception) {
            Timber.e(e, "SSE 토큰 갱신 중 네트워크 오류")
            synchronized(lock) {
                failureCount++
                scheduleReconnectLocked(nextBackoffMillis())
            }
        }
    }

    private inner class Connection : EventSourceListener() {
        @Volatile
        var eventSource: EventSource? = null

        override fun onOpen(eventSource: EventSource, response: Response) {
            Timber.d("SSE 연결 성공 (code: ${response.code})")
        }

        override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
            Timber.d("SSE 이벤트 수신: type=$type, data=$data")
            synchronized(lock) {
                if (connection !== this) return
                failureCount = 0
            }
            when (type) {
                EVENT_UNREAD_STATUS -> onUnreadStatusEvent(data)
                EVENT_RECONNECT -> onReconnectEvent(this, data)
                else -> Unit // 모르는 이벤트는 무시한다.
            }
        }

        override fun onClosed(eventSource: EventSource) = onDisconnected(this, response = null)

        override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) =
            onDisconnected(this, response, t)
    }

    private companion object {
        const val SUBSCRIBE_PATH = "events/subscribe" // BASE_URL이 이미 /v2/ 로 끝난다
        const val APP_VERSION_HEADER = "App-Version"

        const val EVENT_UNREAD_STATUS = "unread-status"
        const val EVENT_RECONNECT = "reconnect"

        const val REASON_TOKEN_EXPIRING = "TOKEN_EXPIRING"
        const val REASON_REPLACED = "REPLACED"
        const val REASON_LOGGED_OUT = "LOGGED_OUT"

        const val RECONNECT_MIN_DELAY_MILLIS = 1_000L
        const val RECONNECT_MAX_DELAY_MILLIS = 5_000L
        const val MAX_BACKOFF_MILLIS = 60_000L

        // 헤더 오류(400), nginx 거절(403), 경로 오류(404)는 재시도해도 결과가 같다.
        val NON_RETRYABLE_STATUS_CODES = setOf(400, 403, 404)
    }
}
