package com.thesis.sportologia.model.users.entities

import android.location.Address

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
) : User()