package com.thesis.sportologia.model.posts.entities

import com.thesis.sportologia.model.users.entities.UserType

data class PostDataEntity(
    val id: String?,
    var authorId: String,
    var authorName: String,
    var profilePictureUrl: String?,
    var text: String,
    var likesCount: Int,
    var userType: UserType,
    var isLiked: Boolean,
    var isFavourite: Boolean,
    var postedDate: Long,
    var photosUrls: List<String>,
)