package com.thesis.sportologia.model.services

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.thesis.sportologia.di.IoDispatcher
import com.thesis.sportologia.model.OnChange
import com.thesis.sportologia.model.services.entities.*
import com.thesis.sportologia.model.services.sources.ServicesDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryServicesRepository @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val servicesDataSource: ServicesDataSource
) : ServicesRepository {

    override val localChanges = ServicesLocalChanges()
    override val localChangesFlow = MutableStateFlow(OnChange(localChanges))

    override suspend fun getPagedServices(
        userId: String,
        searchQuery: String,
        filter: FilterParamsServices
    ): Flow<PagingData<ServiceDataEntity>> {
        val loader: ServicesPageLoader = { lastTimestamp, pageIndex, pageSize ->
            try {
                servicesDataSource.getPagedServices(
                    userId,
                    searchQuery,
                    filter,
                    lastTimestamp,
                    pageSize
                )
            } catch (e: Exception) {
                Log.d("abcdef", e.toString())
                throw Exception()
            }
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

    override suspend fun getPagedUserServices(userId: String): Flow<PagingData<ServiceDataEntity>> {
        val loader: ServicesPageLoader = { lastTimestamp, pageIndex, pageSize ->
            servicesDataSource.getPagedUserServices(userId, lastTimestamp, pageSize)
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

    override suspend fun getPagedUserFavouriteServices(
        userId: String,
        serviceType: ServiceType?
    ): Flow<PagingData<ServiceDataEntity>> {
        val loader: ServicesPageLoader = { lastTimestamp, pageIndex, pageSize ->
            servicesDataSource.getPagedUserFavouriteServices(
                userId,
                serviceType,
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
            pagingSourceFactory = { ServicesPagingSource(loader) }
        ).flow
    }

    override suspend fun getPagedUserAcquiredServices(
        userId: String,
        serviceType: ServiceType?
    ): Flow<PagingData<ServiceDataEntity>> {
        val loader: ServicesPageLoader = { lastTimestamp, pageIndex, pageSize ->
            servicesDataSource.getPagedUserAcquiredServices(
                userId,
                serviceType,
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
            pagingSourceFactory = { ServicesPagingSource(loader) }
        ).flow
    }

    override suspend fun getExercise(
        serviceId: String,
        exerciseId: String,
        userId: String
    ): ExerciseDataEntity {
        return servicesDataSource.getServiceDetailed(
            serviceId,
            userId
        ).exerciseDataEntities.firstOrNull { it.id == exerciseId } ?: throw Exception()
    }

    override suspend fun getService(serviceId: String, userId: String): ServiceDataEntity {
        return servicesDataSource.getService(serviceId, userId)
    }

    override suspend fun getServiceDetailed(
        serviceId: String,
        userId: String
    ): ServiceDetailedDataEntity {
        return servicesDataSource.getServiceDetailed(serviceId, userId)
    }

    override suspend fun createService(serviceDetailedDataEntity: ServiceDetailedDataEntity) {
        servicesDataSource.createService(serviceDetailedDataEntity)
    }

    override suspend fun updateService(serviceDetailedDataEntity: ServiceDetailedDataEntity) {
        servicesDataSource.updateService(serviceDetailedDataEntity)
    }

    override suspend fun deleteService(serviceId: String) {
        servicesDataSource.deleteService(serviceId)
    }

    override suspend fun acquireService(userId: String, serviceId: String) {
        servicesDataSource.setIsAcquired(userId, serviceId, true)
    }

    override suspend fun setIsFavourite(
        userId: String,
        serviceId: String,
        isFavourite: Boolean
    ) {
        servicesDataSource.setIsFavourite(userId, serviceId, isFavourite)
    }

    private companion object {
        const val PAGE_SIZE = 6
    }
}