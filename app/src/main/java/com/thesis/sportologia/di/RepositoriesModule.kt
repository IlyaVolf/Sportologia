package com.thesis.sportologia.di

import com.thesis.sportologia.model.posts.InMemoryPostsRepository
import com.thesis.sportologia.model.posts.PostsRepository
import com.thesis.sportologia.model.users.InMemoryUsersRepository
import com.thesis.sportologia.model.users.UsersRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoriesModule {

    @Binds
    abstract fun bindPostsRepository(
        inMemoryPostsRepository: InMemoryPostsRepository
    ): PostsRepository

    @Binds
    abstract fun bindUsersRepository(
        inMemoryPostsRepository: InMemoryUsersRepository
    ): UsersRepository

}