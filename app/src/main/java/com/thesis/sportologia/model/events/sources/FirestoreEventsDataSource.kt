package com.thesis.sportologia.model.events.sources

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.thesis.sportologia.model.events.entities.EventDataEntity
import com.thesis.sportologia.model.events.entities.EventFirestoreEntity
import com.thesis.sportologia.model.events.entities.FilterParamsEvents
import com.thesis.sportologia.model.users.entities.UserFirestoreEntity
import com.thesis.sportologia.model.users.entities.UserType
import com.thesis.sportologia.utils.toPosition
import kotlinx.coroutines.tasks.await
import java.lang.reflect.Field
import java.util.*
import javax.inject.Inject

// TODO вообще нужно проверять внимательно на сущестсование документов с указанным айди. При тестировании!
// TODO вообще храним только id автора, по которому получим пользователяя и данные: аватар, имя и т.п
class FirestoreEventsDataSource @Inject constructor() : EventsDataSource {

    /** т.к. версия пробная и отсутствуют Google Cloud Functions, для ускорения загрузки во вред
    оптимизации размера данных, мы получаем сразу все id пользователей, которыйп оставили лайк. По-началу
    ничего страшно, когда лайков мало. Зато потом размер получаемых клиентом докмуентов будет
    огромных, если лайков, например, миллион.
    Поэтому вообще это нужно на сервере проверять, лайкнул или нет. И не получать эти списки ни
    в коем случае. Но пока техническо мозможно только такая реализация */

    private val database = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override suspend fun getPagedEvents(
        userId: String,
        searchQuery: String,
        filter: FilterParamsEvents,
        lastMarker: String?,
        pageSize: Int
    ): List<EventDataEntity> {
        val currentPageDocuments: QuerySnapshot?
        val currentPageIds = mutableListOf<String>()
        val currentPageLikes = mutableListOf<Boolean>()
        val currentPageFavs = mutableListOf<Boolean>()

        val query = database.collection("events")

        /*database.collection("events").get().addOnSuccessListener { snap ->
            snap.documents.forEach { doc ->
                database.collection("events").document(doc.id).update("tokens", FieldValue.delete())
            }
        }

        database.collection("events").get().addOnSuccessListener { snap ->
            snap.documents.forEach { doc ->
                database.collection("events").document(doc.id).update(
                    hashMapOf<String, Any>(
                        "tokens" to doc.get("name").toString().split(" ").filter { it.isNotBlank() }
                            .map {
                                it.lowercase(
                                    Locale.getDefault()
                                )
                            } + "")
                )
            }
        }

        if (filter.dateFrom != null) {
            query.whereGreaterThan("dateFrom", filter.dateFrom!!)
        }
        if (filter.dateTo != null) {
            query.whereLessThan("dateTo", filter.dateTo!!)
        }*/

        val searchQueryTokens = searchQuery.split(" ").filter { it.isNotBlank() }.map {
            it.lowercase(
                Locale.getDefault()
            )
        }.ifEmpty { listOf("") }

        if (lastMarker == null) {
            currentPageDocuments = database.collection("events")
                .whereArrayContainsAny("tokens", searchQueryTokens)
                .orderBy("datePlusId", Query.Direction.ASCENDING)
                .limit(pageSize.toLong())
                .get()
                .await()
        } else {
            currentPageDocuments = database.collection("events")
                .whereArrayContainsAny("tokens", searchQueryTokens)
                .orderBy("datePlusId", Query.Direction.ASCENDING)
                .limit(pageSize.toLong())
                .startAfter(lastMarker)
                .get()
                .await()
        }

        if (currentPageDocuments.isEmpty) {
            return emptyList()
        }

        val events = currentPageDocuments.toObjects(EventFirestoreEntity::class.java)

        val usersLists = events.map { it.organizerId }

        val usersDocuments = database.collection("users")
            .whereIn("id", usersLists)
            .get()
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()

        val users = usersDocuments.toObjects(UserFirestoreEntity::class.java)

        currentPageDocuments.forEach {
            currentPageIds.add(it.id)
        }
        events.forEach {
            currentPageLikes.add(it.usersIdsLiked.contains(userId))
            currentPageFavs.add(it.usersIdsFavs.contains(userId))
        }

        val res = mutableListOf<EventDataEntity>()
        for (i in events.indices) {
            res.add(
                EventDataEntity(
                    id = currentPageIds[i],
                    name = events[i].name!!,
                    description = events[i].description!!,
                    organizerId = events[i].organizerId!!,
                    organizerName = users.first { it.id == events[i].organizerId }.name!!,
                    userType = when (users.first { it.id == events[i].organizerId }.userType) {
                        "ATHLETE" -> UserType.ATHLETE
                        "ORGANIZATION" -> UserType.ORGANIZATION
                        else -> throw Exception()
                    },
                    profilePictureUrl = users.first { it.id == events[i].organizerId }.profilePhotoURI,
                    dateFrom = events[i].dateFrom!!,
                    dateTo = events[i].dateTo,
                    position = events[i].position.toPosition()!!,
                    price = events[i].price!!,
                    currency = events[i].currency!!,
                    categories = events[i].categories!!,
                    likesCount = events[i].likesCount!!,
                    isLiked = currentPageLikes[i],
                    isFavourite = currentPageFavs[i],
                    photosUrls = events[i].photosUrls,
                    postedDate = events[i].postedDate
                )
            )
        }

        return res
    }

