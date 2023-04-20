package com.thesis.sportologia.model.services.sources

import androidx.paging.PagingData
import com.thesis.sportologia.model.services.entities.ServiceDetailedDataEntity
import com.thesis.sportologia.model.users.entities.UserType
import kotlinx.coroutines.flow.Flow

interface ServicesDataSource {

    suspend fun getPagedUserServices(
        userId: String,
        lastMarker: Long?,
        pageSize: Int
    ): List<ServiceDetailedDataEntity>

    suspend fun getPagedUserSubscribedOnServices(
        userId: String,
        userType: UserType?,
        lastMarker: Long?,
        pageSize: Int
    ): List<ServiceDetailedDataEntity>

    suspend fun getPagedUserFavouriteServices(
        userId: String,
        userType: UserType?,
        lastMarker: Long?,
        pageSize: Int
    ): List<ServiceDetailedDataEntity>

    suspend fun getService(serviceId: String, userId: String): ServiceDetailedDataEntity?

    suspend fun createService(serviceDataEntity: ServiceDetailedDataEntity)

    suspend fun updateService(serviceDataEntity: ServiceDetailedDataEntity)

    suspend fun deleteService(serviceId: String)

    suspend fun setIsLiked(userId: String, serviceDataEntity: ServiceDetailedDataEntity, isLiked: Boolean)

    suspend fun setIsFavourite(userId: String, serviceDataEntity: ServiceDetailedDataEntity, isFavourite: Boolean)

}