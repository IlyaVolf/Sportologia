package com.thesis.sportologia.model.users.entities

import com.thesis.sportologia.utils.Position

data class UserSnippet(
    val id: String,
    val name: String,
    val profilePhotoURI: String?,
    val position: Position?,
    val categories: Map<String, Boolean>,
)