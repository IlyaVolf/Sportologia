package com.thesis.sportologia.ui.users.adapters

import android.util.Log
import androidx.fragment.app.Fragment
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentFilterUsersBinding
import com.thesis.sportologia.databinding.FragmentListUsersHeaderBinding
import com.thesis.sportologia.model.users.entities.FilterParamsUsers
import com.thesis.sportologia.ui.FilterFragmentUsers
import com.thesis.sportologia.utils.Categories
import com.thesis.sportologia.utils.concatList
import com.thesis.sportologia.utils.concatMap

class UsersHeaderAdapterSearch(
    fragment: Fragment,
    filterParamsUsers: FilterParamsUsers,
) : UsersHeaderAdapter(fragment, filterParamsUsers) {

    override fun createHolder(
        fragment: Fragment,
        binding: FragmentListUsersHeaderBinding,
    ): Holder {
        return HolderSearch(fragment, binding, filterParamsUsers)
    }

    class HolderSearch(
        fragment: Fragment,
        binding: FragmentListUsersHeaderBinding,
        var filterParamsUsers: FilterParamsUsers,
    ) : Holder(fragment, binding) {

        override val update: (FilterParamsUsers) -> Unit = { filterParamsUsers ->
            this.filterParamsUsers = filterParamsUsers
            restrictionsParser()
            sortingParser()
        }

        override val renderHeader = {
            enableUsersChosenFilters()
        }

        override val restrictionsParser: () -> Unit = {
            val restrictionBlockList = mutableListOf<String>()

            when (filterParamsUsers.usersType) {
                FilterParamsUsers.UsersType.ATHLETES -> restrictionBlockList.add(
                    fragment.getString(
                        R.string.search_athletes
                    )
                )
                FilterParamsUsers.UsersType.ORGANIZATIONS -> restrictionBlockList.add(
                    fragment.getString(
                        R.string.search_organizations
                    )
                )
                else -> {}
            }

            if (filterParamsUsers.categories != null) {
                val restrictionBlock = StringBuilder("")
                val localizedCategories = Categories.getLocalizedCategories(
                    fragment.context!!,
                    filterParamsUsers.categories!!
                )

                restrictionBlock.append(concatMap(localizedCategories, ", "))

                restrictionBlockList.add(restrictionBlock.toString())
            }

            if (filterParamsUsers.distance != null) {
                val restrictionBlock = StringBuilder("")

                restrictionBlock.append(fragment.getString(R.string.within))
                    .append(" ")
                    .append(filterParamsUsers.distance)
                    .append(" ")
                    .append(fragment.getString(R.string.km))

                restrictionBlockList.add(restrictionBlock.toString())
            }

            if (filterParamsUsers.address != null) {
                val restrictionBlock = StringBuilder("")

                restrictionBlock.append(filterParamsUsers.address)

                restrictionBlockList.add(restrictionBlock.toString())
            }

            val restrictionsString = concatList(restrictionBlockList, splittingDot)
            if (restrictionsString == "") {
                binding.usersChosenFilters.restrictions.text =
                    fragment.getString(R.string.filter_not_specified)
            } else {
                binding.usersChosenFilters.restrictions.text = restrictionsString
            }
        }

        override val sortingParser: () -> Unit = {
            binding.usersChosenFilters.sorting.text = when (filterParamsUsers.sortBy) {
                FilterParamsUsers.UsersSortBy.RELEVANCE -> fragment.getString(R.string.filter_sort_by_relevance)
            }
        }

        private val splittingDot = " " + fragment.context!!.getString(R.string.split_dot) + " "

    }

}