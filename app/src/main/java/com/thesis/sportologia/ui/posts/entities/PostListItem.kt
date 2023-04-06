package com.thesis.sportologia.ui.posts.entities

import com.thesis.sportologia.model.posts.entities.PostDataEntity

data class PostListItem(
    val postDataEntity: PostDataEntity,
    val isInProgress: Boolean,
) {
    val id: String get() = postDataEntity.id!!
    val authorId: String get() = postDataEntity.authorId
    val authorName: String get() = postDataEntity.authorName
    val profilePictureUrl: String? get() = postDataEntity.profilePictureUrl
    val text: String get() = postDataEntity.text
    val likesCount: Int get() = postDataEntity.likesCount
    val isLiked: Boolean get() = postDataEntity.isLiked
    val isFavourite: Boolean get() = postDataEntity.isFavourite
    val postedDate: Long get() = postDataEntity.postedDate
    val photosUrls: List<String> get() = postDataEntity.photosUrls
}