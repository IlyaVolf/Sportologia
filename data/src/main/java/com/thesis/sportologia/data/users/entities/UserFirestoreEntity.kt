package com.thesis.sportologia.data.users.entities

import com.google.firebase.firestore.GeoPoint

data class UserFirestoreEntity(
    var id: String? = null,
    var birthDate: Long? = null,
    var gender: String? = null,
    var nickname: String? = null,
    var name: String? = null,
    var description: String? = null,
    var followersCount: Int? = null,
    var followingsCount: Int? = null,
    var photosCount: Int? = null,
    var position: GeoPoint? = null,
    var profilePhotoURI: String? = null,
    var categories: Map<String, Boolean>? = null,
    var userType: String? = null
)