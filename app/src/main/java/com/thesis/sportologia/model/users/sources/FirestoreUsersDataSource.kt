package com.thesis.sportologia.model.users.sources

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.thesis.sportologia.model.services.entities.FilterParamsServices
import com.thesis.sportologia.model.services.entities.ServiceDataEntity
import com.thesis.sportologia.model.services.entities.ServiceDetailedDataEntity
import com.thesis.sportologia.model.services.entities.ServiceType
import com.thesis.sportologia.model.settings.sources.SettingsDataSource
import com.thesis.sportologia.model.users.entities.*
import com.thesis.sportologia.model.users.exceptions.UserWithEmailAlreadyExists
import com.thesis.sportologia.model.users.exceptions.UserWithIdAlreadyExists
import com.thesis.sportologia.model.users.exceptions.WrongEmailOrPasswordException
import com.thesis.sportologia.utils.AuthException
import com.thesis.sportologia.utils.Position
import com.thesis.sportologia.utils.toPosition
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreUsersDataSource @Inject constructor(
    private val settings: SettingsDataSource,
) : UsersDataSource {

    private val database = FirebaseFirestore.getInstance()

    override suspend fun getAccount(): AccountDataEntity {
        val token = settings.getToken() ?: throw AuthException()
        val record = getRecordByToken(token)
        return record.account
    }

    private suspend fun getRecordByToken(token: String): Record {
        val tokenDocument = database.collection(TOKENS_PATH)
            .whereEqualTo(FieldPath.documentId(), token)
            .get()
            .addOnFailureListener {
                throw AuthException()
            }
            .await().firstOrNull() ?: throw AuthException()

        val userId = tokenDocument.get("userId").toString()

        val account = getCurrentAccount(userId)

        return Record(
            account = account,
        )
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

    override suspend fun signUp(signUpDataEntity: SignUpDataEntity) {
        if (!database.collection(ACCOUNTS_PATH)
                .whereEqualTo("email", signUpDataEntity.email)
                .get()
                .await()
                .isEmpty
        ) {
            throw UserWithEmailAlreadyExists()
        }
        if (!database.collection(ACCOUNTS_PATH)
                .whereEqualTo("id", signUpDataEntity.userId)
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
                        .document(signUpDataEntity.userId),
                    hashMapOf<String, Any>(
                        "email" to signUpDataEntity.email,
                        "password" to signUpDataEntity.password,
                    )
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

        return followersDocument.isEmpty
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

    private data class PhotosFirestoreDataEntity(
        var photos: List<String> = emptyList()
    )

    private class Record(
        var account: AccountDataEntity,
        var token: String? = null,
    )

    companion object {
        const val PHOTOS_LIMIT = 4L
        const val ACCOUNTS_PATH = "accounts"
        const val USERS_PATH = "users"
        const val TOKENS_PATH = "tokens"
        const val FOLLOWERS_PATH = "followers"
        const val PHOTOS_PATH = "photos"
        const val FOLLOWINGS_IDS_PATH = "followingsIds"
    }

}