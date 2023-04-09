package com.thesis.sportologia.utils

import com.google.firebase.firestore.GeoPoint

data class Position(
    val latitude: Double,
    val longitude: Double,
)

fun GeoPoint?.toPosition(): Position? {
    return if (this != null) {
        Position(this.latitude, this.longitude)
    } else {
        null
    }
}