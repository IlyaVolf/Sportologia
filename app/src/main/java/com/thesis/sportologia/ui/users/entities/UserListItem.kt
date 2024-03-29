package com.thesis.sportologia.ui.users.entities

import com.thesis.sportologia.model.photos.entities.Photo
import com.thesis.sportologia.model.users.entities.User
import com.thesis.sportologia.utils.Position

abstract class UserListItem(
    val user: User,
) {
    val id: String get() = user.id
    val name: String get() = user.name
    val description: String get() = user.description
    val profilePhotoURI: String? get() = user.profilePhotoURI
    val followersCount: Int get() = user.followersCount
    val followingsCount: Int get() = user.followingsCount
    val position: Position? get() = user.position
    val categories: Map<String, Boolean> get() = user.categories
    val isSubscribed: Boolean get() = user.isSubscribed
    val photosCount: Int get() = user.photosCount
    val photosSnippets: List<String> get() = user.photosSnippets
}