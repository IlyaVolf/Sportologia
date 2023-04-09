package com.thesis.sportologia.model.events.sources

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.thesis.sportologia.model.events.entities.EventDataEntity
import com.thesis.sportologia.model.events.entities.EventFirestoreEntity
import com.thesis.sportologia.model.users.entities.UserFireStoreEntity
import com.thesis.sportologia.model.users.entities.UserType
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject

// TODO вообще нужно проверять внимательно на сущестсование документов с указанным айди. При тестировании!
// TODO вообще храним только id автора, по которому получим пользователяя и данные: аватар, имя и т.п
class FireStoreEventsDataSource @Inject constructor() : EventsDataSource {

    /** т.к. версия пробная и отсутствуют Google Cloud Functions, для ускорения загрузки во вред
    оптимизации размера данных, мы получаем сразу все id пользователей, которыйп оставили лайк. По-началу
    ничего страшно, когда лайков мало. Зато потом размер получаемых клиентом докмуентов будет
    огромных, если лайков, например, миллион.
    Поэтому вообще это нужно на сервере проверять, лайкнул или нет. И не получать эти списки ни
    в коем случае. Но пока техническо мозможно только такая реализация */

    private val database = FirebaseFirestore.getInstance()

    override suspend fun getPagedUserEvents(
        userId: String,
        lastMarker: Long?,
        pageSize: Int
    ): List<EventDataEntity> {
        /*val currentPageDocuments: QuerySnapshot?
        val currentPageIds = mutableListOf<String>()
        val currentPageLikes = mutableListOf<Boolean>()
        val currentPageFavs = mutableListOf<Boolean>()

        if (lastMarker == null) {
            currentPageDocuments = database.collection("events")
                .whereEqualTo("authorId", userId)
                .orderBy("eventedDate", Query.Direction.DESCENDING)
                .limit(pageSize.toLong())
                .get()
                .await()
        } else {
            currentPageDocuments = database.collection("events")
                .whereEqualTo("authorId", userId)
                .orderBy("eventedDate", Query.Direction.DESCENDING)
                .limit(pageSize.toLong())
                .startAfter(lastMarker)
                .get()
                .await()
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
            res.add(
                EventDataEntity(
                    id = currentPageIds[i],
                    authorId = events[i].authorId!!,
                    authorName = events[i].authorName!!,
                    profilePictureUrl = events[i].profilePictureUrl,
                    text = events[i].text!!,
                    likesCount = events[i].likesCount!!,
                    userType = when (events[i].userType) {
                        "ATHLETE" -> UserType.ATHLETE
                        "ORGANIZATION" -> UserType.ORGANIZATION
                        else -> throw Exception()
                    },
                    isLiked = currentPageLikes[i],
                    isFavourite = currentPageFavs[i],
                    eventedDate = events[i].eventedDate!!,
                    photosUrls = events[i].photosUrls,
                )
            )
        }

        return res*/
        return emptyList()
    }

