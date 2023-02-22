package com.thesis.sportologia.model.posts.entities

import java.util.*

data class Post(
    val id: Long?,
    val authorId: Int,
    val profilePictureUrl: String?,
    val text: String,
    val likesCount: Int,
    val isLiked: Boolean,
    val isAddedToFavourites: Boolean,
    val postedDate: Calendar,
    val photosUrls: List<String>?,
)