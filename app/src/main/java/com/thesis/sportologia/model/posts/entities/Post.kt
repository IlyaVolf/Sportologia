package com.thesis.sportologia.model.posts.entities

import java.util.*

data class Post(
    val id: Long,
    var authorId: String,
    var authorName: String,
    var profilePictureUrl: String?,
    var text: String,
    var likesCount: Int,
    var isAuthorAthlete: Boolean,
    var isLiked: Boolean,
    var isFavourite: Boolean,
    var postedDate: Long,
    var photosUrls: List<String>?,
)