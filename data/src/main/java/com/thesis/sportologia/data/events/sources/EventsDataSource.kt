package com.thesis.sportologia.data.events.sources

import com.thesis.sportologia.data.events.entities.CreateEventDataEntity
import com.thesis.sportologia.data.events.entities.EventDataEntity
import com.thesis.sportologia.data.events.entities.FilterParamsEvents
import com.thesis.sportologia.data.users.entities.UserDataEntity

interface EventsDataSource {

    suspend fun getPagedEvents(
        userId: Long,
        searchQuery: String,
        filter: FilterParamsEvents,
        lastMarker: String?,
        pageSize: Int
    ): List<EventDataEntity>

    suspend fun getPagedUserEvents(
        organizerId: String,
        userId: Long,
        isUpcomingOnly: Boolean,
        lastMarker: String?,
        pageSize: Int
    ): List<EventDataEntity>

    suspend fun getPagedUserSubscribedOnEvents(
        userId: Long,
        isUpcomingOnly: Boolean,
        lastMarker: String?,
        pageSize: Int
    ): List<EventDataEntity>

    suspend fun getPagedUserFavouriteEvents(
        userId: Long,
        isUpcomingOnly: Boolean,
        lastMarker: String?,
        pageSize: Int
    ): List<EventDataEntity>

    suspend fun getEvent(eventId: Long, userId: Long): EventDataEntity

    suspend fun createEvent(organizer: UserDataEntity, createEventDataEntity: CreateEventDataEntity)

    suspend fun updateEvent(eventDataEntity: EventDataEntity)

    suspend fun deleteEvent(eventId: Long)

    suspend fun setIsLiked(userId: Long, eventId: Long, isLiked: Boolean)

    suspend fun setIsFavourite(userId: Long, eventId: Long, isFavourite: Boolean)

}