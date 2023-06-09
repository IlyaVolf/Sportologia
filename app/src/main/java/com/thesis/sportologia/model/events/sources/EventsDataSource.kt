package com.thesis.sportologia.model.events.sources

import com.thesis.sportologia.model.events.entities.EventDataEntity
import com.thesis.sportologia.model.events.entities.FilterParamsEvents

interface EventsDataSource {

    suspend fun getPagedEvents(
        userId: String,
        searchQuery: String,
        filter: FilterParamsEvents,
        lastMarker: String?,
        pageSize: Int
    ): List<EventDataEntity>

    suspend fun getPagedUserEvents(
        organizerId: String,
        userId: String,
        isUpcomingOnly: Boolean,
        lastMarker: String?,
        pageSize: Int
    ): List<EventDataEntity>

    suspend fun getPagedUserSubscribedOnEvents(
        userId: String,
        isUpcomingOnly: Boolean,
        lastMarker: String?,
        pageSize: Int
    ): List<EventDataEntity>

    suspend fun getPagedUserFavouriteEvents(
        userId: String,
        isUpcomingOnly: Boolean,
        lastMarker: String?,
        pageSize: Int
    ): List<EventDataEntity>

    suspend fun getEvent(eventId: String, userId: String): EventDataEntity

    suspend fun createEvent(eventDataEntity: EventDataEntity)

    suspend fun updateEvent(eventDataEntity: EventDataEntity)

    suspend fun deleteEvent(eventId: String)

    suspend fun setIsLiked(userId: String, eventDataEntity: EventDataEntity, isLiked: Boolean)

    suspend fun setIsFavourite(userId: String, eventDataEntity: EventDataEntity, isFavourite: Boolean)

}