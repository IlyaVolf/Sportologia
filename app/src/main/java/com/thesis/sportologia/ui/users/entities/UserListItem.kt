package com.thesis.sportologia.ui.users.entities

import android.location.Address
import com.thesis.sportologia.model.users.entities.User

abstract class UserListItem(
    val user: User,
) {
    val id: String get() = user.id
    val name: String get() = user.name
    val description: String get() = user.description
    val profilePhotoURI: String? get() = user.profilePhotoURI
    val followersCount: Int get() = user.followersCount
    val followingsCount: Int get() = user.followingsCount
    val address: Address? get() = user.address
    val categories: Map<String, Boolean> get() = user.categories
    val isSubscribed: Boolean get() = user.isSubscribed
}