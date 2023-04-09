package com.thesis.sportologia.ui.events.adapters

import androidx.fragment.app.Fragment
import com.thesis.sportologia.databinding.FragmentListEventsHeaderBinding
import com.thesis.sportologia.model.events.entities.FilterParamsEvents

class EventsHeaderAdapterFavourites(
    fragment: Fragment,
    val listener: FilterListener,
    filterParamsEvents: FilterParamsEvents,
) : EventsHeaderAdapter(fragment, listener, filterParamsEvents) {

    override fun createHolder(
        fragment: Fragment,
        binding: FragmentListEventsHeaderBinding
    ): Holder {
        return HolderFavourites(fragment, listener, binding)
    }

    class HolderFavourites(
        fragment: Fragment,
        listener: FilterListener,
        binding: FragmentListEventsHeaderBinding,
    ) : Holder(fragment, binding, listener) {

        override val renderHeader: () -> Unit = {
            enableEventsFilter()
        }

    }

}