package com.hyeeyoung.wishboard.di

import com.hyeeyoung.wishboard.data.remote.repository.AuthRepositoryImpl
import com.hyeeyoung.wishboard.data.remote.repository.FolderRepositoryImpl
import com.hyeeyoung.wishboard.data.remote.repository.ItemRepositoryImpl
import com.hyeeyoung.wishboard.domain.repository.AuthRepository
import com.hyeeyoung.wishboard.domain.repository.FolderRepository
import com.hyeeyoung.wishboard.domain.repository.ItemRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindSignRepository(repositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindItemRepository(repositoryImpl: ItemRepositoryImpl): ItemRepository

    @Binds
    abstract fun bindFolderRepository(repositoryImpl: FolderRepositoryImpl): FolderRepository
}