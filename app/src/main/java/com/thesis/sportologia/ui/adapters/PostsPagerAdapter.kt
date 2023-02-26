package com.thesis.sportologia.ui.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ItemPostBinding
import com.thesis.sportologia.model.posts.entities.Post
import com.thesis.sportologia.ui.views.ItemPostView
import com.thesis.sportologia.utils.parseDate
import java.net.URI

class PostsPagerAdapter : PagingDataAdapter<Post, PostsPagerAdapter.Holder>(PostsDiffCallback()) {

    private lateinit var context: Context

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = getItem(position) ?: return

        val itemPost = ItemPostView(holder.binding, context)

        val actionsMore = if (item.authorId == CurrentAccount().id) {
            arrayOf("Редактировать", "Удалить")
        } else {
            arrayOf("Пожаловаться")
        }

        itemPost.setText(item.text)
        itemPost.setUsername(item.authorName)
        itemPost.setAvatar(item.profilePictureUrl)
        itemPost.setDate(parseDate(item.postedDate))
        itemPost.setLikes(item.likesCount, item.isLiked)
        itemPost.setPhotos()

        itemPost.getBinding().more2.setOnClickListener {
            createSpinnerDialog(actionsMore)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPostBinding.inflate(inflater, parent, false)
        return Holder(binding)
    }

    class Holder(
        val binding: ItemPostBinding
    ) : RecyclerView.ViewHolder(binding.root)

    private fun createSpinnerDialog(actions: Array<String>) {
        val builder = AlertDialog.Builder(context, R.style.DialogStyleBasic)
        //builder.setTitle(context.getString(R.string.ask_cancel_create_post))
        builder.setItems(actions
        ) { dialog, which ->

        }

        val dialog: AlertDialog = builder.create()

        dialog.show()
    }

}

class PostsDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}