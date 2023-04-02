package com.thesis.sportologia.model.users.entities

import android.location.Address
import com.thesis.sportologia.model.photos.entities.Photo

data class Organization(
    override var address: Address?,
    override var id: String,
    override var name: String,
    override var description: String,
    override var profilePhotoURI: String?,
    override var followersCount: Int,
    override var followingsCount: Int,
    override var categories: Map<String, Boolean>,
    override var isSubscribed: Boolean,
    override var innerRating: Int,
    override var photosCount: Int,
    override var photosSnippets: List<Photo>,
) : User()