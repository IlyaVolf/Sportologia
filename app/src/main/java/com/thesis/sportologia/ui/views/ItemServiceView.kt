package com.thesis.sportologia.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.*
import com.squareup.picasso.Picasso
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ItemServiceBinding
import com.thesis.sportologia.utils.Categories
import com.thesis.sportologia.utils.concatMap
import com.thesis.sportologia.utils.getPriceWithCurrency
import com.thesis.sportologia.utils.setAvatar
import java.net.URI
import kotlin.properties.Delegates


typealias OnItemServiceActionListener = (OnItemServiceAction) -> Unit

enum class OnItemServiceAction {
    INFO_BLOCK,
    STATS_BLOCK,
    ORGANIZER_BLOCK,
    PHOTOS_BLOCK,
    FAVS,
    MORE
}

class ItemServiceView(
    readyBinding: ItemServiceBinding? = null,
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: ItemServiceBinding

    private var isAddedToFavs by Delegates.notNull<Boolean>()
    private var mode = Mode.PREVIEW

    private var listeners = mutableListOf<OnItemServiceActionListener?>()

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
        readyBinding: ItemServiceBinding?,
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

    constructor(readyBinding: ItemServiceBinding?, context: Context, attrs: AttributeSet?) : this(
        readyBinding,
        context,
        attrs,
        0
    )

    constructor(readyBinding: ItemServiceBinding?, context: Context) : this(
        readyBinding,
        context,
        null
    )


    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.item_service, this, true)
        binding = readyBinding ?: ItemServiceBinding.bind(this)
        initAttributes(attrs, defStyleAttr, defStyleRes)
        setUpModeRender()
        initListeners()
    }

    private fun initAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.ItemServiceView, defStyleAttr, defStyleRes
        )

        val isMainPhotoSquareLimited =
            typedArray.getBoolean(R.styleable.ItemServiceView_is_isMainPhotoSquareLimited, false)
        binding.servicePhotosBlock.setMainPhotoSquareLimit(isMainPhotoSquareLimited)

        typedArray.recycle()
    }

    fun setMode(mode: Mode) {
        this.mode = mode
        setUpModeRender()
    }

    private fun setUpModeRender() {
        when (mode) {
            Mode.PREVIEW -> {
                binding.serviceMore.visibility = GONE
                binding.serviceInfo.visibility = VISIBLE
            }
            Mode.DETAILED -> {
                binding.serviceMore.visibility = VISIBLE
                binding.serviceInfo.visibility = GONE
            }
        }
    }

    fun setFavs(isAddedToFavs: Boolean) {
        this.isAddedToFavs = isAddedToFavs

        if (this.isAddedToFavs) {
            binding.serviceStar.setImageResource(R.drawable.icon_star_pressed)
            binding.serviceStar.setColorFilter(context.getColor(R.color.background_inverted))
        } else {
            binding.serviceStar.setImageResource(R.drawable.icon_star_unpressed)
            binding.serviceStar.setColorFilter(context.getColor(R.color.background_inverted))
        }
    }

    // TODO временный метод. Нужно через setFavs после + ответа с сервера
    private fun toggleFavs() {
        isAddedToFavs = !isAddedToFavs

        if (this.isAddedToFavs) {
            binding.serviceStar.setImageResource(R.drawable.icon_star_pressed)
            binding.serviceStar.setColorFilter(context.getColor(R.color.background_inverted))
        } else {
            binding.serviceStar.setImageResource(R.drawable.icon_star_unpressed)
            binding.serviceStar.setColorFilter(context.getColor(R.color.background_inverted))
        }
    }

    fun setServiceName(name: String) {
        binding.serviceName.text = name
    }

    fun setDescription(description: String) {
        binding.serviceDescription.text = description
    }

    fun setAuthorName(username: String) {
        binding.serviceUserName.text = username
    }

    fun setAuthorAvatar(uriImage: String?) {
        setAvatar(uriImage, context, binding.serviceAvatar)
    }

    fun setAcquiredNumber(acquiredNumber: Int) {
        binding.serviceAcquiredNumber.text = acquiredNumber.toString()
    }

    fun setReviewsNumber(reviewsNumber: Int) {
        binding.serviceReviewsNumber.text = reviewsNumber.toString()
    }

    fun setRating(rating: String) {
        binding.serviceRatingAverage.text = rating
    }

    // TODO логика преобразования валют в VM
    fun setPrice(price: Float, currency: String) {
        binding.servicePrice.text = getPriceWithCurrency(context, price, currency)
    }

    fun setCategories(categories: Map<String, Boolean>) {
        val categoriesLocalized = Categories.getLocalizedCategories(context, categories)
        val categoriesString = concatMap(categoriesLocalized, ", ")
        if (categoriesString != "") {
            binding.serviceCategories.text = categoriesString
        } else {
            binding.serviceCategories.text = context.getString(R.string.categories_not_specified)
        }
    }

    fun setPhotos(photosURIs: List<String>?) {
        binding.servicePhotosBlock.uploadPhotos(photosURIs ?: listOf())
        if (photosURIs == null || photosURIs.isEmpty()) {
            binding.serviceTextSpaceBottom.visibility = GONE
        } else {
            binding.serviceTextSpaceBottom.visibility = VISIBLE
        }
    }

    private fun initListeners() {
        binding.serviceInfoBlock.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnItemServiceAction.INFO_BLOCK)
            }
        }

        binding.serviceStatsBlock.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnItemServiceAction.STATS_BLOCK)
            }
        }

        binding.serviceOrganizerBlock.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnItemServiceAction.ORGANIZER_BLOCK)
            }
        }

        binding.serviceStar.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnItemServiceAction.FAVS)
                toggleFavs()
            }
        }

        binding.servicePhotosBlock.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnItemServiceAction.PHOTOS_BLOCK)
            }
        }

        binding.serviceMore.setOnClickListener {
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

    enum class Mode {
        PREVIEW, DETAILED
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