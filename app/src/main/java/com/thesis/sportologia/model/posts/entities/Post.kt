package com.thesis.sportologia.model.posts.entities

import java.util.*

data class Post(
    val id: Long,
    var authorId: Int,
    var authorName: String,
    var profilePictureUrl: String?,
    var text: String,
    var likesCount: Int,
    var isAuthorAthlete: Boolean,
    var isLiked: Boolean,
    var isFavourite: Boolean,
    var postedDate: Calendar,
    var photosUrls: List<String>?,
)