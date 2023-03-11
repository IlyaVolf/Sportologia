package com.thesis.sportologia.sources

import android.location.Address

abstract class UserSource {
    abstract var id: Long
    abstract var name: String
    abstract var description: String
    abstract var profilePhotoURI: String
    abstract var followersCount: Int
    abstract var followingsCount: Int
    abstract var address: Address
    abstract var location: String
    abstract var categories: Map<String, Boolean>
    abstract var photosURIs: List<String>
    abstract var postsIds: List<Long>
    abstract var servicesIds: List<Long>
    abstract var eventsIds: List<Long>
    abstract var followersIds: List<Long>
    abstract var followingsIds: List<Long>
    abstract var likedPostsIds: List<Int>
    abstract var likedEventsIds: List<Int>
    abstract var likedServicesIds: List<Int>
    abstract var favouritePostsIds: List<Int>
    abstract var favouriteEventsIds: List<Int>
    abstract var favouriteServicesIds: List<Int>
    abstract var purchasedServicesIds: List<Int>
}
