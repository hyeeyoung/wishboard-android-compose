package com.hyeeyoung.wishboard.di

import com.hyeeyoung.wishboard.data.remote.service.AuthService
import com.hyeeyoung.wishboard.data.remote.service.FolderService
import com.hyeeyoung.wishboard.data.remote.service.ItemService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ServiceModule {
    @Singleton
    @Provides
    fun provideAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Singleton
    @Provides
    fun provideItemService(retrofit: Retrofit): ItemService =
        retrofit.create(ItemService::class.java)

    @Singleton
    @Provides
    fun provideFolderService(retrofit: Retrofit): FolderService =
        retrofit.create(FolderService::class.java)
}
