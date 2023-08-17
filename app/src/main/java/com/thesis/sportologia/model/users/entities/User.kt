package com.thesis.sportologia.model.users.entities

import android.location.Address
import com.thesis.sportologia.model.photos.entities.Photo
import com.thesis.sportologia.utils.Position

// TODO photo count?
abstract class User {
    abstract var id: String
    abstract var name: String
    abstract var description: String
    abstract var profilePhotoURI: String?
    abstract var followersCount: Int
    abstract var followingsCount: Int
    abstract var position: Position?
    abstract var categories: Map<String, Boolean>
    abstract var isSubscribed: Boolean
    abstract var photosCount: Int
    abstract var photosSnippets: List<String>

    fun toUserSnippet(): UserSnippet {
        return UserSnippet(
            this.id,
            this.name,
            this.profilePhotoURI,
            this.position,
            this.categories
        )
    }
}