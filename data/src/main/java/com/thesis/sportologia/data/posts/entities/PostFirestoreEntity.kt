package com.thesis.sportologia.data.posts.entities

data class PostFirestoreEntity(
    var id: String? = null,
    var authorId: String? = null,
    var text: String? = null,
    var likesCount: Int? = null,
    var userType: String? = null,
    var postedDate: Long? = null,
    var photosUrls: List<String> = mutableListOf(),
    var usersIdsLiked: List<Long> = mutableListOf(),
    var usersIdsFavs: List<Long> = mutableListOf()
)