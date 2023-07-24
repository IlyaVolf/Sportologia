package com.thesis.sportologia.data.events.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.thesis.sportologia.core.entities.AuthException
import com.thesis.sportologia.core.entities.OnChange
import com.thesis.sportologia.data.accounts.sources.AccountsDataSource
import com.thesis.sportologia.data.events.*
import com.thesis.sportologia.data.events.entities.CreateEventDataEntity
import com.thesis.sportologia.data.events.entities.EventDataEntity
import com.thesis.sportologia.data.events.entities.FilterParamsEvents
import com.thesis.sportologia.data.events.sources.EventsDataSource
import com.thesis.sportologia.data.events.entities.EventDataEntity
import com.thesis.sportologia.data.events.repositories.EventsLocalChanges
import com.thesis.sportologia.data.events.sources.EventsDataSource
import com.thesis.sportologia.data.events.EventsDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import javax.inject.Inject

class EventsDataRepositoryImpl @Inject constructor(
    private val eventsDataSource: EventsDataSource,
    private val usersDataSource: UsersDataSource,
    private val accountsDataSource: AccountsDataSource,
) : EventsDataRepository {

    private val localChanges = EventsLocalChanges()
    private val localChangesFlow = MutableStateFlow(OnChange(localChanges))

    override suspend fun getPagedEvents(
        searchQuery: String,
        filter: FilterParamsEvents
    ): Flow<PagingData<EventDataEntity>> {
        val currentUserId: Long = accountsDataSource.getCurrentUserId() ?: throw AuthException()
        
        val loader: EventsPageLoader = { lastTimestamp, pageIndex, pageSize ->
            eventsDataSource.getPagedEvents(
                currentUserId,
                searchQuery,
                filter,
                lastTimestamp,
                pageSize
            )
        }

        return Pager(
            config = PAGING_CONFING,
            pagingSourceFactory = { EventsPagingSource(loader) }
        ).flow
    }

    override suspend fun getPagedUserEvents(
        organizerId: String,
        isUpcomingOnly: Boolean
    ): Flow<PagingData<EventDataEntity>> {
        val currentUserId: Long = accountsDataSource.getCurrentUserId() ?: throw AuthException()
        
        val loader: EventsPageLoader = { lastTimestamp, _, pageSize ->
            eventsDataSource.getPagedUserEvents(
                organizerId,
                currentUserId,
                isUpcomingOnly,
                lastTimestamp,
                pageSize
            )
        }

        return Pager(
            config = PAGING_CONFING,
            pagingSourceFactory = { EventsPagingSource(loader) }
        ).flow
    }

    override suspend fun getPagedSubscribedOnEvents(
        isUpcomingOnly: Boolean
    ): Flow<PagingData<EventDataEntity>> {
        val currentUserId: Long = accountsDataSource.getCurrentUserId() ?: throw AuthException()
        
        val loader: EventsPageLoader = { lastTimestamp, _, pageSize ->
            eventsDataSource.getPagedUserSubscribedOnEvents(
                currentUserId,
                isUpcomingOnly,
                lastTimestamp,
                pageSize
            )
        }

        return Pager(
            config = PAGING_CONFING,
            pagingSourceFactory = { EventsPagingSource(loader) }
        ).flow
    }


    override suspend fun getPagedFavouriteEvents(
        isUpcomingOnly: Boolean
    ): Flow<PagingData<EventDataEntity>> {
        val currentUserId: Long = accountsDataSource.getCurrentUserId() ?: throw AuthException()
        
        val loader: EventsPageLoader = { lastTimestamp, _, pageSize ->
            eventsDataSource.getPagedUserFavouriteEvents(
                currentUserId,
                isUpcomingOnly,
                lastTimestamp,
                pageSize
            )
        }

        return Pager(
            config = PAGING_CONFING,
            pagingSourceFactory = { EventsPagingSource(loader) }
        ).flow
    }

    override suspend fun getEvent(eventId: Long): EventDataEntity {
        val currentUserId: Long = accountsDataSource.getCurrentUserId() ?: throw AuthException()
        
        return eventsDataSource.getEvent(eventId, currentUserId)
    }

    override suspend fun createEvent(createEventDataEntity: CreateEventDataEntity) {
        val currentUserId: Long = accountsDataSource.getCurrentUserId() ?: throw AuthException()
        val user = usersDataSource.getAccount(currentUserId)
        
        return eventsDataSource.createEvent(user, createEventDataEntity)
    }

    override suspend fun updateEvent(eventDataEntity: EventDataEntity) {
        return eventsDataSource.updateEvent(eventDataEntity)
    }

    override suspend fun deleteEvent(eventId: Long) {
        eventsDataSource.deleteEvent(eventId)
        localChanges.remove(eventId)
    }

    override suspend fun setIsLiked(
        eventId: Long,
        isLiked: Boolean
    ) {
        val currentUserId: Long = accountsDataSource.getCurrentUserId() ?: throw AuthException()
        val user = usersDataSource.getAccount(currentUserId)
        
        eventsDataSource.setIsLiked(currentUserId, eventId, isLiked)
    }

    override suspend fun setIsFavourite(
        eventId: Long,
        isFavourite: Boolean
    ) {
        val currentUserId: Long = accountsDataSource.getCurrentUserId() ?: throw AuthException()
        
        eventsDataSource.setIsFavourite(currentUserId, eventId, isFavourite)
    }

    private suspend fun combineEventsFlows(originEventsFlow: Flow<PagingData<EventDataEntity>>):
            Flow<PagingData<EventDataEntity>> {
        originEventsFlow.collect {
            it.map { event ->
                localChanges.isLikedFlags[event.id] = event.isLiked
                localChanges.isFavouriteFlags[event.id] = event.isFavourite
                localChanges.likesCount[event.id] = event.likesCount
            }
        }

        return combine(
            originEventsFlow,
            localChangesFlow.debounce(50),
            ::merge
        )
    }

    private fun merge(
        events: PagingData<EventDataEntity>,
        localChanges: OnChange<EventsLocalChanges>
    ): PagingData<EventDataEntity> {
        return events
            .map { event ->
                val localFavoriteFlag = localChanges.value.isFavouriteFlags[event.id]
                val localLikedFlag = localChanges.value.isLikedFlags[event.id]
                val localLikesCountFlag = localChanges.value.likesCount[event.id]

                var eventWithLocalChanges = event.copy()
                if (localFavoriteFlag != null) {
                    eventWithLocalChanges =
                        eventWithLocalChanges.copy(isFavourite = localFavoriteFlag)
                }
                if (localLikedFlag != null) {
                    eventWithLocalChanges = eventWithLocalChanges.copy(isLiked = localLikedFlag)
                }
                if (localLikesCountFlag != null) {
                    eventWithLocalChanges =
                        eventWithLocalChanges.copy(likesCount = localLikesCountFlag)
                }

                eventWithLocalChanges
            }
    }

    private companion object {
        const val PAGE_SIZE = 7

        val PAGING_CONFING = PagingConfig(
            pageSize = PAGE_SIZE,
            initialLoadSize = PAGE_SIZE,
            prefetchDistance = PAGE_SIZE / 2,
            enablePlaceholders = false
        )
    }
}