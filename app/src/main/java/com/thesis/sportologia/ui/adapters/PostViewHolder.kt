package com.thesis.sportologia.ui.adapters

import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.model.posts.entities.Post
import com.thesis.sportologia.ui.base.BaseViewHolder
import com.thesis.sportologia.ui.views.ItemPostView
import com.thesis.sportologia.ui.views.OnItemPostActionListener
import com.thesis.sportologia.ui.views.OnSpinnerMoreActionListener
import com.thesis.sportologia.utils.parseDate
import java.net.URI

class PostViewHolder(
    private val onItemPostActionListener: OnItemPostActionListener,
    private val onSpinnerMoreActionListener: OnSpinnerMoreActionListener,
    private val view: ItemPostView,
) : BaseViewHolder<Post>(view.getBinding()) {

    override fun bindItem(item: Post) {

        val actionsMore = if (item.authorId == CurrentAccount().id) {
            arrayListOf("Редактировать", "Удалить")
        } else {
            arrayListOf("Пожаловаться")
        }

        resetView()

        view.setText(item.text)
        view.setUsername(item.authorName)
        view.setAvatar(URI(item.profilePictureUrl))
        view.setDate(parseDate(item.postedDate))
        view.setLikes(item.likesCount, item.isLiked)
        view.setPhotos()

       // view.getBinding().more.setListener { onSpinnerMoreActionListener }
       // view.getBinding().more.initAdapter(actionsMore)
    }

    private fun resetView() {
    }
}

enum class ActionsMore(val action: String) {
    EDIT("Редактировать"),
    DELETE("Удалить"),
    REPORT( "Пожаловаться"),
}