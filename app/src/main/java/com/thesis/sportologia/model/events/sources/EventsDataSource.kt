package com.thesis.sportologia.model.events.sources

import androidx.paging.PagingData
import com.thesis.sportologia.model.events.entities.EventDataEntity
import com.thesis.sportologia.model.users.entities.UserType
import kotlinx.coroutines.flow.Flow

interface EventsDataSource {

    suspend fun getPagedUserEvents(
        userId: String,
        lastMarker: Long?,
        pageSize: Int
    ): List<EventDataEntity>

    suspend fun getPagedUserSubscribedOnEvents(
        userId: String,
        userType: UserType?,
        lastMarker: Long?,
        pageSize: Int
    ): List<EventDataEntity>

    suspend fun getPagedUserFavouriteEvents(
        userId: String,
        userType: UserType?,
        lastMarker: Long?,
        pageSize: Int
    ): List<EventDataEntity>

    suspend fun getEvent(eventId: String, userId: String): EventDataEntity?

    suspend fun createEvent(eventDataEntity: EventDataEntity)

    suspend fun updateEvent(eventDataEntity: EventDataEntity)

    suspend fun deleteEvent(eventId: String)

    suspend fun setIsLiked(userId: String, eventDataEntity: EventDataEntity, isLiked: Boolean)

    suspend fun setIsFavourite(userId: String, eventDataEntity: EventDataEntity, isFavourite: Boolean)

}