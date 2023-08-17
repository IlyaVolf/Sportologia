package com.thesis.sportologia.ui.events.entities

import com.thesis.sportologia.model.events.entities.EventDataEntity
import com.thesis.sportologia.utils.Position

data class EventListItem(
    val eventDataEntity: EventDataEntity,
    val isInProgress: Boolean,
) {
    val id: String get() = eventDataEntity.id!!
    val name: String get() = eventDataEntity.name
    val description: String get() = eventDataEntity.description
    val organizerId: String get() = eventDataEntity.organizerId
    val organizerName: String get() = eventDataEntity.organizerName
    val position: Position? get() = eventDataEntity.position
    val dateFrom: Long get() = eventDataEntity.dateFrom
    val dateTo: Long? get() = eventDataEntity.dateTo
    val price: Float get() = eventDataEntity.price
    val currency: String get() = eventDataEntity.currency
    val profilePictureUrl: String? get() = eventDataEntity.profilePictureUrl
    val likesCount: Int get() = eventDataEntity.likesCount
    val categories: Map<String, Boolean> get() = eventDataEntity.categories
    val isLiked: Boolean get() = eventDataEntity.isLiked
    val isFavourite: Boolean get() = eventDataEntity.isFavourite
    val photosUrls: List<String> get() = eventDataEntity.photosUrls
}