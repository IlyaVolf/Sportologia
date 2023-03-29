package com.thesis.sportologia.model.services

class ServicesLocalChanges {
    // TODO по идее - это надо для каждой VM
    val idsInProgress = mutableSetOf<Long>()

    // TODO фото
    val isFavouriteFlags = mutableMapOf<Long, Boolean>()
    val isAcquiredFlag = mutableMapOf<Long, Boolean>()

    fun clear() {
        idsInProgress.clear()
        isFavouriteFlags.clear()
        isAcquiredFlag.clear()
    }
}