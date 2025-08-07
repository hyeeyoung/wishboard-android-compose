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
        val unauthorizedExceptionResponse =
            Response.Builder().request(originRequest).protocol(Protocol.HTTP_1_1).code(401)
                .message(SnackbarMessage.AUTO_LOGIN)
                .body(
                    """{
        "success": false,
        "code": "",
        "message": "${SnackbarMessage.AUTO_LOGIN}",
        "data": {}
    }""".toResponseBody("application/json".toMediaType()),
                )
                .build()
        val authRequest = originRequest.newAuthBuilder().build()
        val response = chain.proceed(authRequest)

        when (response.code) {
            401 -> {
                response.close()
                return updateRefreshToken(chain) ?: unauthorizedExceptionResponse
            }
        }

        return response
    }

    private fun updateRefreshToken(chain: Interceptor.Chain): Response? {
        val newToken = runBlocking {
            try {
                val tokens = authService.refreshToken(
                    deviceInfoHeader = UUID.randomUUID().toString(),
                    token = Token(accessToken = localStorage.accessToken, refreshToken = localStorage.refreshToken),
                ).data
                localStorage.updateToken(
                    accessToken = tokens.accessToken,
                    refreshToken = tokens.refreshToken,
                )
                return@runBlocking tokens
            } catch (e: Exception) {
                Timber.e("토큰 리프레시 실패(${e.message})")
                handleAutoLoginExpiration()
                return@runBlocking null
            }
        }

        // Call original request
        return if (newToken?.accessToken?.isNotBlank() == true) {
            val refreshedRequest = chain.request().newAuthBuilder().build()
            chain.proceed(refreshedRequest)
        } else {
            null
        }
    }

    private fun Request.newAuthBuilder() =
        this.newBuilder()
            .addHeader(AUTHORIZATION, "$TOKEN_PREF${localStorage.accessToken}")

    private fun handleAutoLoginExpiration() {
        Timber.d("Token refresh failed, clearing token info")
        localStorage.clear()
        GlobalState.isExpiredAuthLogin.value = true
    }

    companion object {
        private const val AUTHORIZATION = "Authorization"
        private const val TOKEN_PREF = "Bearer "
        const val DEVICE_INFO_HEADER_NAME = "Device-Info"
    }
}