    override suspend fun getPagedUserEvents(
        userId: String,
        isUpcomingOnly: Boolean,
        lastMarker: String?,
        pageSize: Int
    ): List<EventDataEntity> {
        val currentPageDocuments: QuerySnapshot?
        val currentPageIds = mutableListOf<String>()
        val currentPageLikes = mutableListOf<Boolean>()
        val currentPageFavs = mutableListOf<Boolean>()

        /* val a = database.collection("events")
             .get()
             .await()

         a.forEach {
             database.collection("events").document(it.id)
                 .update(
                     hashMapOf<String, Any>(
                         "datePlusId" to "${it.get("dateFrom")}${it.id}"
                     )
                 ).await()
         }*/

        if (lastMarker == null) {
            currentPageDocuments = database.collection("events")
                .whereEqualTo("organizerId", userId)
                .orderBy("datePlusId", Query.Direction.ASCENDING)
                .limit(pageSize.toLong())
                .get()
                .await()
        } else {
            currentPageDocuments = database.collection("events")
                .whereEqualTo("organizerId", userId)
                .orderBy("datePlusId", Query.Direction.ASCENDING)
                .limit(pageSize.toLong())
                .startAfter(lastMarker)
                .get()
                .await()
        }

        if (currentPageDocuments.isEmpty) {
            return emptyList()
        }

        val events = currentPageDocuments.toObjects(EventFirestoreEntity::class.java)

        val userDocument = database.collection("users")
            .document(userId)
            .get()
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()
        val user = userDocument.toObject(UserFirestoreEntity::class.java)
            ?: throw Exception("error user loading")

        currentPageDocuments.forEach {
            currentPageIds.add(it.id)
        }
        events.forEach {
            currentPageLikes.add(it.usersIdsLiked.contains(userId))
            currentPageFavs.add(it.usersIdsFavs.contains(userId))
        }

        val res = mutableListOf<EventDataEntity>()
        for (i in events.indices) {
            res.add(
                EventDataEntity(
                    id = currentPageIds[i],
                    name = events[i].name!!,
                    description = events[i].description!!,
                    organizerId = user.id!!,
                    organizerName = user.name!!,
                    userType = when (user.userType) {
                        "ATHLETE" -> UserType.ATHLETE
                        "ORGANIZATION" -> UserType.ORGANIZATION
                        else -> throw Exception()
                    },
                    profilePictureUrl = user.profilePhotoURI,
                    dateFrom = events[i].dateFrom!!,
                    dateTo = events[i].dateTo,
                    position = events[i].position.toPosition()!!,
                    price = events[i].price!!,
                    currency = events[i].currency!!,
                    categories = events[i].categories!!,
                    likesCount = events[i].likesCount!!,
                    isLiked = currentPageLikes[i],
                    isFavourite = currentPageFavs[i],
                    photosUrls = events[i].photosUrls,
                    postedDate = events[i].postedDate
                )
            )
        }

        return res
    }

