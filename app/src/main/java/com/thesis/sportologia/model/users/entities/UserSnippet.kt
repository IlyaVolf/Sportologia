package com.thesis.sportologia.model.users.entities

import android.location.Address

data class UserSnippet(
    val id: String,
    val name: String,
    val profilePhotoURI: String?,
    val address: Address?,
    val categories: Map<String, Boolean>,
)