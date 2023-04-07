package com.thesis.sportologia.model.users.entities

data class UserFireStoreEntity(
    var followersIds: List<String> = mutableListOf(),
)