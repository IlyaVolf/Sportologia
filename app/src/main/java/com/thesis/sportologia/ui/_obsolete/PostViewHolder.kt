package com.thesis.sportologia.ui._obsolete

/**class PostViewHolder(
    private val onItemPostActionListener: OnItemPostActionListener,
    private val onSpinnerMoreActionListener: OnSpinnerMoreActionListener,
    private val view: ItemPostView,
) : BaseViewHolder<Post>(view.getBinding()) {

    override fun bindItem(item: Post) {

        val actionsMore = if (item.authorId == CurrentAccount().id) {
            arrayListOf("Редактировать", "Удалить")
        } else {
            arrayListOf("Пожаловаться")
        }

        resetView()

        view.setText(item.text)
        view.setUsername(item.authorName)
        view.setAuthorAvatar(item.profilePictureUrl)
        view.setDate(item.postedDate)
        view.setLikes(item.likesCount, item.isLiked)
        view.setPhotos()

       // view.getBinding().more.setListener { onSpinnerMoreActionListener }
       // view.getBinding().more.initAdapter(actionsMore)
    }

    private fun resetView() {
    }
}

enum class ActionsMore(val action: String) {
    EDIT("Редактировать"),
    DELETE("Удалить"),
    REPORT( "Пожаловаться"),
}*/