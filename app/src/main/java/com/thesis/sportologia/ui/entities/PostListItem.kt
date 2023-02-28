package com.thesis.sportologia.ui.entities

import com.thesis.sportologia.model.posts.entities.Post
import java.util.*

data class PostListItem(
    val post: Post,
    val isInProgress: Boolean,
) {
    val id: Long get() = post.id
    val authorId: Int get() = post.authorId
    val authorName: String get() = post.authorName
    val profilePictureUrl: String? get() = post.profilePictureUrl
    val text: String get() = post.text
    val likesCount: Int get() = post.likesCount
    val isLiked: Boolean get() = post.isLiked
    val isFavourite: Boolean get() = post.isFavourite
    val postedDate: Calendar get() = post.postedDate
    val photosUrls: List<String>? get() = post.photosUrls
}