package com.thesis.sportologia.data.users.entities

import com.thesis.sportologia.core.entities.Gender
import com.thesis.sportologia.core.entities.Position
import com.thesis.sportologia.core.entities.UserType

data class AthleteDataEntity(
    val gender: Gender,
    val birthDate: Long,
    override var position: Position?,
    override var id: Long,
    override var nickname: String,
    override var name: String,
    override var userType: UserType,
    override var description: String,
    override var profilePhotoURI: String?,
    override var followersCount: Int,
    override var followingsCount: Int,
    override var categories: Map<String, Boolean>,
    override var isSubscribed: Boolean,
    override var photosCount: Int,
    override var photosSnippets: List<String>,
) : UserDataEntity()