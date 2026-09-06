package com.hyeeyoung.wishboard.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.BuildConfig
import com.hyeeyoung.wishboard.data.local.WishBoardPreference
import com.hyeeyoung.wishboard.domain.usecase.auth.GetWebViewTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BulkRegisterWebViewViewModel @Inject constructor(
    private val getWebViewTokenUseCase: GetWebViewTokenUseCase,
    private val localStorage: WishBoardPreference,
) : ViewModel() {

    private val _webViewToken = MutableStateFlow<String?>(null)
    val webViewToken = _webViewToken.asStateFlow()

    private val _bridgeResponse = MutableSharedFlow<BridgeResponse>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val bridgeResponse = _bridgeResponse.asSharedFlow()

    val deviceInfo: String get() = localStorage.deviceId
    val userAgent: String = "wishboard-aos/${if (BuildConfig.DEBUG) "dev" else "prod"}"

    init {
        fetchWebViewToken()
    }

    private fun fetchWebViewToken() {
        viewModelScope.launch {
            getWebViewTokenUseCase()
                .onSuccess { token -> _webViewToken.value = token }
                .onFailure { Timber.e(it, "웹뷰 토큰 발급 실패") }
        }
    }

    // 웹 → 앱: WishboardBridge.postMessage(json) 수신 후 처리
    fun handleBridgeMessage(json: String) {
        viewModelScope.launch {
            try {
                val obj = JSONObject(json)
                val type = obj.getString("type")
                val requestId = obj.getInt("requestId")

                if (type != "REQUEST_WEBVIEW_TOKEN") return@launch // 모르는 타입은 무시

                getWebViewTokenUseCase()
                    .onSuccess { token ->
                        _bridgeResponse.emit(BridgeResponse(requestId, token, deviceInfo))
                    }
                    .onFailure {
                        Timber.e(it, "웹뷰 토큰 재발급 실패 (requestId=$requestId)")
                        _bridgeResponse.emit(BridgeResponse(requestId, null, null))
                    }
            } catch (e: JSONException) {
                Timber.e(e, "브릿지 메시지 파싱 실패: $json")
            }
        }
    }

    data class BridgeResponse(
        val requestId: Int,
        val token: String?,
        val deviceInfo: String?,
    )
}
