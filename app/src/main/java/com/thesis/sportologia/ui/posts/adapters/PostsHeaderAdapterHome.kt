package com.thesis.sportologia.ui.posts.adapters

import androidx.fragment.app.Fragment

class PostsHeaderAdapterHome(
    fragment: Fragment,
    listener: FilterListener,
) : PostsHeaderAdapter(fragment, listener) {

    override val renderHeader = {
        enablePostsFilter()
    }

}