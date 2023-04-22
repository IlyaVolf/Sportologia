package com.thesis.sportologia.model.services.sources

import com.thesis.sportologia.model.services.entities.FilterParamsServices
import com.thesis.sportologia.model.services.entities.ServiceDataEntity
import com.thesis.sportologia.model.services.entities.ServiceDetailedDataEntity
import com.thesis.sportologia.model.services.entities.ServiceType

interface ServicesDataSource {

    suspend fun getPagedServices(
        userId: String,
        searchQuery: String,
        filter: FilterParamsServices,
        lastMarker: Long?,
        pageSize: Int
    ): List<ServiceDataEntity>

    suspend fun getPagedUserServices(
        userId: String,
        lastMarker: Long?,
        pageSize: Int
    ): List<ServiceDataEntity>

    suspend fun getPagedUserFavouriteServices(
        userId: String,
        serviceType: ServiceType?,
        lastMarker: Long?,
        pageSize: Int
    ): List<ServiceDataEntity>

    suspend fun getPagedUserAcquiredServices(
        userId: String,
        serviceType: ServiceType?,
        lastMarker: Long?,
        pageSize: Int
    ): List<ServiceDataEntity>

    suspend fun getService(serviceId: String, userId: String): ServiceDataEntity

    suspend fun getServiceDetailed(serviceId: String, userId: String): ServiceDetailedDataEntity

    suspend fun createService(serviceDataEntity: ServiceDetailedDataEntity)

    suspend fun updateService(serviceDataEntity: ServiceDetailedDataEntity)

    suspend fun deleteService(serviceId: String)

    suspend fun setIsFavourite(userId: String, serviceId: String, isFavourite: Boolean)

    suspend fun setIsAcquired(userId: String, serviceId: String, isAcquired: Boolean)

}