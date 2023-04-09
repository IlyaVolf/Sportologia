package com.thesis.sportologia.model.users.entities

import com.google.firebase.firestore.GeoPoint

data class UserFirestoreEntity(
    var id: String? = null,
    var name: String? = null,
    var description: String? = null,
    var followersCount: Int? = null,
    var followingsCount: Int? = null,
    var photosCount: Int? = null,
    var position: GeoPoint? = null,
    var profilePhotoURI: String? = null,
    var userType: String? = null
)