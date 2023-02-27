package com.thesis.sportologia.ui.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ItemPostBinding
import com.thesis.sportologia.ui.*
import com.thesis.sportologia.ui.entities.PostListItem
import com.thesis.sportologia.ui.views.ItemPostView
import com.thesis.sportologia.ui.views.OnItemPostAction
import com.thesis.sportologia.utils.ResourcesUtils.getString
import com.thesis.sportologia.utils.findTopNavController
import com.thesis.sportologia.utils.parseDate

class PostsPagerAdapter(
    val fragment: Fragment,
    private val mode: ListPostsMode,
    private val listener: MoreButtonListener,
) : PagingDataAdapter<PostListItem, PostsPagerAdapter.Holder>(PostsDiffCallback()) {

    private lateinit var context: Context

    override fun onBindViewHolder(holder: Holder, position: Int) {

        Log.d("BUGFIX", "$position")

        val item = getItem(position) ?: return

        val itemPost = ItemPostView(holder.binding, context)

        itemPost.setListener {
            when (it) {
                OnItemPostAction.HEADER_BLOCK -> onHeaderBlockPressed()
                OnItemPostAction.MORE -> createSpinnerDialog(item)
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

    private fun createOnEditDialog(postListItem: PostListItem) {
        val builder = AlertDialog.Builder(context, R.style.DialogStyleBasic)
        builder.setMessage(getString(R.string.ask_delete_post_warning))
        builder.setNegativeButton(getString(R.string.action_delete)) { dialog, _ ->
            listener.onPostDelete(postListItem)
        }
        builder.setNeutralButton(getString(R.string.action_cancel)) { dialog, _ ->
            dialog.cancel()
        }
        val dialog: AlertDialog = builder.create()

        dialog.show()

        dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            .setTextColor(context.getColor(R.color.purple_medium))
        dialog.getButton(DialogInterface.BUTTON_NEUTRAL)
            .setTextColor(context.getColor(R.color.purple_medium))

        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).isAllCaps = false
        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).isAllCaps = false
    }

    private fun createSpinnerDialog(
        postListItem: PostListItem
    ) {
        val actionsMore = if (postListItem.authorId == CurrentAccount().id) {
            arrayOf("Редактировать", "Удалить")
        } else {
            arrayOf("Пожаловаться")
        }

        val builder = AlertDialog.Builder(context, R.style.DialogStyleBasic)
        builder.setItems(
            actionsMore
        ) { dialog, which ->
            getItem(which)
            if (postListItem.authorId == CurrentAccount().id) {
                when (which) {
                    0 -> {
                        onEditButtonPressed(postListItem.id)
                    }
                    1 -> {
                        createOnEditDialog(postListItem)
                    }
                }
            } else {
                // listener.onReport(postListItem)
            }
        }

        val dialog: AlertDialog = builder.create()

        dialog.show()
    }

    private fun onEditButtonPressed(postId: Long) {
        val direction = TabsFragmentDirections.actionTabsFragmentToEditPostFragment(
            CreateEditPostFragment.PostId(postId)
        )

        fragment.findTopNavController().navigate(direction,
            navOptions {
                anim {
                    enter = R.anim.enter
                    exit = R.anim.exit
                    popEnter = R.anim.pop_enter
                    popExit = R.anim.pop_exit
                }
            })
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
            ListPostsMode.FAVOURITES_PAGE -> {
                fragment.findNavController().navigate(
                    R.id.action_favouritesFragment_to_profileFragment,
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

    interface MoreButtonListener {
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