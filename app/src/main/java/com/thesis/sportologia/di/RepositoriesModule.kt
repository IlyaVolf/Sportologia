package com.thesis.sportologia.di

import com.thesis.sportologia.model.events.EventsRepository
import com.thesis.sportologia.model.events.InMemoryEventsRepository
import com.thesis.sportologia.model.photos.InMemoryPhotosRepository
import com.thesis.sportologia.model.photos.PhotosRepository
import com.thesis.sportologia.model.posts.InMemoryPostsRepository
import com.thesis.sportologia.model.posts.PostsRepository
import com.thesis.sportologia.model.services.InMemoryServicesRepository
import com.thesis.sportologia.model.services.ServicesRepository
import com.thesis.sportologia.model.users.AuthTokenImpRepository
import com.thesis.sportologia.model.users.AuthTokenRepository
import com.thesis.sportologia.model.users.InMemoryUsersRepository
import com.thesis.sportologia.model.users.UsersRepository
import com.thesis.sportologia.utils.flows.DefaultLazyFlowSubjectFactory
import com.thesis.sportologia.utils.flows.LazyFlowSubjectFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoriesModule {

    @Binds
    abstract fun bindPostsRepository(
        inMemoryPostsRepository: InMemoryPostsRepository
    ): PostsRepository

    @Binds
    abstract fun bindEventsRepository(
        inMemoryEventsRepository: InMemoryEventsRepository
    ): EventsRepository

    @Binds
    abstract fun bindServicesRepository(
        inMemoryServicesRepository: InMemoryServicesRepository
    ): ServicesRepository


    @Binds
    abstract fun bindUsersRepository(
        inMemoryUsersRepository: InMemoryUsersRepository
    ): UsersRepository

    @Binds
    abstract fun bindPhotosRepository(
        inMemoryPhotosRepository: InMemoryPhotosRepository
    ): PhotosRepository

    @Binds
    abstract fun bindAuthTokenRepository(
        authTokenImpRepository: AuthTokenImpRepository
    ): AuthTokenRepository

}