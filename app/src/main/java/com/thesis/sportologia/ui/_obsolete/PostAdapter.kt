package com.thesis.sportologia.ui._obsolete

/**class PostAdapter(
    private val OnItemPostActionListener: OnItemPostActionListener,
    private val onSpinnerMoreActionListener: OnSpinnerMoreActionListener,
) : BaseAdapter<BaseViewHolder<Post>, Post>() {

    override fun takeViewHolder(parent: ViewGroup): BaseViewHolder<Post> {
        val viewBinding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        val itemPost = ItemPostView(viewBinding, parent.context)

        return PostViewHolder(OnItemPostActionListener, onSpinnerMoreActionListener, itemPost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Post> =
        takeViewHolder(parent)
}*/