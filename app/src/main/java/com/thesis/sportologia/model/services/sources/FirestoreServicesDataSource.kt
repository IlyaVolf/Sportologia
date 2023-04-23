package com.thesis.sportologia.model.services.sources

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.thesis.sportologia.model.services.entities.*
import com.thesis.sportologia.model.users.entities.UserFirestoreEntity
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

    override suspend fun getPagedServices(
        userId: String,
        searchQuery: String,
        filter: FilterParamsServices,
        lastMarker: Long?,
        pageSize: Int
    ): List<ServiceDataEntity> {
        val currentPageDocuments: QuerySnapshot?
        val currentPageIds = mutableListOf<String>()
        val currentPageAcquired = mutableListOf<Boolean>()
        val currentPageFavs = mutableListOf<Boolean>()

        /*database.collection(SERVICES_PATH).get(Source.SERVER).addOnSuccessListener { snap ->
            snap.documents.forEach { doc ->
                database.collection(SERVICES_PATH).document(doc.id).update(
                    hashMapOf<String, Any>(
                        "tokens" to doc.get("name").toString().split(" ").filter { it.isNotBlank() }
                            .map {
                                it.lowercase(
                                    Locale.getDefault()
                                )
                            } + "")
                )
            }
        }*/

        val searchQueryTokens = searchQuery.split(" ").filter { it.isNotBlank() }.map {
            it.lowercase(
                Locale.getDefault()
            )
        }.ifEmpty { listOf("") }

        if (lastMarker == null) {
            currentPageDocuments = database.collection(SERVICES_PATH)
                .whereArrayContainsAny("tokens", searchQueryTokens)
                .orderBy("dateCreatedMillis", Query.Direction.DESCENDING)
                .limit(pageSize.toLong())
                .get(Source.SERVER)
                .await()
        } else {
            currentPageDocuments = database.collection(SERVICES_PATH)
                .whereArrayContainsAny("tokens", searchQueryTokens)
                .orderBy("dateCreatedMillis", Query.Direction.DESCENDING)
                .limit(pageSize.toLong())
                .startAfter(lastMarker)
                .get(Source.SERVER)
                .await()
        }

        if (currentPageDocuments.isEmpty) {
            return emptyList()
        }

        val services = currentPageDocuments.toObjects(ServiceDetailedFirestoreEntity::class.java)

        val usersLists = services.map { it.authorId }

        val usersDocuments = database.collection(USERS_PATH)
            .whereIn("id", usersLists)
            .get(Source.SERVER)
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()

        val users = usersDocuments.toObjects(UserFirestoreEntity::class.java)

        currentPageDocuments.forEach {
            currentPageIds.add(it.id)
        }
        services.forEach {
            currentPageAcquired.add(it.usersIdsAcquired.contains(userId))
            currentPageFavs.add(it.usersIdsFavs.contains(userId))
        }

        val res = mutableListOf<ServiceDataEntity>()
        for (i in services.indices) {
            res.add(
                ServiceDataEntity(
                    type = services[i].type!!,
                    id = services[i].id,
                    name = services[i].name!!,
                    generalDescription = services[i].generalDescription!!,
                    generalPhotosUrls = services[i].generalPhotosUrls,
                    authorId = services[i].authorId!!,
                    authorName = users.first { it.id == services[i].authorId }.name!!,
                    authorType = when (users.first { it.id == services[i].authorId }.userType) {
                        UserType.ATHLETE.name -> UserType.ATHLETE
                        UserType.ORGANIZATION.name  -> UserType.ORGANIZATION
                        else -> throw Exception("backend data consistency exception")
                    },
                    profilePictureUrl = users.first { it.id == services[i].authorId }.profilePhotoURI,
                    price = services[i].price!!,
                    currency = services[i].currency!!,
                    categories = services[i].categories!!,
                    rating = services[i].rating,
                    acquiredNumber = services[i].acquiredNumber!!,
                    reviewsNumber = services[i].reviewsNumber!!,
                    isFavourite = services[i].usersIdsFavs.contains(userId),
                    isAcquired = services[i].usersIdsAcquired.contains(userId),
                    dateCreatedMillis = services[i].dateCreatedMillis!!
                )
            )
        }

        return res
    }

    override suspend fun getPagedUserServices(
        userId: String,
        lastMarker: Long?,
        pageSize: Int
    ): List<ServiceDataEntity> {
        val currentPageDocuments: QuerySnapshot?
        val currentPageIds = mutableListOf<String>()
        val currentPageAcquired = mutableListOf<Boolean>()
        val currentPageFavs = mutableListOf<Boolean>()

        if (lastMarker == null) {
            currentPageDocuments = database.collection(SERVICES_PATH)
                .whereEqualTo("authorId", userId)
                .orderBy("dateCreatedMillis", Query.Direction.DESCENDING)
                .limit(pageSize.toLong())
                .get(Source.SERVER)
                .await()
        } else {
            currentPageDocuments = database.collection(SERVICES_PATH)
                .whereEqualTo("authorId", userId)
                .orderBy("dateCreatedMillis", Query.Direction.DESCENDING)
                .limit(pageSize.toLong())
                .startAfter(lastMarker)
                .get(Source.SERVER)
                .await()
        }

        if (currentPageDocuments.isEmpty) {
            return emptyList()
        }

        val services = currentPageDocuments.toObjects(ServiceDetailedFirestoreEntity::class.java)

        val userDocument = database.collection(USERS_PATH)
            .document(userId)
            .get(Source.SERVER)
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()
        val user = userDocument.toObject(UserFirestoreEntity::class.java)
            ?: throw Exception("error user loading")

        currentPageDocuments.forEach {
            currentPageIds.add(it.id)
        }
        services.forEach {
            currentPageAcquired.add(it.usersIdsAcquired.contains(userId))
            currentPageFavs.add(it.usersIdsFavs.contains(userId))
        }

        val res = mutableListOf<ServiceDataEntity>()
        for (i in services.indices) {
            res.add(
                ServiceDataEntity(
                    type = services[i].type!!,
                    id = services[i].id,
                    name = services[i].name!!,
                    generalDescription = services[i].generalDescription!!,
                    generalPhotosUrls = services[i].generalPhotosUrls,
                    authorId = user.id!!,
                    authorName = user.name!!,
                    authorType = when (user.userType) {
                        "ATHLETE" -> UserType.ATHLETE
                        "ORGANIZATION" -> UserType.ORGANIZATION
                        else -> throw Exception("backend data consistency exception")
                    },
                    profilePictureUrl = user.profilePhotoURI,
                    price = services[i].price!!,
                    currency = services[i].currency!!,
                    categories = services[i].categories!!,
                    rating = services[i].rating,
                    acquiredNumber = services[i].acquiredNumber!!,
                    reviewsNumber = services[i].reviewsNumber!!,
                    isFavourite = services[i].usersIdsFavs.contains(userId),
                    isAcquired = services[i].usersIdsAcquired.contains(userId),
                    dateCreatedMillis = services[i].dateCreatedMillis!!
                )
            )
        }

        return res
    }

    override suspend fun getPagedUserFavouriteServices(
        userId: String,
        serviceType: ServiceType?,
        lastMarker: Long?,
        pageSize: Int
    ): List<ServiceDataEntity> {
        val currentPageDocuments: QuerySnapshot?
        val currentPageAcquired = mutableListOf<Boolean>()
        val currentPageFavs = mutableListOf<Boolean>()
        val currentPageAuthors = mutableListOf<UserFirestoreEntity>()

        if (lastMarker == null) {
            currentPageDocuments = database.collection(SERVICES_PATH)
                .whereArrayContains("usersIdsFavs", userId)
                .orderBy("dateCreatedMillis", Query.Direction.DESCENDING)
                .limit(pageSize.toLong())
                .get(Source.SERVER)
                .await()
        } else {
            currentPageDocuments = database.collection(SERVICES_PATH)
                .whereArrayContains("usersIdsFavs", userId)
                .orderBy("dateCreatedMillis", Query.Direction.DESCENDING)
                .limit(pageSize.toLong())
                .startAfter(lastMarker)
                .get(Source.SERVER)
                .await()
        }

        if (currentPageDocuments.isEmpty) {
            return emptyList()
        }

        val services = currentPageDocuments.toObjects(ServiceDetailedFirestoreEntity::class.java)

        services.forEach {
            currentPageAcquired.add(it.usersIdsAcquired.contains(userId))
            currentPageFavs.add(it.usersIdsFavs.contains(userId))

            val authorDocument = database.collection(USERS_PATH)
                .document(it.authorId!!)
                .get(Source.SERVER)
                .await()

            val author =
                authorDocument.toObject(UserFirestoreEntity::class.java) ?: throw Exception()

            currentPageAuthors.add(author)
        }

        val res = mutableListOf<ServiceDataEntity>()
        for (i in services.indices) {
            res.add(
                ServiceDataEntity(
                    type = services[i].type!!,
                    id = services[i].id,
                    name = services[i].name!!,
                    generalDescription = services[i].generalDescription!!,
                    generalPhotosUrls = services[i].generalPhotosUrls,
                    authorId = currentPageAuthors[i].id!!,
                    authorName = currentPageAuthors[i].name!!,
                    authorType = when (currentPageAuthors[i].userType) {
                        "ATHLETE" -> UserType.ATHLETE
                        "ORGANIZATION" -> UserType.ORGANIZATION
                        else -> throw Exception("backend data consistency exception")
                    },
                    profilePictureUrl = currentPageAuthors[i].profilePhotoURI,
                    price = services[i].price!!,
                    currency = services[i].currency!!,
                    categories = services[i].categories!!,
                    rating = services[i].rating,
                    acquiredNumber = services[i].acquiredNumber!!,
                    reviewsNumber = services[i].reviewsNumber!!,
                    isFavourite = services[i].usersIdsFavs.contains(userId),
                    isAcquired = services[i].usersIdsAcquired.contains(userId),
                    dateCreatedMillis = services[i].dateCreatedMillis!!
                )
            )
        }

        return res
    }

    override suspend fun getPagedUserAcquiredServices(
        userId: String,
        serviceType: ServiceType?,
        lastMarker: Long?,
        pageSize: Int
    ): List<ServiceDataEntity> {
        val currentPageDocuments: QuerySnapshot?
        val currentPageAcquired = mutableListOf<Boolean>()
        val currentPageFavs = mutableListOf<Boolean>()
        val currentPageAuthors = mutableListOf<UserFirestoreEntity>()

        if (lastMarker == null) {
            currentPageDocuments = database.collection(SERVICES_PATH)
                .whereArrayContains("usersIdsAcquired", userId)
                .orderBy("dateCreatedMillis", Query.Direction.DESCENDING)
                .limit(pageSize.toLong())
                .get(Source.SERVER)
                .await()
        } else {
            currentPageDocuments = database.collection(SERVICES_PATH)
                .whereArrayContains("usersIdsAcquired", userId)
                .orderBy("dateCreatedMillis", Query.Direction.DESCENDING)
                .limit(pageSize.toLong())
                .startAfter(lastMarker)
                .get(Source.SERVER)
                .await()
        }

        if (currentPageDocuments.isEmpty) {
            return emptyList()
        }

        val services = currentPageDocuments.toObjects(ServiceDetailedFirestoreEntity::class.java)

        services.forEach {
            currentPageAcquired.add(it.usersIdsAcquired.contains(userId))
            currentPageFavs.add(it.usersIdsFavs.contains(userId))

            val authorDocument = database.collection(USERS_PATH)
                .document(it.authorId!!)
                .get(Source.SERVER)
                .await()

            val author =
                authorDocument.toObject(UserFirestoreEntity::class.java) ?: throw Exception()

            currentPageAuthors.add(author)
        }

        val res = mutableListOf<ServiceDataEntity>()
        for (i in services.indices) {
            res.add(
                ServiceDataEntity(
                    type = services[i].type!!,
                    id = services[i].id,
                    name = services[i].name!!,
                    generalDescription = services[i].generalDescription!!,
                    generalPhotosUrls = services[i].generalPhotosUrls,
                    authorId = currentPageAuthors[i].id!!,
                    authorName = currentPageAuthors[i].name!!,
                    authorType = when (currentPageAuthors[i].userType) {
                        "ATHLETE" -> UserType.ATHLETE
                        "ORGANIZATION" -> UserType.ORGANIZATION
                        else -> throw Exception("backend data consistency exception")
                    },
                    profilePictureUrl = currentPageAuthors[i].profilePhotoURI,
                    price = services[i].price!!,
                    currency = services[i].currency!!,
                    categories = services[i].categories!!,
                    rating = services[i].rating,
                    acquiredNumber = services[i].acquiredNumber!!,
                    reviewsNumber = services[i].reviewsNumber!!,
                    isFavourite = services[i].usersIdsFavs.contains(userId),
                    isAcquired = services[i].usersIdsAcquired.contains(userId),
                    dateCreatedMillis = services[i].dateCreatedMillis!!
                )
            )
        }

        return res
    }

    override suspend fun getService(
        serviceId: String,
        userId: String
    ): ServiceDataEntity {
        val serviceDocument = database.collection(SERVICES_PATH)
            .document(serviceId)
            .get(Source.SERVER)
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()

        val service =
            serviceDocument.toObject(ServiceDetailedFirestoreEntity::class.java)
                ?: throw Exception()

        val userDocument = database.collection(USERS_PATH)
            .document(service.authorId!!)
            .get(Source.SERVER)
            .addOnFailureListener {
                throw Exception("user not found")
            }
            .await()
        val user = userDocument.toObject(UserFirestoreEntity::class.java)
            ?: throw Exception("error user loading")

        val res = ServiceDataEntity(
            type = service.type!!,
            id = service.id,
            name = service.name!!,
            generalDescription = service.generalDescription!!,
            generalPhotosUrls = service.generalPhotosUrls,
            authorId = user.id!!,
            authorName = user.name!!,
            authorType = when (user.userType) {
                "ATHLETE" -> UserType.ATHLETE
                "ORGANIZATION" -> UserType.ORGANIZATION
                else -> throw Exception("backend data consistency exception")
            },
            profilePictureUrl = user.profilePhotoURI,
            price = service.price!!,
            currency = service.currency!!,
            categories = service.categories!!,
            rating = service.rating,
            acquiredNumber = service.acquiredNumber!!,
            reviewsNumber = service.reviewsNumber!!,
            isFavourite = service.usersIdsFavs.contains(userId),
            isAcquired = service.usersIdsAcquired.contains(userId),
            dateCreatedMillis = service.dateCreatedMillis!!
        )

        return res
    }

    override suspend fun getServiceDetailed(
        serviceId: String,
        userId: String
    ): ServiceDetailedDataEntity {
        val serviceDocument = database.collection(SERVICES_PATH)
            .document(serviceId)
            .get(Source.SERVER)
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()

        val service =
            serviceDocument.toObject(ServiceDetailedFirestoreEntity::class.java)
                ?: throw Exception()

        val userDocument = database.collection(USERS_PATH)
            .document(service.authorId!!)
            .get(Source.SERVER)
            .addOnFailureListener {
                throw Exception("user not found")
            }
            .await()
        val user = userDocument.toObject(UserFirestoreEntity::class.java)
            ?: throw Exception("error user loading")

        val exercisesDocument = database
            .collection(SERVICES_PATH)
            .document(serviceId)
            .collection(EXERCISES_PATH)
            .get(Source.SERVER)
            .await()

        val exercises =
            exercisesDocument.toObjects(ExerciseFirestoreEntity::class.java).map { exercise ->
                ExerciseDataEntity(
                    id = exercise.id,
                    name = exercise.name!!,
                    description = exercise.description!!,
                    setsNumber = exercise.setsNumber!!,
                    repsNumber = exercise.repsNumber!!,
                    regularity = exercise.regularity!!,
                    photosUris = exercise.photosUris
                )
            }

        val res = ServiceDetailedDataEntity(
            type = service.type!!,
            id = service.id,
            name = service.name!!,
            generalDescription = service.generalDescription!!,
            generalPhotosUrls = service.generalPhotosUrls,
            detailedDescription = service.detailedDescription!!,
            detailedPhotosUrls = service.detailedPhotosUrls,
            authorId = user.id!!,
            authorName = user.name!!,
            authorType = when (user.userType) {
                "ATHLETE" -> UserType.ATHLETE
                "ORGANIZATION" -> UserType.ORGANIZATION
                else -> throw Exception("backend data consistency exception")
            },
            profilePictureUrl = user.profilePhotoURI,
            price = service.price!!,
            currency = service.currency!!,
            categories = service.categories!!,
            exerciseDataEntities = exercises,
            rating = service.rating,
            acquiredNumber = service.acquiredNumber!!,
            reviewsNumber = service.reviewsNumber!!,
            isFavourite = service.usersIdsFavs.contains(userId),
            isAcquired = service.usersIdsAcquired.contains(userId),
            dateCreatedMillis = service.dateCreatedMillis!!
        )

        return res
    }

    override suspend fun createService(serviceDataEntity: ServiceDetailedDataEntity) {
        val generalPhotosFirestore = mutableListOf<Uri>()
        /*serviceDataEntity.generalPhotosUrls.forEach {
            val photosRef = storage.reference.child("images/services/${UUID.randomUUID()}")
            val downloadUrl = photosRef.putFile(Uri.parse(it))
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
                .await()
                .storage.downloadUrl.await()
            generalPhotosFirestore.add(downloadUrl)
        }*/

        val detailedPhotosFirestore = mutableListOf<Uri>()
        /*serviceDataEntity.generalPhotosUrls.forEach {
            val photosRef = storage.reference.child("images/services/${UUID.randomUUID()}")
            val downloadUrl = photosRef.putFile(Uri.parse(it))
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
                .await()
                .storage.downloadUrl.await()
            detailedPhotosFirestore.add(downloadUrl)
        }*/

        val serviceFirestoreEntity = hashMapOf(
            "type" to serviceDataEntity.type,
            "name" to serviceDataEntity.name,
            "authorId" to serviceDataEntity.authorId,
            "rating" to null,
            "reviewsNumber" to 0,
            "acquiredNumber" to 0,
            "generalDescription" to serviceDataEntity.generalDescription,
            "generalPhotosUrls" to serviceDataEntity.generalPhotosUrls,
            "detailedDescription" to serviceDataEntity.detailedDescription,
            "detailedPhotosUrls" to serviceDataEntity.detailedPhotosUrls,
            "price" to serviceDataEntity.price,
            "categories" to serviceDataEntity.categories,
            "currency" to serviceDataEntity.currency,
            "dateCreatedMillis" to Calendar.getInstance().timeInMillis,
            "usersIdsAcquired" to listOf<String>(),
            "usersIdsFavs" to listOf<String>()
        )

        // TODO должна быть атомарной

        val documentRef = database.collection(SERVICES_PATH).document()

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

        serviceDataEntity.exerciseDataEntities.forEach { exercise ->
            val documentExerciseRef = database
                .collection(SERVICES_PATH)
                .document(documentRef.id)
                .collection(EXERCISES_PATH)
                .document()

            val exerciseFirestoreEntity = hashMapOf(
                "name" to exercise.name,
                "repsNumber" to exercise.repsNumber,
                "setsNumber" to exercise.setsNumber,
                "description" to exercise.description,
                "regularity" to exercise.regularity,
                "photosUris" to exercise.photosUris
            )

            documentExerciseRef
                .set(exerciseFirestoreEntity)
                .await()

            documentExerciseRef
                .update(
                    hashMapOf<String, Any>(
                        "id" to documentExerciseRef.id,
                        "tokens" to serviceDataEntity.name.split(" ").filter { it.isNotBlank() }
                            .map {
                                it.lowercase(
                                    Locale.getDefault()
                                )
                            } + ""
                    )
                )
                .await()
        }
    }

    override suspend fun updateService(serviceDataEntity: ServiceDetailedDataEntity) {
        if (serviceDataEntity.id == null) throw Exception("null id")

        val serviceFirestoreEntity = hashMapOf(
            "name" to serviceDataEntity.name,
            "generalDescription" to serviceDataEntity.generalDescription,
            "generalPhotosUrls" to serviceDataEntity.generalPhotosUrls,
            "detailedDescription" to serviceDataEntity.detailedDescription,
            "detailedPhotosUrls" to serviceDataEntity.detailedPhotosUrls,
            "price" to serviceDataEntity.price,
            "categories" to serviceDataEntity.categories,
            "currency" to serviceDataEntity.currency,
            "exercises" to serviceDataEntity.exerciseDataEntities,
            "tokens" to serviceDataEntity.name.split(" ").filter { it.isNotBlank() }
                .map {
                    it.lowercase(
                        Locale.getDefault()
                    )
                } + ""
        )

        database.collection(SERVICES_PATH)
            .document(serviceDataEntity.id)
            .update(serviceFirestoreEntity)
            .await()

        val batch = database.batch()
        database.collection(SERVICES_PATH)
            .document(serviceDataEntity.id)
            .collection(EXERCISES_PATH)
            .get(Source.SERVER)
            .addOnSuccessListener {
                for (document in it.documents) {
                    batch.delete(document.reference)
                }
                batch.commit()
            }
            .await()

        serviceDataEntity.exerciseDataEntities.forEach { exercise ->
            val documentRef = database
                .collection(SERVICES_PATH)
                .document(serviceDataEntity.id)
                .collection(EXERCISES_PATH)
                .document()

            val exerciseFirestoreEntity = hashMapOf(
                "name" to exercise.name,
                "repsNumber" to exercise.repsNumber,
                "setsNumber" to exercise.setsNumber,
                "description" to exercise.description,
                "regularity" to exercise.regularity,
                "photosUris" to exercise.photosUris
            )

            documentRef
                .set(exerciseFirestoreEntity)
                .await()

            documentRef
                .update(
                    hashMapOf<String, Any>(
                        "id" to documentRef.id,
                    )
                )
                .await()
        }
    }

    override suspend fun deleteService(serviceId: String) {
        database.collection(SERVICES_PATH)
            .document(serviceId)
            .delete()
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()
    }

    override suspend fun setIsFavourite(
        userId: String,
        serviceId: String,
        isFavourite: Boolean
    ) {
        Log.d("abcdef", "isFavourite $isFavourite")
        if (isFavourite) {
            database.collection(SERVICES_PATH)
                .document(serviceId)
                .update(
                    hashMapOf<String, Any>(
                        "usersIdsFavs" to FieldValue.arrayUnion(userId)
                    )
                )
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
        } else {
            database.collection(SERVICES_PATH)
                .document(serviceId)
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
        serviceId: String,
        isAcquired: Boolean
    ) {
        if (isAcquired) {
            database.collection(SERVICES_PATH)
                .document(serviceId)
                .update(
                    hashMapOf<String, Any>(
                        "acquiredNumber" to FieldValue.increment(1L),
                        "usersIdsAcquired" to FieldValue.arrayUnion(userId)
                    )
                )
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
        } else {
            database.collection(SERVICES_PATH)
                .document(serviceId)
                .update(
                    hashMapOf<String, Any>(
                        "acquiredNumber" to FieldValue.increment(-1L),
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
        const val SERVICES_PATH = "services"
        const val EXERCISES_PATH = "exercises"
        const val USERS_PATH = "users"
    }

}