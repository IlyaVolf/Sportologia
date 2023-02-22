package com.thesis.sportologia.di

import com.thesis.sportologia.model.posts.InMemoryPostsRepository
import com.thesis.sportologia.model.posts.PostsRepository
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

}