    override suspend fun getPagedUserSubscribedOnEvents(
        userId: String,
        isUpcomingOnly: Boolean,
        lastMarker: String?,
        pageSize: Int
    ): List<EventDataEntity> {
        val currentPageDocuments: QuerySnapshot?
        val currentPageIds = mutableListOf<String>()
        val currentPageLikes = mutableListOf<Boolean>()
        val currentPageFavs = mutableListOf<Boolean>()

        val followersIdsDocument = database.collection("users")
            .document(userId)
            .collection("followersIds")
            .get()
            .addOnFailureListener { e ->
                throw Exception(e)
            }.await()

        val followersIds = followersIdsDocument.map { it.id }

        val usersDocuments = database.collection("users")
            .whereIn(FieldPath.documentId(), followersIds)
            .get()
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()

        val users = usersDocuments.toObjects(UserFirestoreEntity::class.java)
        val usersMap = hashMapOf<String, UserFirestoreEntity>()
        users.forEach { usersMap[it.id!!] = it }

        Log.d("abcdef", "aaaaaaaa $lastMarker")

        if (lastMarker == null) {
            if (!isUpcomingOnly) {
                currentPageDocuments = database.collection("events")
                    .whereIn("organizerId", followersIds)
                    .orderBy("datePlusId", Query.Direction.ASCENDING)
                    .limit(pageSize.toLong())
                    .get()
                    .addOnFailureListener { e ->
                        Log.d("abcdef", "$e")
                        throw Exception(e)
                    }
                    .await()
            } else {
                currentPageDocuments = database.collection("events")
                    .whereIn("organizerId", followersIds)
                    .whereGreaterThan("datePlusId", Calendar.getInstance().timeInMillis.toString())
                    .orderBy("datePlusId", Query.Direction.ASCENDING)
                    .limit(pageSize.toLong())
                    .get()
                    .addOnFailureListener { e ->
                        Log.d("abcdef", "$e")
                        throw Exception(e)
                    }
                    .await()
            }
        } else {
            if (!isUpcomingOnly) {
                currentPageDocuments = database.collection("events")
                    .whereIn("organizerId", followersIds)
                    .orderBy("datePlusId", Query.Direction.ASCENDING)
                    .limit(pageSize.toLong())
                    .startAfter(lastMarker)
                    .get()
                    .addOnFailureListener { e ->
                        Log.d("abcdef", "$e")
                        throw Exception(e)
                    }
                    .await()
            } else {
                currentPageDocuments = database.collection("events")
                    .whereIn("organizerId", followersIds)
                    .whereGreaterThan("datePlusId", Calendar.getInstance().timeInMillis.toString())
                    .orderBy("datePlusId", Query.Direction.ASCENDING)
                    .limit(pageSize.toLong())
                    .startAfter(lastMarker)
                    .get()
                    .addOnFailureListener { e ->
                        Log.d("abcdef", "$e")
                        throw Exception(e)
                    }
                    .await()
            }
        }

        if (currentPageDocuments.isEmpty) {
            return emptyList()
        }

        val events = currentPageDocuments.toObjects(EventFirestoreEntity::class.java)

        currentPageDocuments.forEach {
            currentPageIds.add(it.id)
        }
        events.forEach {
            currentPageLikes.add(it.usersIdsLiked.contains(userId))
            currentPageFavs.add(it.usersIdsFavs.contains(userId))
        }

        val res = mutableListOf<EventDataEntity>()
        for (i in events.indices) {
            val organizerId = events[i].organizerId!!
            res.add(
                EventDataEntity(
                    id = currentPageIds[i],
                    name = events[i].name!!,
                    description = events[i].description!!,
                    organizerId = organizerId,
                    organizerName = usersMap[organizerId]!!.name!!,
                    userType = when (usersMap[organizerId]!!.userType) {
                        "ATHLETE" -> UserType.ATHLETE
                        "ORGANIZATION" -> UserType.ORGANIZATION
                        else -> throw Exception()
                    },
                    profilePictureUrl = usersMap[organizerId]!!.profilePhotoURI,
                    dateFrom = events[i].dateFrom!!,
                    dateTo = events[i].dateTo,
                    position = events[i].position.toPosition()!!,
                    price = events[i].price!!,
                    currency = events[i].currency!!,
                    categories = events[i].categories!!,
                    likesCount = events[i].likesCount!!,
                    isLiked = currentPageLikes[i],
                    isFavourite = currentPageFavs[i],
                    photosUrls = events[i].photosUrls,
                    postedDate = events[i].postedDate
                )
            )
        }

        return res
    }

