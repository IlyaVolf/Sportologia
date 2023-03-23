package com.thesis.sportologia.ui.events.entities

import android.location.Address
import com.thesis.sportologia.model.events.entities.Event
import java.util.*

data class EventListItem(
    val event: Event,
    val isInProgress: Boolean,
) {
    val id: Long get() = event.id
    val name: String get() = event.name
    val description: String get() = event.description
    val organizerId: String get() = event.organizerId
    val organizerName: String get() = event.organizerName
    val address: Address? get() = event.address // TODO non-null
    val dateFrom: Long get() = event.dateFrom
    val dateTo: Long? get() = event.dateTo
    val price: Float get() = event.price
    val currency: String get() = event.currency
    val profilePictureUrl: String? get() = event.profilePictureUrl
    val likesCount: Int get() = event.likesCount
    val categories: Map<String, Boolean> get() = event.categories
    val isLiked: Boolean get() = event.isLiked
    val isFavourite: Boolean get() = event.isFavourite
    val photosUrls: List<String>? get() = event.photosUrls
}