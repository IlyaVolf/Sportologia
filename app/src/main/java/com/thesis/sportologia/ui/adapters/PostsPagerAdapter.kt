package com.thesis.sportologia.ui.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ItemPostBinding
import com.thesis.sportologia.model.posts.entities.Post
import com.thesis.sportologia.ui.entities.PostListItem
import com.thesis.sportologia.ui.views.ItemPostView
import com.thesis.sportologia.ui.views.OnItemPostAction
import androidx.navigation.fragment.findNavController
import com.thesis.sportologia.ui.ListPostsMode
import com.thesis.sportologia.utils.parseDate
import java.net.URI

class PostsPagerAdapter(
    val fragment: Fragment,
    private val mode: ListPostsMode,
    private val listener: Listener
) : PagingDataAdapter<PostListItem, PostsPagerAdapter.Holder>(PostsDiffCallback()) {

    private lateinit var context: Context

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = getItem(position) ?: return

        val itemPost = ItemPostView(holder.binding, context)

        itemPost.setListener {
            when (it) {
                OnItemPostAction.HEADER_BLOCK -> onHeaderBlockPressed()
                OnItemPostAction.MORE -> createSpinnerDialog(listener, item)
                OnItemPostAction.LIKE -> listener.onToggleLike(item)
                OnItemPostAction.FAVS -> listener.onToggleFavouriteFlag(item)
                else -> {}
            }
        }

        itemPost.setText(item.text)
        itemPost.setUsername(item.authorName)
        itemPost.setAvatar(item.profilePictureUrl)
        itemPost.setDate(parseDate(item.postedDate))
        itemPost.setLikes(item.likesCount, item.isLiked)
        itemPost.setFavs(item.isFavourite)
        itemPost.setPhotos()
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

    private fun createSpinnerDialog(
        listener: Listener,
        postListItem: PostListItem
    ) {
        val actionsMore = if (postListItem.authorId == CurrentAccount().id) {
            arrayOf("Редактировать", "Удалить")
        } else {
            arrayOf("Пожаловаться")
        }

        val builder = AlertDialog.Builder(context, R.style.DialogStyleBasic)
        //builder.setTitle(context.getString(R.string.ask_cancel_create_post))
        builder.setItems(
            actionsMore
        ) { dialog, which ->
            getItem(which)
            if (postListItem.authorId == CurrentAccount().id) {
                when (which) {
                    // 0 -> listener.onPostEdit(postListItem)
                    1 -> listener.onPostDelete(postListItem)
                }
            } else {
                // listener.onReport(postListItem)
            }
        }

        val dialog: AlertDialog = builder.create()

        dialog.show()
    }

    private fun onHeaderBlockPressed() {
        when (mode) {
            ListPostsMode.HOME_PAGE -> {
                fragment.findNavController().navigate(
                    R.id.action_homeFragment_to_profileFragment,
                    null,
                    navOptions {
                        anim {
                            enter = R.anim.slide_in_right
                            exit = R.anim.slide_out_left
                            popEnter = R.anim.slide_in_left
                            popExit = R.anim.slide_out_right
                        }
                    })
            }
            ListPostsMode.PROFILE_OWN_PAGE -> {
                fragment.findNavController().navigate(
                    R.id.action_profileOwnFragment_to_profileFragment,
                    null,
                    navOptions {
                        anim {
                            enter = R.anim.slide_in_right
                            exit = R.anim.slide_out_left
                            popEnter = R.anim.slide_in_left
                            popExit = R.anim.slide_out_right
                        }
                    })
            }
        }
    }

    interface Listener {
        /**
         * Called when the user taps the "Delete" button in a list item
         */
        fun onPostDelete(postListItem: PostListItem)

        /**
         * Called when the user taps the "Star" button in a list item.
         */
        fun onToggleFavouriteFlag(postListItem: PostListItem)

        /**
         * Called when the user taps the "Star" button in a list item.
         */
        fun onToggleLike(postListItem: PostListItem)
    }

}

class PostsDiffCallback : DiffUtil.ItemCallback<PostListItem>() {
    override fun areItemsTheSame(oldItem: PostListItem, newItem: PostListItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PostListItem, newItem: PostListItem): Boolean {
        return oldItem == newItem
    }
}