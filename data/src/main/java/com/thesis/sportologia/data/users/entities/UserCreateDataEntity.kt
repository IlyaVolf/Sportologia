package com.thesis.sportologia.data.users.entities

import com.thesis.sportologia.core.entities.Gender
import com.thesis.sportologia.core.entities.Position
import com.thesis.sportologia.core.entities.UserType

data class UserCreateDataEntity(
     var email: String,
     var password: String,
     var userId: String,
     var name: String,
     var userType: UserType,
     var gender: Gender?,
     var birthDate: Long?,
     var description: String,
     var profilePhotoURI: String?,
     var position: Position?,
     var categories: Map<String, Boolean>,
)