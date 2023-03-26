package com.thesis.sportologia.model.services

import androidx.paging.PagingData
import com.thesis.sportologia.model.services.entities.FilterParamsServices
import com.thesis.sportologia.model.services.entities.Service
import com.thesis.sportologia.model.services.entities.ServiceDetailed
import com.thesis.sportologia.model.services.entities.ServiceType
import kotlinx.coroutines.flow.Flow

interface ServicesRepository {

    suspend fun getPagedUserServices(userId: String): Flow<PagingData<Service>>

    suspend fun getPagedUserFavouriteServices(userId: String, serviceType: ServiceType?): Flow<PagingData<Service>>

    suspend fun getPagedUserAcquiredServices(userId: String, serviceType: ServiceType?): Flow<PagingData<Service>>

    suspend fun getPagedServices(searchQuery: String, filter: FilterParamsServices): Flow<PagingData<Service>>

    suspend fun getService(serviceId: Long): Service?

    suspend fun getServiceDetailed(serviceId: Long): ServiceDetailed?

    suspend fun createService(servicesDetailed: ServiceDetailed)

    suspend fun updateService(servicesDetailed: ServiceDetailed)

    suspend fun deleteService(serviceId: Long)

    suspend fun acquireService(serviceId: Long)

    suspend fun setIsFavourite(userId: String, serviceId: Long, isFavourite: Boolean)

}