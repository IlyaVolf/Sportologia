package com.thesis.sportologia.model.services.sources

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.thesis.sportologia.model.services.entities.ServiceDetailedDataEntity
import com.thesis.sportologia.model.services.entities.ServiceDetailedFirestoreEntity
import com.thesis.sportologia.model.users.entities.UserFirestoreEntity
import com.thesis.sportologia.model.users.entities.UserType
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

// TODO вообще нужно проверять внимательно на сущестсование документов с указанным айди. При тестировании!
// TODO вообще храним только id автора, по которому получим пользователяя и данные: аватар, имя и т.п
class FirestoreServicesDataSource @Inject constructor() : ServicesDataSource {

    /** т.к. версия пробная и отсутствуют Google Cloud Functions, для ускорения загрузки во вред
    оптимизации размера данных, мы получаем сразу все id пользователей, которыйп оставили лайк. По-началу
    ничего страшно, когда лайков мало. Зато потом размер получаемых клиентом докмуентов будет
    огромных, если лайков, например, миллион.
    Поэтому вообще это нужно на сервере проверять, лайкнул или нет. И не получать эти списки ни
    в коем случае. Но пока техническо мозможно только такая реализация */

    private val database = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    override suspend fun getPagedUserServices(
        userId: String,
        lastMarker: Long?,
        pageSize: Int
    ): List<ServiceDetailedDataEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun getPagedUserSubscribedOnServices(
        userId: String,
        userType: UserType?,
        lastMarker: Long?,
        pageSize: Int
    ): List<ServiceDetailedDataEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun getPagedUserFavouriteServices(
        userId: String,
        userType: UserType?,
        lastMarker: Long?,
        pageSize: Int
    ): List<ServiceDetailedDataEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun getService(serviceId: String, userId: String): ServiceDetailedDataEntity? {
        TODO("Not yet implemented")
    }

    override suspend fun createService(serviceDataEntity: ServiceDetailedDataEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun updateService(serviceDataEntity: ServiceDetailedDataEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteService(serviceId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun setIsLiked(
        userId: String,
        serviceDataEntity: ServiceDetailedDataEntity,
        isLiked: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun setIsFavourite(
        userId: String,
        serviceDataEntity: ServiceDetailedDataEntity,
        isFavourite: Boolean
    ) {
        TODO("Not yet implemented")
    }

}