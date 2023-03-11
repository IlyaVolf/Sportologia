package com.thesis.sportologia.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.thesis.sportologia.R

fun setAvatar(uri: String?, context: Context, avatarView: ImageView) {
    if (uri != null) {
        if (uri.isNotBlank()) {
            Glide.with(context)
                .load(uri)
                .circleCrop()
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .into(avatarView)
        }
    } else {
        Glide.with(context)
            .load(R.drawable.avatar)
            .into(avatarView)
    }
}