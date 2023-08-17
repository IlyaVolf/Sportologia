package com.thesis.sportologia.model.users.entities

data class AccountDataEntity(
    var id: String,
    var name: String,
    var profilePhotoURI: String?,
    var userType: UserType
)