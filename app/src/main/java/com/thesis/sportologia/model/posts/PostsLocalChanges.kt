package com.thesis.sportologia.model.posts

class PostsLocalChanges {
    // TODO по идее - это надо для каждой VM
    val idsInProgress = mutableSetOf<Long>()

    // TODO фото
    val isLikedFlags = mutableMapOf<Long, Boolean>()
    val isFavouriteFlags = mutableMapOf<Long, Boolean>()

    fun remove(id: Long) {
        idsInProgress.remove(id)
        isLikedFlags.remove(id)
        isFavouriteFlags.remove(id)
    }

    fun clear() {
        idsInProgress.clear()
        isFavouriteFlags.clear()
        isLikedFlags.clear()
    }
}