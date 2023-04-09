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

    suspend fun getPagedEvents(
        userId: String,
        searchQuery: String,
        filter: FilterParamsEvents
    ): Flow<PagingData<EventDataEntity>>

    suspend fun getPagedUserEvents(userId: String): Flow<PagingData<EventDataEntity>>

    suspend fun getPagedUserSubscribedOnEvents(
        userId: String,
        isUpcomingOnly: Boolean
    ): Flow<PagingData<EventDataEntity>>

    suspend fun getPagedUserFavouriteEvents(
        userId: String,
        isUpcomingOnly: Boolean
    ): Flow<PagingData<EventDataEntity>>

    suspend fun getEvent(eventId: String, userId: String): EventDataEntity?

    suspend fun createEvent(eventDataEntity: EventDataEntity)

    suspend fun updateEvent(eventDataEntity: EventDataEntity)

    suspend fun deleteEvent(eventId: String)

    suspend fun setIsLiked(userId: String, eventDataEntity: EventDataEntity, isLiked: Boolean)

    suspend fun setIsFavourite(userId: String, eventDataEntity: EventDataEntity, isFavourite: Boolean)

}