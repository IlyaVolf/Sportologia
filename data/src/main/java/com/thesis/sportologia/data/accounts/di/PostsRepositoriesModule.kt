package com.thesis.sportologia.data.accounts.di

import com.thesis.sportologia.data.PostsDataRepository
import com.thesis.sportologia.data.posts.repositories.PostsDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface PostsRepositoriesModule {

    @Binds
    @Singleton
    fun bindOrdersDataSource(
        postsDataRepository: PostsDataRepositoryImpl
    ): PostsDataRepository

}