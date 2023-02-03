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
import com.thesis.sportologia.databinding.ItemServiceBinding
import java.net.URI
import kotlin.properties.Delegates


typealias OnItemServiceActionListener = (OnItemServiceAction) -> Unit

enum class OnItemServiceAction {
    STATS_BLOCK,
    ORGANIZER_BLOCK,
    PHOTOS_BLOCK,
    FAVS,
    MORE
}

class ItemServiceView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: ItemServiceBinding

    private var isAddedToFavs by Delegates.notNull<Boolean>()

    private var listeners = mutableListOf<OnItemServiceActionListener?>()

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
        inflater.inflate(R.layout.item_service, this, true)
        binding = ItemServiceBinding.bind(this)
        initAttributes(attrs, defStyleAttr, defStyleRes)
        initListeners()
    }

    private fun initAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.ItemServiceView, defStyleAttr, defStyleRes
        )

        typedArray.recycle()
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

    fun setDescription(text: String) {
        binding.description.text = text
    }

    fun setAvatar(uriImage: URI) {
        Picasso.get()
            .load(uriImage.toString())
            .error(R.drawable.avatar)
            .into(binding.avatar)
    }

    fun setOrganizerName(username: String) {
        binding.userName.text = username
    }

    fun setAcquiredNumber(acquiredNumber: Int) {
        binding.acquiredNumber.text = acquiredNumber.toString()
    }

    fun setReviewsNumber(reviewsNumber: Int) {
        binding.reviewsNumber.text = reviewsNumber.toString()
    }

    fun setRating(rating: String) {
        binding.ratingAverage.text = rating
    }

    // TODO логика преобразования валют в VM
    fun setPrice(price: String, currencyAbbr: String) {
        if (price == "0") {
            binding.price.text = context.getString(R.string.free)
        } else {
            binding.price.text = "$price $currencyAbbr"
        }
    }

    // TODO photos
    fun setPhotos() {
    }

    private fun initListeners() {
        binding.statsBlock.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnItemServiceAction.STATS_BLOCK)
            }
        }

        binding.organizerBlock.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnItemServiceAction.ORGANIZER_BLOCK)
            }
        }

        binding.star.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnItemServiceAction.FAVS)
                toggleFavs()
            }
        }

        binding.photosBlock.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnItemServiceAction.PHOTOS_BLOCK)
            }
        }

        binding.more.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnItemServiceAction.MORE)
            }
        }
    }

    fun setListener(listener: OnItemServiceActionListener?) {
        listeners.add(listener)
    }

    fun removeListener(listener: OnItemServiceActionListener?) {
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