package com.thesis.sportologia.model.events

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.thesis.sportologia.di.IoDispatcher
import com.thesis.sportologia.model.events.entities.Event
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryEventsRepository @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) :
    EventsRepository {

    private val dateFrom: Calendar = Calendar.getInstance()
    val dateTo: Calendar = Calendar.getInstance()

    init {
        dateFrom.set(2023, 5, 3, 16, 0, 0)
        dateTo.set(2023, 5, 3, 20, 0, 0)
    }

    private val eventSample = Event(
        id = 0L,
        organizerId = "1",
        organizerName = "Игорь Чиёсов",
        isOrganizerAthlete = true,
        profilePictureUrl = "https://i.imgur.com/tGbaZCY.jpg",
        dateFrom = dateFrom,
        dateTo = dateTo,
        address = null,
        price = 1000f,
        currency = "Rubles",
        categories = hashMapOf(
            Pair("забег", true),
            Pair("мастер-класс", false),
        ),
        description = "Я Игорю и я провожу лучшие занятия для Андрея",
        likesCount = 5,
        isLiked = false,
        isFavourite = false,
        photosUrls = null,
    )

    private val events = mutableListOf(
        eventSample,
        Event(
            id = 1L,
            organizerId = "2",
            organizerName = "Тренажёрный зал Строитель",
            isOrganizerAthlete = false,
            profilePictureUrl = "https://i.imgur.com/tGbaZCY.jpg",
            dateFrom = dateFrom,
            dateTo = dateTo,
            address = null,
            price = 1400f,
            currency = "Rubles",
            categories = hashMapOf(
                Pair("забег", false),
                Pair("мастер-класс", true),
            ),
            description = "Лучший в мире мастер-класс",
            likesCount = 10,
            isLiked = true,
            isFavourite = false,
            photosUrls = null,
        ),
        eventSample.copy(id = 2L),
        eventSample.copy(id = 3L),
        eventSample.copy(id = 4L),
        eventSample.copy(id = 5L),
        eventSample.copy(id = 6L),
        eventSample.copy(id = 7L),
        eventSample.copy(id = 8L),
        eventSample.copy(id = 9L),
        eventSample.copy(id = 10L),
        eventSample.copy(id = 11L),
        eventSample.copy(id = 12L),
        eventSample.copy(
            organizerName = "Антон Игорев",
            id = 13L,
            description = "abcdefghiklmnopqrstvuxwyz"
        ),
        eventSample.copy(id = 14L),
        eventSample.copy(id = 15L),
        eventSample.copy(id = 16L),
        eventSample.copy(id = 17L),
        eventSample.copy(id = 18L),
        eventSample.copy(id = 19L),
        eventSample.copy(id = 20L),
        eventSample.copy(id = 21L),
        eventSample.copy(id = 22L),
        eventSample.copy(id = 23L),
        eventSample.copy(id = 24L),
        eventSample.copy(id = 25L),
    )

    private val followersIds = mutableListOf("2", "3", "5")

    /* override suspend fun getUserEvents(userId: Int): List<Event> {
        delay(1000)
        return events.filter { it.organizerId == userId }
    } */


    private suspend fun getUserEvents(pageIndex: Int, pageSize: Int, userId: String): List<Event> =
        withContext(
            ioDispatcher
        ) {
            delay(1000)
            val offset = pageIndex * pageSize

            val filteredEvents = events.filter { it.organizerId == userId }.reversed()

            // TODO SORT BY DATE

            // TODO
            // throw Exception("a")

            if (offset >= filteredEvents.size) {
                return@withContext listOf<Event>()
            } else if (offset + pageSize >= filteredEvents.size) {
                return@withContext filteredEvents.subList(offset, filteredEvents.size)
            } else {
                return@withContext filteredEvents.subList(offset, offset + pageSize)
            }
        }

    override suspend fun getPagedUserEvents(userId: String): Flow<PagingData<Event>> {
        val loader: EventsPageLoader = { pageIndex, pageSize ->
            getUserEvents(pageIndex, pageSize, userId)
        }

        //delay(2000)

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
        athTorgF: Boolean?
    ): Flow<PagingData<Event>> {
        val loader: EventsPageLoader = { pageIndex, pageSize ->
            getUserSubscribedOnEvents(pageIndex, pageSize, userId, athTorgF)
        }

        //delay(2000)

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

    private suspend fun getUserSubscribedOnEvents(
        pageIndex: Int,
        pageSize: Int,
        userId: String,
        athTorgF: Boolean?
    ): List<Event> = withContext(
        ioDispatcher
    ) {
        val res = mutableListOf<Event>()

        delay(1000)

        // TODO
        //throw Exception("a")

        val offset = pageIndex * pageSize

        followersIds.forEach { id ->
            if (athTorgF == null) {
                res.addAll(events.filter { it.organizerId == id })
            } else {
                res.addAll(events.filter { it.organizerId == id && it.isOrganizerAthlete == athTorgF })
            }
        }

        // TODO МЕТОД ФИГНЯ

        // TODO SORT BY DATE

        if (offset >= res.size) {
            return@withContext listOf<Event>()
        } else if (offset + pageSize >= res.size) {
            return@withContext res.subList(offset, res.size)
        } else {
            return@withContext res.subList(offset, offset + pageSize)
        }
    }

    override suspend fun getPagedUserFavouriteEvents(athTorgF: Boolean?): Flow<PagingData<Event>> {
        val loader: EventsPageLoader = { pageIndex, pageSize ->
            getUserFavouriteEvents(pageIndex, pageSize, athTorgF)
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

    override suspend fun getEvent(eventId: Long): Event? = withContext(ioDispatcher) {
        delay(1000)

        return@withContext if (events.none { it.id == eventId }) null else events.filter { it.id == eventId }[0]
    }

    suspend fun getUserFavouriteEvents(
        pageIndex: Int,
        pageSize: Int,
        athTorgF: Boolean?
    ): List<Event> =
        withContext(
            ioDispatcher
        ) {
            delay(1000)
            val offset = pageIndex * pageSize

            // временный и корявый метод! Ибо тут не учитыааются пользователи
            val filteredEvents = if (athTorgF != null) {
                events.filter { it.isFavourite && it.isOrganizerAthlete == athTorgF }.reversed()
            } else {
                events.filter { it.isFavourite }.reversed()
            }

            // TODO SORT BY DATE

            // TODO
            //throw Exception("a")

            if (offset >= filteredEvents.size) {
                return@withContext listOf<Event>()
            } else if (offset + pageSize >= filteredEvents.size) {
                return@withContext filteredEvents.subList(offset, filteredEvents.size)
            } else {
                return@withContext filteredEvents.subList(offset, offset + pageSize)
            }
        }

    /*override suspend fun getUserSubscribedOnEvents(userId: Int, athTorgF: Boolean?): List<Event> {
        delay(1000)
        val res = mutableListOf<Event>()

        followersIds.forEach { id ->
            if (athTorgF == null) {
                res.addAll(events.filter { it.organizerId == id })
            } else {
                res.addAll(events.filter { it.organizerId == id && it.isAuthorAthlete == athTorgF })
            }
        }

        return res
    }*/

    override suspend fun createEvent(event: Event) {
        delay(1000)
        events.add(event)

        //throw Exception("Ошибка подключения: проверьте соединение с интернетом.")
    }

    override suspend fun updateEvent(event: Event) {
        delay(1000)

        events[events.indexOfFirst { it.id == event.id }] = event

    }

    override suspend fun deleteEvent(eventId: Long) {
        delay(1000)
        events.removeIf { it.id == eventId }
    }

    override suspend fun setIsLiked(userId: String, event: Event, isLiked: Boolean) {
        withContext(ioDispatcher) {
            delay(1000)

            val eventInList = events.find { it.id == event.id } ?: throw IllegalStateException()

            eventInList.isLiked = isLiked

            if (isLiked) {
                eventInList.likesCount++
            } else {
                eventInList.likesCount--
            }
        }
    }

    override suspend fun setIsFavourite(userId: String, event: Event, isFavourite: Boolean) =
        withContext(ioDispatcher) {
            delay(1000)

            // TODO
            //throw Exception("a")

            events.find { it.id == event.id }?.isFavourite = isFavourite
        }

    /*override suspend fun likeEvent(userId: Int, event: Event) {
        delay(1000)
        updateEvent(event.copy(isLiked = true, likesCount = event.likesCount + 1))
    }

    override suspend fun unlikeEvent(userId: Int, event: Event) {
        delay(1000)
        updateEvent(event.copy(isLiked = false, likesCount = event.likesCount - 1))
    }

    override suspend fun addEventToFavourites(userId: Int, event: Event) {
        delay(1000)
        updateEvent(event.copy(isAddedToFavourites = true))
    }

    override suspend fun removeEventFromFavourites(userId: Int, event: Event) {
        delay(1000)
        updateEvent(event.copy(isAddedToFavourites = false))
    }*/

    private companion object {
        const val PAGE_SIZE = 6
    }
}