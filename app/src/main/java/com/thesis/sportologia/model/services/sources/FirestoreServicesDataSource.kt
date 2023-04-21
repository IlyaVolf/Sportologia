package com.thesis.sportologia.model.services.sources

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.thesis.sportologia.model.services.entities.ServiceDetailedDataEntity
import com.thesis.sportologia.model.users.entities.UserType
import kotlinx.coroutines.tasks.await
import java.util.*
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
        val generalPhotosFirestore = mutableListOf<Uri>()
        serviceDataEntity.generalPhotosUrls.forEach {
            val photosRef = storage.reference.child("images/services/${UUID.randomUUID()}")
            val downloadUrl = photosRef.putFile(it.toUri())
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
                .await()
                .storage.downloadUrl.await()
            generalPhotosFirestore.add(downloadUrl)
        }

        val detailedPhotosFirestore = mutableListOf<Uri>()
        serviceDataEntity.generalPhotosUrls.forEach {
            val photosRef = storage.reference.child("images/services/${UUID.randomUUID()}")
            val downloadUrl = photosRef.putFile(it.toUri())
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
                .await()
                .storage.downloadUrl.await()
            detailedPhotosFirestore.add(downloadUrl)
        }

        val serviceFirestoreEntity = hashMapOf(
            "type" to serviceDataEntity.type,
            "name" to serviceDataEntity.name,
            "authorId" to serviceDataEntity.authorId,
            "rating" to null,
            "reviewsNumber" to 0,
            "generalDescription" to serviceDataEntity.generalDescription,
            "generalPhotosUrls" to serviceDataEntity.generalPhotosUrls,
            "detailedDescription" to serviceDataEntity.detailedDescription,
            "detailedPhotosUrls" to serviceDataEntity.detailedPhotosUrls,
            "price" to serviceDataEntity.price,
            "acquiredNumber" to serviceDataEntity.acquiredNumber,
            "categories" to serviceDataEntity.categories,
            "currency" to serviceDataEntity.currency,
            "dateCreatedMillis" to Calendar.getInstance().timeInMillis,
            "exercises" to serviceDataEntity.exercises,
            "usersIdsAcquired" to listOf<String>(),
            "usersIdsFavs" to listOf<String>()
        )

        // TODO должна быть атомарной

        val documentRef = database.collection(PATH).document()

        documentRef
            .set(serviceFirestoreEntity)
            .await()

        documentRef
            .update(
                hashMapOf<String, Any>(
                    "id" to documentRef.id,
                )
            )
            .await()
    }

    override suspend fun updateService(serviceDataEntity: ServiceDetailedDataEntity) {
        val serviceFirestoreEntity = hashMapOf(
            "name" to serviceDataEntity.name,
            "generalDescription" to serviceDataEntity.generalDescription,
            "generalPhotosUrls" to serviceDataEntity.generalPhotosUrls,
            "detailedDescription" to serviceDataEntity.detailedDescription,
            "detailedPhotosUrls" to serviceDataEntity.detailedPhotosUrls,
            "price" to serviceDataEntity.price,
            "categories" to serviceDataEntity.categories,
            "currency" to serviceDataEntity.currency,
            "exercises" to serviceDataEntity.exercises,
        )

        database.collection(PATH)
            .document(serviceDataEntity.id!!)
            .update(serviceFirestoreEntity)
            .await()
    }

    override suspend fun deleteService(serviceId: String) {
        database.collection(PATH)
            .document(serviceId)
            .delete()
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()
    }

    override suspend fun setIsFavourite(
        userId: String,
        serviceDataEntity: ServiceDetailedDataEntity,
        isFavourite: Boolean
    ) {
        if (isFavourite) {
            database.collection(PATH)
                .document(serviceDataEntity.id!!)
                .update(
                    hashMapOf<String, Any>(
                        "usersIdsFavs" to FieldValue.arrayUnion(userId)
                    )
                )
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
        } else {
            database.collection(PATH)
                .document(serviceDataEntity.id!!)
                .update(
                    hashMapOf<String, Any>(
                        "usersIdsFavs" to FieldValue.arrayRemove(userId)
                    )
                )
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
                .await()
        }
    }

    override suspend fun setIsAcquired(
        userId: String,
        serviceDataEntity: ServiceDetailedDataEntity,
        isAcquired: Boolean
    ) {
        if (isAcquired) {
            database.collection(PATH)
                .document(serviceDataEntity.id!!)
                .update(
                    hashMapOf<String, Any>(
                        "usersIdsAcquired" to FieldValue.arrayUnion(userId)
                    )
                )
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
        } else {
            database.collection(PATH)
                .document(serviceDataEntity.id!!)
                .update(
                    hashMapOf<String, Any>(
                        "usersIdsAcquired" to FieldValue.arrayRemove(userId)
                    )
                )
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
                .await()
        }
    }

    companion object {
        const val PATH = "services"
    }

}