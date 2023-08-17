package com.thesis.sportologia.model.users.di

import com.thesis.sportologia.model.users.sources.FirestoreUsersDataSource
import com.thesis.sportologia.model.users.sources.UsersDataSource
import com.thesis.sportologia.utils.flows.LazyFlowSubjectFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface UsersSourcesModule {

    @Binds
    @Singleton
    fun bindOrdersDataSource(
        servicesDataSource: FirestoreUsersDataSource
    ): UsersDataSource

}