package com.thesis.sportologia.model.events.di

import com.thesis.sportologia.model.events.sources.FireStoreEventsDataSource
import com.thesis.sportologia.model.events.sources.EventsDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface EventsSourcesModule {

    @Binds
    @Singleton
    fun bindOrdersDataSource(
        eventsDataSource: FireStoreEventsDataSource
    ): EventsDataSource

}