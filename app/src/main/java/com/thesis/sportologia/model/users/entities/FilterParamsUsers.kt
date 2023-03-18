package com.thesis.sportologia.model.users.entities

import android.location.Address
import com.thesis.sportologia.model.FilterParams

// TODO Parcelable
data class FilterParamsUsers(
    var categories: HashMap<String, Boolean>?,
    var isAthTOrgF: Boolean?,
    var distance: Int?,
    var address: Address?,
) : FilterParams {
    companion object {
        fun newEmptyInstance(): FilterParamsUsers {
            return FilterParamsUsers(null, null, null, null)
        }
    }
}
