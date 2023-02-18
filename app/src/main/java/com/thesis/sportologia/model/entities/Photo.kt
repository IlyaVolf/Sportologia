package com.thesis.sportologia.model.entities

import com.thesis.sportologia.sources.entities.Photo as PhotoSource

data class Photo(
    val imgUrl: String?
)

fun List<PhotoSource>.toUiPhoto(): List<Photo> = map {
    Photo(
        imgUrl = it.imgUrl,
    )
}
