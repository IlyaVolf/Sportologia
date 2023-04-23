package com.thesis.sportologia.model.users.entities

import com.thesis.sportologia.model.photos.entities.Photo
import com.thesis.sportologia.utils.Position

data class Athlete(
    val gender: GenderType,
    override var position: Position?,
    override var id: String,
    override var name: String,
    override var description: String,
    override var profilePhotoURI: String?,
    override var followersCount: Int,
    override var followingsCount: Int,
    override var categories: Map<String, Boolean>,
    override var isSubscribed: Boolean,
    override var photosCount: Int,
    override var photosSnippets: List<String>,
) : User()