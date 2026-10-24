package com.hyeeyoung.wishboard.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.hyeeyoung.wishboard.BuildConfig
import com.hyeeyoung.wishboard.data.local.WishBoardPreference
import com.hyeeyoung.wishboard.data.remote.interceptor.AddInfoInterceptor
import com.hyeeyoung.wishboard.data.remote.interceptor.AuthInterceptor
import com.hyeeyoung.wishboard.data.remote.service.GuestAuthService
import com.hyeeyoung.wishboard.domain.util.JsonUtil
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    @Named("Authenticated")
    fun provideRetrofit(@Named("Authenticated") client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(client)
        .addConverterFactory(JsonUtil.json.asConverterFactory(requireNotNull("application/json".toMediaTypeOrNull())))
        .build()

    @Provides
    @Singleton
    @Named("NonAuthenticated")
    fun provideNonAuthenticatedRetrofit(@Named("NonAuthenticated") client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(
                JsonUtil.json.asConverterFactory(requireNotNull("application/json".toMediaTypeOrNull())),
            )
            .build()

    @Provides
    @Singleton
    fun provideAuthInterceptor(interceptor: AuthInterceptor): Interceptor = interceptor

    @Provides
    @Singleton
    @Named("Authenticated")
    fun provideOkHttpClientBuilder(
        @ApplicationContext context: Context,
        addInfoInterceptor: AddInfoInterceptor,
        interceptor: AuthInterceptor,
    ): OkHttpClient =
        OkHttpClient.Builder().apply {
            connectTimeout(10, TimeUnit.SECONDS)
            writeTimeout(10, TimeUnit.SECONDS)
            readTimeout(10, TimeUnit.SECONDS)
            addInterceptor(addInfoInterceptor)
            addInterceptor(interceptor)
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    },
                )
                addInterceptor(
                    ChuckerInterceptor.Builder(context)
                        .collector(ChuckerCollector(context, showNotification = false))
                        .build(),
                )
            }
        }.build()

    /**
     * SSE 전용 클라이언트. 서버가 이벤트가 없으면 30초마다 heartbeat만 보내므로 읽기 타임아웃을 끈다.
     * 로깅/Chucker 인터셉터는 응답 본문을 끝까지 읽으려 해 스트림을 막을 수 있어 붙이지 않는다.
     */
    @Provides
    @Singleton
    @Named("Sse")
    fun provideSseOkHttpClient(
        addInfoInterceptor: AddInfoInterceptor,
        interceptor: AuthInterceptor,
    ): OkHttpClient =
        OkHttpClient.Builder().apply {
            connectTimeout(10, TimeUnit.SECONDS)
            readTimeout(0, TimeUnit.SECONDS)
            addInterceptor(addInfoInterceptor)
            addInterceptor(interceptor)
        }.build()

    @Provides
    @Singleton
    @Named("NonAuthenticated")
    fun provideNonAuthenticatedOkHttpClient(
        @ApplicationContext context: Context,
        addInfoInterceptor: AddInfoInterceptor,
    ): OkHttpClient =
        OkHttpClient.Builder().apply {
            connectTimeout(10, TimeUnit.SECONDS)
            writeTimeout(10, TimeUnit.SECONDS)
            readTimeout(10, TimeUnit.SECONDS)
            addInterceptor(addInfoInterceptor)
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    },
                )
                addInterceptor(
                    ChuckerInterceptor.Builder(context)
                        .collector(ChuckerCollector(context, showNotification = false))
                        .build(),
                )
            }
        }.build()

    @Provides
    @Singleton
    fun provideTokenInterceptor(
        localStorage: WishBoardPreference,
        authService: GuestAuthService,
    ): AuthInterceptor = AuthInterceptor(
        localStorage = localStorage,
        authService = authService,
    )
}
