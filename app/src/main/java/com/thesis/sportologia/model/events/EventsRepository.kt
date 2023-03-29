package com.thesis.sportologia.model.events

import androidx.paging.PagingData
import com.thesis.sportologia.model.OnChange
import com.thesis.sportologia.model.events.entities.Event
import com.thesis.sportologia.model.events.entities.FilterParamsEvents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface EventsRepository {

    val localChanges: EventsLocalChanges
    val localChangesFlow: MutableStateFlow<OnChange<EventsLocalChanges>>

    suspend fun getPagedUserEvents(userId: String): Flow<PagingData<Event>>

    suspend fun getPagedUserSubscribedOnEvents(userId: String, isUpcomingOnly: Boolean): Flow<PagingData<Event>>

    suspend fun getPagedUserFavouriteEvents(isUpcomingOnly: Boolean): Flow<PagingData<Event>>

    suspend fun getPagedEvents(searchQuery: String, filter: FilterParamsEvents): Flow<PagingData<Event>>

    suspend fun getEvent(postId: Long): Event?

    suspend fun createEvent(post: Event)

    suspend fun updateEvent(post: Event)

    suspend fun deleteEvent(postId: Long)

    suspend fun setIsLiked(userId: String, post: Event, isLiked: Boolean)

    suspend fun setIsFavourite(userId: String, post: Event, isFavourite: Boolean)

}