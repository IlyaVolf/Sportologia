package com.thesis.sportologia.data.events.repositories

class EventsLocalChanges {
    // TODO по идее - это надо для каждой VM
    val idsInProgress = mutableSetOf<Long>()

    // TODO фото
    val isLikedFlags = mutableMapOf<Long, Boolean>()
    val isFavouriteFlags = mutableMapOf<Long, Boolean>()
    val likesCount = mutableMapOf<Long, Int>()

    fun remove(id: Long) {
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