    override suspend fun getPagedUserSubscribedOnEvents(
        userId: String,
        userType: UserType?,
        lastMarker: Long?,
        pageSize: Int
    ): List<EventDataEntity> {
        /*val currentPageDocuments: QuerySnapshot?
        val currentPageIds = mutableListOf<String>()
        val currentPageLikes = mutableListOf<Boolean>()
        val currentPageFavs = mutableListOf<Boolean>()

        val userDocument = database.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener {
                if (!it.exists()) {
                    throw Exception("no such user")
                }
            }.await()

        val user = userDocument.toObject(UserFireStoreEntity::class.java) ?: return emptyList()

        if (lastMarker == null) {
            if (userType == null) {
                currentPageDocuments = database.collection("events")
                    .whereIn("authorId", user.followersIds)
                    .orderBy("eventedDate", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
                    .get()
                    .await()
            } else {
                currentPageDocuments = database.collection("events")
                    .whereIn("authorId", user.followersIds)
                    .whereEqualTo("userType", userType)
                    .orderBy("eventedDate", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
                    .get()
                    .await()
            }
        } else {
            if (userType == null) {
                currentPageDocuments = database.collection("events")
                    .whereIn("authorId", user.followersIds)
                    .orderBy("eventedDate", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
                    .startAfter(lastMarker)
                    .get()
                    .await()
            } else {
                currentPageDocuments = database.collection("events")
                    .whereIn("authorId", user.followersIds)
                    .whereEqualTo("userType", userType)
                    .orderBy("eventedDate", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
                    .startAfter(lastMarker)
                    .get()
                    .await()
            }
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
            res.add(
                EventDataEntity(
                    id = currentPageIds[i],
                    authorId = events[i].authorId!!,
                    authorName = events[i].authorName!!,
                    profilePictureUrl = events[i].profilePictureUrl,
                    text = events[i].text!!,
                    likesCount = events[i].likesCount!!,
                    userType = when (events[i].userType) {
                        "ATHLETE" -> UserType.ATHLETE
                        "ORGANIZATION" -> UserType.ORGANIZATION
                        else -> throw Exception()
                    },
                    isLiked = currentPageLikes[i],
                    isFavourite = currentPageFavs[i],
                    eventedDate = events[i].eventedDate!!,
                    photosUrls = events[i].photosUrls,
                )
            )
        }

        return res*/
        return emptyList()
    }

    override suspend fun getPagedUserFavouriteEvents(
        userId: String,
        userType: UserType?,
        lastMarker: Long?,
        pageSize: Int
    ): List<EventDataEntity> {
        /*val usersFavsEventsIds = mutableListOf<String>()

        val currentPageDocuments: QuerySnapshot?
        val currentPageLikes = mutableListOf<Boolean>()
        val currentPageFavs = mutableListOf<Boolean>()

        val userFavsEventsIdsDocuments = database.collection("users")
            .document(userId)
            .collection("favsEvents")
            .get()
            .await()

        userFavsEventsIdsDocuments.forEach {
            usersFavsEventsIds.add(it.id)
        }

        Log.d("abcdef", "usersFavsEventsIds $usersFavsEventsIds")

        if (lastMarker == null) {
            if (userType == null) {
                currentPageDocuments = database.collection("events")
                    .whereIn("id", usersFavsEventsIds)
                    .orderBy("eventedDate", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
                    .get()
                    .addOnFailureListener { throw Exception(it) }
                    .await()
            } else {
                currentPageDocuments = database.collection("events")
                    .whereIn("id", usersFavsEventsIds)
                    .whereEqualTo("userType", userType)
                    .orderBy("eventedDate", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
                    .get()
                    .addOnFailureListener { throw Exception(it) }
                    .await()
            }
        } else {
            if (userType == null) {
                currentPageDocuments = database.collection("events")
                    .whereIn("id", usersFavsEventsIds)
                    .orderBy("eventedDate", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
                    .startAfter(lastMarker)
                    .get()
                    .addOnFailureListener { throw Exception(it) }
                    .await()
            } else {
                currentPageDocuments = database.collection("events")
                    .whereIn("id", usersFavsEventsIds)
                    .whereEqualTo("userType", userType)
                    .orderBy("eventedDate", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
                    .startAfter(lastMarker)
                    .get()
                    .addOnFailureListener { throw Exception(it) }
                    .await()
            }
        }

        Log.d("abcdef", "events ${currentPageDocuments.documents}")

        val events = currentPageDocuments.toObjects(EventFirestoreEntity::class.java)

        events.forEach {
            currentPageLikes.add(it.usersIdsLiked.contains(userId))
            currentPageFavs.add(it.usersIdsFavs.contains(userId))
        }

        val res = mutableListOf<EventDataEntity>()
        for (i in events.indices) {
            res.add(
                EventDataEntity(
                    id = events[i].id,
                    authorId = events[i].authorId!!,
                    authorName = events[i].authorName!!,
                    profilePictureUrl = events[i].profilePictureUrl,
                    text = events[i].text!!,
                    likesCount = events[i].likesCount!!,
                    userType = when (events[i].userType) {
                        "ATHLETE" -> UserType.ATHLETE
                        "ORGANIZATION" -> UserType.ORGANIZATION
                        else -> throw Exception()
                    },
                    isLiked = currentPageLikes[i],
                    isFavourite = currentPageFavs[i],
                    eventedDate = events[i].eventedDate!!,
                    photosUrls = events[i].photosUrls,
                )
            )
        }

        return res
    }

    override suspend fun getEvent(eventId: String, userId: String): EventDataEntity? {
        val document = database.collection("events")
            .document(eventId)
            .get()
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()

        val event = document.toObject(EventFirestoreEntity::class.java) ?: return null

        val res = EventDataEntity(
            id = document.id,
            authorId = event.authorId!!,
            authorName = event.authorName!!,
            profilePictureUrl = event.profilePictureUrl,
            text = event.text!!,
            likesCount = event.likesCount!!,
            userType = when (event.userType) {
                "ATHLETE" -> UserType.ATHLETE
                "ORGANIZATION" -> UserType.ORGANIZATION
                else -> throw Exception()
            },
            isLiked = event.usersIdsLiked.contains(userId),
            isFavourite = event.usersIdsFavs.contains(userId),
            eventedDate = event.eventedDate!!,
            photosUrls = event.photosUrls,
        )

        return res*/
        return emptyList()
    }

