package com.thesis.sportologia.views

import android.content.Context
import android.media.Image
import android.os.Parcel
import android.os.Parcelable
import android.text.BoringLayout
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.*
import com.squareup.picasso.Picasso
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ItemPostBinding
import java.net.URI
import kotlin.properties.Delegates


typealias OnItemPostActionListener = (OnItemPostAction) -> Unit

enum class OnItemPostAction {
    HEADER_BLOCK,
    PHOTOS_BLOCK,
    LIKE,
    FAVS,
    MORE
}

class ItemPostView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: ItemPostBinding

    private var likesCount by Delegates.notNull<String>()
    private var isLiked by Delegates.notNull<Boolean>()
    private var isAddedToFavs by Delegates.notNull<Boolean>()

    private var listeners = mutableListOf<OnItemPostActionListener?>()

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.item_post, this, true)
        binding = ItemPostBinding.bind(this)
        initAttributes(attrs, defStyleAttr, defStyleRes)
        initListeners()
    }

    private fun initAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.ItemPostView, defStyleAttr, defStyleRes
        )

        val isMainPhotoSquareLimited =
            typedArray.getBoolean(R.styleable.ItemPostView_ip_isMainPhotoSquareLimited, false)
        binding.photosBlock.setMainPhotoSquareLimit(isMainPhotoSquareLimited)

        typedArray.recycle()
    }

    fun setLikes(likesCount: Int, isLiked: Boolean) {
        this.isLiked = isLiked

        if (likesCount < 0) {
            this.likesCount = "0"
            this.isLiked = false
        } else if (likesCount < 1000) {
            this.likesCount = likesCount.toString()
        } else if (likesCount < 1000000) {
            val string = likesCount.toString().substring(0)
            this.likesCount = likesCount.toString().substring(0, string.length - 3) + "K"
        } else {
            val string = likesCount.toString().substring(0)
            this.likesCount = likesCount.toString().substring(0, string.length - 6) + "M"
        }

        binding.likesCount.text = this.likesCount

        if (this.isLiked) {
            binding.like.setColorFilter(context.getColor(R.color.pink))
        } else {
            binding.like.setColorFilter(context.getColor(R.color.element_tertiary))
        }
    }

    // TODO временный метод. Нужно через setLikes после + ответа с сервера
    private fun toggleLike() {
        isLiked = !isLiked

        if (this.isLiked) {
            binding.like.setColorFilter(context.getColor(R.color.pink))
        } else {
            binding.like.setColorFilter(context.getColor(R.color.element_tertiary))
        }
    }

    fun setFavs(isAddedToFavs: Boolean) {
        this.isAddedToFavs = isAddedToFavs

        if (this.isAddedToFavs) {
            binding.star.setImageResource(R.drawable.icon_star_pressed)
            binding.star.setColorFilter(context.getColor(R.color.background_inverted))
        } else {
            binding.star.setImageResource(R.drawable.icon_star_unpressed)
            binding.star.setColorFilter(context.getColor(R.color.background_inverted))
        }
    }

    // TODO временный метод. Нужно через setFavs после + ответа с сервера
    private fun toggleFavs() {
        isAddedToFavs = !isAddedToFavs

        if (this.isAddedToFavs) {
            binding.star.setImageResource(R.drawable.icon_star_pressed)
            binding.star.setColorFilter(context.getColor(R.color.background_inverted))
        } else {
            binding.star.setImageResource(R.drawable.icon_star_unpressed)
            binding.star.setColorFilter(context.getColor(R.color.background_inverted))
        }
    }

    fun setText(text: String) {
        binding.text.text = text
    }

    fun setAvatar(uriImage: URI) {
        Picasso.get()
            .load(uriImage.toString())
            .error(R.drawable.avatar)
            .into(binding.avatar)
    }

    fun setUsername(username: String) {
        binding.userName.text = username
    }

    // TODO parser
    fun setDate(date: String) {
        binding.date.text = date
    }

    // TODO photos
    fun setPhotos() {
    }

    private fun initListeners() {
        binding.headerBlock.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnItemPostAction.HEADER_BLOCK)
            }
        }

        binding.text.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnItemPostAction.HEADER_BLOCK)
            }
        }

        binding.like.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnItemPostAction.LIKE)
                toggleLike()
            }
        }

        binding.star.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnItemPostAction.FAVS)
                toggleFavs()
            }
        }

        binding.photosBlock.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnItemPostAction.PHOTOS_BLOCK)
            }
        }

        binding.more.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnItemPostAction.MORE)
            }
        }
    }

    fun setListener(listener: OnItemPostActionListener?) {
        listeners.add(listener)
    }

    fun removeListener(listener: OnItemPostActionListener?) {
        listeners.remove(listener)
    }

    // SAVE

    /*override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()!!
        val savedState = SavedState(superState)
        savedState.enteredText = binding.textBlock.text.toString()
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)

        val enteredText = savedState.enteredText

        Log.d("BUGFIX", "HELLO")

        binding.textBlock.post {
            binding.textBlock.setText(enteredText)
        }
    }

    class SavedState : BaseSavedState {

        var enteredText: String? = null

        constructor(superState: Parcelable) : super(superState)

        constructor(parcel: Parcel) : super(parcel) {
            enteredText = parcel.readString()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeString(enteredText)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel): SavedState {
                    return SavedState(source)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return Array(size) { null }
                }
            }
        }
    }*/

}