package com.thesis.sportologia.ui.events.adapters

import androidx.fragment.app.Fragment
import com.thesis.sportologia.databinding.FragmentListEventsHeaderBinding
import com.thesis.sportologia.model.events.entities.FilterParamsEvents

class EventsHeaderAdapterHome(
    fragment: Fragment,
    val listener: FilterListener,
    filterParamsEvents: FilterParamsEvents,
) : EventsHeaderAdapter(fragment, listener, filterParamsEvents) {

    override fun createHolder(
        fragment: Fragment,
        binding: FragmentListEventsHeaderBinding
    ): Holder {
        return HolderHome(fragment, listener, binding)
    }

    class HolderHome(
        fragment: Fragment,
        listener: FilterListener,
        binding: FragmentListEventsHeaderBinding,
    ) : Holder(fragment, binding, listener) {

        override val renderHeader: () -> Unit = {
            enableEventsFilter()
        }

    }

}