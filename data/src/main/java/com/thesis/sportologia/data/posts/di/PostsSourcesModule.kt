package com.thesis.sportologia.data.posts.di

import com.thesis.sportologia.data.events.sources.EventsDataSource
import com.thesis.sportologia.data.events.sources.FirestoreEventsDataSource
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
        eventsDataSource: FirestoreEventsDataSource
    ): EventsDataSource

}