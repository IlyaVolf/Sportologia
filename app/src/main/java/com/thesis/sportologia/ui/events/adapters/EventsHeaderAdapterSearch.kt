package com.thesis.sportologia.ui.events.adapters

import androidx.fragment.app.Fragment

class EventsHeaderAdapterSearch(
    fragment: Fragment,
    listener: FilterListener,
) : EventsHeaderAdapter(fragment, listener) {

    override val renderHeader: () -> Unit = {
        enableEventsChosenFilters()
    }

}