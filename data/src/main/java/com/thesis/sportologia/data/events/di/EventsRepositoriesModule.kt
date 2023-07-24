package com.thesis.sportologia.data.events.di

import com.thesis.sportologia.data.events.EventsDataRepository
import com.thesis.sportologia.data.events.repositories.EventsDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface EventsRepositoriesModule {

    @Binds
    @Singleton
    fun bindEventsRepository(
        eventsRepository: EventsDataRepositoryImpl
    ): EventsDataRepository

}