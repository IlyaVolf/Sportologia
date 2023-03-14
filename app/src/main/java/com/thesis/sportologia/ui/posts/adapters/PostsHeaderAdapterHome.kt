package com.thesis.sportologia.ui.posts.adapters

import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.thesis.sportologia.ui.posts.ListPostsViewModel


class PostsHeaderAdapterHome(
    fragment: Fragment,
    listener: FilterListener,
    athTorgF: Boolean?,
) : PostsHeaderAdapter(fragment, listener) {

    override val renderHeader = {
        binding.postsFilter.root.isVisible = true
        binding.postsFilterSpace.isVisible = true

        binding.createPostButton.isVisible = false
        binding.createPostButtonSpace.isVisible = false

        binding.createPostButton.setOnClickListener {
            onCreatePostButtonPressed()
        }

        binding.postsFilter.spinner.initAdapter(filterOptionsList, getFilterValue(athTorgF))
        binding.postsFilter.spinner.setListener(postsFilterListener)
    }

}