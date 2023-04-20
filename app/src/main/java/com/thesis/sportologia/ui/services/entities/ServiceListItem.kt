package com.thesis.sportologia.ui.services.entities

import com.thesis.sportologia.model.services.entities.ServiceDataEntity
import com.thesis.sportologia.model.services.entities.ServiceType
import com.thesis.sportologia.model.users.entities.UserType

data class ServiceListItem(
    val serviceDataEntity: ServiceDataEntity,
    val isInProgress: Boolean,
) {
    val id: String get() = serviceDataEntity.id!!
    val name: String get() = serviceDataEntity.name
    val description: String get() = serviceDataEntity.generalDescription
    val authorId: String get() = serviceDataEntity.authorId
    val authorName: String get() = serviceDataEntity.authorName
    val authorType: UserType get() = serviceDataEntity.authorType
    val price: Float get() = serviceDataEntity.price
    val serviceType: ServiceType get() = serviceDataEntity.type
    val currency: String get() = serviceDataEntity.currency
    val profilePictureUrl: String? get() = serviceDataEntity.profilePictureUrl
    val categories: Map<String, Boolean> get() = serviceDataEntity.categories
    val acquiredNumber: Int get() = serviceDataEntity.acquiredNumber
    val reviewsNumber: Int get() = serviceDataEntity.reviewsNumber
    val rating: Float? get() = serviceDataEntity.rating
    val isFavourite: Boolean get() = serviceDataEntity.isFavourite
    val photosUrls: List<String> get() = serviceDataEntity.generalPhotosUrls
}