package com.hyeeyoung.wishboard.data.remote.interceptor

import com.hyeeyoung.wishboard.config.GlobalState
import com.hyeeyoung.wishboard.data.local.WishBoardPreference
import com.hyeeyoung.wishboard.data.remote.service.GuestAuthService
import com.hyeeyoung.wishboard.domain.model.auth.Token
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.SnackbarMessage
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val localStorage: WishBoardPreference,
    private val authService: GuestAuthService,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest = chain.request()
        val authRequest = originRequest.newAuthBuilder()
        val response = chain.proceed(authRequest)

        when (response.code) {
            401 -> {
                val responseBody = response.peekBody(Long.MAX_VALUE).string()
                response.close()

                val snackbarMessage = responseBody.toSnackbarMessage()
                return if (snackbarMessage == null) {
                    updateRefreshToken(chain) ?: run {
                        handleAutoLoginExpiration()
                        getUnauthorizedExceptionResponse(originRequest = originRequest)
                    }
                } else {
                    handleAutoLoginExpiration(snackbarMessage)
                    getUnauthorizedExceptionResponse(originRequest = originRequest, snackbarMessage = snackbarMessage)
                }
            }
        }

        return response
    }

    private fun updateRefreshToken(chain: Interceptor.Chain): Response? {
        val newToken = runBlocking {
            try {
                val tokens = authService.refreshToken(
                    deviceInfo = localStorage.deviceId,
                    token = Token(accessToken = localStorage.accessToken, refreshToken = localStorage.refreshToken),
                ).data
                localStorage.updateToken(
                    accessToken = tokens.accessToken,
                    refreshToken = tokens.refreshToken,
                )
                return@runBlocking tokens
            } catch (e: Exception) {
                Timber.e("토큰 리프레시 실패(${e.message})")
                handleAutoLoginExpiration(e.message?.toSnackbarMessage())
                return@runBlocking null
            }
        }

        // Call original request
        return if (newToken?.accessToken?.isNotBlank() == true) {
            val refreshedRequest = chain.request().newAuthBuilder()
            chain.proceed(refreshedRequest)
        } else {
            null
        }
    }

    private fun Request.newAuthBuilder(): Request {
        return this.newBuilder()
            .addHeader(AUTHORIZATION, "$TOKEN_PREF${localStorage.accessToken}")
            .addHeader(
                DEVICE_INFO_HEADER_NAME,
                localStorage.deviceId.ifEmpty {
                    val deviceId = UUID.randomUUID().toString()
                    localStorage.deviceId = deviceId
                    deviceId
                },
            ).build()
    }

    private fun handleAutoLoginExpiration(message: String? = null) {
        Timber.d("Token refresh failed, clearing token info")
        localStorage.clear()
        Timber.e("${message ?: SnackbarMessage.AUTO_LOGIN}")
        GlobalState.autoLoginExpiryInfo.value = true to (message ?: SnackbarMessage.AUTO_LOGIN)
    }

    private fun String.toSnackbarMessage(): String? {
        return when {
            this.contains(AuthErrorCode.NOT_FOUND_USER.name) -> {
                AuthErrorCode.NOT_FOUND_USER.snackbarMessage
            }

            this.contains(AuthErrorCode.LOGOUT_BY_DEVICE_OVERFLOW.name) -> {
                AuthErrorCode.LOGOUT_BY_DEVICE_OVERFLOW.snackbarMessage
            }

            else -> null
        }
    }

    private fun getUnauthorizedExceptionResponse(
        originRequest: Request,
        snackbarMessage: String? = SnackbarMessage.AUTO_LOGIN,
    ): Response {
        val msg = snackbarMessage ?: SnackbarMessage.AUTO_LOGIN
        return Response.Builder().request(originRequest).protocol(Protocol.HTTP_1_1).code(401)
            .message(msg)
            .body(
                """{
        "success": false,
        "code": "",
        "message": "$msg",
        "data": {}
    }""".toResponseBody("application/json".toMediaType()),
            )
            .build()
    }

    private enum class AuthErrorCode(val snackbarMessage: String = "") {
        LOGOUT_BY_DEVICE_OVERFLOW("최대 3대 기기에서만 로그인할 수 있어\n현재 기기에서 로그아웃되었어요."),
        TOKEN_EXPIRED(SnackbarMessage.AUTO_LOGIN),
        INVALID_TOKEN(SnackbarMessage.AUTO_LOGIN),
        NOT_FOUND_USER(
            "앗, 이용할 수 없는 계정입니다!\n다시 로그인해 주세요.",
        ),
    }

    companion object {
        private const val AUTHORIZATION = "Authorization"
        private const val TOKEN_PREF = "Bearer "
        const val DEVICE_INFO_HEADER_NAME = "Device-Info"
    }
}
