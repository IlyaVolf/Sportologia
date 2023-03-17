package com.thesis.sportologia.model.events.entities

import android.location.Address
import com.thesis.sportologia.ui.events.entities.EventCreateEditItem
import com.thesis.sportologia.ui.events.entities.EventListItem
import java.util.*

data class Event(
    val id: Long,
    val name: String,
    var description: String,
    var organizerId: String,
    var organizerName: String,
    var isOrganizerAthlete: Boolean,
    var profilePictureUrl: String?,
    var dateFrom: Long,
    var dateTo: Long?,
    var address: Address?, // TODO NON-NULL
    var price: Float,
    var currency: String,
    var categories: Map<String, Boolean>,
    var likesCount: Int,
    var isLiked: Boolean,
    var isFavourite: Boolean,
    var photosUrls: List<String>?,
) {

    fun toEventCreateEditItem(): EventCreateEditItem {
        return EventCreateEditItem(
            name,
            description,
            dateFrom,
            dateTo,
            address,
            price.toString(),
            currency,
            categories,
            photosUrls
        )
    }

    companion object {
        val emptyCategoriesMap
            get() = hashMapOf(
                Pair(RUNNING, false),
                Pair(MASTER_CLASS, false),
                Pair(MARTIAL_ARTS, false)
            )

        const val RUNNING = "Running"
        const val MASTER_CLASS = "Master class"
        const val MARTIAL_ARTS = "Martial arts"
    }
}