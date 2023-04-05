package com.thesis.sportologia.ui.users.entities

import android.location.Address
import com.thesis.sportologia.model.users.entities.UserSnippet
import com.thesis.sportologia.utils.Position

data class UserSnippetListItem(
    val userSnippet: UserSnippet,
    val isInProgress: Boolean,
) {
    val id: String get() = userSnippet.id
    val name: String get() = userSnippet.name
    val profilePhotoURI: String? get() = userSnippet.profilePhotoURI
    val position: Position? get() = userSnippet.position
    val categories: Map<String, Boolean> get() = userSnippet.categories
}