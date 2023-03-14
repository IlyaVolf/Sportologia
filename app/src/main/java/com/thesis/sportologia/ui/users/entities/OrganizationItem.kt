package com.thesis.sportologia.ui.users.entities

import com.thesis.sportologia.model.users.entities.Organization

data class OrganizationItem(
    val organization: Organization,
) : UserItem(organization)