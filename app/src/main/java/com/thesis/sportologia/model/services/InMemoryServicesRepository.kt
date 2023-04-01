package com.thesis.sportologia.model.services

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.thesis.sportologia.di.IoDispatcher
import com.thesis.sportologia.model.OnChange
import com.thesis.sportologia.model.services.entities.*
import com.thesis.sportologia.model.users.entities.UserType
import com.thesis.sportologia.utils.Regularity
import com.thesis.sportologia.utils.TrainingProgrammesCategories
import com.thesis.sportologia.utils.containsAnyCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryServicesRepository @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ServicesRepository {

    override val localChanges = ServicesLocalChanges()
    override val localChangesFlow = MutableStateFlow(OnChange(localChanges))

    /////////////////////////////////////////////////////////////////////////////////

    private val serviceSample = ServiceDetailed(
        id = 0L,
        name = "Программа быстрого похудения",
        type = ServiceType.TRAINING_PROGRAM,
        generalDescription = "Результат уже через 5 недель",
        authorId = "stroitel",
        authorName = "Тренажёрный зал Строитель",
        authorType = UserType.ORGANIZATION,
        profilePictureUrl = null,
        price = 3200f,
        currency = "Rubles",
        categories = hashMapOf(
            Pair(TrainingProgrammesCategories.KEEPING_FORM, false),
            Pair(TrainingProgrammesCategories.LOSING_WEIGHT, true),
            Pair(TrainingProgrammesCategories.GAINING_MUSCLES_MASS, false),
            Pair(TrainingProgrammesCategories.ELSE, false),
        ),
        isFavourite = false,
        isAcquired = false,
        generalPhotosUrls = mutableListOf("https://best5supplements.com/wp-content/uploads/2017/11/bicep-before-after-1024x536.jpg"),
        rating = 5.0F,
        acquiredNumber = 13,
        reviewsNumber = 2,
        detailedDescription = "Тренер Наталья. Для связи используйте WhatsApp",
        detailedPhotosUrls = listOf(),
        exercises = mutableListOf(),
        dateCreatedMillis = Calendar.getInstance().timeInMillis
    )

    private val serviceSample2 = ServiceDetailed(
        id = 1L,
        name = "Набор мышечной массы гантялями",
        type = ServiceType.TRAINING_PROGRAM,
        generalDescription = "Нет денег на зал, но есть гантели дома? - не беда.",
        authorId = "i_volf",
        authorName = "Илья Вольф",
        authorType = UserType.ATHLETE,
        profilePictureUrl = null,
        price = 0f,
        currency = "Rubles",
        categories = hashMapOf(
            Pair(TrainingProgrammesCategories.KEEPING_FORM, false),
            Pair(TrainingProgrammesCategories.LOSING_WEIGHT, false),
            Pair(TrainingProgrammesCategories.GAINING_MUSCLES_MASS, true),
            Pair(TrainingProgrammesCategories.ELSE, false),
        ),
        isFavourite = false,
        isAcquired = true,
        generalPhotosUrls = listOf(),
        rating = 5.0F,
        acquiredNumber = 13,
        reviewsNumber = 2,
        detailedDescription = "Делать надо качсетвенно. Отписываться сюда: ***.com",
        detailedPhotosUrls = listOf(),
        exercises = mutableListOf(
            Exercise(
                0L,
                "Отжимания",
                "Грудью касаемся пола",
                3,
                30,
                hashMapOf(
                    Pair(Regularity.EVERYDAY, true),
                    Pair(Regularity.IN_A_DAY, false),
                    Pair(Regularity.MONDAY, false),
                    Pair(Regularity.TUESDAY, false),
                    Pair(Regularity.WEDNESDAY, false),
                    Pair(Regularity.THURSDAY, false),
                    Pair(Regularity.FRIDAY, false),
                    Pair(Regularity.SATURDAY, false),
                    Pair(Regularity.SUNDAY, false),
                ),
                listOf("https://roliki-magazin.ru/wp-content/uploads/8/a/3/8a3c052b55651b2419bada36d4038ad7.jpeg"),
            ),
            Exercise(
                1L,
                "Подтягивания",
                "До подбородка",
                2,
                12,
                hashMapOf(
                    Pair(Regularity.EVERYDAY, false),
                    Pair(Regularity.IN_A_DAY, false),
                    Pair(Regularity.MONDAY, true),
                    Pair(Regularity.TUESDAY, false),
                    Pair(Regularity.WEDNESDAY, true),
                    Pair(Regularity.THURSDAY, false),
                    Pair(Regularity.FRIDAY, true),
                    Pair(Regularity.SATURDAY, false),
                    Pair(Regularity.SUNDAY, false),
                ),
                listOf(),
            )
        ),
        dateCreatedMillis = Calendar.getInstance().timeInMillis
    )

    private val servicesDetailed = mutableListOf(
        serviceSample,
        serviceSample2,
        serviceSample.copy(id = 2L, isFavourite = true, isAcquired = false),
        serviceSample.copy(id = 3L),
        serviceSample.copy(id = 4L),
        serviceSample.copy(id = 5L),
        serviceSample.copy(id = 6L),
        serviceSample.copy(id = 7L),
        serviceSample.copy(id = 8L),
        serviceSample.copy(id = 9L),
        serviceSample.copy(id = 10L),
        serviceSample.copy(id = 11L),
        serviceSample.copy(id = 12L),
        serviceSample.copy(id = 13L),
        serviceSample.copy(id = 14L),
        serviceSample.copy(id = 15L),
        serviceSample.copy(id = 16L),
        serviceSample.copy(id = 17L),
        serviceSample.copy(id = 18L),
        serviceSample.copy(id = 19L),
        serviceSample.copy(id = 20L),
        serviceSample.copy(id = 21L),
        serviceSample.copy(id = 22L),
        serviceSample.copy(id = 23L),
        serviceSample.copy(id = 24L),
        serviceSample.copy(id = 25L),
    )

    //private val services = servicesDetailed.map { it.toGeneral() }.toMutableList()

    private fun List<ServiceDetailed>.toServices(): List<Service> {
        return this.map { it.copy().toGeneral().copy() }
    }

    private val followersIds = mutableListOf("i_chiesov", "stroitel", "nikita")

    /* override suspend fun getUserServices(userId: Int): List<Service> {
        delay(1000)
        return services.filter { it.organizerId == userId }
    } */


    private suspend fun getUserServices(
        pageIndex: Int,
        pageSize: Int,
        userId: String
    ): List<Service> = withContext(ioDispatcher) {
        delay(1000)
        val offset = pageIndex * pageSize

        val filteredServices =
            servicesDetailed.filter { it.authorId == userId }.sortedBy { it.rating }.toServices()

        // TODO
        // throw Exception("a")

        if (offset >= filteredServices.size) {
            return@withContext listOf<Service>()
        } else if (offset + pageSize >= filteredServices.size) {
            return@withContext filteredServices.subList(offset, filteredServices.size)
        } else {
            return@withContext filteredServices.subList(offset, offset + pageSize)
        }
    }

    override suspend fun getPagedUserServices(userId: String): Flow<PagingData<Service>> {
        val loader: ServicesPageLoader = { pageIndex, pageSize ->
            getUserServices(pageIndex, pageSize, userId)
        }

        //delay(2000)

        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { ServicesPagingSource(loader) }
        ).flow
    }

    private suspend fun getUserSubscribedOnServices(
        pageIndex: Int,
        pageSize: Int,
        userId: String,
        isUpcomingOnly: Boolean
    ): List<Service> = withContext(
        ioDispatcher
    ) {
        val res = mutableListOf<Service>()

        delay(1000)

        // TODO
        //throw Exception("a")

        val offset = pageIndex * pageSize

        followersIds.forEach { id ->
            res.addAll(servicesDetailed.filter { it.authorId == id }.toServices())
        }

        res.sortedBy { it.rating }

        // TODO МЕТОД ФИГНЯ

        // TODO SORT BY DATE

        if (offset >= res.size) {
            return@withContext listOf<Service>()
        } else if (offset + pageSize >= res.size) {
            return@withContext res.subList(offset, res.size)
        } else {
            return@withContext res.subList(offset, offset + pageSize)
        }
    }

    override suspend fun getPagedUserFavouriteServices(
        userId: String,
        serviceType: ServiceType?
    ): Flow<PagingData<Service>> {
        val loader: ServicesPageLoader = { pageIndex, pageSize ->
            getUserFavouriteServices(pageIndex, pageSize, userId, serviceType)
        }

        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { ServicesPagingSource(loader) }
        ).flow
    }

    override suspend fun getPagedUserAcquiredServices(
        userId: String,
        serviceType: ServiceType?
    ): Flow<PagingData<Service>> {
        val loader: ServicesPageLoader = { pageIndex, pageSize ->
            getUserAcquiredServices(pageIndex, pageSize, userId, serviceType)
        }

        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { ServicesPagingSource(loader) }
        ).flow
    }

    override suspend fun getService(serviceId: Long): Service? = withContext(ioDispatcher) {
        delay(1000)

        //throw Exception("abc")

        return@withContext if (servicesDetailed.none { it.id == serviceId }) {
            null
        } else {
            servicesDetailed.filter { it.id == serviceId }.toServices()[0]
        }
    }

    override suspend fun getExercise(serviceId: Long, exerciseId: Long): Exercise? =
        withContext(ioDispatcher) {
            delay(1000)

            //throw Exception("abc")

            val a = 5

            return@withContext if (servicesDetailed.none { it.id == serviceId }) {
                null
            } else if (servicesDetailed.first { it.id == serviceId }.exercises.none { it.id == exerciseId }) {
                null
            } else {
                servicesDetailed.first { it.id == serviceId }.exercises.first { it.id == exerciseId }
            }
        }

    override suspend fun getServiceDetailed(serviceId: Long): ServiceDetailed? =
        withContext(ioDispatcher) {
            delay(1000)

            //throw Exception("abc")

            return@withContext if (servicesDetailed.none { it.id == serviceId }) {
                null
            } else {
                servicesDetailed.filter { it.id == serviceId }[0]
            }
        }

    private suspend fun getUserFavouriteServices(
        pageIndex: Int,
        pageSize: Int,
        userId: String,
        serviceType: ServiceType?
    ): List<Service> = withContext(ioDispatcher) {
        delay(1000)
        val offset = pageIndex * pageSize

        // временный и корявый метод! Ибо тут не учитыааются пользователи
        val filteredServices =
            servicesDetailed.filter { it.isFavourite }.sortedBy { it.rating }.toServices()

        // TODO SORT BY DATE

        // TODO
        //throw Exception("a")

        if (offset >= filteredServices.size) {
            return@withContext listOf<Service>()
        } else if (offset + pageSize >= filteredServices.size) {
            return@withContext filteredServices.subList(offset, filteredServices.size)
        } else {
            return@withContext filteredServices.subList(offset, offset + pageSize)
        }
    }

    suspend fun getUserAcquiredServices(
        pageIndex: Int,
        pageSize: Int,
        userId: String,
        serviceType: ServiceType?
    ): List<Service> = withContext(ioDispatcher) {
        delay(1000)
        val offset = pageIndex * pageSize

        // временный и корявый метод! Ибо тут не учитыааются пользователи
        val filteredServices = servicesDetailed.filter { it.isAcquired }.toServices()

        // TODO SORT BY DATE

        // TODO
        //throw Exception("a")

        if (offset >= filteredServices.size) {
            return@withContext listOf<Service>()
        } else if (offset + pageSize >= filteredServices.size) {
            return@withContext filteredServices.subList(offset, filteredServices.size)
        } else {
            return@withContext filteredServices.subList(offset, offset + pageSize)
        }
    }

    override suspend fun getPagedServices(searchQuery: String, filter: FilterParamsServices)
            : Flow<PagingData<Service>> {
        val loader: ServicesPageLoader = { pageIndex, pageSize ->
            getServices(pageIndex, pageSize, searchQuery, filter)
        }

        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { ServicesPagingSource(loader) }
        ).flow
    }

    private suspend fun getServices(
        pageIndex: Int,
        pageSize: Int,
        searchQuery: String,
        filter: FilterParamsServices
    ): List<Service> =
        withContext(ioDispatcher) {
            delay(1000)
            val offset = pageIndex * pageSize

            // временный и корявый метод! Ибо тут не учитыааются пользователи
            val servicesFound = servicesDetailed.filter {
                containsAnyCase(it.name, searchQuery)
            }.sortedBy { it.rating }.toServices()

            // TODO SORT BY DATE

            // TODO
            //throw Exception("a")

            if (offset >= servicesFound.size) {
                return@withContext listOf<Service>()
            } else if (offset + pageSize >= servicesFound.size) {
                return@withContext servicesFound.subList(offset, servicesFound.size)
            } else {
                return@withContext servicesFound.subList(offset, offset + pageSize)
            }
        }

    /*override suspend fun getUserSubscribedOnServices(userId: Int, athTorgF: Boolean?): List<Service> {
        delay(1000)
        val res = mutableListOf<Service>()

        followersIds.forEach { id ->
            if (athTorgF == null) {
                res.addAll(services.filter { it.organizerId == id })
            } else {
                res.addAll(services.filter { it.organizerId == id && it.isAuthorAthlete == athTorgF })
            }
        }

        return res
    }*/

    override suspend fun createService(serviceDetailed: ServiceDetailed) {
        delay(1000)
        servicesDetailed.add(serviceDetailed)

        //throw Exception("Ошибка подключения: проверьте соединение с интернетом.")
    }

    override suspend fun updateService(serviceDetailed: ServiceDetailed) {
        delay(1000)

        servicesDetailed[servicesDetailed.indexOfFirst { it.id == serviceDetailed.id }] =
            serviceDetailed
    }

    override suspend fun deleteService(serviceId: Long) {
        delay(1000)
        servicesDetailed.removeIf { it.id == serviceId }
        localChanges.remove(serviceId)
    }

    override suspend fun acquireService(serviceId: Long) {
        delay(1000)
        servicesDetailed.find { it.id == serviceId }?.isAcquired = true
    }

    override suspend fun setIsFavourite(
        userId: String,
        serviceId: Long,
        isFavourite: Boolean
    ) = withContext(ioDispatcher) {
        delay(1000)

        // TODO
        //throw Exception("a")

        servicesDetailed.find { it.id == serviceId }?.isFavourite = isFavourite
    }

    /*override suspend fun createExercise(exercise: Exercise) {
        delay(1000)

        servicesDetailed[servicesDetailed.indexOfFirst { it.id == exercise.serviceId }].exercises.add(
            exercise
        )
        //throw Exception("Ошибка подключения: проверьте соединение с интернетом.")
    }

    override suspend fun updateExercise(exercise: Exercise) {
        delay(1000)

        servicesDetailed[servicesDetailed.indexOfFirst { it.id == exercise.serviceId }].exercises[servicesDetailed.indexOfFirst { it.id == exercise.id }] =
            exercise
    }

    override suspend fun deleteExercise(exercise: Exercise) {
        delay(1000)
        servicesDetailed[servicesDetailed.indexOfFirst { it.id == exercise.serviceId }].exercises.removeIf { it.id == exercise.id }
    }*/

    private companion object {
        const val PAGE_SIZE = 6
    }
}