package com.thesis.sportologia.data.accounts.sources

import com.google.firebase.auth.FirebaseAuth
import com.thesis.sportologia.core.entities.AuthException
import com.thesis.sportologia.data.users.entities.*
import kotlinx.coroutines.tasks.await


class FirebaseAccountsDataSource : AccountsDataSource {

    private val auth = FirebaseAuth.getInstance()

    // TODO решить вопрос с toLongOrNull
    override fun getCurrentUserId(): Long? {
        return auth.currentUser?.uid?.toLongOrNull()
    }

    override suspend fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnFailureListener {
                throw AuthException()
            }.await()
    }

    override suspend fun signUp(userCreateData: UserCreateDataEntity) {
        auth.createUserWithEmailAndPassword(userCreateData.email, userCreateData.password)
            .addOnFailureListener {
                throw AuthException()
            }.await()
    }

    /*private suspend fun getAccountData(userId: String): AccountDataEntity {
        val userDocument = database.collection("users")
            .document(userId)
            .get()
            .addOnFailureListener {
                throw Exception()
            }
            .await()

        val user = userDocument.toObject(UserFirestoreEntity::class.java)
            ?: throw Exception() // throw NoSuchUserException()

        return AccountDataEntity(
            id = user.id!!.toLongOrNull()!!,
            nickname = user.nickname!!,
            name = user.name!!,
            profilePhotoURI = user.profilePhotoURI
        )
    }*/

}