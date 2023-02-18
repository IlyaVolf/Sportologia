package com.thesis.sportologia.utils

import android.widget.ImageView
import com.thesis.sportologia.R
import com.thesis.sportologia.utils.image_loader.ImageLoader

fun ImageView.loadImage(url: String) {
    ImageLoader
        .load(url)
        .error(R.drawable.img_not_found)
        .placeholder(R.drawable.img_not_found)
        .centerCrop()
        .roundedCorners(ResourcesUtils.getPxByDp(4f))
        .into(this)
}
