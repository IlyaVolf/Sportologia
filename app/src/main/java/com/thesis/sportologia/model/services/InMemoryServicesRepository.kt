package com.thesis.sportologia.model.services

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.thesis.sportologia.di.IoDispatcher
import com.thesis.sportologia.model.services.entities.Service
import com.thesis.sportologia.model.services.entities.FilterParamsServices
import com.thesis.sportologia.model.users.entities.UserType
import com.thesis.sportologia.utils.Categories
import com.thesis.sportologia.utils.TrainingProgrammesCategories
import com.thesis.sportologia.utils.containsAnyCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryServicesRepository @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ServicesRepository {

    private val serviceSample = Service(
        id = 0L,
        name = "Программа быстрого похудения",
        type = Service.ServiceType.TRAINING_PROGRAM,
        publicDescription = "Результат уже через 5 недель",
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
        photosUrls = mutableListOf("https://best5supplements.com/wp-content/uploads/2017/11/bicep-before-after-1024x536.jpg"),
        rating = 5.0F,
        acquiredNumber = 13,
        reviewsNumber = 2,
    )

    private val serviceSample2 = Service(
        id = 1L,
        name = "Набор мышечной массы гантялями",
        type = Service.ServiceType.TRAINING_PROGRAM,
        publicDescription = "Нет денег на зал, но есть гантели дома? - не беда.",
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
        photosUrls = null,
        rating = 5.0F,
        acquiredNumber = 13,
        reviewsNumber = 2,
    )

    private val services = mutableListOf(
        serviceSample,
        serviceSample2,
        serviceSample.copy(id = 2L, isFavourite = true, isAcquired = true),
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

    private val followersIds = mutableListOf("i_chiesov", "stroitel", "nikita")

    /* override suspend fun getUserServices(userId: Int): List<Service> {
        delay(1000)
        return services.filter { it.organizerId == userId }
    } */


    private suspend fun getUserServices(
        pageIndex: Int,
        pageSize: Int,
        userId: String
    ): List<Service> =
        withContext(
            ioDispatcher
        ) {
            delay(1000)
            val offset = pageIndex * pageSize

            val filteredServices = services.filter { it.authorId == userId }.sortedBy { it.rating }

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
            res.addAll(services.filter { it.authorId == id })
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

    override suspend fun getPagedUserFavouriteServices(userId: String, serviceType: Service.ServiceType?): Flow<PagingData<Service>> {
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

    override suspend fun getPagedUserAcquiredServices(userId: String, serviceType: Service.ServiceType?): Flow<PagingData<Service>> {
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

        return@withContext if (services.none { it.id == serviceId }) null else services.filter { it.id == serviceId }[0]
    }

    suspend fun getUserFavouriteServices(
        pageIndex: Int,
        pageSize: Int,
        userId: String,
        serviceType: Service.ServiceType?
    ): List<Service> = withContext(ioDispatcher) {
        delay(1000)
        val offset = pageIndex * pageSize

        // временный и корявый метод! Ибо тут не учитыааются пользователи
        val filteredServices = services.filter { it.isFavourite }.sortedBy { it.rating }

        filteredServices.sortedBy { it.rating }

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
        serviceType: Service.ServiceType?
    ): List<Service> = withContext(ioDispatcher) {
        delay(1000)
        val offset = pageIndex * pageSize

        // временный и корявый метод! Ибо тут не учитыааются пользователи
        val filteredServices = services.filter { it.isAcquired }

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
            val servicesFound = services.filter {
                containsAnyCase(it.name, searchQuery)
            }.sortedBy { it.rating }

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

    override suspend fun createService(service: Service) {
        delay(1000)
        services.add(service)

        //throw Exception("Ошибка подключения: проверьте соединение с интернетом.")
    }

    override suspend fun updateService(service: Service) {
        delay(1000)

        services[services.indexOfFirst { it.id == service.id }] = service

    }

    override suspend fun deleteService(serviceId: Long) {
        delay(1000)
        services.removeIf { it.id == serviceId }
    }

    override suspend fun setIsFavourite(
        userId: String,
        service: Service,
        isFavourite: Boolean
    ) = withContext(ioDispatcher) {
        delay(1000)

        // TODO
        //throw Exception("a")

        services.find { it.id == service.id }?.isFavourite = isFavourite
    }

    /*override suspend fun likeService(userId: Int, service: Service) {
        delay(1000)
        updateService(service.copy(isLiked = true, likesCount = service.likesCount + 1))
    }

    override suspend fun unlikeService(userId: Int, service: Service) {
        delay(1000)
        updateService(service.copy(isLiked = false, likesCount = service.likesCount - 1))
    }

    override suspend fun addServiceToFavourites(userId: Int, service: Service) {
        delay(1000)
        updateService(service.copy(isAddedToFavourites = true))
    }

    override suspend fun removeServiceFromFavourites(userId: Int, service: Service) {
        delay(1000)
        updateService(service.copy(isAddedToFavourites = false))
    }*/

    private companion object {
        const val PAGE_SIZE = 6
    }
}