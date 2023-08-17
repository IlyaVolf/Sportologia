package com.thesis.sportologia.model.events

class EventsLocalChanges {
    // TODO по идее - это надо для каждой VM
    val idsInProgress = mutableSetOf<String>()

    // TODO фото
    val isLikedFlags = mutableMapOf<String, Boolean>()
    val isFavouriteFlags = mutableMapOf<String, Boolean>()
    val likesCount = mutableMapOf<String, Int>()

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