    override suspend fun getPagedUserFavouriteEvents(
        userId: String,
        isUpcomingOnly: Boolean,
        lastMarker: String?,
        pageSize: Int
    ): List<EventDataEntity> {
        val currentPageDocuments: QuerySnapshot?
        val currentPageLikes = mutableListOf<Boolean>()
        val currentPageFavs = mutableListOf<Boolean>()
        val currentPageAuthors = mutableListOf<UserFirestoreEntity>()

        if (lastMarker == null) {
            if (!isUpcomingOnly) {
                currentPageDocuments = database.collection("events")
                    .whereArrayContains("usersIdsFavs", userId)
                    .orderBy("datePlusId", Query.Direction.ASCENDING)
                    .limit(pageSize.toLong())
                    .get()
                    .addOnFailureListener { Log.d("abcdef", "$it"); throw Exception(it) }
                    .await()
            } else {
                currentPageDocuments = database.collection("events")
                    .whereArrayContains("usersIdsFavs", userId)
                    .whereGreaterThan("datePlusId", Calendar.getInstance().timeInMillis.toString())
                    .orderBy("datePlusId", Query.Direction.ASCENDING)
                    .limit(pageSize.toLong())
                    .get()
                    .addOnFailureListener { Log.d("abcdef", "$it"); throw Exception(it) }
                    .await()
            }
        } else {
            if (!isUpcomingOnly) {
                currentPageDocuments = database.collection("events")
                    .whereArrayContains("usersIdsFavs", userId)
                    .orderBy("datePlusId", Query.Direction.ASCENDING)
                    .limit(pageSize.toLong())
                    .startAfter(lastMarker)
                    .get()
                    .addOnFailureListener { Log.d("abcdef", "$it"); throw Exception(it) }
                    .await()
            } else {
                currentPageDocuments = database.collection("events")
                    .whereArrayContains("usersIdsFavs", userId)
                    .whereGreaterThan("datePlusId", Calendar.getInstance().timeInMillis.toString())
                    .orderBy("datePlusId", Query.Direction.ASCENDING)
                    .limit(pageSize.toLong())
                    .startAfter(lastMarker)
                    .get()
                    .addOnFailureListener { Log.d("abcdef", "$it"); throw Exception(it) }
                    .await()
            }
        }

        if (currentPageDocuments.isEmpty) {
            return emptyList()
        }

        val events = currentPageDocuments.toObjects(EventFirestoreEntity::class.java)

        events.forEach {
            currentPageLikes.add(it.usersIdsLiked.contains(userId))
            currentPageFavs.add(it.usersIdsFavs.contains(userId))

            val authorDocument = database.collection("users")
                .document(it.organizerId!!)
                .get()
                .await()

            val author =
                authorDocument.toObject(UserFirestoreEntity::class.java) ?: throw Exception()

            currentPageAuthors.add(author)
        }

        val res = mutableListOf<EventDataEntity>()
        for (i in events.indices) {
            res.add(
                EventDataEntity(
                    id = events[i].id,
                    name = events[i].name!!,
                    description = events[i].description!!,
                    organizerId = currentPageAuthors[i].id!!,
                    organizerName = currentPageAuthors[i].name!!,
                    userType = when (currentPageAuthors[i].userType) {
                        "ATHLETE" -> UserType.ATHLETE
                        "ORGANIZATION" -> UserType.ORGANIZATION
                        else -> throw Exception()
                    },
                    profilePictureUrl = currentPageAuthors[i].profilePhotoURI,
                    dateFrom = events[i].dateFrom!!,
                    dateTo = events[i].dateTo,
                    position = events[i].position.toPosition()!!,
                    price = events[i].price!!,
                    currency = events[i].currency!!,
                    categories = events[i].categories!!,
                    likesCount = events[i].likesCount!!,
                    isLiked = currentPageLikes[i],
                    isFavourite = currentPageFavs[i],
                    photosUrls = events[i].photosUrls,
                    postedDate = events[i].postedDate
                )
            )
        }

        return res
    }

