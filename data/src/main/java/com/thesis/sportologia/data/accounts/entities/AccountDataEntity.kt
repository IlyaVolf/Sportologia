package com.thesis.sportologia.data.accounts.entities

import com.thesis.sportologia.core.entities.Position
import com.thesis.sportologia.data.posts.entities.PostDataEntity

data class AccountDataEntity(
    val id: Long,
    val nickname: String,
    val name: String,
    val profilePhotoURI: String?,
)