package com.thesis.sportologia.model.users.sources

import com.thesis.sportologia.model.services.entities.FilterParamsServices
import com.thesis.sportologia.model.services.entities.ServiceDataEntity
import com.thesis.sportologia.model.services.entities.ServiceDetailedDataEntity
import com.thesis.sportologia.model.services.entities.ServiceType
import com.thesis.sportologia.model.users.entities.AccountDataEntity
import com.thesis.sportologia.model.users.entities.SignUpDataEntity
import com.thesis.sportologia.model.users.entities.User
import com.thesis.sportologia.utils.AuthException

interface UsersDataSource {

    suspend fun getAccount(): AccountDataEntity

    suspend fun getUser(currentUserId: String, userId: String): User

    suspend fun signIn(email: String, password: String): String

    suspend fun signUp(signUpDataEntity: SignUpDataEntity)

}