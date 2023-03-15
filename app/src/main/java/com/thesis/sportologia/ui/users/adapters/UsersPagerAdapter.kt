package com.thesis.sportologia.ui.users.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.thesis.sportologia.databinding.ItemUserBinding
import com.thesis.sportologia.ui.users.entities.UserSnippetListItem
import com.thesis.sportologia.utils.*

class UsersPagerAdapter(
    val fragment: Fragment,
    private val onUserSnippetItemPressedAction: (String) -> Unit,
) : PagingDataAdapter<UserSnippetListItem, UsersPagerAdapter.Holder>(UsersDiffCallback()) {

    private lateinit var context: Context

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val userSnippetListItem = getItem(position) ?: return
        holder.binding.apply {
            root.setOnClickListener { onUserSnippetItemPressedAction(userSnippetListItem.id) }
            iuName.text = userSnippetListItem.name
            setAvatar(userSnippetListItem.profilePhotoURI, context, iuAvatar)

            val addressText = userSnippetListItem.address?.toString() ?: ""
            if (addressText == "") {
                iuAddress.visibility = GONE
            } else {
                iuAddress.visibility = VISIBLE
                iuAddress.text = addressText
            }

            val categoriesText = concatMap(userSnippetListItem.categories, ", ")
            if (categoriesText == "") {
                iuCategories.visibility = GONE
            } else {
                iuCategories.visibility = VISIBLE
                iuCategories.text = categoriesText
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(inflater, parent, false)
        return Holder(binding)
    }

    class Holder(
        val binding: ItemUserBinding
    ) : RecyclerView.ViewHolder(binding.root)


    class UsersDiffCallback : DiffUtil.ItemCallback<UserSnippetListItem>() {
        override fun areItemsTheSame(
            oldItem: UserSnippetListItem,
            newItem: UserSnippetListItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: UserSnippetListItem,
            newItem: UserSnippetListItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}