    override suspend fun getEvent(eventId: String, userId: String): EventDataEntity? {
        TODO("Not yet implemented")
    }

    override suspend fun createEvent(eventDataEntity: EventDataEntity) {
        val eventFireStoreEntity = hashMapOf(
            "name" to eventDataEntity.name,
            "description" to eventDataEntity.description,
            "organizerId" to eventDataEntity.organizerId,
            "organizerName" to eventDataEntity.organizerName,
            "userType" to eventDataEntity.userType,
            "profilePictureUrl" to eventDataEntity.profilePictureUrl,
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
            "photosUrls" to eventDataEntity.photosUrls,
            "usersIdsLiked" to listOf<String>(),
            "usersIdsFavs" to listOf<String>()
        )

        // TODO должна быть атомарной

        val documentRef = database.collection("events").document()

        documentRef
            .set(eventFireStoreEntity)
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()

        database.collection("events")
            .add(eventFireStoreEntity)
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()

        documentRef
            .update(hashMapOf<String, Any>("id" to documentRef.id))
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()

    }

    override suspend fun updateEvent(eventDataEntity: EventDataEntity) {
        val eventFireStoreEntity = hashMapOf(
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
            "photosUrls" to eventDataEntity.photosUrls,
        )

        database.collection("events")
            .document(eventDataEntity.id!!)
            .update(eventFireStoreEntity)
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()
    }

    override suspend fun deleteEvent(eventId: String) {
        database.collection("events")
            .document(eventId)
            .delete()
            .addOnFailureListener { e ->
                throw Exception(e)
            }
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
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
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
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
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
                .addOnFailureListener { e ->
                    throw Exception(e)
                }

            database.collection("users")
                .document(userId)
                .collection("favsEvents")
                .document(eventDataEntity.id)
                .set(hashMapOf<String, Any>())
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
        } else {
            database.collection("events")
                .document(eventDataEntity.id!!)
                .update(
                    hashMapOf<String, Any>(
                        "likesCount" to FieldValue.increment(-1L),
                        "usersIdsLiked" to FieldValue.arrayRemove(userId)
                    )
                )
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
                .await()

            database.collection("users")
                .document(userId)
                .collection("favsEvents")
                .document(eventDataEntity.id)
                .delete()
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
                .await()
        }
    }
}