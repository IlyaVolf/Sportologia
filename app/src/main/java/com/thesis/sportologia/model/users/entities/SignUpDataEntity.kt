package com.thesis.sportologia.model.users.entities

import com.thesis.sportologia.utils.Position

data class SignUpDataEntity(
     var email: String,
     var password: String,
     var userId: String,
     var name: String,
     var gender: GenderType?,
     var description: String,
     var profilePhotoURI: String?,
     var position: Position?,
     var categories: Map<String, Boolean>,
)