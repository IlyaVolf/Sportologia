package com.thesis.sportologia.ui.events.adapters

import androidx.core.view.isVisible
import androidx.fragment.app.Fragment

class EventsHeaderAdapterFavourites(
    fragment: Fragment,
    listener: FilterListener
) : EventsHeaderAdapter(fragment, listener) {

    override val renderHeader: () -> Unit = {
        binding.eventsFilter.root.isVisible = true
        binding.eventsFilterSpace.isVisible = true

        binding.eventsChosenFilters.root.isVisible = false
        binding.eventsChosenFiltersSpace.isVisible = false

        binding.createEventButton.isVisible = false
        binding.createEventButtonSpace.isVisible = false

        binding.createEventButton.setOnClickListener {
            onCreateEventButtonPressed()
        }

        binding.eventsFilter.spinner.initAdapter(filterOptionsList)
        binding.eventsFilter.spinner.setListener(eventsFilterListener)
    }

}