package com.thesis.sportologia.model.users

import com.thesis.sportologia.model.users.entities.User

interface UsersRepository {

    suspend fun getUser(userId: String): User?

    suspend fun setIsSubscribe(followerId: String, followingId: String, isSubscribed: Boolean)

}