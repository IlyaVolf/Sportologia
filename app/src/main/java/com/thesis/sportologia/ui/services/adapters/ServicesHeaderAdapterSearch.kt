package com.thesis.sportologia.ui.services.adapters

import androidx.fragment.app.Fragment
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentListServicesHeaderBinding
import com.thesis.sportologia.model.services.entities.FilterParamsServices
import com.thesis.sportologia.model.services.entities.ServiceType
import com.thesis.sportologia.utils.*

class ServicesHeaderAdapterSearch(
    fragment: Fragment,
    val listener: FilterListener,
    filterParamsServices: FilterParamsServices,
) : ServicesHeaderAdapter(fragment, listener, filterParamsServices) {

    override fun createHolder(
        fragment: Fragment,
        binding: FragmentListServicesHeaderBinding
    ): Holder {
        return HolderSearch(fragment, listener, binding, filterParamsServices)
    }

    class HolderSearch(
        fragment: Fragment,
        listener: FilterListener,
        binding: FragmentListServicesHeaderBinding,
        var filterParamsServices: FilterParamsServices
    ) : Holder(fragment, binding, listener) {

        override val renderHeader: () -> Unit = {
            enableServicesChosenFilters(parser)
        }

        private val parser = {
            restrictionsParser()
            sortingParser()
        }

        val restrictionsParser: () -> Unit = {
            val restrictionBlockList = mutableListOf<String>()

            if (filterParamsServices.categories != null) {
                val restrictionBlock = StringBuilder("")
                val localizedCategories = Categories.getLocalizedCategories(
                    fragment.context!!,
                    filterParamsServices.categories!!
                )

                restrictionBlock.append(concatMap(localizedCategories, ", "))

                restrictionBlockList.add(restrictionBlock.toString())
            }

            if (filterParamsServices.priceFrom != null) {
                val restrictionBlock = StringBuilder("")

                if (filterParamsServices.priceFrom == 0F) {
                    restrictionBlock.append(fragment.getString(R.string.free))
                } else {
                    restrictionBlock.append(fragment.getString(R.string.under))
                        .append(" ")
                        .append(formatPrice(filterParamsServices.priceFrom.toString()))
                        .append(" ")
                        .append(fragment.getString(R.string.ruble_abbreviation))
                }

                restrictionBlockList.add(restrictionBlock.toString())
            }

            if (filterParamsServices.ratingFrom != null) {
                val restrictionBlock = StringBuilder("")

                restrictionBlock.append(fragment.getString(R.string.rating_from))
                    .append(" ")
                    .append(formatFloat(filterParamsServices.ratingFrom!!, 1, true))

                restrictionBlockList.add(restrictionBlock.toString())
            }

            if (filterParamsServices.serviceType != null) {
                val restrictionBlock = StringBuilder("")

                restrictionBlock.append(
                    when (filterParamsServices.serviceType!!) {
                        ServiceType.TRAINING_PROGRAM ->
                            fragment.getString(R.string.service_training_program_short)
                    }
                )

                restrictionBlockList.add(restrictionBlock.toString())
            }

            val restrictionsString = concatList(restrictionBlockList, splittingDot)
            if (restrictionsString == "") {
                binding.servicesChosenFilters.restrictions.text =
                    fragment.getString(R.string.filter_not_specified)
            } else {
                binding.servicesChosenFilters.restrictions.text = restrictionsString
            }
        }

        val sortingParser: () -> Unit = {
            binding.servicesChosenFilters.sorting.text = when (filterParamsServices.sortBy) {
                FilterParamsServices.ServicesSortBy.Popularity -> fragment.getString(R.string.filter_sort_by_popularity)
                FilterParamsServices.ServicesSortBy.Rating -> fragment.getString(R.string.filter_sort_by_rating)
                FilterParamsServices.ServicesSortBy.Price -> fragment.getString(R.string.filter_sort_by_price)
            }
        }

        private val splittingDot = " " + fragment.context!!.getString(R.string.split_dot) + " "

    }

}