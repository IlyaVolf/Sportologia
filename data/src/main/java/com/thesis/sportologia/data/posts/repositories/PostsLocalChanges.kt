package com.thesis.sportologia.data.posts.repositories

class PostsLocalChanges {
    val isLikedFlags = mutableMapOf<Long, Boolean>()
    val isFavouriteFlags = mutableMapOf<Long, Boolean>()
    val likesCount = mutableMapOf<Long, Int>()

    fun remove(postId: Long) {
        isLikedFlags.remove(postId)
        isFavouriteFlags.remove(postId)
        likesCount.remove(postId)
    }

    fun clear() {
        isFavouriteFlags.clear()
        isLikedFlags.clear()
        likesCount.clear()
    }
}