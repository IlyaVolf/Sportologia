package com.thesis.sportologia.model.services

import androidx.paging.PagingData
import com.thesis.sportologia.model.OnChange
import com.thesis.sportologia.model.services.entities.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface ServicesRepository {

    val localChanges: ServicesLocalChanges
    val localChangesFlow: MutableStateFlow<OnChange<ServicesLocalChanges>>

    suspend fun getPagedUserServices(userId: String): Flow<PagingData<ServiceDataEntity>>

    suspend fun getPagedUserFavouriteServices(userId: String, serviceType: ServiceType?): Flow<PagingData<ServiceDataEntity>>

    suspend fun getPagedUserAcquiredServices(userId: String, serviceType: ServiceType?): Flow<PagingData<ServiceDataEntity>>

    suspend fun getPagedServices(searchQuery: String, filter: FilterParamsServices): Flow<PagingData<ServiceDataEntity>>

    suspend fun getService(serviceId: String): ServiceDataEntity?

    suspend fun getServiceDetailed(serviceId: String): ServiceDetailedDataEntity?

    suspend fun createService(servicesDetailed: ServiceDetailedDataEntity)

    suspend fun updateService(servicesDetailed: ServiceDetailedDataEntity)

    suspend fun deleteService(serviceId: String)

    suspend fun acquireService(serviceId: String)

    suspend fun setIsFavourite(userId: String, serviceId: String, isFavourite: Boolean)

    suspend fun getExercise(serviceId: String, exerciseId: String): Exercise?

}