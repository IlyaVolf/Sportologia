package com.thesis.sportologia.model.posts

class PostsLocalChanges {
    // TODO по идее - это надо для каждой VM
    val idsInProgress = mutableSetOf<String>()

    // TODO фото
    val isLikedFlags = mutableMapOf<String, Boolean>()
    val isFavouriteFlags = mutableMapOf<String, Boolean>()
    val likesCount = mutableMapOf<String, Long>()

    fun remove(id: String) {
        idsInProgress.remove(id)
        isLikedFlags.remove(id)
        isFavouriteFlags.remove(id)
        likesCount.remove(id)
    }

    fun clear() {
        idsInProgress.clear()
        isFavouriteFlags.clear()
        isLikedFlags.clear()
        likesCount.clear()
    }
}