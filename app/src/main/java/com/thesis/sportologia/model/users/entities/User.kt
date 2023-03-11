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
}
