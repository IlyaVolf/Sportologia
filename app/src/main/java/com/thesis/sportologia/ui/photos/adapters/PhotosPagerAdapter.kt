package com.thesis.sportologia.ui.photos.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.thesis.sportologia.databinding.ItemPhotoBinding
import com.thesis.sportologia.ui.photos.entities.PhotoListItem
import com.thesis.sportologia.utils.*

class PhotosPagerAdapter(
    val fragment: Fragment,
) : PagingDataAdapter<PhotoListItem, PhotosPagerAdapter.Holder>(PostsDiffCallback()) {

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val photoListItem = getItem(position) ?: return

        setPhoto(photoListItem.photoUri, fragment.context!!, holder.binding.photoItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPhotoBinding.inflate(inflater, parent, false)
        return Holder(binding)
    }

    class Holder(
        val binding: ItemPhotoBinding
    ) : RecyclerView.ViewHolder(binding.root)

}

class PostsDiffCallback : DiffUtil.ItemCallback<PhotoListItem>() {
    override fun areItemsTheSame(oldItem: PhotoListItem, newItem: PhotoListItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PhotoListItem, newItem: PhotoListItem): Boolean {
        return oldItem == newItem
    }
}