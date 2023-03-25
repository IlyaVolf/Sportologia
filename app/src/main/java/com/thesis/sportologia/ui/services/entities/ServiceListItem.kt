package com.thesis.sportologia.ui.services.entities

import com.thesis.sportologia.model.services.entities.Service
import com.thesis.sportologia.model.services.entities.ServiceType
import com.thesis.sportologia.model.users.entities.UserType

data class ServiceListItem(
    val service: Service,
    val isInProgress: Boolean,
) {
    val id: Long get() = service.id
    val name: String get() = service.name
    val description: String get() = service.generalDescription
    val authorId: String get() = service.authorId
    val authorName: String get() = service.authorName
    val authorType: UserType get() = service.authorType
    val price: Float get() = service.price
    val serviceType: ServiceType get() = service.type
    val currency: String get() = service.currency
    val profilePictureUrl: String? get() = service.profilePictureUrl
    val categories: Map<String, Boolean> get() = service.categories
    val acquiredNumber: Int get() = service.acquiredNumber
    val reviewsNumber: Int get() = service.reviewsNumber
    val rating: Float get() = service.rating
    val isFavourite: Boolean get() = service.isFavourite
    val photosUrls: List<String>? get() = service.generalPhotosUrls
}