package com.thesis.sportologia.ui.views

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ViewSelectStarsBinding

typealias OnSelectStarsActionListener = (OnSelectStarsAction) -> Unit

enum class OnSelectStarsAction {
    FIRST,
    SECOND,
    THIRD,
    FOURTH,
    FIFTH
}

class SelectStarsView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: ViewSelectStarsBinding

    private var grade = 0

    private var listeners = mutableListOf<OnSelectStarsActionListener?>()

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
        inflater.inflate(R.layout.view_select_stars, this, true)
        binding = ViewSelectStarsBinding.bind(this)
        initAttributes(attrs, defStyleAttr, defStyleRes)
        visualizeStars()
        initListeners()
    }

    private fun initAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.SelectStarsView, defStyleAttr, defStyleRes
        )

        grade = typedArray.getInteger(R.styleable.SelectStarsView_ss_grade, 0)

        typedArray.recycle()
    }

    private fun visualizeStars() {
        val stars =
            mutableListOf(binding.star1, binding.star2, binding.star3, binding.star4, binding.star5)

        for (i in 0 until grade) {
            stars[i].setColorFilter(context.getColor(R.color.background_inverted))
        }

        for (i in grade until stars.size) {
            stars[i].setColorFilter(context.getColor(R.color.element_tertiary))
        }
    }

    fun setGrade(grade: Int) {
        this.grade = grade
    }

    fun getGrade(): Int {
        return grade
    }

    private fun initListeners() {
        binding.star1.setOnClickListener {
            listeners.forEach { listener ->
                grade = 1
                visualizeStars()
                listener?.invoke(OnSelectStarsAction.FIRST)
            }
        }
        binding.star2.setOnClickListener {
            listeners.forEach { listener ->
                grade = 2
                visualizeStars()
                listener?.invoke(OnSelectStarsAction.SECOND)
            }
        }
        binding.star3.setOnClickListener {
            listeners.forEach { listener ->
                grade = 3
                visualizeStars()
                listener?.invoke(OnSelectStarsAction.THIRD)
            }
        }
        binding.star4.setOnClickListener {
            listeners.forEach { listener ->
                grade = 4
                visualizeStars()
                listener?.invoke(OnSelectStarsAction.FOURTH)
            }
        }
        binding.star5.setOnClickListener {
            listeners.forEach { listener ->
                grade = 5
                visualizeStars()
                listener?.invoke(OnSelectStarsAction.FIFTH)
            }
        }
    }

    fun setListener(listener: OnSelectStarsActionListener?) {
        listeners.add(listener)
    }

    fun removeListener(listener: OnSelectStarsActionListener?) {
        listeners.remove(listener)
    }

    // SAVE

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()!!
        val savedState = SavedState(superState)
        savedState.savedGrade = grade
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)

        grade = savedState.savedGrade ?: 0
        visualizeStars()
    }

    class SavedState : BaseSavedState {

        var savedGrade: Int? = null

        constructor(superState: Parcelable) : super(superState)

        constructor(parcel: Parcel) : super(parcel) {
            savedGrade = parcel.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(savedGrade ?: 0)
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