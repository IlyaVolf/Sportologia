package com.thesis.sportologia.ui.posts.adapters

import androidx.fragment.app.Fragment

class PostsHeaderAdapterProfileOwn(
    fragment: Fragment,
    listener: FilterListener,
) : PostsHeaderAdapter(fragment, listener) {

    override val renderHeader = {
        enableCreatePostButton()
    }

}