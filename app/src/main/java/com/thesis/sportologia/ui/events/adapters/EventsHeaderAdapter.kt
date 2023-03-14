package com.thesis.sportologia.ui.events.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
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

        return Holder(fragment, renderHeader, binding)
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

    protected val filterOptionsList = listOf(
        fragment.context?.getString(R.string.filter_events_all) ?: "",
        fragment.context?.getString(R.string.filter_events_upcoming) ?: "",
    )

    protected val eventsFilterListener: OnSpinnerOnlyOutlinedActionListener = {
        when (it) {
            filterOptionsList[0] -> listener.filterApply(false)
            filterOptionsList[1] -> listener.filterApply(true)
        }
    }

    protected fun onCreateEventButtonPressed() {
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