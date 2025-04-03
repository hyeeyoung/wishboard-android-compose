package com.hyeeyoung.wishboard.di

import com.hyeeyoung.wishboard.data.remote.repository.AuthRepositoryImpl
import com.hyeeyoung.wishboard.data.remote.repository.FolderRepositoryImpl
import com.hyeeyoung.wishboard.data.remote.repository.ItemRepositoryImpl
import com.hyeeyoung.wishboard.data.remote.repository.NotiRepositoryImpl
import com.hyeeyoung.wishboard.data.remote.repository.SystemRepositoryImpl
import com.hyeeyoung.wishboard.data.remote.repository.UserRepositoryImpl
import com.hyeeyoung.wishboard.domain.repository.AuthRepository
import com.hyeeyoung.wishboard.domain.repository.FolderRepository
import com.hyeeyoung.wishboard.domain.repository.ItemRepository
import com.hyeeyoung.wishboard.domain.repository.NotiRepository
import com.hyeeyoung.wishboard.domain.repository.SystemRepository
import com.hyeeyoung.wishboard.domain.repository.UserRepository
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

    @Binds
    abstract fun bindNotiRepository(repositoryImpl: NotiRepositoryImpl): NotiRepository

    @Binds
    abstract fun bindUserRepository(repositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindSystemRepository(repositoryImpl: SystemRepositoryImpl): SystemRepository
}
