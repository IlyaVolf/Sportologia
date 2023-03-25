package com.thesis.sportologia.model.services

import androidx.paging.PagingData
import com.thesis.sportologia.model.services.entities.FilterParamsServices
import com.thesis.sportologia.model.services.entities.Service
import kotlinx.coroutines.flow.Flow

interface ServicesRepository {

    suspend fun getPagedUserServices(userId: String): Flow<PagingData<Service>>

    suspend fun getPagedUserFavouriteServices(userId: String, serviceType: Service.ServiceType?): Flow<PagingData<Service>>

    suspend fun getPagedUserAcquiredServices(userId: String, serviceType: Service.ServiceType?): Flow<PagingData<Service>>

    suspend fun getPagedServices(searchQuery: String, filter: FilterParamsServices): Flow<PagingData<Service>>

    suspend fun getService(postId: Long): Service?

    suspend fun createService(service: Service)

    suspend fun updateService(service: Service)

    suspend fun deleteService(serviceId: Long)

    suspend fun acquireService(serviceId: Long)

    suspend fun setIsFavourite(userId: String, serviceId: Long, isFavourite: Boolean)

}