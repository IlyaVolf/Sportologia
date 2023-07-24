package com.thesis.sportologia.data.users.entities

import com.thesis.sportologia.core.entities.Position
import com.thesis.sportologia.core.entities.UserType

abstract class UserDataEntity {
    abstract var id: Long
    abstract var nickname: String
    abstract var name: String
    abstract var userType: UserType
    abstract var description: String
    abstract var profilePhotoURI: String?
    abstract var followersCount: Int
    abstract var followingsCount: Int
    abstract var position: Position?
    abstract var categories: Map<String, Boolean>
    abstract var isSubscribed: Boolean
    abstract var photosCount: Int
    abstract var photosSnippets: List<String>
}