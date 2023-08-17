package com.thesis.sportologia.model.events.entities

import com.google.firebase.firestore.GeoPoint

data class EventFirestoreEntity(
    var id: String? = null,
    var name: String? = null,
    var description: String? = null,
    var organizerId: String? = null,
    var userType: String? = null,
    var dateFrom: Long? = null,
    var dateTo: Long? = null,
    var postedDate: Long? = null,
    var position: GeoPoint? = null,
    var price: Float? = null,
    var currency: String? = null,
    var categories: Map<String, Boolean>? = null,
    var likesCount: Int? = null,
    var photosUrls: List<String> = mutableListOf(),
    var usersIdsLiked: List<String> = mutableListOf(),
    var usersIdsFavs: List<String> = mutableListOf()
)