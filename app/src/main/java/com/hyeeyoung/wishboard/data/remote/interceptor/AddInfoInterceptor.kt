package com.hyeeyoung.wishboard.data.remote.interceptor

import com.hyeeyoung.wishboard.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddInfoInterceptor @Inject constructor(
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder()
                .addHeader(USER_AGENT, "wishboard-aos/${if (BuildConfig.DEBUG) "dev" else "prod"}")
                .build(),
        )
    }

    companion object {
        const val USER_AGENT = "User-Agent"
    }
}
