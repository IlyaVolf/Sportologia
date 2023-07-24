package com.thesis.sportologia.data.posts.entities

data class CreatePostDataEntity(
    var text: String,
    var photosUrls: List<String>,
)