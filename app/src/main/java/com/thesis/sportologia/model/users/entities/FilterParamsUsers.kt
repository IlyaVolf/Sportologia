package com.thesis.sportologia.model.users.entities

import android.location.Address
import com.thesis.sportologia.model.FilterParams

// TODO Parcelable
data class FilterParamsUsers(
    var categories: HashMap<String, Boolean>?,
    var usersType: UsersType,
    var distance: Int?,
    var address: Address?,
    val sortBy: UsersSortBy
) : FilterParams {

    enum class UsersSortBy {
        RELEVANCE
    }

    enum class UsersType {
        ALL, ATHLETES, ORGANIZATIONS
    }

    companion object {
        fun newEmptyInstance(): FilterParamsUsers {
            return FilterParamsUsers(null, UsersType.ALL, null, null, UsersSortBy.RELEVANCE)
        }
    }
}
