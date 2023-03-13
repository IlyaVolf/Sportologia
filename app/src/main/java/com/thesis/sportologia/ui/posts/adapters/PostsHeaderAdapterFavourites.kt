package com.thesis.sportologia.ui.posts.adapters

import androidx.core.view.isVisible
import androidx.fragment.app.Fragment


class PostsHeaderAdapterFavourites(
    fragment: Fragment,
    listener: FilterListener
) : PostsHeaderAdapter(fragment, listener) {

    override val renderHeader: () -> Unit = {
        binding.postsFilter.root.isVisible = true
        binding.postsFilterSpace.isVisible = true

        binding.createPostButton.isVisible = false
        binding.createPostButtonSpace.isVisible = false

        binding.createPostButton.setOnClickListener {
            onCreatePostButtonPressed()
        }

        binding.postsFilter.spinner.initAdapter(filterOptionsList)
        binding.postsFilter.spinner.setListener(postsFilterListener)
    }

}