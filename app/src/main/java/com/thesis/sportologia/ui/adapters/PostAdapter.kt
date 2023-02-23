package com.thesis.sportologia.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.thesis.sportologia.databinding.ItemPostBinding
import com.thesis.sportologia.model.posts.entities.Post
import com.thesis.sportologia.ui.base.BaseAdapter
import com.thesis.sportologia.ui.base.BaseViewHolder
import com.thesis.sportologia.ui.views.ItemPostView
import com.thesis.sportologia.ui.views.OnItemPostActionListener
import com.thesis.sportologia.ui.views.OnSpinnerMoreActionListener

class PostAdapter(
    private val OnItemPostActionListener: OnItemPostActionListener,
    private val onSpinnerMoreActionListener: OnSpinnerMoreActionListener,
) : BaseAdapter<BaseViewHolder<Post>, Post>() {

    override fun takeViewHolder(parent: ViewGroup): BaseViewHolder<Post> {
        val viewBinding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        val itemPost = ItemPostView(viewBinding, parent.context)

        return PostViewHolder(OnItemPostActionListener, onSpinnerMoreActionListener, itemPost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Post> =
        takeViewHolder(parent)
}