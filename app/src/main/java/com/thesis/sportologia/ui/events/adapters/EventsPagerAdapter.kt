package com.thesis.sportologia.ui.events.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ItemEventBinding
import com.thesis.sportologia.ui.*
import com.thesis.sportologia.ui.events.entities.EventListItem
import com.thesis.sportologia.ui.views.ItemEventView
import com.thesis.sportologia.ui.views.OnItemEventAction
import com.thesis.sportologia.utils.*
import com.thesis.sportologia.utils.ResourcesUtils.getString

/** class EventsPagerAdapter(
    val fragment: Fragment,
    private val mode: ListEventsMode,
    private val listener: MoreButtonListener,
) : PagingDataAdapter<EventListItem, EventsPagerAdapter.Holder>(EventsDiffCallback()) {

    private lateinit var context: Context

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val eventListItem = getItem(position) ?: return

        val itemEvent = ItemEventView(holder.binding, context)

        itemEvent.setListener {
            when (it) {
                OnItemEventAction.ORGANIZER_BLOCK -> onOrganizerBlockPressed(eventListItem.organizerId)
                OnItemEventAction.ADDRESS_BLOCK -> {}
                OnItemEventAction.PHOTOS_BLOCK -> {}
                OnItemEventAction.LIKE -> listener.onToggleLike(eventListItem)
                OnItemEventAction.FAVS -> listener.onToggleFavouriteFlag(eventListItem)
                OnItemEventAction.MORE -> onMoreButtonPressed(eventListItem)
                else -> {}
            }
        }

        itemEvent.setOrganizerName(eventListItem.organizerName)
        itemEvent.setDescription(eventListItem.description)
        itemEvent.setOrganizerAvatar(eventListItem.profilePictureUrl)
        itemEvent.setPrice(eventListItem.price, eventListItem.currency)
        itemEvent.setDate(parseDate(eventListItem.dateFrom), parseDate(eventListItem.dateTo))
        itemEvent.setLikes(eventListItem.likesCount, eventListItem.isLiked)
        itemEvent.setFavs(eventListItem.isFavourite)
        itemEvent.setPhotos()
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
            getString(R.string.ask_delete_post_warning),
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

    private fun onEditButtonPressed(postId: Long) {
        // TODO CREATEEDIT
        /*val direction = TabsFragmentDirections.actionTabsFragmentToEditEventFragment(
            CreateEditEventFragment.EventId(postId)
        )

        fragment.findTopNavController().navigate(direction,
            navOptions {
                anim {
                    enter = R.anim.enter
                    exit = R.anim.exit
                    popEnter = R.anim.pop_enter
                    popExit = R.anim.pop_exit
                }
            })*/
    }

    private fun onOrganizerBlockPressed(userId: String) {
        when (mode) {
            ListEventsMode.HOME_PAGE -> {
                val direction = HomeFragmentDirections.actionHomeFragmentToProfileFragment(userId)
                fragment.findNavController().navigate(direction,
                    navOptions {
                        anim {
                            enter = R.anim.slide_in_right
                            exit = R.anim.slide_out_left
                            popEnter = R.anim.slide_in_left
                            popExit = R.anim.slide_out_right
                        }
                    })
            }
            ListEventsMode.FAVOURITES_PAGE -> {
                val direction =
                    FavouritesFragmentDirections.actionFavouritesFragmentToProfileFragment(userId)
                fragment.findNavController().navigate(
                    direction,
                    navOptions {
                        anim {
                            enter = R.anim.slide_in_right
                            exit = R.anim.slide_out_left
                            popEnter = R.anim.slide_in_left
                            popExit = R.anim.slide_out_right
                        }
                    })
            }
            ListEventsMode.PROFILE_OWN_PAGE -> {
                val direction =
                    ProfileOwnFragmentDirections.actionProfileOwnFragmentToProfileFragment(userId)
                fragment.findNavController().navigate(
                    direction,
                    navOptions {
                        anim {
                            enter = R.anim.slide_in_right
                            exit = R.anim.slide_out_left
                            popEnter = R.anim.slide_in_left
                            popExit = R.anim.slide_out_right
                        }
                    })
            }
        }
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
} */