package com.thesis.sportologia.model.events

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.thesis.sportologia.di.IoDispatcher
import com.thesis.sportologia.model.OnChange
import com.thesis.sportologia.model.events.entities.EventDataEntity
import com.thesis.sportologia.model.events.entities.FilterParamsEvents
import com.thesis.sportologia.model.events.sources.EventsDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryEventsRepository @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val eventsDataSource: EventsDataSource
) : EventsRepository {

    override val localChanges = EventsLocalChanges()
    override val localChangesFlow = MutableStateFlow(OnChange(localChanges))

    override suspend fun getPagedEvents(
        searchQuery: String,
        filter: FilterParamsEvents
    ): Flow<PagingData<EventDataEntity>> {
        val loader: EventsPageLoader = { lastTimestamp, pageIndex, pageSize ->
            // eventsDataSource.getPagedUserEvents(userId, lastTimestamp, pageSize)
            emptyList()
        }

        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { EventsPagingSource(loader) }
        ).flow
    }

    override suspend fun getPagedUserEvents(userId: String): Flow<PagingData<EventDataEntity>> {
        val loader: EventsPageLoader = { lastTimestamp, _, pageSize ->
            eventsDataSource.getPagedUserEvents(userId, lastTimestamp, pageSize)
        }

        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { EventsPagingSource(loader) }
        ).flow
    }

    override suspend fun getPagedUserSubscribedOnEvents(
        userId: String,
        isUpcomingOnly: Boolean
    ): Flow<PagingData<EventDataEntity>> {
        val loader: EventsPageLoader = { lastTimestamp, _, pageSize ->
            eventsDataSource.getPagedUserSubscribedOnEvents(
                userId,
                isUpcomingOnly,
                lastTimestamp,
                pageSize
            )
        }

        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { EventsPagingSource(loader) }
        ).flow
    }


    override suspend fun getPagedUserFavouriteEvents(
        userId: String,
        isUpcomingOnly: Boolean
    ): Flow<PagingData<EventDataEntity>> {
        val loader: EventsPageLoader = { lastTimestamp, _, pageSize ->
            eventsDataSource.getPagedUserFavouriteEvents(
                userId,
                isUpcomingOnly,
                lastTimestamp,
                pageSize
            )
        }

        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { EventsPagingSource(loader) }
        ).flow
    }

    override suspend fun getEvent(eventId: String, userId: String): EventDataEntity? {
        return eventsDataSource.getEvent(eventId, userId)
    }

    override suspend fun createEvent(eventDataEntity: EventDataEntity) {
        return eventsDataSource.createEvent(eventDataEntity)
    }

    override suspend fun updateEvent(eventDataEntity: EventDataEntity) {
        return eventsDataSource.updateEvent(eventDataEntity)
    }

    override suspend fun deleteEvent(eventId: String) {
        eventsDataSource.deleteEvent(eventId)
        localChanges.remove(eventId)
    }

    override suspend fun setIsLiked(
        userId: String,
        eventDataEntity: EventDataEntity,
        isLiked: Boolean
    ) = withContext(ioDispatcher) {
        withTimeout(AWAITING_TIME) {
            eventsDataSource.setIsLiked(userId, eventDataEntity, isLiked)
        }
    }

    override suspend fun setIsFavourite(
        userId: String,
        eventDataEntity: EventDataEntity,
        isFavourite: Boolean
    ) = withContext(ioDispatcher) {
        withTimeout(AWAITING_TIME) {
            eventsDataSource.setIsFavourite(userId, eventDataEntity, isFavourite)
        }
    }

    private companion object {
        const val PAGE_SIZE = 7
        const val AWAITING_TIME = 5000L
    }
}