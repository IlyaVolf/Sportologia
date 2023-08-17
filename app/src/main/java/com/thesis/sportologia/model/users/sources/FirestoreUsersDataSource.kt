package com.thesis.sportologia.model.users.sources

import com.google.firebase.firestore.*
import com.thesis.sportologia.model.posts.sources.FirestorePostsDataSource
import com.thesis.sportologia.model.settings.sources.SettingsDataSource
import com.thesis.sportologia.model.users.entities.*
import com.thesis.sportologia.model.users.exceptions.UserWithEmailAlreadyExists
import com.thesis.sportologia.model.users.exceptions.UserWithIdAlreadyExists
import com.thesis.sportologia.model.users.exceptions.WrongEmailOrPasswordException
import com.thesis.sportologia.utils.AuthException
import com.thesis.sportologia.utils.toPosition
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class FirestoreUsersDataSource @Inject constructor(
    private val settings: SettingsDataSource,
) : UsersDataSource {

    private val database = FirebaseFirestore.getInstance()

    override suspend fun getAccount(): AccountDataEntity {
        val token = settings.getToken() ?: throw AuthException()

        return getAccountByToken(token)
    }

    private suspend fun getAccountByToken(token: String): AccountDataEntity {
        val tokenDocument = database.collection(TOKENS_PATH)
            .whereEqualTo(FieldPath.documentId(), token)
            .get()
            .addOnFailureListener {
                throw AuthException()
            }
            .await().firstOrNull() ?: throw AuthException()

        val userId = tokenDocument.get("userId").toString()

        return getCurrentAccount(userId)
    }

    private suspend fun getCurrentAccount(userId: String): AccountDataEntity {
        val userDocument = database.collection(USERS_PATH)
            .document(userId)
            .get()
            .addOnFailureListener {
                throw Exception()
            }
            .await()

        val user = userDocument.toObject(UserFirestoreEntity::class.java) ?: throw Exception()

        val res = AccountDataEntity(
            id = user.id!!,
            name = user.name!!,
            profilePhotoURI = user.profilePhotoURI,
            userType = when (user.userType) {
                UserType.ATHLETE.name -> UserType.ATHLETE
                UserType.ORGANIZATION.name -> UserType.ORGANIZATION
                else -> throw Exception("backend data consistency exception")
            }
        )

        return res
    }

    override suspend fun signIn(email: String, password: String): String {
        val accountDocument = database.collection(ACCOUNTS_PATH)
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .await()
            .firstOrNull() ?: throw WrongEmailOrPasswordException()

        val userId = accountDocument.id
        val documentRef = database.collection(TOKENS_PATH).document()
        documentRef.set(hashMapOf<String, Any>("userId" to userId))

        return documentRef.id
    }

    override suspend fun signUp(userCreateDataEntity: UserCreateDataEntity) {
        if (!database.collection(ACCOUNTS_PATH)
                .whereEqualTo("email", userCreateDataEntity.email)
                .get()
                .await()
                .isEmpty
        ) {
            throw UserWithEmailAlreadyExists()
        }
        if (!database.collection(ACCOUNTS_PATH)
                .whereEqualTo("id", userCreateDataEntity.userId)
                .get()
                .await()
                .isEmpty
        ) {
            throw UserWithIdAlreadyExists()
        }

        database.runTransaction { transaction ->

            transaction
                .set(
                    database.collection(ACCOUNTS_PATH)
                        .document(userCreateDataEntity.userId),
                    hashMapOf<String, Any>(
                        "email" to userCreateDataEntity.email,
                        "password" to userCreateDataEntity.password,
                    )
                )

            val userFirestoreEntity = when (userCreateDataEntity.userType) {
                UserType.ATHLETE -> {
                    hashMapOf(
                        "id" to userCreateDataEntity.userId,
                        "name" to userCreateDataEntity.name,
                        "description" to userCreateDataEntity.description,
                        "userType" to userCreateDataEntity.userType,
                        "gender" to userCreateDataEntity.gender!!,
                        "birthdate" to userCreateDataEntity.birthDate!!,
                        "profilePhotoURI" to userCreateDataEntity.profilePhotoURI,
                        "position" to userCreateDataEntity.position?.toGeoPoint(),
                        "categories" to userCreateDataEntity.categories,
                        "photosCount" to 0,
                        "followersCount" to 0,
                        "followingsCount" to 0,
                    )
                }
                UserType.ORGANIZATION -> {
                    hashMapOf(
                        "id" to userCreateDataEntity.userId,
                        "name" to userCreateDataEntity.name,
                        "description" to userCreateDataEntity.description,
                        "userType" to userCreateDataEntity.userType,
                        "profilePhotoURI" to userCreateDataEntity.profilePhotoURI,
                        "position" to userCreateDataEntity.position?.toGeoPoint(),
                        "categories" to userCreateDataEntity.categories,
                        "photosCount" to 0,
                        "followersCount" to 0,
                        "followingsCount" to 0,
                    )
                }
            }

            transaction
                .set(
                    database.collection(USERS_PATH).document(userCreateDataEntity.userId),
                    userFirestoreEntity
                )

            transaction.update(database.collection(USERS_PATH)
                .document(userCreateDataEntity.userId),
                hashMapOf<String, Any>(
                    "tokens" to userCreateDataEntity.name.split(" ").filter { it.isNotBlank() }
                        .map {
                            it.lowercase(
                                Locale.getDefault()
                            )
                        } + "")
            )

            null
        }.addOnFailureListener {
            throw AuthException()
        }.await()
    }

    override suspend fun getUser(currentUserId: String, userId: String): User {
        val userDocument = database.collection(USERS_PATH)
            .document(userId)
            .get()
            .addOnFailureListener {
                throw Exception()
            }
            .await()

        val user = userDocument.toObject(UserFirestoreEntity::class.java)
            ?: throw Exception() // throw NoSuchUserException()

        val res = when (user.userType) {
            UserType.ORGANIZATION.name -> {
                Organization(
                    id = user.id!!,
                    name = user.name!!,
                    description = user.description!!,
                    position = user.position.toPosition(),
                    profilePhotoURI = user.profilePhotoURI,
                    followersCount = user.followersCount!!,
                    followingsCount = user.followingsCount!!,
                    categories = user.categories!!,
                    isSubscribed = checkIfUserFollows(currentUserId, userId),
                    photosCount = user.photosCount!!,
                    photosSnippets = getUserPhotos(userId, true)
                )
            }
            UserType.ATHLETE.name -> {
                Athlete(
                    gender = when (user.gender) {
                        GenderType.MALE.name -> GenderType.MALE
                        GenderType.FEMALE.name -> GenderType.FEMALE
                        else -> throw Exception("backend data consistency exception")
                    },
                    id = user.id!!,
                    name = user.name!!,
                    birthDate = user.birthDate!!,
                    description = user.description!!,
                    position = user.position.toPosition(),
                    profilePhotoURI = user.profilePhotoURI,
                    followersCount = user.followersCount!!,
                    followingsCount = user.followingsCount!!,
                    categories = user.categories!!,
                    isSubscribed = checkIfUserFollows(currentUserId, userId),
                    photosCount = user.photosCount!!,
                    photosSnippets = getUserPhotos(userId, true)
                )
            }
            else -> throw Exception("backend data consistency exception")
        }

        return res
    }

    private suspend fun checkIfUserFollows(currentUserId: String, userId: String): Boolean {
        val followersDocument = database.collection(USERS_PATH)
            .document(currentUserId)
            .collection(FOLLOWINGS_IDS_PATH)
            .whereEqualTo(FieldPath.documentId(), userId)
            .get()
            .addOnFailureListener {
                throw Exception()
            }
            .await()

        return followersDocument.documents.isNotEmpty()
    }

    private suspend fun getUserPhotos(userId: String, isLimited: Boolean): List<String> {
        val photoDocuments = database.collection(USERS_PATH)
            .document(userId)
            .collection(PHOTOS_PATH)
            .document("photos")
            .get()
            .addOnFailureListener {
                throw Exception()
            }
            .await()

        val res = if (isLimited) {
            photoDocuments.toObject(PhotosFirestoreDataEntity::class.java)?.photos?.take(
                PHOTOS_LIMIT.toInt()
            ) ?: emptyList()
        } else {
            photoDocuments.toObject(PhotosFirestoreDataEntity::class.java)?.photos ?: emptyList()
        }
        return res
    }

    override suspend fun updateUser(userEditDataEntity: UserEditDataEntity) {
        val userFirestoreEntity = when (userEditDataEntity.userType) {
            UserType.ATHLETE -> {
                hashMapOf(
                    "name" to userEditDataEntity.name,
                    "description" to userEditDataEntity.description,
                    "userType" to userEditDataEntity.userType,
                    "gender" to userEditDataEntity.gender!!,
                    "birthdate" to userEditDataEntity.birthDate!!,
                    "profilePhotoURI" to userEditDataEntity.profilePhotoURI,
                    "position" to userEditDataEntity.position?.toGeoPoint(),
                    "categories" to userEditDataEntity.categories,
                )
            }
            UserType.ORGANIZATION -> {
                hashMapOf(
                    "name" to userEditDataEntity.name,
                    "description" to userEditDataEntity.description,
                    "userType" to userEditDataEntity.userType,
                    "profilePhotoURI" to userEditDataEntity.profilePhotoURI,
                    "position" to userEditDataEntity.position?.toGeoPoint(),
                    "categories" to userEditDataEntity.categories,
                )
            }
        }

        database.collection(USERS_PATH)
            .document(userEditDataEntity.userId)
            .update(userFirestoreEntity)
            .await()
    }

    override suspend fun deleteUser(userId: String) {
        database.runTransaction { transaction ->

            transaction.delete(
                database.collection(USERS_PATH)
                    .document(userId)
            )

            transaction.delete(
                database.collection(ACCOUNTS_PATH)
                    .document(userId)
            )

            null
        }.await()
    }

    override suspend fun setIsSubscribed(
        followerId: String,
        followingId: String,
        isLiked: Boolean
    ) {
        database.runTransaction { transaction ->

            transaction.set(
                database.collection(USERS_PATH)
                    .document(followerId).collection(FOLLOWINGS_IDS_PATH).document(followingId),
                hashMapOf<String, Any>()
            )

            transaction.set(
                database.collection(USERS_PATH)
                    .document(followingId).collection(FOLLOWERS_IDS_PATH).document(followerId),
                hashMapOf<String, Any>()
            )

            if (isLiked) {
                transaction.update(
                    database.collection(USERS_PATH)
                        .document(followerId),
                    hashMapOf<String, Any>(
                        "followingsCount" to FieldValue.increment(1L),
                    )
                )

                transaction.update(
                    database.collection(USERS_PATH)
                        .document(followingId),
                    hashMapOf<String, Any>(
                        "followersCount" to FieldValue.increment(1L),
                    )
                )
            } else {
                transaction.update(
                    database.collection(USERS_PATH)
                        .document(followerId),
                    hashMapOf<String, Any>(
                        "followingsCount" to FieldValue.increment(-1L),
                    )
                )

                transaction.update(
                    database.collection(USERS_PATH)
                        .document(followingId),
                    hashMapOf<String, Any>(
                        "followersCount" to FieldValue.increment(-1L),
                    )
                )
            }

            null
        }.await()
    }

    override suspend fun getPagedUsers(
        searchQuery: String,
        filter: FilterParamsUsers,
        lastMarker: String?,
        pageSize: Int
    ): List<UserSnippet> {

        /*database.collection(USERS_PATH).get().addOnSuccessListener { snap ->
            snap.documents.forEach { doc ->
                database.collection(USERS_PATH).document(doc.id)
                    .update("tokens", FieldValue.delete())
            }
        }

        database.collection(USERS_PATH).get().addOnSuccessListener { snap ->
            snap.documents.forEach { doc ->
                database.collection(USERS_PATH).document(doc.id).update(
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


        val currentPageDocuments: QuerySnapshot?

        if (lastMarker == null) {
            currentPageDocuments =
                if (filter.usersType == FilterParamsUsers.UsersType.ALL) {
                    database.collection(USERS_PATH)
                        .whereArrayContainsAny("tokens", searchQueryTokens)
                        .orderBy("id")
                        .limit(pageSize.toLong())
                        .get()
                        .addOnFailureListener { e ->
                            throw Exception(e)
                        }
                        .await()
                } else {
                    database.collection(USERS_PATH)
                        .whereArrayContainsAny("tokens", searchQueryTokens)
                        .whereEqualTo("userType", filter.usersType)
                        .orderBy("id")
                        .limit(pageSize.toLong())
                        .get()
                        .addOnFailureListener { e ->
                            throw Exception(e)
                        }
                        .await()
                }
        } else {
            if (filter.usersType == FilterParamsUsers.UsersType.ALL) {
                currentPageDocuments = database.collection(USERS_PATH)
                    .whereArrayContainsAny("tokens", searchQueryTokens)
                    .orderBy("id")
                    .limit(pageSize.toLong())
                    .startAfter(lastMarker)
                    .get()
                    .addOnFailureListener { e ->
                        throw Exception(e)
                    }
                    .await()
            } else {
                currentPageDocuments = database.collection(USERS_PATH)
                    .whereArrayContainsAny("tokens", searchQueryTokens)
                    .whereEqualTo("userType", filter.usersType)
                    .orderBy("id")
                    .limit(pageSize.toLong())
                    .startAfter(lastMarker)
                    .get()
                    .addOnFailureListener { e ->
                        throw Exception(e)
                    }
                    .await()
            }
        }

        if (currentPageDocuments.isEmpty) {
            return emptyList()
        }

        val users = currentPageDocuments.toObjects(UserFirestoreEntity::class.java)

        val res = users.map { user ->
            UserSnippet(
                id = user.id!!,
                name = user.name!!,
                profilePhotoURI = user.profilePhotoURI,
                position = user.position.toPosition(),
                categories = user.categories!!
            )
        }

        return res
    }

    override suspend fun getPagedFollowers(
        userId: String,
        lastMarker: String?,
        pageSize: Int
    ): List<UserSnippet> {
        val currentPageDocuments: QuerySnapshot?

        if (lastMarker == null) {
            currentPageDocuments = database.collection(USERS_PATH)
                .document(userId)
                .collection(FOLLOWERS_IDS_PATH)
                .limit(pageSize.toLong())
                .get()
                .await()
        } else {
            currentPageDocuments = database.collection(USERS_PATH)
                .document(userId)
                .collection(FOLLOWERS_IDS_PATH)
                .limit(pageSize.toLong())
                .startAfter(lastMarker)
                .get()
                .await()
        }

        if (currentPageDocuments.isEmpty) {
            return emptyList()
        }

        val followersIds = currentPageDocuments.map { it.id }

        val usersDocuments = database.collection(FirestorePostsDataSource.USERS_PATH)
            .whereIn(FieldPath.documentId(), followersIds)
            .get()
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()
        val users = usersDocuments.toObjects(UserFirestoreEntity::class.java)

        val res = users.map { user ->
            UserSnippet(
                id = user.id!!,
                name = user.name!!,
                profilePhotoURI = user.profilePhotoURI,
                position = user.position.toPosition(),
                categories = user.categories!!
            )
        }

        return res
    }

    override suspend fun getPagedFollowings(
        userId: String,
        lastMarker: String?,
        pageSize: Int
    ): List<UserSnippet> {
        val currentPageDocuments: QuerySnapshot?

        if (lastMarker == null) {
            currentPageDocuments = database.collection(USERS_PATH)
                .document(userId)
                .collection(FOLLOWINGS_IDS_PATH)
                .limit(pageSize.toLong())
                .get()
                .await()
        } else {
            currentPageDocuments = database.collection(USERS_PATH)
                .document(userId)
                .collection(FOLLOWINGS_IDS_PATH)
                .limit(pageSize.toLong())
                .startAfter(lastMarker)
                .get()
                .await()
        }

        if (currentPageDocuments.isEmpty) {
            return emptyList()
        }

        val followersIds = currentPageDocuments.map { it.id }

        val usersDocuments = database.collection(FirestorePostsDataSource.USERS_PATH)
            .whereIn(FieldPath.documentId(), followersIds)
            .get()
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()
        val users = usersDocuments.toObjects(UserFirestoreEntity::class.java)

        val res = users.map { user ->
            UserSnippet(
                id = user.id!!,
                name = user.name!!,
                profilePhotoURI = user.profilePhotoURI,
                position = user.position.toPosition(),
                categories = user.categories!!
            )
        }

        return res
    }

    override suspend fun checkEmailExists(email: String): Boolean {
        return !database.collection(ACCOUNTS_PATH)
            .whereEqualTo("email", email)
            .get()
            .await()
            .isEmpty
    }

    private data class PhotosFirestoreDataEntity(
        var photos: List<String> = emptyList()
    )

    companion object {
        const val PHOTOS_LIMIT = 4L
        const val ACCOUNTS_PATH = "accounts"
        const val USERS_PATH = "users"
        const val TOKENS_PATH = "tokens"
        const val PHOTOS_PATH = "photos"
        const val FOLLOWERS_IDS_PATH = "followersIds"
        const val FOLLOWINGS_IDS_PATH = "followingsIds"
    }

}