package com.thesis.sportologia.ui.adapters

import com.thesis.sportologia.model.posts.entities.Post
import com.thesis.sportologia.ui.base.BaseViewHolder
import com.thesis.sportologia.ui.views.ItemPostView
import com.thesis.sportologia.ui.views.OnItemPostAction
import com.thesis.sportologia.utils.parseDate
import java.net.URI

class PostViewHolder(
    private val onItemPostAction: OnItemPostAction,
    private val view: ItemPostView,
) : BaseViewHolder<Post>(view.getBinding()) {

    override fun bindItem(item: Post) {
        resetView()

        view.setText(item.text)
        view.setUsername(item.authorName)
        view.setAvatar(URI(item.profilePictureUrl))
        view.setDate(parseDate(item.postedDate))
        view.setLikes(item.likesCount, item.isLiked)
        view.setPhotos()

        view.setListener {
            onItemPostAction
        }
    }

    private fun resetView() {
    }
}