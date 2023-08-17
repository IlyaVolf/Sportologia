package com.thesis.sportologia.di

import android.util.Log
import com.thesis.sportologia.utils.createDefaultGlobalScope
import com.thesis.sportologia.utils.flows.DefaultLazyFlowSubjectFactory
import com.thesis.sportologia.utils.flows.LazyFlowSubjectFactory
import com.thesis.sportologia.utils.logger.LogCatLogger
import com.thesis.sportologia.utils.logger.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

/**
 * Module for providing [Logger] implementation based on
 * system [Log] class.
 */
@Module
@InstallIn(SingletonComponent::class)
class StuffsModule {

    /**
     * We don't need scope annotation here because LogCatHolder is
     * 'object' (already singleton)
     */
    @Provides
    fun provideLogger(): Logger {
        return LogCatLogger
    }

    // TODO тут временно!
    @Provides
    @Singleton
    fun provideLazyFlowSubjectFactory(): LazyFlowSubjectFactory {
        return DefaultLazyFlowSubjectFactory(
            dispatcher = Dispatchers.IO
        )
    }

    // TODO тут временно!
    @Provides
    fun provideCoroutineScope(): CoroutineScope {
        return createDefaultGlobalScope()
    }

}
