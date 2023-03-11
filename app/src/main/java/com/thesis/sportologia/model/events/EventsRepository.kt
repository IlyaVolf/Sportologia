package com.thesis.sportologia.model.events

import androidx.paging.PagingData
import com.thesis.sportologia.model.events.entities.Event
import kotlinx.coroutines.flow.Flow

interface EventsRepository {

    suspend fun getPagedUserEvents(userId: String): Flow<PagingData<Event>>

    suspend fun getPagedUserSubscribedOnEvents(userId: String, athTorgF: Boolean?): Flow<PagingData<Event>>

    suspend fun getPagedUserFavouriteEvents(athTorgF: Boolean?): Flow<PagingData<Event>>

    suspend fun getEvent(postId: Long): Event?

    suspend fun createEvent(post: Event)

    suspend fun updateEvent(post: Event)

    suspend fun deleteEvent(postId: Long)

    suspend fun setIsLiked(userId: String, post: Event, isLiked: Boolean)

    suspend fun setIsFavourite(userId: String, post: Event, isFavourite: Boolean)

}