    override suspend fun getEvent(eventId: String, userId: String): EventDataEntity {
        val eventDocument = database.collection("events")
            .document(eventId)
            .get()
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()

        val event = eventDocument.toObject(EventFirestoreEntity::class.java) ?: throw Exception()

        val userDocument = database.collection("users")
            .document(event.organizerId!!)
            .get()
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()
        val user = userDocument.toObject(UserFirestoreEntity::class.java)
            ?: throw Exception("error user loading")

        val res = EventDataEntity(
            id = event.id,
            name = event.name!!,
            description = event.description!!,
            organizerId = user.id!!,
            organizerName = user.name!!,
            userType = when (user.userType) {
                "ATHLETE" -> UserType.ATHLETE
                "ORGANIZATION" -> UserType.ORGANIZATION
                else -> throw Exception()
            },
            profilePictureUrl = user.profilePhotoURI,
            dateFrom = event.dateFrom!!,
            dateTo = event.dateTo,
            position = event.position.toPosition()!!,
            price = event.price!!,
            currency = event.currency!!,
            categories = event.categories!!,
            likesCount = event.likesCount!!,
            isLiked = event.usersIdsLiked.contains(userId),
            isFavourite = event.usersIdsFavs.contains(userId),
            photosUrls = event.photosUrls,
            postedDate = event.postedDate
        )

        return res
    }

    override suspend fun createEvent(eventDataEntity: EventDataEntity) {
        val photosFirestore = mutableListOf<Uri>()
        eventDataEntity.photosUrls.forEach {
            val photosRef = storage.reference.child("images/events/${UUID.randomUUID()}")
            val downloadUrl = photosRef.putFile(it.toUri())
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
                .await()
                .storage.downloadUrl.await()
            photosFirestore.add(downloadUrl)
        }

        val eventFirestoreEntity = hashMapOf(
            "name" to eventDataEntity.name,
            "description" to eventDataEntity.description,
            "organizerId" to eventDataEntity.organizerId,
            "userType" to eventDataEntity.userType,
            "dateFrom" to eventDataEntity.dateFrom,
            "dateTo" to eventDataEntity.dateTo,
            "position" to GeoPoint(
                eventDataEntity.position.latitude,
                eventDataEntity.position.longitude
            ),
            "price" to eventDataEntity.price,
            "currency" to eventDataEntity.currency,
            "categories" to eventDataEntity.categories,
            "likesCount" to eventDataEntity.likesCount,
            "postedDate" to Calendar.getInstance().timeInMillis,
            "photosUrls" to photosFirestore,
            "usersIdsLiked" to listOf<String>(),
            "usersIdsFavs" to listOf<String>()
        )

        // TODO должна быть атомарной

        val documentRef = database.collection("events").document()

        documentRef
            .set(eventFirestoreEntity)
            .addOnFailureListener { e ->
                Log.d("abcdef", "$e")
                throw Exception(e)
            }
            .await()

        documentRef
            .update(
                hashMapOf(
                    "id" to documentRef.id,
                    "datePlusId" to "${eventDataEntity.dateFrom}${documentRef.id}",
                    "tokens" to eventDataEntity.name.split(" ").filter { it.isNotBlank() }
                        .map {
                            it.lowercase(
                                Locale.getDefault()
                            )
                        } + ""
                )
            )
            .addOnFailureListener { e ->
                Log.d("abcdef", "$e")
                throw Exception(e)
            }
            .await()

    }

