package com.thesis.sportologia.ui.posts.entities

import com.thesis.sportologia.model.posts.entities.PostDataEntity


// TODO parcelable!
data class PostCreateEditItem(
    var text: String?,
    var photosUrls: List<String>
) {
    companion object {
        fun getEmptyInstance(): PostCreateEditItem {
            return PostCreateEditItem(
                null,
                listOf()
            )
        }
    }
}

fun PostDataEntity.toCreateEditItem(): PostCreateEditItem {
    return PostCreateEditItem(
        text = text,
        photosUrls = photosUrls
    )
}