package com.thesis.sportologia.model.services.di

import com.thesis.sportologia.model.services.sources.FirestoreServicesDataSource
import com.thesis.sportologia.model.services.sources.ServicesDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ServicesSourcesModule {

    @Binds
    @Singleton
    fun bindOrdersDataSource(
        servicesDataSource: FirestoreServicesDataSource
    ): ServicesDataSource

}