package com.thesis.sportologia.views

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.EditTextBasicBinding


typealias OnEditTextBasicActionListener = (OnEditTextBasicAction) -> Unit

enum class OnEditTextBasicAction {
    POSITIVE
}

class EditTextBasicView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: EditTextBasicBinding

    private var listener: OnEditTextBasicActionListener? = null

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
        inflater.inflate(R.layout.edit_text_basic, this, true)
        binding = EditTextBasicBinding.bind(this)
        initializeAttributes(attrs, defStyleAttr, defStyleRes)
    }

    private fun initializeAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.EditTextBasicView, defStyleAttr, defStyleRes
        )

        with(binding) {
            val title = typedArray.getString(R.styleable.EditTextBasicView_editTextTitle)
            binding.title.text = title

            val lines = typedArray.getInteger(R.styleable.EditTextBasicView_lines,0)
            if (lines > 0) {
                textBlock.maxLines = lines
            }

            val limit = typedArray.getInteger(R.styleable.EditTextBasicView_limit,0)
            if (limit > 0) {
                textBlock.filters = arrayOf<InputFilter>(LengthFilter(limit))
            }

            val hint = typedArray.getString(R.styleable.EditTextBasicView_hint)
            textBlock.hint = hint

        }

        typedArray.recycle()
    }

    private fun initListeners() {
        binding.root.setOnClickListener {
            this.listener?.invoke(OnEditTextBasicAction.POSITIVE)
        }
    }

    fun setListener(listener: OnEditTextBasicActionListener?) {
        this.listener = listener
    }

    // SAVE

    override fun onSaveInstanceState(): Parcelable {
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
    }

}