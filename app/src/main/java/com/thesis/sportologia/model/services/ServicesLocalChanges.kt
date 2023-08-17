package com.thesis.sportologia.model.services

class ServicesLocalChanges {
    // TODO по идее - это надо для каждой VM
    val idsInProgress = mutableSetOf<String>()

    // TODO фото
    val isFavouriteFlags = mutableMapOf<String, Boolean>()
    val isAcquiredFlag = mutableMapOf<String, Boolean>()

    fun remove(id: String) {
        idsInProgress.remove(id)
        isFavouriteFlags.remove(id)
    }

    fun clear() {
        idsInProgress.clear()
        isFavouriteFlags.clear()
        isAcquiredFlag.clear()
    }
}