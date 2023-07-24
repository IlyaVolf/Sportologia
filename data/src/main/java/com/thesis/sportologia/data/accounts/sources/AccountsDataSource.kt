package com.thesis.sportologia.data.accounts.sources

import com.thesis.sportologia.data.users.entities.UserCreateDataEntity

interface AccountsDataSource {

    fun getCurrentUserId(): Long?

    suspend fun signIn(email: String, password: String)

    suspend fun signUp(userCreateData: UserCreateDataEntity)

}