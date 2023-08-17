package com.thesis.sportologia.model.photos.entities

data class UserPhotos(
    val userId: String,
    val photosUris: MutableList<Photo>
)