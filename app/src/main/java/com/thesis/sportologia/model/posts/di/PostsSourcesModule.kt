package com.thesis.sportologia.model.posts.di

import com.thesis.sportologia.model.posts.sources.FirestorePostsDataSource
import com.thesis.sportologia.model.posts.sources.PostsDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface PostsSourcesModule {

    @Binds
    @Singleton
    fun bindOrdersDataSource(
        postsDataSource: FirestorePostsDataSource
    ): PostsDataSource

}