package com.thesis.sportologia.di

import com.thesis.sportologia.model.events.EventsRepository
import com.thesis.sportologia.model.events.EventsRepositoryImpl
import com.thesis.sportologia.model.photos.PhotosRepositoryImpl
import com.thesis.sportologia.model.photos.PhotosRepository
import com.thesis.sportologia.model.posts.PostsRepositoryImpl
import com.thesis.sportologia.model.posts.PostsRepository
import com.thesis.sportologia.model.services.ServicesRepositoryImpl
import com.thesis.sportologia.model.services.ServicesRepository
import com.thesis.sportologia.model.users.AuthTokenImpRepository
import com.thesis.sportologia.model.users.AuthTokenRepository
import com.thesis.sportologia.model.users.InMemoryUsersRepository
import com.thesis.sportologia.model.users.UsersRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoriesModule {

    @Binds
    abstract fun bindPostsRepository(
        postsRepositoryImpl: PostsRepositoryImpl
    ): PostsRepository

    @Binds
    abstract fun bindEventsRepository(
        eventsRepositoryImpl: EventsRepositoryImpl
    ): EventsRepository

    @Binds
    abstract fun bindServicesRepository(
        servicesRepositoryImpl: ServicesRepositoryImpl
    ): ServicesRepository


    @Binds
    abstract fun bindUsersRepository(
        inMemoryUsersRepository: InMemoryUsersRepository
    ): UsersRepository

    @Binds
    abstract fun bindPhotosRepository(
        photosRepositoryImpl: PhotosRepositoryImpl
    ): PhotosRepository

    @Binds
    abstract fun bindAuthTokenRepository(
        authTokenImpRepository: AuthTokenImpRepository
    ): AuthTokenRepository

}