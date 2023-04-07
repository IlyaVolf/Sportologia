package com.thesis.sportologia.model.posts.entities

data class PostFireStoreEntity(
    var authorId: String? = null,
    var authorName: String? = null,
    var profilePictureUrl: String? = null,
    var text: String? = null,
    var likesCount: Int? = null,
    var userType: String? = null,
    var postedDate: Long? = null,
    var photosUrls: List<String> = mutableListOf(),
    var usersIdsLiked: List<String> = mutableListOf(),
    var usersIdsFavs: List<String> = mutableListOf()
)