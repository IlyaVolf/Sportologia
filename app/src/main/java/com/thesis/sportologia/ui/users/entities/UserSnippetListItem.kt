package com.thesis.sportologia.ui.users.entities

import android.location.Address
import com.thesis.sportologia.model.users.entities.UserSnippet

data class UserSnippetListItem(
    val userSnippet: UserSnippet,
    val isInProgress: Boolean,
) {
    val id: String get() = userSnippet.id
    val name: String get() = userSnippet.name
    val profilePhotoURI: String? get() = userSnippet.profilePhotoURI
    val address: Address? get() = userSnippet.address
    val categories: Map<String, Boolean> get() = userSnippet.categories
}