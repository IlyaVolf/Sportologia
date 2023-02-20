package com.thesis.sportologia.ui.views

import android.app.AlertDialog
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ViewMultiChoiceBasicBinding


typealias OnMultiChoiceBasicActionListener = (OnMultiChoiceBasicAction) -> Unit

enum class OnMultiChoiceBasicAction {
    POSITIVE
}

class MultiChoiceBasicView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: ViewMultiChoiceBasicBinding

    private var listener: OnMultiChoiceBasicActionListener? = null

    private lateinit var data: Array<String>
    private lateinit var checkedData: BooleanArray

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
        inflater.inflate(R.layout.view_multi_choice_basic, this, true)
        binding = ViewMultiChoiceBasicBinding.bind(this)
        initAttributes(attrs, defStyleAttr, defStyleRes)
    }

    private fun initAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.MultiChoiceBasicView, defStyleAttr, defStyleRes
        )

        with(binding) {
            val title = typedArray.getString(R.styleable.MultiChoiceBasicView_multiChoiceTitle)
            binding.title.text = title

            val hint = typedArray.getString(R.styleable.MultiChoiceBasicView_multiChoiceHint)
            textBlock.hint = hint

        }

        typedArray.recycle()
    }

    fun initMultiChoiceList(list: List<String>, hint: String) {
        data = list.toTypedArray()
        checkedData = BooleanArray(data.size) { false }

        binding.textBlock.setOnClickListener {
            val localData = data.clone()
            val localCheckedData = checkedData.clone()

            val builder = AlertDialog.Builder(context, R.style.DialogStyleBasic)
            // Boolean array for initial selected items

            builder.setTitle(hint)
            builder.setMultiChoiceItems(localData, localCheckedData) { dialog, which, isChecked ->
                // Update the current focused item's checked status
                localCheckedData[which] = isChecked
                // Get the current focused item
                val currentItem = localData[which]
                // Notify the current action
            }
            // Set the positive/yes button click listener
            builder.setPositiveButton(context.getString(R.string.action_ok)) { dialog, which ->
                // Do something when click positive button
                //binding.root = "Your preferred colors..... \n"
                checkedData = localCheckedData.clone()
                updateText()
            }
            // Set the neutral/cancel button click listener
            builder.setNeutralButton(R.string.action_cancel) { dialog, which ->
                // Do something when click the neutral button
                updateText()
            }
            val dialog = builder.create()

            // Display the alert dialog on interface
            dialog.show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(context.getColor(R.color.purple_medium))
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                .setTextColor(context.getColor(R.color.purple_medium))
        }
    }

    fun getCheckedData(): List<DataExtended> {
        val list = mutableListOf<DataExtended>()

        for (i in checkedData.indices) {
            list.add(DataExtended(data[i], checkedData[i]))
        }

        return list
    }

    fun setListener(listener: OnMultiChoiceBasicActionListener?) {
        this.listener = listener
    }

    private fun updateText() {
        var text = ""

        var isAnyChecked = false
        for (i in checkedData.indices) {
            if (checkedData[i]) {
                text += data[i] + ", "
                isAnyChecked = true
            }
        }

        if (isAnyChecked) {
            text = text.substring(0, text.length - 2)
        }

        binding.textBlock.text = text
    }

    private fun initListeners() {
        binding.root.setOnClickListener {
            this.listener?.invoke(OnMultiChoiceBasicAction.POSITIVE)
        }
    }

    data class DataExtended(
        val data: String,
        val checked: Boolean
    )

    // SAVE

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()!!
        val savedState = SavedState(superState)

        savedState.savedText = binding.textBlock.text.toString()
        savedState.savedCheckedData = checkedData

        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)

        val savedText = savedState.savedText
        val savedCheckedData = savedState.savedCheckedData

        binding.textBlock.post {
            binding.textBlock.text = savedText
        }

        checkedData = savedCheckedData ?: BooleanArray(data.size) { false }
    }

    class SavedState : BaseSavedState {

        var savedText: String? = null
        var savedCheckedData : BooleanArray? = null


        constructor(superState: Parcelable) : super(superState)

        constructor(parcel: Parcel) : super(parcel) {
            savedText = parcel.readString()
            parcel.readBooleanArray(savedCheckedData!!)
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeString(savedText)
            out.writeBooleanArray(savedCheckedData)
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