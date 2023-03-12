package com.thesis.sportologia.ui.posts.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.navOptions
import com.thesis.sportologia.utils.findTopNavController
import androidx.recyclerview.widget.RecyclerView
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentListPostsHeaderBinding
import com.thesis.sportologia.ui.CreateEditPostFragment
import com.thesis.sportologia.ui.posts.ListPostsMode
import com.thesis.sportologia.ui.TabsFragmentDirections
import com.thesis.sportologia.ui.views.OnSpinnerOnlyOutlinedActionListener


class PostsHeaderAdapter(
    val fragment: Fragment,
    private val mode: ListPostsMode,
    private val listener: FilterListener
) : RecyclerView.Adapter<PostsHeaderAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentListPostsHeaderBinding.inflate(inflater, parent, false)

        return Holder(fragment, mode, binding, listener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return 1
    }

    interface FilterListener {

        fun filterApply(athTorgF: Boolean?)

    }

    class Holder(
        val fragment: Fragment,
        private val mode: ListPostsMode,
        private val viewBinding: FragmentListPostsHeaderBinding,
        private val listener: FilterListener
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        fun bind() {
            val filterOptionsList = listOf(
                fragment.context?.getString(R.string.filter_posts_all) ?: "",
                fragment.context?.getString(R.string.filter_posts_athletes) ?: "",
                fragment.context?.getString(R.string.filter_posts_organizations) ?: ""
            )

            val postsFilterListener: OnSpinnerOnlyOutlinedActionListener = {
                when (it) {
                    filterOptionsList[0] -> listener.filterApply(null)
                    filterOptionsList[1] -> listener.filterApply(true)
                    filterOptionsList[2] -> listener.filterApply(false)
                }
            }


            when (mode) {
                ListPostsMode.HOME_PAGE -> {
                    viewBinding.postsFilter.root.isVisible = true
                    viewBinding.postsFilterSpace.isVisible = true

                    viewBinding.createPostButton.isVisible = false
                    viewBinding.createPostButtonSpace.isVisible = false

                    viewBinding.createPostButton.setOnClickListener {
                        onCreatePostButtonPressed()
                    }

                    viewBinding.postsFilter.spinner.initAdapter(filterOptionsList)
                    viewBinding.postsFilter.spinner.setListener(postsFilterListener)
                }
                ListPostsMode.FAVOURITES_PAGE -> {
                    viewBinding.postsFilter.root.isVisible = true
                    viewBinding.postsFilterSpace.isVisible = true

                    viewBinding.createPostButton.isVisible = false
                    viewBinding.createPostButtonSpace.isVisible = false

                    viewBinding.createPostButton.setOnClickListener {
                        onCreatePostButtonPressed()
                    }

                    viewBinding.postsFilter.spinner.initAdapter(filterOptionsList)
                    viewBinding.postsFilter.spinner.setListener(postsFilterListener)
                }
                ListPostsMode.PROFILE_OWN_PAGE -> {
                    viewBinding.postsFilter.root.isVisible = false
                    viewBinding.postsFilterSpace.isVisible = false

                    viewBinding.createPostButton.isVisible = true
                    viewBinding.createPostButtonSpace.isVisible = true

                    viewBinding.createPostButton.setOnClickListener {
                        onCreatePostButtonPressed()
                    }
                }
                ListPostsMode.PROFILE_OTHER_PAGE -> {
                    viewBinding.postsFilter.root.isVisible = false
                    viewBinding.postsFilterSpace.isVisible = false

                    viewBinding.createPostButton.isVisible = false
                    viewBinding.createPostButtonSpace.isVisible = false
                }
            }

        }

        private fun onCreatePostButtonPressed() {
            val direction = TabsFragmentDirections.actionTabsFragmentToEditPostFragment(
                CreateEditPostFragment.PostId(null)
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

    }
}