package com.thesis.sportologia.data.posts.entities

data class PostDataEntity(
    var id: Long,
    var authorId: Long,
    var authorName: String,
    var authorProfilePictureUrl: String?,
    var authorUserType: UserTypeDataEntity,
    var text: String,
    var photosUrls: List<String>,
    var dateCreated: Long,
    var likesCount: Int,
    var isLiked: Boolean,
    var isFavourite: Boolean,
)