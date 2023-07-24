package com.thesis.sportologia.data.events

import androidx.paging.PagingData
import com.thesis.sportologia.data.events.entities.CreateEventDataEntity
import com.thesis.sportologia.data.events.entities.EventDataEntity
import com.thesis.sportologia.data.events.entities.FilterParamsEvents
import kotlinx.coroutines.flow.Flow

interface EventsDataRepository {

    suspend fun getPagedEvents(
        searchQuery: String,
        filter: FilterParamsEvents
    ): Flow<PagingData<EventDataEntity>>

    suspend fun getPagedUserEvents(
        organizerId: String,
        isUpcomingOnly: Boolean
    ): Flow<PagingData<EventDataEntity>>

    suspend fun getPagedSubscribedOnEvents(
        isUpcomingOnly: Boolean
    ): Flow<PagingData<EventDataEntity>>

    suspend fun getPagedFavouriteEvents(
        isUpcomingOnly: Boolean
    ): Flow<PagingData<EventDataEntity>>

    suspend fun getEvent(eventId: Long): EventDataEntity?

    suspend fun createEvent(createEventDataEntity: CreateEventDataEntity)

    suspend fun updateEvent(eventDataEntity: EventDataEntity)

    suspend fun deleteEvent(eventId: Long)

    suspend fun setIsLiked(eventId: Long, isLiked: Boolean)

    suspend fun setIsFavourite(eventId: Long, isFavourite: Boolean)

}