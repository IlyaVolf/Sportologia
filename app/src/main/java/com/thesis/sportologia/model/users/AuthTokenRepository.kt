package com.thesis.sportologia.model.users

interface AuthTokenRepository {

    suspend fun setToken(token: String)

    suspend fun getToken(): String?

    suspend fun clearToken()

}