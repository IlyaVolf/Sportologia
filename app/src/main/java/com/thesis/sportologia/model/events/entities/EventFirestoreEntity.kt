package com.thesis.sportologia.model.events.entities

import com.google.firebase.firestore.GeoPoint

data class EventFirestoreEntity(
    var id: String? = null,
    val name: String? = null,
    val description: String? = null,
    var organizerId: String? = null,
    var organizerName: String? = null,
    var userType: String? = null,
    var profilePictureUrl: String? = null,
    var dateFrom: Long? = null,
    var dateTo: Long? = null,
    var postedDate: Long? = null,
    var position: GeoPoint? = null,
    var price: Float? = null,
    val currency: String? = null,
    var categories: Map<String, Boolean> = hashMapOf(),
    var likesCount: Int? = null,
    var photosUrls: List<String> = mutableListOf(),
    var usersIdsLiked: List<String> = mutableListOf(),
    var usersIdsFavs: List<String> = mutableListOf()
)