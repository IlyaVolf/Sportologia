package com.thesis.sportologia.model.users.entities

import android.location.Address

// TODO photo count?
abstract class User {
    abstract var id: String
    abstract var name: String
    abstract var description: String
    abstract var profilePhotoURI: String?
    abstract var followersCount: Int
    abstract var followingsCount: Int
    abstract var address: Address?
    abstract var categories: Map<String, Boolean>
    abstract var isSubscribed: Boolean
    abstract var innerRating: Int // 0..100

    fun toUserSnippet(): UserSnippet {
        return UserSnippet(
            this.id,
            this.name,
            this.profilePhotoURI,
            this.address,
            this.categories
        )
    }
}
