package com.thesis.sportologia.views

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.TimeBasicBinding
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


typealias OnTimeBasicActionListener = (OnTimeBasicAction) -> Unit

enum class OnTimeBasicAction {
    POSITIVE
}

class TimeBasicView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: TimeBasicBinding

    private var listener: OnTimeBasicActionListener? = null

    private var dateAndTime = Calendar.getInstance()

    private var isTimeEnabled = false

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
        inflater.inflate(R.layout.date_basic, this, true)
        binding = TimeBasicBinding.bind(this)
        initializeAttributes(attrs, defStyleAttr, defStyleRes)
    }

    private fun initializeAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.TimeBasicView, defStyleAttr, defStyleRes
        )

        val title = typedArray.getString(R.styleable.TimeBasicView_timeTitle)
        binding.title.text = title

        val hint = typedArray.getString(R.styleable.TimeBasicView_timeHint)
        binding.textBlock.hint = hint

        isTimeEnabled = typedArray.getBoolean(
            R.styleable.TimeBasicView_timeIsTimeEnabled,
            false
        )

        val t = OnTimeSetListener { view, hourOfDay, minute ->
            dateAndTime[Calendar.HOUR_OF_DAY] = hourOfDay
            dateAndTime[Calendar.MINUTE] = minute

            binding.textBlock.text = parseTime()
        }

        binding.textBlock.setOnClickListener {
            invokeTimePicker(t)
        }


        typedArray.recycle()
    }

    // TODO other languages support
    private fun parseTime(): String {
        return SimpleDateFormat("HH:mm").format(dateAndTime)
    }

    private fun invokeTimePicker(t: OnTimeSetListener) {
        TimePickerDialog(
            context, t,
            dateAndTime[Calendar.HOUR_OF_DAY],
            dateAndTime[Calendar.MINUTE],
            true
        ).show()
    }

    private fun initListeners() {
        binding.root.setOnClickListener {
            this.listener?.invoke(OnTimeBasicAction.POSITIVE)
        }
    }

    fun setListener(listener: OnTimeBasicActionListener?) {
        this.listener = listener
    }

    // SAVE

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()!!
        val savedState = SavedState(superState)
        savedState.savedTime = binding.textBlock.text.toString()
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)

        val savedTime = savedState.savedTime

        binding.textBlock.post {
            binding.textBlock.text = savedTime
        }
    }

    class SavedState : BaseSavedState {

        var savedTime: String? = null

        constructor(superState: Parcelable) : super(superState)

        constructor(parcel: Parcel) : super(parcel) {
            savedTime = parcel.readString()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeString(savedTime)
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