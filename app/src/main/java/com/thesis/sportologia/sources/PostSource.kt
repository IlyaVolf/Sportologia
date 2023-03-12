package com.thesis.sportologia.sources

import java.util.*

data class PostSource (
    val id: Long?,
    val authorId: Int,
    val authorName: String,
    val profilePictureUrl: String?,
    val text: String,
    val likesCount: Int,
    val isAuthorAthlete: Boolean,
    val isLiked: Boolean,
    val isAddedToFavourites: Boolean,
    val postedDate: Calendar,
    val photosUrls: List<String>?,
)