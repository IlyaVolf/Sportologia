package com.thesis.sportologia.ui.views

import android.content.Context

import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ViewPhotosBlockPageBinding
import kotlin.properties.Delegates

class PhotosBlockPageView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: ViewPhotosBlockPageBinding

    private var photosNumber by Delegates.notNull<Int>()

    private var photo1 by Delegates.notNull<Int>()
    private var photo2 by Delegates.notNull<Int>()
    private var photo3 by Delegates.notNull<Int>()
    private var photo4 by Delegates.notNull<Int>()

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
        inflater.inflate(R.layout.view_photos_block_page, this, true)
        binding = ViewPhotosBlockPageBinding.bind(this)
        initAttributes(attrs, defStyleAttr, defStyleRes)
    }

    private fun initAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.PhotosBlockPageView, defStyleAttr, defStyleRes
        )


        val pn = typedArray.getInteger(R.styleable.PhotosBlockPageView_pbp_photosNumber, 0)
        photosNumber = if (pn in 0..10) {
            pn
        } else {
            0
        }

        photo1 = typedArray.getResourceId(R.styleable.PhotosBlockPageView_pbp_photo_1, 0)
        photo2 = typedArray.getResourceId(R.styleable.PhotosBlockPageView_pbp_photo_2, 0)
        photo3 = typedArray.getResourceId(R.styleable.PhotosBlockPageView_pbp_photo_3, 0)
        photo4 = typedArray.getResourceId(R.styleable.PhotosBlockPageView_pbp_photo_4, 0)

        drawPhotos()

        typedArray.recycle()
    }

    private fun drawPhotos() {
        binding.row1.visibility = GONE
        binding.row1.weightSum = 160f

        binding.photo1.visibility = GONE
        binding.photo2.visibility = GONE
        binding.photo3.visibility = GONE
        binding.photo4.visibility = GONE
        binding.space1.visibility = GONE
        binding.space2.visibility = GONE
        binding.space3.visibility = GONE

        when (photosNumber) {
            0 -> {
            }
            1 -> {
                binding.row1.visibility = VISIBLE

                binding.photo1.setImageResource(photo1)
                binding.photo1.visibility = VISIBLE
            }
            2 -> {
                binding.row1.visibility = VISIBLE

                binding.space1.visibility = VISIBLE

                binding.photo1.setImageResource(photo1)
                binding.photo1.visibility = VISIBLE
                binding.photo2.setImageResource(photo2)
                binding.photo2.visibility = VISIBLE
            }
            3 -> {
                binding.row1.visibility = VISIBLE

                binding.space1.visibility = VISIBLE
                binding.space2.visibility = VISIBLE

                binding.photo1.setImageResource(photo1)
                binding.photo1.visibility = VISIBLE
                binding.photo2.setImageResource(photo2)
                binding.photo2.visibility = VISIBLE
                binding.photo3.setImageResource(photo3)
                binding.photo3.visibility = VISIBLE
            }
            4 -> {
                binding.row1.visibility = VISIBLE

                binding.space1.visibility = VISIBLE
                binding.space2.visibility = VISIBLE
                binding.space3.visibility = VISIBLE

                binding.photo1.setImageResource(photo1)
                binding.photo1.visibility = VISIBLE
                binding.photo2.setImageResource(photo2)
                binding.photo2.visibility = VISIBLE
                binding.photo3.setImageResource(photo3)
                binding.photo3.visibility = VISIBLE
                binding.photo4.setImageResource(photo4)
                binding.photo4.visibility = VISIBLE
            }
        }
    }

    fun getPhotoNumber(): Int {
        return photosNumber
    }

    /*override fun onSaveInstanceState(): Parcelable {
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
    }*/

}