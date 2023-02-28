package com.thesis.sportologia.ui.adapters

import android.content.Context
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
import com.thesis.sportologia.utils.*
import com.thesis.sportologia.utils.ResourcesUtils.getString

class PostsPagerAdapter(
    val fragment: Fragment,
    private val mode: ListPostsMode,
    private val listener: MoreButtonListener,
) : PagingDataAdapter<PostListItem, PostsPagerAdapter.Holder>(PostsDiffCallback()) {

    private lateinit var context: Context

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val postListItem = getItem(position) ?: return

        val itemPost = ItemPostView(holder.binding, context)

        itemPost.setListener {
            when (it) {
                OnItemPostAction.HEADER_BLOCK -> onHeaderBlockPressed()
                OnItemPostAction.MORE -> onMoreButtonPressed(postListItem)
                OnItemPostAction.LIKE -> listener.onToggleLike(postListItem)
                OnItemPostAction.FAVS -> listener.onToggleFavouriteFlag(postListItem)
                else -> {}
            }
        }

        itemPost.setText(postListItem.text)
        itemPost.setUsername(postListItem.authorName)
        itemPost.setAvatar(postListItem.profilePictureUrl)
        itemPost.setDate(parseDate(postListItem.postedDate))
        itemPost.setLikes(postListItem.likesCount, postListItem.isLiked)
        itemPost.setFavs(postListItem.isFavourite)
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
        createSimpleDialog(
            context,
            null,
            getString(R.string.ask_delete_post_warning),
            getString(R.string.action_delete),
            { _, _ ->
                run {
                    listener.onPostDelete(postListItem)
                }
            },
            getString(R.string.action_cancel),
            { dialog, _ ->
                run {
                    dialog.cancel()
                }
            },
            null,
            null,
        )
    }

    private fun onMoreButtonPressed(
        postListItem: PostListItem
    ) {
        val actionsMore: Array<Pair<String, DialogOnClickAction?>> =
            if (postListItem.authorId == CurrentAccount().id) {
                arrayOf(
                    Pair(
                        "Редактировать"
                    ) { _, _ ->
                        run {
                            onEditButtonPressed(postListItem.id)
                        }
                    },
                    Pair("Удалить") { _, _ ->
                        run {
                            createOnEditDialog(postListItem)
                        }
                    },
                )
            } else {
                arrayOf(
                    Pair("Пожаловаться") { _, _ -> }
                )
            }

        createSpinnerDialog(
            context,
            null,
            null,
            actionsMore
        )
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