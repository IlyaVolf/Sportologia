package com.thesis.sportologia.ui.views

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
import com.thesis.sportologia.databinding.ItemEventBinding
import com.thesis.sportologia.databinding.ItemPostBinding
import com.thesis.sportologia.utils.convertPrice
import com.thesis.sportologia.utils.getCurrencyAbbreviation
import com.thesis.sportologia.utils.parseDateRange
import com.thesis.sportologia.utils.setAvatar
import java.net.URI
import java.util.Calendar
import kotlin.properties.Delegates


typealias OnItemEventActionListener = (OnItemEventAction) -> Unit

enum class OnItemEventAction {
    ORGANIZER_BLOCK,
    ADDRESS_BLOCK,
    PHOTOS_BLOCK,
    LIKE,
    FAVS,
    MORE
}

class ItemEventView(
    readyBinding: ItemEventBinding? = null,
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: ItemEventBinding

    private var likesCount by Delegates.notNull<String>()
    private var isLiked by Delegates.notNull<Boolean>()
    private var isAddedToFavs by Delegates.notNull<Boolean>()

    private var listeners = mutableListOf<OnItemEventActionListener?>()

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        null,
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)


    constructor(
        readyBinding: ItemEventBinding?,
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : this(
        readyBinding,
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(readyBinding: ItemEventBinding?, context: Context, attrs: AttributeSet?) : this(
        readyBinding,
        context,
        attrs,
        0
    )

    constructor(readyBinding: ItemEventBinding?, context: Context) : this(
        readyBinding,
        context,
        null
    )


    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.item_event, this, true)
        binding = readyBinding ?: ItemEventBinding.bind(this)
        initAttributes(attrs, defStyleAttr, defStyleRes)
        initListeners()
    }

    private fun initAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.ItemEventView, defStyleAttr, defStyleRes
        )

        val isMainPhotoSquareLimited =
            typedArray.getBoolean(R.styleable.ItemEventView_ie_isMainPhotoSquareLimited, false)
        binding.eventPhotosBlock.setMainPhotoSquareLimit(isMainPhotoSquareLimited)

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

        binding.eventLikesCount.text = this.likesCount

        if (this.isLiked) {
            binding.eventLike.setColorFilter(context.getColor(R.color.pink))
        } else {
            binding.eventLike.setColorFilter(context.getColor(R.color.element_tertiary))
        }
    }

    // TODO временный метод. Нужно через setLikes после + ответа с сервера
    private fun toggleLike() {
        isLiked = !isLiked

        if (this.isLiked) {
            binding.eventLike.setColorFilter(context.getColor(R.color.pink))
        } else {
            binding.eventLike.setColorFilter(context.getColor(R.color.element_tertiary))
        }
    }

    fun setFavs(isAddedToFavs: Boolean) {
        this.isAddedToFavs = isAddedToFavs

        if (this.isAddedToFavs) {
            binding.eventStar.setImageResource(R.drawable.icon_star_pressed)
            binding.eventStar.setColorFilter(context.getColor(R.color.background_inverted))
        } else {
            binding.eventStar.setImageResource(R.drawable.icon_star_unpressed)
            binding.eventStar.setColorFilter(context.getColor(R.color.background_inverted))
        }
    }

    // TODO временный метод. Нужно через setFavs после + ответа с сервера
    private fun toggleFavs() {
        isAddedToFavs = !isAddedToFavs

        if (this.isAddedToFavs) {
            binding.eventStar.setImageResource(R.drawable.icon_star_pressed)
            binding.eventStar.setColorFilter(context.getColor(R.color.background_inverted))
        } else {
            binding.eventStar.setImageResource(R.drawable.icon_star_unpressed)
            binding.eventStar.setColorFilter(context.getColor(R.color.background_inverted))
        }
    }

    fun setEventName(text: String) {
        binding.eventName.text = text
    }

    fun setDescription(text: String) {
        binding.eventDescription.text = text
    }

    fun setOrganizerAvatar(uri: String?) {
        setAvatar(uri, context, binding.eventAvatar)
    }

    fun setOrganizerName(username: String) {
        binding.eventUserName.text = username
    }

    fun setDate(dateFromMillis: Long, dateToMillis: Long) {
        val dateNow = Calendar.getInstance()
        val dateFrom = Calendar.getInstance()
        dateFrom.timeInMillis = dateFromMillis
        val dateTo = Calendar.getInstance()
        dateTo.timeInMillis = dateToMillis

        if (dateNow.timeInMillis > dateToMillis) {
            binding.eventEndedLabel.visibility = VISIBLE
        } else {
            binding.eventEndedLabel.visibility = GONE
        }

        val parsedDates = parseDateRange(dateFrom, dateTo)
        binding.eventDate.text = parsedDates
    }

    fun setAddress(address: String) {
        binding.eventAddress.text = address
    }

    // TODO логика преобразования валют в VM
    fun setPrice(price: Float, currency: String) {
        binding.eventPrice.text = convertPrice(context, price, currency)
    }

    // TODO photos
    fun setPhotos() {
    }

    private fun initListeners() {
        binding.eventOrganizerBlock.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnItemEventAction.ORGANIZER_BLOCK)
            }
        }

        binding.eventAddressBlock.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnItemEventAction.ADDRESS_BLOCK)
            }
        }

        binding.eventLike.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnItemEventAction.LIKE)
                toggleLike()
            }
        }

        binding.eventStar.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnItemEventAction.FAVS)
                toggleFavs()
            }
        }

        binding.eventPhotosBlock.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnItemEventAction.PHOTOS_BLOCK)
            }
        }

        binding.eventMore.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnItemEventAction.MORE)
            }
        }
    }

    fun setListener(listener: OnItemEventActionListener?) {
        listeners.add(listener)
    }

    fun removeListener(listener: OnItemEventActionListener?) {
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