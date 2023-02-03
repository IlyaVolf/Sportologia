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
import com.thesis.sportologia.databinding.ItemReviewBinding
import java.net.URI
import kotlin.properties.Delegates


typealias OnItemReviewActionListener = (OnItemReviewAction) -> Unit

enum class OnItemReviewAction {
    AUTHOR_BLOCK,
}

class ItemReviewView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: ItemReviewBinding

    private var listeners = mutableListOf<OnItemReviewActionListener?>()

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
        inflater.inflate(R.layout.item_review, this, true)
        binding = ItemReviewBinding.bind(this)
        initAttributes(attrs, defStyleAttr, defStyleRes)
        initListeners()
    }

    private fun initAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.ItemReviewView, defStyleAttr, defStyleRes
        )

        typedArray.recycle()
    }

    fun setRating(rating: Int) {
        binding.rating.setRating(rating)
    }

    fun setTitle(text: String) {
        binding.title.text = text
    }

    fun setDescription(text: String) {
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
        binding.text.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnItemReviewAction.AUTHOR_BLOCK)
            }
        }
    }

    fun setListener(listener: OnItemReviewActionListener?) {
        listeners.add(listener)
    }

    fun removeListener(listener: OnItemReviewActionListener?) {
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