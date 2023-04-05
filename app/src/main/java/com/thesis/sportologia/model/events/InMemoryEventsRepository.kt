package com.thesis.sportologia.model.events

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.thesis.sportologia.di.IoDispatcher
import com.thesis.sportologia.model.OnChange
import com.thesis.sportologia.model.events.entities.Event
import com.thesis.sportologia.model.events.entities.FilterParamsEvents
import com.thesis.sportologia.utils.Categories
import com.thesis.sportologia.utils.Position
import com.thesis.sportologia.utils.containsAnyCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryEventsRepository @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : EventsRepository {

    override val localChanges = EventsLocalChanges()
    override val localChangesFlow = MutableStateFlow(OnChange(localChanges))

    private val dateFrom: Calendar = Calendar.getInstance()
    private val dateTo: Calendar = Calendar.getInstance()

    private val dateFrom2: Calendar = Calendar.getInstance()
    private val dateTo2: Calendar = Calendar.getInstance()

    init {
        dateFrom.set(2023, 5, 3, 16, 0, 0)
        dateTo.set(2023, 5, 3, 20, 0, 0)

        dateFrom2.set(2023, 1, 25, 10, 0, 0)
        dateTo2.set(2023, 1, 28, 20, 0, 0)
    }

    private val eventSample = Event(
        id = 0L,
        name = "Сходка лыжников в НГУ",
        description = "Я Игорю и я провожу лучшие занятия по лыжам",
        organizerId = "i_chiesov",
        organizerName = "Игорь Чиёсов",
        isOrganizerAthlete = true,
        profilePictureUrl = null,
        dateFrom = dateFrom.timeInMillis,
        dateTo = dateTo.timeInMillis,
        position = Position(54.838235, 83.093832),
        price = 1000f,
        currency = "Rubles",
        categories = hashMapOf(
            Pair(Categories.MARTIAL_ARTS, false),
            Pair(Categories.RUNNING, true),
            Pair(Categories.MASTER_CLASS, false),
        ),
        likesCount = 5,
        isLiked = false,
        isFavourite = false,
        photosUrls = mutableListOf(),
    )

    private val events = mutableListOf(
        eventSample.copy(isFavourite = true),
        Event(
            id = 1L,
            name = "Мастер-класс",
            description = "Лучший в мире мастер-класс",
            organizerId = "stroitel",
            organizerName = "Тренажёрный зал Строитель",
            isOrganizerAthlete = false,
            profilePictureUrl = null,
            dateFrom = dateFrom2.timeInMillis,
            dateTo = dateTo2.timeInMillis,
            position = Position(55.072128, 82.965262),
            price = 0f,
            currency = "Rubles",
            categories = hashMapOf(
                Pair(Categories.MARTIAL_ARTS, false),
                Pair(Categories.RUNNING, false),
                Pair(Categories.MASTER_CLASS, true),
            ),
            likesCount = 10,
            isLiked = false,
            isFavourite = true,
            photosUrls = mutableListOf(
                "https://avatars.mds.yandex.net/get-mpic/5217165/img_id5807486875283978845.jpeg/orig"
            ),
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
            organizerId = "best_mate",
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

    private val followersIds = mutableListOf("i_chiesov", "stroitel", "nikita")

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

            val filteredEvents = events.filter { it.organizerId == userId }.sortedBy { it.dateFrom }

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
        isUpcomingOnly: Boolean
    ): Flow<PagingData<Event>> {
        val loader: EventsPageLoader = { pageIndex, pageSize ->
            getUserSubscribedOnEvents(pageIndex, pageSize, userId, isUpcomingOnly)
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
        isUpcomingOnly: Boolean
    ): List<Event> = withContext(
        ioDispatcher
    ) {
        val res = mutableListOf<Event>()

        delay(1000)

        // TODO
        //throw Exception("a")

        val offset = pageIndex * pageSize

        followersIds.forEach { id ->
            if (isUpcomingOnly) {
                res.addAll(events.filter {
                    if (it.dateTo != null) {
                        it.organizerId == id && it.dateTo!! > Calendar.getInstance().timeInMillis
                    } else {
                        it.organizerId == id && it.dateFrom > Calendar.getInstance().timeInMillis
                    }
                })
            } else {
                res.addAll(events.filter { it.organizerId == id })
            }
        }

        res.sortedBy { it.dateFrom }

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

    override suspend fun getPagedUserFavouriteEvents(isUpcomingOnly: Boolean): Flow<PagingData<Event>> {
        val loader: EventsPageLoader = { pageIndex, pageSize ->
            getUserFavouriteEvents(pageIndex, pageSize, isUpcomingOnly)
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
        isUpcomingOnly: Boolean
    ): List<Event> = withContext(ioDispatcher) {
        delay(1000)
        val offset = pageIndex * pageSize

        // временный и корявый метод! Ибо тут не учитыааются пользователи
        val filteredEvents = if (isUpcomingOnly) {
            events.filter {
                if (it.dateTo != null) {
                    it.isFavourite && it.dateTo!! > Calendar.getInstance().timeInMillis
                } else {
                    it.isFavourite && it.dateFrom > Calendar.getInstance().timeInMillis
                }
            }.sortedBy { it.dateFrom }
        } else {
            events.filter { it.isFavourite }.sortedBy { it.dateFrom }
        }

        filteredEvents.sortedBy { it.dateFrom }

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

    override suspend fun getPagedEvents(searchQuery: String, filter: FilterParamsEvents)
            : Flow<PagingData<Event>> {
        val loader: EventsPageLoader = { pageIndex, pageSize ->
            getEvents(pageIndex, pageSize, searchQuery, filter)
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

    private suspend fun getEvents(
        pageIndex: Int,
        pageSize: Int,
        searchQuery: String,
        filter: FilterParamsEvents
    ): List<Event> =
        withContext(ioDispatcher) {
            delay(1000)
            val offset = pageIndex * pageSize

            // временный и корявый метод! Ибо тут не учитыааются пользователи
            val eventsFound = events.filter {
                containsAnyCase(it.name, searchQuery)
            }.sortedBy { it.dateFrom }

            // TODO SORT BY DATE

            // TODO
            //throw Exception("a")

            if (offset >= eventsFound.size) {
                return@withContext listOf<Event>()
            } else if (offset + pageSize >= eventsFound.size) {
                return@withContext eventsFound.subList(offset, eventsFound.size)
            } else {
                return@withContext eventsFound.subList(offset, offset + pageSize)
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
        localChanges.remove(eventId)
    }

    override suspend fun setIsLiked(userId: String, event: Event, isLiked: Boolean) {
        withContext(ioDispatcher) {
            delay(1000)

            val eventInList =
                events.find { it.id == event.id } ?: throw IllegalStateException()

            eventInList.isLiked = isLiked

            if (isLiked) {
                eventInList.likesCount++
            } else {
                eventInList.likesCount--
            }
        }
    }

    override suspend fun setIsFavourite(
        userId: String,
        event: Event,
        isFavourite: Boolean
    ) = withContext(ioDispatcher) {
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