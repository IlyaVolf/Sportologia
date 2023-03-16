package com.thesis.sportologia.ui.views

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ViewEditTextBasicBinding


typealias OnEditTextBasicActionListener = (OnEditTextBasicAction) -> Unit

enum class OnEditTextBasicAction {
    POSITIVE
}

fun inputTypeMap(inputType: String): Int {
    return when (inputType) {
        "textMultiLine" -> InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_CLASS_TEXT
        "number" -> InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_CLASS_NUMBER
        "textSingleLine" -> InputType.TYPE_CLASS_TEXT
        "textEmailAddress" -> InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        "textPassword" -> InputType.TYPE_TEXT_VARIATION_PASSWORD
        else -> InputType.TYPE_CLASS_TEXT
    }
}

class EditTextBasicView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: ViewEditTextBasicBinding

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
        inflater.inflate(R.layout.view_edit_text_basic, this, true)
        binding = ViewEditTextBasicBinding.bind(this)
        initAttributes(attrs, defStyleAttr, defStyleRes)
    }

    private fun initAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.EditTextBasicView, defStyleAttr, defStyleRes
        )

        val title = typedArray.getString(R.styleable.EditTextBasicView_editTextTitle)
        binding.title.text = title

        val lines = typedArray.getInteger(R.styleable.EditTextBasicView_editTextLines, 0)
        if (lines > 0) {
            binding.textBlock.maxLines = lines
        }

        val limit = typedArray.getInteger(R.styleable.EditTextBasicView_editTextLimit, 0)
        if (limit > 0) {
            binding.textBlock.filters = arrayOf<InputFilter>(LengthFilter(limit))
        }

        val hint = typedArray.getString(R.styleable.EditTextBasicView_editTextHint)
        binding.textBlock.hint = hint

        val inputType = typedArray.getString(R.styleable.EditTextBasicView_editTextInputType) ?: "textMultiLine"
        binding.textBlock.inputType = inputTypeMap(inputType)

        typedArray.recycle()
    }

    fun setTitle(title: String) {
        binding.title.text = title
    }

    fun setText(text: String) {
        binding.textBlock.post {
            binding.textBlock.setText(text)
        }
    }

    fun getTitle(title: String): String {
        return binding.title.text.toString()
    }

    fun getText(): String {
        return binding.textBlock.text.toString()
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