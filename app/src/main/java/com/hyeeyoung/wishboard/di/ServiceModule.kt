package com.hyeeyoung.wishboard.di

import com.hyeeyoung.wishboard.data.remote.service.AuthService
import com.hyeeyoung.wishboard.data.remote.service.FolderService
import com.hyeeyoung.wishboard.data.remote.service.GuestAuthService
import com.hyeeyoung.wishboard.data.remote.service.ItemService
import com.hyeeyoung.wishboard.data.remote.service.NotiService
import com.hyeeyoung.wishboard.data.remote.service.SystemService
import com.hyeeyoung.wishboard.data.remote.service.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ServiceModule {
    @Singleton
    @Provides
    fun provideAuthService(@Named("Authenticated") retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Singleton
    @Provides
    fun provideItemService(@Named("Authenticated") retrofit: Retrofit): ItemService =
        retrofit.create(ItemService::class.java)

    @Singleton
    @Provides
    fun provideFolderService(@Named("Authenticated") retrofit: Retrofit): FolderService =
        retrofit.create(FolderService::class.java)

    @Singleton
    @Provides
    fun provideNotiService(@Named("Authenticated") retrofit: Retrofit): NotiService =
        retrofit.create(NotiService::class.java)

    @Singleton
    @Provides
    fun provideUserService(@Named("Authenticated") retrofit: Retrofit): UserService =
        retrofit.create(UserService::class.java)

    @Singleton
    @Provides
    fun provideSystemService(@Named("Authenticated") retrofit: Retrofit): SystemService =
        retrofit.create(SystemService::class.java)

    @Singleton
    @Provides
    fun provideGuestAuthService(@Named("NonAuthenticated") retrofit: Retrofit): GuestAuthService =
        retrofit.create(GuestAuthService::class.java)
}
