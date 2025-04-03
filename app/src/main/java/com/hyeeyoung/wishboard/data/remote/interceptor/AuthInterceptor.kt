package com.hyeeyoung.wishboard.data.remote.interceptor

import com.hyeeyoung.wishboard.BuildConfig
import com.hyeeyoung.wishboard.config.GlobalState
import com.hyeeyoung.wishboard.data.local.WishBoardPreference
import com.hyeeyoung.wishboard.data.remote.model.auth.ResponseToken
import com.hyeeyoung.wishboard.data.remote.model.base.BaseResponse
import com.hyeeyoung.wishboard.domain.util.JsonUtil
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val localStorage: WishBoardPreference,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest = chain.request()
        val authRequest = originRequest.newAuthBuilder().build()
        val response = chain.proceed(authRequest)

        when (response.code) {
            401 -> {
                response.close()
                val requestBody = FormBody.Builder().add(REFRESH_TOKEN, localStorage.refreshToken).build()
                val refreshRequest =
                    originRequest.newBuilder().url("${BuildConfig.BASE_URL}auth/refresh").post(requestBody).build()
                val refreshResponse = chain.proceed(refreshRequest)
                val responseBody = refreshResponse.body?.string()

                if (refreshResponse.isSuccessful) {
                    Timber.d("토큰 리프레시 성공")
                    val refreshData = JsonUtil.json.decodeFromString<BaseResponse<ResponseToken>>(
                        responseBody ?: throw NullPointerException("refreshResponse.body is null"),
                    ).data

                    localStorage.updateToken(
                        accessToken = refreshData.token.accessToken,
                        refreshToken = refreshData.token.refreshToken,
                    )

                    refreshResponse.close()
                    return chain.proceed(originRequest.newAuthBuilder().build())
                } else {
                    Timber.e("토큰 리프레시 실패(${refreshResponse.message})")
                    handleAutoLoginExpiration()
                }
            }
        }

        return response
    }

    private fun Request.newAuthBuilder() =
        this.newBuilder()
            .addHeader(AUTHORIZATION, "$TOKEN_PREF${localStorage.accessToken}")
            .addHeader(USER_AGENT, "wishboard-aos/${if (BuildConfig.DEBUG) "dev" else "prod"}")

    private fun handleAutoLoginExpiration() {
        Timber.d("Token refresh failed, clearing token info")
        localStorage.clearForLogout()
        GlobalState.isExpiredAuthLogin.value = true
    }

    companion object {
        private const val AUTHORIZATION = "Authorization"
        private const val USER_AGENT = "User-Agent"
        private const val TOKEN_PREF = "Bearer "
        private const val REFRESH_TOKEN = "refreshToken"
    }
}
