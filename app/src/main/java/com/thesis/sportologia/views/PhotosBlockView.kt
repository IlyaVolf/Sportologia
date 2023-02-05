package com.thesis.sportologia.views

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ViewPhotosBlockBinding
import kotlin.properties.Delegates

class PhotosBlockView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: ViewPhotosBlockBinding

    private var isMainPhotoSquareLimited by Delegates.notNull<Boolean>()
    private var photosNumber by Delegates.notNull<Int>()

    private var photo1 by Delegates.notNull<Int>()
    private var photo2 by Delegates.notNull<Int>()
    private var photo3 by Delegates.notNull<Int>()
    private var photo4 by Delegates.notNull<Int>()
    private var photo5 by Delegates.notNull<Int>()
    private var photo6 by Delegates.notNull<Int>()
    private var photo7 by Delegates.notNull<Int>()
    private var photo8 by Delegates.notNull<Int>()
    private var photo9 by Delegates.notNull<Int>()
    private var photo10 by Delegates.notNull<Int>()

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
        inflater.inflate(R.layout.view_photos_block, this, true)
        binding = ViewPhotosBlockBinding.bind(this)
        initAttributes(attrs, defStyleAttr, defStyleRes)
    }

    private fun initAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.PhotosBlockView, defStyleAttr, defStyleRes
        )

        isMainPhotoSquareLimited =
            typedArray.getBoolean(R.styleable.PhotosBlockView_pb_isMainPhotoSquareLimited, false)
        val pn = typedArray.getInteger(R.styleable.PhotosBlockView_pb_photosNumber, 0)
        photosNumber = if (pn in 0..10) {
            pn
        } else {
            0
        }

        photo1 = typedArray.getResourceId(R.styleable.PhotosBlockView_pb_photo_1, 0)
        photo2 = typedArray.getResourceId(R.styleable.PhotosBlockView_pb_photo_2, 0)
        photo3 = typedArray.getResourceId(R.styleable.PhotosBlockView_pb_photo_3, 0)
        photo4 = typedArray.getResourceId(R.styleable.PhotosBlockView_pb_photo_4, 0)
        photo5 = typedArray.getResourceId(R.styleable.PhotosBlockView_pb_photo_5, 0)
        photo6 = typedArray.getResourceId(R.styleable.PhotosBlockView_pb_photo_6, 0)
        photo7 = typedArray.getResourceId(R.styleable.PhotosBlockView_pb_photo_7, 0)
        photo8 = typedArray.getResourceId(R.styleable.PhotosBlockView_pb_photo_8, 0)
        photo9 = typedArray.getResourceId(R.styleable.PhotosBlockView_pb_photo_9, 0)
        photo10 = typedArray.getResourceId(R.styleable.PhotosBlockView_pb_photo_10, 0)

        drawPhotos()

        typedArray.recycle()
    }

    fun setMainPhotoSquareLimit(flag: Boolean) {
        isMainPhotoSquareLimited = flag
    }


    private fun drawPhotos() {
        binding.row1.visibility = GONE
        binding.row1.weightSum = 120f
        binding.row1.layoutParams.height =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120f, resources.displayMetrics)
                .toInt()
        binding.row2.visibility = GONE
        binding.row2.weightSum = 120f
        binding.row2.layoutParams.height =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120f, resources.displayMetrics)
                .toInt()
        binding.row3.visibility = GONE
        binding.row3.weightSum = 120f
        binding.row3.layoutParams.height =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120f, resources.displayMetrics)
                .toInt()
        binding.photo1Square.visibility = GONE
        binding.photo1.visibility = GONE
        binding.photo2.visibility = GONE
        binding.photo3.visibility = GONE
        binding.photo4.visibility = GONE
        binding.photo5.visibility = GONE
        binding.photo6.visibility = GONE
        binding.photo7.visibility = GONE
        binding.photo8.visibility = GONE
        binding.photo9.visibility = GONE
        binding.photo10.visibility = GONE
        binding.space1.visibility = GONE
        binding.space11.visibility = GONE
        binding.space12.visibility = GONE
        binding.space21.visibility = GONE
        binding.space22.visibility = GONE
        binding.space31.visibility = GONE
        binding.space32.visibility = GONE
        binding.space2.visibility = GONE
        binding.space3.visibility = GONE

        val bindingPhoto1 = if (isMainPhotoSquareLimited) binding.photo1Square else binding.photo1

        when (photosNumber) {
            0 -> {
            }
            1 -> {
                bindingPhoto1.setImageResource(photo1)
                bindingPhoto1.visibility = VISIBLE
            }
            2 -> {
                binding.row1.visibility = VISIBLE
                binding.row1.weightSum = 40f
                binding.row1.layoutParams.height =
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        160f,
                        resources.displayMetrics
                    ).toInt()

                binding.space1.visibility = VISIBLE

                bindingPhoto1.visibility = VISIBLE
                bindingPhoto1.setImageResource(photo1)
                binding.photo2.visibility = VISIBLE
                binding.photo2.setImageResource(photo2)
            }
            3 -> {
                binding.row1.visibility = VISIBLE
                binding.row1.weightSum = 80f

                binding.space1.visibility = VISIBLE

                binding.space11.visibility = VISIBLE

                bindingPhoto1.setImageResource(photo1)
                bindingPhoto1.visibility = VISIBLE
                binding.photo2.setImageResource(photo2)
                binding.photo2.visibility = VISIBLE
                binding.photo3.setImageResource(photo3)
                binding.photo3.visibility = VISIBLE
            }
            4 -> {
                binding.row1.visibility = VISIBLE

                binding.space1.visibility = VISIBLE
                binding.space11.visibility = VISIBLE
                binding.space12.visibility = VISIBLE

                bindingPhoto1.setImageResource(photo1)
                bindingPhoto1.visibility = VISIBLE
                binding.photo2.setImageResource(photo2)
                binding.photo2.visibility = VISIBLE
                binding.photo3.setImageResource(photo3)
                binding.photo3.visibility = VISIBLE
                binding.photo4.setImageResource(photo4)
                binding.photo4.visibility = VISIBLE
            }
            5 -> {
                binding.row1.visibility = VISIBLE
                binding.row1.weightSum = 80f
                binding.row1.layoutParams.height =
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        100f,
                        resources.displayMetrics
                    ).toInt()

                binding.row2.visibility = VISIBLE
                binding.row2.weightSum = 80f
                binding.row2.layoutParams.height =
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        100f,
                        resources.displayMetrics
                    ).toInt()

                binding.space1.visibility = VISIBLE
                binding.space11.visibility = VISIBLE

                binding.space2.visibility = VISIBLE
                binding.space21.visibility = VISIBLE

                bindingPhoto1.setImageResource(photo1)
                bindingPhoto1.visibility = VISIBLE
                binding.photo2.setImageResource(photo2)
                binding.photo2.visibility = VISIBLE
                binding.photo3.setImageResource(photo3)
                binding.photo3.visibility = VISIBLE
                binding.photo5.setImageResource(photo4)
                binding.photo5.visibility = VISIBLE
                binding.photo6.setImageResource(photo5)
                binding.photo6.visibility = VISIBLE
            }
            6 -> {
                binding.row1.visibility = VISIBLE
                binding.row1.weightSum = 80f
                binding.row1.layoutParams.height =
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        100f,
                        resources.displayMetrics
                    ).toInt()

                binding.row2.visibility = VISIBLE
                binding.row2.layoutParams.height =
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        100f,
                        resources.displayMetrics
                    ).toInt()

                binding.space1.visibility = VISIBLE
                binding.space11.visibility = VISIBLE

                binding.space2.visibility = VISIBLE
                binding.space21.visibility = VISIBLE
                binding.space22.visibility = VISIBLE

                bindingPhoto1.setImageResource(photo1)
                bindingPhoto1.visibility = VISIBLE
                binding.photo2.setImageResource(photo2)
                binding.photo2.visibility = VISIBLE
                binding.photo3.setImageResource(photo3)
                binding.photo3.visibility = VISIBLE
                binding.photo5.setImageResource(photo4)
                binding.photo5.visibility = VISIBLE
                binding.photo6.setImageResource(photo5)
                binding.photo6.visibility = VISIBLE
                binding.photo7.setImageResource(photo6)
                binding.photo7.visibility = VISIBLE
            }
            7 -> {
                binding.row1.visibility = VISIBLE
                binding.row1.layoutParams.height =
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        100f,
                        resources.displayMetrics
                    ).toInt()

                binding.row2.visibility = VISIBLE
                binding.row2.layoutParams.height =
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        100f,
                        resources.displayMetrics
                    ).toInt()

                binding.space1.visibility = VISIBLE
                binding.space11.visibility = VISIBLE
                binding.space12.visibility = VISIBLE

                binding.space2.visibility = VISIBLE
                binding.space21.visibility = VISIBLE
                binding.space22.visibility = VISIBLE

                bindingPhoto1.setImageResource(photo1)
                bindingPhoto1.visibility = VISIBLE
                binding.photo2.setImageResource(photo2)
                binding.photo2.visibility = VISIBLE
                binding.photo3.setImageResource(photo3)
                binding.photo3.visibility = VISIBLE
                binding.photo4.setImageResource(photo4)
                binding.photo4.visibility = VISIBLE
                binding.photo5.setImageResource(photo5)
                binding.photo5.visibility = VISIBLE
                binding.photo6.setImageResource(photo6)
                binding.photo6.visibility = VISIBLE
                binding.photo7.setImageResource(photo7)
                binding.photo7.visibility = VISIBLE
            }
            8 -> {
                binding.row1.visibility = VISIBLE
                binding.row1.weightSum = 80f
                binding.row1.layoutParams.height =
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        100f,
                        resources.displayMetrics
                    ).toInt()

                binding.row2.visibility = VISIBLE
                binding.row2.weightSum = 80f
                binding.row2.layoutParams.height =
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        100f,
                        resources.displayMetrics
                    ).toInt()

                binding.row3.visibility = VISIBLE
                binding.row3.layoutParams.height =
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        100f,
                        resources.displayMetrics
                    ).toInt()

                binding.space1.visibility = VISIBLE
                binding.space11.visibility = VISIBLE

                binding.space2.visibility = VISIBLE
                binding.space21.visibility = VISIBLE

                binding.space3.visibility = VISIBLE
                binding.space31.visibility = VISIBLE
                binding.space32.visibility = VISIBLE

                bindingPhoto1.setImageResource(photo1)
                bindingPhoto1.visibility = VISIBLE
                binding.photo2.setImageResource(photo2)
                binding.photo2.visibility = VISIBLE
                binding.photo3.setImageResource(photo3)
                binding.photo3.visibility = VISIBLE
                binding.photo5.setImageResource(photo4)
                binding.photo5.visibility = VISIBLE
                binding.photo6.setImageResource(photo5)
                binding.photo6.visibility = VISIBLE
                binding.photo8.setImageResource(photo6)
                binding.photo8.visibility = VISIBLE
                binding.photo9.setImageResource(photo7)
                binding.photo9.visibility = VISIBLE
                binding.photo10.setImageResource(photo8)
                binding.photo10.visibility = VISIBLE
            }
            9 -> {
                binding.row1.visibility = VISIBLE
                binding.row1.weightSum = 80f
                binding.row1.layoutParams.height =
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        100f,
                        resources.displayMetrics
                    ).toInt()

                binding.row2.visibility = VISIBLE
                binding.row2.layoutParams.height =
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        100f,
                        resources.displayMetrics
                    ).toInt()

                binding.row3.visibility = VISIBLE
                binding.row3.layoutParams.height =
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        100f,
                        resources.displayMetrics
                    ).toInt()

                binding.space1.visibility = VISIBLE
                binding.space11.visibility = VISIBLE

                binding.space2.visibility = VISIBLE
                binding.space21.visibility = VISIBLE
                binding.space22.visibility = VISIBLE

                binding.space3.visibility = VISIBLE
                binding.space31.visibility = VISIBLE
                binding.space32.visibility = VISIBLE

                bindingPhoto1.setImageResource(photo1)
                bindingPhoto1.visibility = VISIBLE
                binding.photo2.setImageResource(photo2)
                binding.photo2.visibility = VISIBLE
                binding.photo3.setImageResource(photo3)
                binding.photo3.visibility = VISIBLE
                binding.photo5.setImageResource(photo4)
                binding.photo5.visibility = VISIBLE
                binding.photo6.setImageResource(photo5)
                binding.photo6.visibility = VISIBLE
                binding.photo7.setImageResource(photo6)
                binding.photo7.visibility = VISIBLE
                binding.photo8.setImageResource(photo7)
                binding.photo8.visibility = VISIBLE
                binding.photo9.setImageResource(photo8)
                binding.photo9.visibility = VISIBLE
                binding.photo10.setImageResource(photo9)
                binding.photo10.visibility = VISIBLE
            }
            10 -> {
                binding.row1.visibility = VISIBLE
                binding.row1.layoutParams.height =
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        100f,
                        resources.displayMetrics
                    ).toInt()

                binding.row2.visibility = VISIBLE
                binding.row2.layoutParams.height =
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        100f,
                        resources.displayMetrics
                    ).toInt()

                binding.row3.visibility = VISIBLE
                binding.row3.layoutParams.height =
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        100f,
                        resources.displayMetrics
                    ).toInt()

                binding.space1.visibility = VISIBLE
                binding.space11.visibility = VISIBLE
                binding.space12.visibility = VISIBLE

                binding.space2.visibility = VISIBLE
                binding.space21.visibility = VISIBLE
                binding.space22.visibility = VISIBLE

                binding.space3.visibility = VISIBLE
                binding.space31.visibility = VISIBLE
                binding.space32.visibility = VISIBLE

                bindingPhoto1.setImageResource(photo1)
                bindingPhoto1.visibility = VISIBLE
                binding.photo2.setImageResource(photo2)
                binding.photo2.visibility = VISIBLE
                binding.photo3.setImageResource(photo3)
                binding.photo3.visibility = VISIBLE
                binding.photo4.setImageResource(photo4)
                binding.photo4.visibility = VISIBLE
                binding.photo5.setImageResource(photo5)
                binding.photo5.visibility = VISIBLE
                binding.photo5.setImageResource(photo5)
                binding.photo5.visibility = VISIBLE
                binding.photo6.setImageResource(photo6)
                binding.photo6.visibility = VISIBLE
                binding.photo7.setImageResource(photo7)
                binding.photo7.visibility = VISIBLE
                binding.photo8.setImageResource(photo8)
                binding.photo8.visibility = VISIBLE
                binding.photo9.setImageResource(photo9)
                binding.photo9.visibility = VISIBLE
                binding.photo10.setImageResource(photo10)
                binding.photo10.visibility = VISIBLE
            }

        }
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