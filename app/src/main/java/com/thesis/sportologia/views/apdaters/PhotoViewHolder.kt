package com.thesis.sportologia.views.apdaters

import com.thesis.sportologia.databinding.ItemPhotoBinding
import com.thesis.sportologia.model.entities.Photo
import com.thesis.sportologia.utils.loadImage

class PhotoViewHolder(
    private val viewBinding: ItemPhotoBinding,
    private val onItemClick: (Photo) -> Unit
) : BaseViewHolder<Photo>(viewBinding) {

    override fun bindItem(item: Photo) {
        viewBinding.apply {
            resetView()

            root.setOnClickListener {
                onItemClick(item)
            }

            photoItem.loadImage(item.imgUrl.orEmpty())
        }
    }

    private fun ItemPhotoBinding.resetView() {
    }
}