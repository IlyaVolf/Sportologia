package com.thesis.sportologia.ui.events.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.navOptions
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ItemEventBinding
import com.thesis.sportologia.ui.TabsFragmentDirections
import com.thesis.sportologia.ui.events.CreateEditEventFragment
import com.thesis.sportologia.ui.events.entities.EventListItem
import com.thesis.sportologia.ui.views.ItemEventView
import com.thesis.sportologia.ui.views.OnItemEventAction
import com.thesis.sportologia.utils.*
import com.thesis.sportologia.utils.ResourcesUtils.getString

class EventsPagerAdapter(
    val fragment: Fragment,
    private val onOrganizerBlockPressedAction: (String) -> Unit,
    private val listener: MoreButtonListener,
) : PagingDataAdapter<EventListItem, EventsPagerAdapter.Holder>(EventsDiffCallback()) {

    private lateinit var context: Context

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val eventListItem = getItem(position) ?: return

        val itemEvent = ItemEventView(holder.binding, context)

        itemEvent.setListener {
            when (it) {
                OnItemEventAction.ORGANIZER_BLOCK -> onOrganizerBlockPressedAction(eventListItem.organizerId)
                OnItemEventAction.LIKE -> listener.onToggleLike(eventListItem)
                OnItemEventAction.FAVS -> listener.onToggleFavouriteFlag(eventListItem)
                OnItemEventAction.MORE -> onMoreButtonPressed(eventListItem)
                OnItemEventAction.ADDRESS_BLOCK -> {}
                OnItemEventAction.PHOTOS_BLOCK -> {}
            }
        }

        itemEvent.setCategories(eventListItem.categories)
        itemEvent.setEventName(eventListItem.name)
        itemEvent.setDescription(eventListItem.description)
        itemEvent.setOrganizerName(eventListItem.organizerName)
        itemEvent.setOrganizerAvatar(eventListItem.profilePictureUrl)
        itemEvent.setAddress(YandexMaps.getAddress(context, eventListItem.position) ?: getString(R.string.not_specified))
        itemEvent.setPrice(eventListItem.price, eventListItem.currency)
        itemEvent.setDate(eventListItem.dateFrom, eventListItem.dateTo)
        itemEvent.setLikes(eventListItem.likesCount, eventListItem.isLiked)
        itemEvent.setFavs(eventListItem.isFavourite)
        itemEvent.setPhotos(eventListItem.photosUrls)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemEventBinding.inflate(inflater, parent, false)
        return Holder(binding)
    }

    class Holder(
        val binding: ItemEventBinding
    ) : RecyclerView.ViewHolder(binding.root)

    private fun createOnEditDialog(eventListItem: EventListItem) {
        createSimpleDialog(
            context,
            null,
            getString(R.string.ask_delete_event_warning),
            getString(R.string.action_delete),
            { _, _ ->
                run {
                    listener.onEventDelete(eventListItem)
                }
            },
            getString(R.string.action_cancel),
            { dialog, _ ->
                run {
                    dialog.cancel()
                }
            },
            null,
            null,
        )
    }

    private fun onMoreButtonPressed(
        eventListItem: EventListItem
    ) {
        val actionsMore: Array<Pair<String, DialogOnClickAction?>> =
            if (eventListItem.organizerId == CurrentAccount().id) {
                arrayOf(
                    Pair(getString(R.string.action_edit)) { _, _ ->
                        run {
                            onEditButtonPressed(eventListItem.id)
                        }
                    },
                    Pair(getString(R.string.action_delete)) { _, _ ->
                        run {
                            createOnEditDialog(eventListItem)
                        }
                    },
                )
            } else {
                arrayOf(
                    Pair(getString(R.string.action_report)) { _, _ -> }
                )
            }

        createSpinnerDialog(
            context,
            null,
            null,
            actionsMore
        )
    }

    private fun onEditButtonPressed(eventId: String) {
        val direction = TabsFragmentDirections.actionTabsFragmentToCreateEditEventFragment(
            CreateEditEventFragment.EventId(eventId)
        )

        fragment.findTopNavController().navigate(direction,
            navOptions {
                anim {
                    enter = R.anim.enter
                    exit = R.anim.exit
                    popEnter = R.anim.pop_enter
                    popExit = R.anim.pop_exit
                }
            })
    }

    interface MoreButtonListener {
        /**
         * Called when the user taps the "Delete" button in a list item
         */
        fun onEventDelete(eventListItem: EventListItem)

        /**
         * Called when the user taps the "Star" button in a list item.
         */
        fun onToggleFavouriteFlag(eventListItem: EventListItem)

        /**
         * Called when the user taps the "Star" button in a list item.
         */
        fun onToggleLike(eventListItem: EventListItem)

    }

}

class EventsDiffCallback : DiffUtil.ItemCallback<EventListItem>() {
    override fun areItemsTheSame(oldItem: EventListItem, newItem: EventListItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: EventListItem, newItem: EventListItem): Boolean {
        return oldItem == newItem
    }
}