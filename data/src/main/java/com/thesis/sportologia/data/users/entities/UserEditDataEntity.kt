package com.thesis.sportologia.data.users.entities

import com.thesis.sportologia.utils.Position

data class UserEditDataEntity(
     var userId: String,
     var name: String,
     var userType: UserType,
     var gender: GenderType?,
     var birthDate: Long?,
     var description: String,
     var profilePhotoURI: String?,
     var position: Position?,
     var categories: Map<String, Boolean>,
)