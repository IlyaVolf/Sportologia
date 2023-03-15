package com.thesis.sportologia.ui.users.entities

import com.thesis.sportologia.model.users.entities.Organization

data class OrganizationListItem(
    val organization: Organization,
) : UserListItem(organization)