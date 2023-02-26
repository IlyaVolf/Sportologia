package com.thesis.sportologia.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.navOptions
import com.thesis.sportologia.utils.findTopNavController
import androidx.recyclerview.widget.RecyclerView
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentListPostsHeaderBinding
import com.thesis.sportologia.ui.ListPostsMode
import com.thesis.sportologia.ui.views.OnSpinnerOnlyOutlinedActionListener


class PostsHeaderAdapter(
    private val postsFilterListener: OnSpinnerOnlyOutlinedActionListener,
    val fragment: Fragment,
    private val mode: ListPostsMode
) : RecyclerView.Adapter<PostsHeaderAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentListPostsHeaderBinding.inflate(inflater, parent, false)

        return Holder(postsFilterListener, fragment, mode, binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return 1
    }

    private fun onCreatePostButtonPressed() {
        fragment.findTopNavController().navigate(R.id.create_post,
            null,
            navOptions {
                anim {
                    enter = R.anim.enter
                    exit = R.anim.exit
                    popEnter = R.anim.pop_enter
                    popExit = R.anim.pop_exit
                }
            })
    }

    class Holder(
        private val postsFilterListener: OnSpinnerOnlyOutlinedActionListener,
        val fragment: Fragment,
        private val mode: ListPostsMode,
        private val viewBinding: FragmentListPostsHeaderBinding,
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        fun bind() {

            when (mode) {
                ListPostsMode.HOME_PAGE -> {
                    viewBinding.postsFilter.root.isVisible = true
                    viewBinding.postsFilterSpace.isVisible = true

                    viewBinding.createPostButton.isVisible = false
                    viewBinding.createPostButtonSpace.isVisible = false

                    viewBinding.createPostButton.setOnClickListener {
                        onCreatePostButtonPressed()
                    }

                    viewBinding.postsFilter.spinner.initAdapter(
                        listOf(
                            fragment.context?.getString(R.string.filter_posts_all) ?: "",
                            fragment.context?.getString(R.string.filter_posts_athletes) ?: "",
                            fragment.context?.getString(R.string.filter_posts_organizations) ?: ""
                        )
                    )
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
            }

        }

        private fun onCreatePostButtonPressed() {
            fragment.findTopNavController().navigate(R.id.create_post,
                null,
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