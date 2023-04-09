package com.thesis.sportologia.model.events

import androidx.paging.PagingData
import com.thesis.sportologia.model.OnChange
import com.thesis.sportologia.model.events.entities.EventDataEntity
import com.thesis.sportologia.model.events.entities.FilterParamsEvents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface EventsRepository {

    val localChanges: EventsLocalChanges
    val localChangesFlow: MutableStateFlow<OnChange<EventsLocalChanges>>

    suspend fun getPagedUserEvents(userId: String): Flow<PagingData<EventDataEntity>>

    suspend fun getPagedUserSubscribedOnEvents(userId: String, isUpcomingOnly: Boolean): Flow<PagingData<EventDataEntity>>

    suspend fun getPagedUserFavouriteEvents(isUpcomingOnly: Boolean): Flow<PagingData<EventDataEntity>>

    suspend fun getPagedEvents(searchQuery: String, filter: FilterParamsEvents): Flow<PagingData<EventDataEntity>>

    suspend fun getEvent(eventId: String): EventDataEntity?

    suspend fun createEvent(event: EventDataEntity)

    suspend fun updateEvent(event: EventDataEntity)

    suspend fun deleteEvent(eventId: String)

    suspend fun setIsLiked(userId: String, event: EventDataEntity, isLiked: Boolean)

    suspend fun setIsFavourite(userId: String, event: EventDataEntity, isFavourite: Boolean)

}