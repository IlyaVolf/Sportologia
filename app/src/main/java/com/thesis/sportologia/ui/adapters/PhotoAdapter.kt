package com.thesis.sportologia.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.thesis.sportologia.databinding.ItemPhotoBinding
import com.thesis.sportologia.model.entities.Photo
import com.thesis.sportologia.ui.base.BaseAdapter
import com.thesis.sportologia.ui.base.BaseViewHolder

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