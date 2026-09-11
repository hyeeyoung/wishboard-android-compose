package com.hyeeyoung.wishboard.di

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
            }
        }.build()

    @Provides
    @Singleton
    @Named("NonAuthenticated")
    fun provideNonAuthenticatedOkHttpClient(
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
