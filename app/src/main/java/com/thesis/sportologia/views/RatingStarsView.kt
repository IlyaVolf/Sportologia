package com.thesis.sportologia.views

import android.content.Context
import android.content.res.ColorStateList
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ViewRatingStarsBinding

class RatingStarsView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: ViewRatingStarsBinding

    private var rating = 5

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
        inflater.inflate(R.layout.view_rating_stars, this, true)
        binding = ViewRatingStarsBinding.bind(this)
        initAttributes(attrs, defStyleAttr, defStyleRes)
    }

    private fun initAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.RatingStarsView, defStyleAttr, defStyleRes
        )

        rating = typedArray.getInteger(R.styleable.RatingStarsView_rating, 5)

        drawStars()

        typedArray.recycle()
    }

    private fun drawStars() {
        when (rating) {
            1 -> {
                binding.star1.setColorFilter(context.getColor(R.color.background_inverted))
                binding.star2.setColorFilter(context.getColor(R.color.element_tertiary))
                binding.star3.setColorFilter(context.getColor(R.color.element_tertiary))
                binding.star4.setColorFilter(context.getColor(R.color.element_tertiary))
                binding.star5.setColorFilter(context.getColor(R.color.element_tertiary))
            }
            2 -> {
                binding.star1.setColorFilter(context.getColor(R.color.background_inverted))
                binding.star2.setColorFilter(context.getColor(R.color.background_inverted))
                binding.star3.setColorFilter(context.getColor(R.color.element_tertiary))
                binding.star4.setColorFilter(context.getColor(R.color.element_tertiary))
                binding.star5.setColorFilter(context.getColor(R.color.element_tertiary))
            }
            3 -> {
                binding.star1.setColorFilter(context.getColor(R.color.background_inverted))
                binding.star2.setColorFilter(context.getColor(R.color.background_inverted))
                binding.star3.setColorFilter(context.getColor(R.color.background_inverted))
                binding.star4.setColorFilter(context.getColor(R.color.element_tertiary))
                binding.star5.setColorFilter(context.getColor(R.color.element_tertiary))
            }
            4 -> {
                binding.star1.setColorFilter(context.getColor(R.color.background_inverted))
                binding.star2.setColorFilter(context.getColor(R.color.background_inverted))
                binding.star3.setColorFilter(context.getColor(R.color.background_inverted))
                binding.star4.setColorFilter(context.getColor(R.color.background_inverted))
                binding.star5.setColorFilter(context.getColor(R.color.element_tertiary))
            }
            5 -> {
                binding.star1.setColorFilter(context.getColor(R.color.background_inverted))
                binding.star2.setColorFilter(context.getColor(R.color.background_inverted))
                binding.star3.setColorFilter(context.getColor(R.color.background_inverted))
                binding.star4.setColorFilter(context.getColor(R.color.background_inverted))
                binding.star5.setColorFilter(context.getColor(R.color.background_inverted))
            }
        }
    }

    fun setRating(rating: Int) {
        if (rating in 1..5) {
            this.rating = rating
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()!!
        val savedState = SavedState(superState)
        savedState.savedRating = rating
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)

        rating = savedState.savedRating ?: 5

        drawStars()
    }

    class SavedState : BaseSavedState {

        var savedRating: Int? = null

        constructor(superState: Parcelable) : super(superState)

        constructor(parcel: Parcel) : super(parcel) {
            savedRating = parcel.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(savedRating!!)
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
    }

}