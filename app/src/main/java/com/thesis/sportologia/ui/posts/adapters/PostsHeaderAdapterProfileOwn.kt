package com.thesis.sportologia.ui.posts.adapters

import androidx.core.view.isVisible
import androidx.fragment.app.Fragment


class PostsHeaderAdapterProfileOwn(
    fragment: Fragment,
    listener: FilterListener
) : PostsHeaderAdapter(fragment, listener) {

    override val renderHeader: () -> Unit = {
        binding.postsFilter.root.isVisible = false
        binding.postsFilterSpace.isVisible = false

        binding.createPostButton.isVisible = true
        binding.createPostButtonSpace.isVisible = true

        binding.createPostButton.setOnClickListener {
            onCreatePostButtonPressed()
        }
    }

}