package com.thesis.sportologia.model.users.entities

import com.thesis.sportologia.utils.Position

data class UserCreateEditDataEntity(
     var email: String,
     var password: String,
     var userId: String,
     var name: String,
     var userType: UserType,
     var gender: GenderType?,
     var description: String,
     var profilePhotoURI: String?,
     var position: Position?,
     var categories: Map<String, Boolean>,
)