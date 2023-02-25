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


class PostsHeaderAdapter(val fragment: Fragment, private val mode: PostsHeaderMode) :
    RecyclerView.Adapter<PostsHeaderAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentListPostsHeaderBinding.inflate(inflater, parent, false)

        return Holder(fragment, mode, binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return 1
    }

    class Holder(
        val fragment: Fragment,
        private val mode: PostsHeaderMode,
        private val viewBinding: FragmentListPostsHeaderBinding,
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        fun bind() {
            when (mode) {
                PostsHeaderMode.HOME_PAGE -> {
                    viewBinding.postsFilter.root.isVisible = true
                    viewBinding.postsFilterSpace.isVisible = true

                    viewBinding.createPostButton.isVisible = false
                    viewBinding.createPostButtonSpace.isVisible = false

                    viewBinding.createPostButton.setOnClickListener {
                        onCreatePostButtonPressed()
                    }
                }
                PostsHeaderMode.OWN_PROFILE_PAGE -> {
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

enum class PostsHeaderMode {
    HOME_PAGE,
    OWN_PROFILE_PAGE
}