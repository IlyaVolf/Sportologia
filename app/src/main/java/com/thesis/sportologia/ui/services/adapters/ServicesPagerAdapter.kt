package com.thesis.sportologia.ui.services.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.navOptions
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ItemServiceBinding
import com.thesis.sportologia.ui.TabsFragmentDirections
import com.thesis.sportologia.ui.services.CreateEditServiceFragment
import com.thesis.sportologia.ui.services.entities.ServiceListItem
import com.thesis.sportologia.ui.views.ItemServiceView
import com.thesis.sportologia.ui.views.OnItemServiceAction
import com.thesis.sportologia.utils.*
import com.thesis.sportologia.utils.ResourcesUtils.getString

class ServicesPagerAdapter(
    val fragment: Fragment,
    private val onAuthorBlockPressedAction: (String) -> Unit,
    private val onStatsBlockPressedAction: (String) -> Unit,
    private val onInfoBlockPressedAction: (String) -> Unit,
    private val listener: MoreButtonListener,
) : PagingDataAdapter<ServiceListItem, ServicesPagerAdapter.Holder>(ServicesDiffCallback()) {

    private lateinit var context: Context

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val serviceListItem = getItem(position) ?: return

        val itemService = ItemServiceView(holder.binding, context)

        itemService.setListener {
            when (it) {
                OnItemServiceAction.ORGANIZER_BLOCK -> onAuthorBlockPressedAction(serviceListItem.authorId)
                OnItemServiceAction.STATS_BLOCK -> onStatsBlockPressedAction(serviceListItem.id)
                OnItemServiceAction.INFO_BLOCK -> onInfoBlockPressedAction(serviceListItem.id)
                OnItemServiceAction.FAVS -> listener.onToggleFavouriteFlag(serviceListItem)
                OnItemServiceAction.MORE -> {}
                OnItemServiceAction.PHOTOS_BLOCK -> {}
            }
        }

        itemService.setCategories(
            TrainingProgrammesCategories.getLocalizedCategories(
                context,
                serviceListItem.categories
            )
        )
        itemService.setServiceName(serviceListItem.name)
        itemService.setDescription(serviceListItem.description)
        itemService.setAuthorName(serviceListItem.authorName)
        itemService.setAuthorType(
            Localization.convertUserTypeEnumToLocalized(
                context,
                serviceListItem.authorType
            )
        )
        itemService.setAuthorAvatar(serviceListItem.profilePictureUrl)
        itemService.setPrice(serviceListItem.price, serviceListItem.currency)
        itemService.setAcquiredNumber(serviceListItem.acquiredNumber)
        itemService.setReviewsNumber(serviceListItem.reviewsNumber)
        itemService.setRating(serviceListItem.rating)
        itemService.setFavs(serviceListItem.isFavourite)
        itemService.setPhotos(serviceListItem.photosUrls)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemServiceBinding.inflate(inflater, parent, false)
        return Holder(binding)
    }

    class Holder(
        val binding: ItemServiceBinding
    ) : RecyclerView.ViewHolder(binding.root)

    interface MoreButtonListener {

        /**
         * Called when the user taps the "Star" button in a list item.
         */
        fun onToggleFavouriteFlag(serviceListItem: ServiceListItem)

    }

}

class ServicesDiffCallback : DiffUtil.ItemCallback<ServiceListItem>() {
    override fun areItemsTheSame(oldItem: ServiceListItem, newItem: ServiceListItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ServiceListItem, newItem: ServiceListItem): Boolean {
        return oldItem == newItem
    }
}