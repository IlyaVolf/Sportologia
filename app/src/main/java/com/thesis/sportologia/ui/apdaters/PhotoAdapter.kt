package com.thesis.sportologia.ui.apdaters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.thesis.sportologia.databinding.ItemPhotoBinding
import com.thesis.sportologia.model.entities.Photo

class PhotoAdapter(
    private val onItemClick: (Photo) -> Unit
) : BaseAdapter<BaseViewHolder<Photo>, Photo>() {

    override fun takeViewHolder(parent: ViewGroup): BaseViewHolder<Photo> =
        PhotoViewHolder(
            viewBinding = ItemPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemClick
        )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Photo> =
        takeViewHolder(parent)
}