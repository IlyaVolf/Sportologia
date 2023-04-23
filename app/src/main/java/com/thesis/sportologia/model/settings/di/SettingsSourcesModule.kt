package com.thesis.sportologia.model.settings.di

import com.thesis.sportologia.model.services.sources.FirestoreServicesDataSource
import com.thesis.sportologia.model.services.sources.ServicesDataSource
import com.thesis.sportologia.model.settings.sources.SettingsDataSource
import com.thesis.sportologia.model.settings.sources.SharedPreferencesSettingsDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppSettingsDataSourceModule {

    @Binds
    @Singleton
    fun bindAppSettingsDataSource(
        settingsDataSource: SharedPreferencesSettingsDataSource
    ): SettingsDataSource

}