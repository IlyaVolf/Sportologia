package com.thesis.sportologia.ui.posts.entities

import com.thesis.sportologia.model.posts.entities.Post


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

fun Post.toCreateEditItem(): PostCreateEditItem {
    return PostCreateEditItem(
        text = text,
        photosUrls = photosUrls
    )
}