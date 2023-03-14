package com.thesis.sportologia.ui.posts.adapters

import androidx.core.view.isVisible
import androidx.fragment.app.Fragment


class PostsHeaderAdapterProfileOther(
    fragment: Fragment,
    listener: FilterListener
) : PostsHeaderAdapter(fragment, listener) {

    override val renderHeader = {
        binding.postsFilter.root.isVisible = false
        binding.postsFilterSpace.isVisible = false

        binding.createPostButton.isVisible = false
        binding.createPostButtonSpace.isVisible = false
    }

}