package com.thesis.sportologia.ui.events.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentListEventsHeaderBinding
import com.thesis.sportologia.ui.views.OnSpinnerOnlyOutlinedActionListener

abstract class EventsHeaderAdapter(
    private val fragment: Fragment,
    private val listener: FilterListener
) : RecyclerView.Adapter<EventsHeaderAdapter.Holder>() {

    protected lateinit var binding: FragmentListEventsHeaderBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        binding = FragmentListEventsHeaderBinding.inflate(inflater, parent, false)

        disableAllItems()

        return Holder(fragment, renderHeader, binding)
    }

    private fun disableAllItems() {
        binding.eventsChosenFilters.root.isVisible = false
        binding.eventsChosenFiltersSpace.isVisible = false
        binding.eventsFilter.root.isVisible = false
        binding.eventsFilterSpace.isVisible = false
        binding.createEventButton.isVisible = false
        binding.createEventButtonSpace.isVisible = false
    }

    protected fun enableEventsFilter(isUpcomingOnly: Boolean) {
        binding.eventsFilter.root.isVisible = true
        binding.eventsFilterSpace.isVisible = true
        binding.eventsFilter.spinner.initAdapter(filterOptionsList, getFilterValue(isUpcomingOnly))
        binding.eventsFilter.spinner.setListener(eventsFilterListener)
    }

    protected fun enableEventsChosenFilters() {
        binding.eventsChosenFilters.root.isVisible = true
        binding.eventsChosenFiltersSpace.isVisible = true
    }

    protected fun enableCreateEventButton() {
        binding.createEventButton.isVisible = true
        binding.createEventButtonSpace.isVisible = true
        binding.createEventButton.setOnClickListener {
            onCreateEventButtonPressed()
        }
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return 1
    }

    abstract val renderHeader: () -> Unit

    interface FilterListener {

        fun filterApply(isUpcomingOnly: Boolean)

    }

    private val filterOptionsList = listOf(
        fragment.context?.getString(R.string.filter_events_upcoming) ?: "",
        fragment.context?.getString(R.string.filter_events_all) ?: "",
    )

    private fun getFilterValue(isUpcomingOnly: Boolean): String {
        return when (isUpcomingOnly) {
            true -> filterOptionsList[0]
            false -> filterOptionsList[1]
        }
    }

    private val eventsFilterListener: OnSpinnerOnlyOutlinedActionListener = {
        when (it) {
            filterOptionsList[0] -> listener.filterApply(true)
            filterOptionsList[1] -> listener.filterApply(false)
        }
    }

    private fun onCreateEventButtonPressed() {
        /*val direction = TabsFragmentDirections.actionTabsFragmentToEditEventFragment(
            CreateEditEventFragment.EventId(null)
        )*/

        /*fragment.findTopNavController().navigate(direction,
            navOptions {
                anim {
                    enter = R.anim.enter
                    exit = R.anim.exit
                    popEnter = R.anim.pop_enter
                    popExit = R.anim.pop_exit
                }
            })*/
    }

    class Holder(
        val fragment: Fragment,
        private val renderHeader: () -> Unit,
        private val viewBinding: FragmentListEventsHeaderBinding,
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        fun bind() {
            renderHeader()
        }

    }
}