    override suspend fun updateEvent(eventDataEntity: EventDataEntity) {
        val photosFirestore = eventDataEntity.photosUrls
        /*val photosFirestore = mutableListOf<Uri>()
        eventDataEntity.photosUrls.forEach {
            val photosRef = storage.reference.child("images/events/${UUID.randomUUID()}")
            val downloadUrl = photosRef.putFile(it.toUri())
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
                .await()
                .storage.downloadUrl.await()
            photosFirestore.add(downloadUrl)
        }*/

        val eventFirestoreEntity = hashMapOf(
            "description" to eventDataEntity.description,
            "dateFrom" to eventDataEntity.dateFrom,
            "dateTo" to eventDataEntity.dateTo,
            "position" to GeoPoint(
                eventDataEntity.position.latitude,
                eventDataEntity.position.longitude
            ),
            "price" to eventDataEntity.price,
            "currency" to eventDataEntity.currency,
            "categories" to eventDataEntity.categories,
            "likesCount" to eventDataEntity.likesCount,
            "photosUrls" to photosFirestore,
            "datePlusId" to "${eventDataEntity.dateFrom}${eventDataEntity.id}",
            "tokens" to eventDataEntity.name.split(" ").filter { it.isNotBlank() }
                .map {
                    it.lowercase(
                        Locale.getDefault()
                    )
                } + ""
        )

        database.collection("events")
            .document(eventDataEntity.id!!)
            .update(eventFirestoreEntity)
            .await()
    }

    override suspend fun deleteEvent(eventId: String) {
        database.collection("events")
            .document(eventId)
            .delete()
            .await()
    }

    override suspend fun setIsLiked(
        userId: String,
        eventDataEntity: EventDataEntity,
        isLiked: Boolean
    ) {
        if (isLiked) {
            database.collection("events")
                .document(eventDataEntity.id!!)
                .update(
                    hashMapOf<String, Any>(
                        "likesCount" to FieldValue.increment(1L),
                        "usersIdsLiked" to FieldValue.arrayUnion(userId)
                    )
                )
                .await()
        } else {
            database.collection("events")
                .document(eventDataEntity.id!!)
                .update(
                    hashMapOf<String, Any>(
                        "likesCount" to FieldValue.increment(-1L),
                        "usersIdsLiked" to FieldValue.arrayRemove(userId)
                    )
                )
                .await()
        }
    }

    override suspend fun setIsFavourite(
        userId: String,
        eventDataEntity: EventDataEntity,
        isFavourite: Boolean
    ) {
        if (isFavourite) {
            database.collection("events")
                .document(eventDataEntity.id!!)
                .update(
                    hashMapOf<String, Any>(
                        "usersIdsFavs" to FieldValue.arrayUnion(userId)
                    )
                )
        } else {
            database.collection("events")
                .document(eventDataEntity.id!!)
                .update(
                    hashMapOf<String, Any>(
                        "usersIdsFavs" to FieldValue.arrayRemove(userId)
                    )
                )
                .await()
        }
    }
}