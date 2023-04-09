package com.thesis.sportologia.model.events.sources

import android.util.Log
import com.google.firebase.firestore.*
import com.thesis.sportologia.model.events.entities.EventDataEntity
import com.thesis.sportologia.model.events.entities.EventFirestoreEntity
import com.thesis.sportologia.model.events.entities.FilterParamsEvents
import com.thesis.sportologia.model.users.entities.UserFirestoreEntity
import com.thesis.sportologia.model.users.entities.UserType
import com.thesis.sportologia.utils.toPosition
import kotlinx.coroutines.tasks.await
import java.util.Calendar
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
    override suspend fun getPagedUserEvents(
        userId: String,
        filter: FilterParamsEvents,
        lastMarker: String?,
        pageSize: Int
    ): List<EventDataEntity> {
        val currentPageDocuments: QuerySnapshot?
        val currentPageIds = mutableListOf<String>()
        val currentPageLikes = mutableListOf<Boolean>()
        val currentPageFavs = mutableListOf<Boolean>()

        val query = database.collection("events")

        if (filter.dateFrom != null) {
            query.whereGreaterThan("dateFrom", filter.dateFrom!!)
        }
        if (filter.dateTo != null) {
            query.whereLessThan("dateTo", filter.dateTo!!)
        }

        if (lastMarker == null) {
            currentPageDocuments = database.collection("events")
                .orderBy("dateFrom", Query.Direction.ASCENDING)
                .limit(pageSize.toLong())
                .get()
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
                .await()
        } else {
            currentPageDocuments = database.collection("events")
                .orderBy("dateFrom", Query.Direction.ASCENDING)
                .limit(pageSize.toLong())
                .startAfter(lastMarker)
                .get()
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
                .await()
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
                    categories = events[i].categories,
                    likesCount = events[i].likesCount!!,
                    isLiked = currentPageLikes[i],
                    isFavourite = currentPageFavs[i],
                    photosUrls = events[i].photosUrls,
                )
            )
        }

        return res
    }

    override suspend fun getPagedUserEvents(
        userId: String,
        lastMarker: String?,
        pageSize: Int
    ): List<EventDataEntity> {
        val currentPageDocuments: QuerySnapshot?
        val currentPageIds = mutableListOf<String>()
        val currentPageLikes = mutableListOf<Boolean>()
        val currentPageFavs = mutableListOf<Boolean>()

        /*val a = database.collection("events")
            .get()
            .await()

        a.forEach { database.collection("events").document(it.id).update(hashMapOf<String, Any>("id" to it.id)).await() }*/

        if (lastMarker == null) {
            currentPageDocuments = database.collection("events")
                .whereEqualTo("organizerId", userId)
                .orderBy("dateFrom", Query.Direction.ASCENDING)
                .limit(pageSize.toLong())
                .get()
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
                .await()
        } else {
            currentPageDocuments = database.collection("events")
                .whereEqualTo("organizerId", userId)
                .orderBy("dateFrom", Query.Direction.ASCENDING)
                .limit(pageSize.toLong())
                .startAfter(lastMarker)
                .get()
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
                .await()
        }

        val events = currentPageDocuments.toObjects(EventFirestoreEntity::class.java)

        Log.d("abcdef", "events $events")

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
                    categories = events[i].categories,
                    likesCount = events[i].likesCount!!,
                    isLiked = currentPageLikes[i],
                    isFavourite = currentPageFavs[i],
                    photosUrls = events[i].photosUrls,
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

        if (lastMarker == null) {
            if (!isUpcomingOnly) {
                currentPageDocuments = database.collection("events")
                    .whereIn("organizerId", followersIds)
                    .orderBy("dateFrom", Query.Direction.ASCENDING)
                    .orderBy("id", Query.Direction.ASCENDING)
                    .limit(pageSize.toLong())
                    .get()
                    .addOnFailureListener { e ->
                        throw Exception(e)
                    }
                    .await()
            } else {
                currentPageDocuments = database.collection("events")
                    .whereIn("organizerId", followersIds)
                    .whereGreaterThan("dateFrom", Calendar.getInstance().timeInMillis)
                    .orderBy("dateFrom", Query.Direction.ASCENDING)
                    .orderBy("id", Query.Direction.ASCENDING)
                    .limit(pageSize.toLong())
                    .get()
                    .addOnFailureListener { e ->
                        throw Exception(e)
                    }
                    .await()
            }
        } else {
            if (!isUpcomingOnly) {
                currentPageDocuments = database.collection("events")
                    .whereIn("organizerId", followersIds)
                    .orderBy("dateFrom", Query.Direction.ASCENDING)
                    .orderBy("id", Query.Direction.ASCENDING)
                    .limit(pageSize.toLong())
                    .startAfter(lastMarker)
                    .get()
                    .addOnFailureListener { e ->
                        throw Exception(e)
                    }
                    .await()
            } else {
                currentPageDocuments = database.collection("events")
                    .whereIn("organizerId", followersIds)
                    .whereGreaterThan("dateFrom", Calendar.getInstance().timeInMillis)
                    .orderBy("dateFrom", Query.Direction.ASCENDING)
                    .orderBy("id", Query.Direction.ASCENDING)
                    .limit(pageSize.toLong())
                    .startAfter(lastMarker)
                    .get()
                    .addOnFailureListener { e ->
                        throw Exception(e)
                    }
                    .await()
            }
        }

        val events = currentPageDocuments.toObjects(EventFirestoreEntity::class.java)

        Log.d("abcdef", "events $lastMarker $events")

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
                    categories = events[i].categories,
                    likesCount = events[i].likesCount!!,
                    isLiked = currentPageLikes[i],
                    isFavourite = currentPageFavs[i],
                    photosUrls = events[i].photosUrls,
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
                    .orderBy("dateFrom", Query.Direction.ASCENDING)
                    .limit(pageSize.toLong())
                    .get()
                    .addOnFailureListener { Log.d("abcdef", "$it"); throw Exception(it) }
                    .await()
            } else {
                currentPageDocuments = database.collection("events")
                    .whereArrayContains("usersIdsFavs", userId)
                    .whereGreaterThan("dateFrom", Calendar.getInstance().timeInMillis)
                    .orderBy("dateFrom", Query.Direction.ASCENDING)
                    .limit(pageSize.toLong())
                    .get()
                    .addOnFailureListener { throw Exception(it) }
                    .await()
            }
        } else {
            if (!isUpcomingOnly) {
                currentPageDocuments = database.collection("events")
                    .whereArrayContains("usersIdsFavs", userId)
                    .orderBy("dateFrom", Query.Direction.ASCENDING)
                    .limit(pageSize.toLong())
                    .startAfter(lastMarker)
                    .get()
                    .addOnFailureListener { throw Exception(it) }
                    .await()
            } else {
                currentPageDocuments = database.collection("events")
                    .whereArrayContains("usersIdsFavs", userId)
                    .whereGreaterThan("dateFrom", Calendar.getInstance().timeInMillis)
                    .orderBy("dateFrom", Query.Direction.ASCENDING)
                    .limit(pageSize.toLong())
                    .startAfter(lastMarker)
                    .get()
                    .addOnFailureListener { throw Exception(it) }
                    .await()
            }
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
                    categories = events[i].categories,
                    likesCount = events[i].likesCount!!,
                    isLiked = currentPageLikes[i],
                    isFavourite = currentPageFavs[i],
                    photosUrls = events[i].photosUrls,
                )
            )
        }

        return res
    }

    override suspend fun getEvent(eventId: String, userId: String): EventDataEntity? {
        val eventDocument = database.collection("events")
            .document(eventId)
            .get()
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()

        val event = eventDocument.toObject(EventFirestoreEntity::class.java) ?: return null

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
            categories = event.categories,
            likesCount = event.likesCount!!,
            isLiked = event.usersIdsLiked.contains(userId),
            isFavourite = event.usersIdsFavs.contains(userId),
            photosUrls = event.photosUrls,
        )

        return res
    }

    override suspend fun createEvent(eventDataEntity: EventDataEntity) {
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
            "eventedDate" to Calendar.getInstance().timeInMillis,
            "photosUrls" to eventDataEntity.photosUrls,
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
            .update(hashMapOf<String, Any>("id" to documentRef.id))
            .addOnFailureListener { e ->
                Log.d("abcdef", "$e")
                throw Exception(e)
            }
            .await()

    }

    override suspend fun updateEvent(eventDataEntity: EventDataEntity) {
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
            "photosUrls" to eventDataEntity.photosUrls,
        )

        database.collection("events")
            .document(eventDataEntity.id!!)
            .update(eventFirestoreEntity)
            .addOnFailureListener { e ->
                Log.d("abcdef", "$e")
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
        } else {
            database.collection("events")
                .document(eventDataEntity.id!!)
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
}