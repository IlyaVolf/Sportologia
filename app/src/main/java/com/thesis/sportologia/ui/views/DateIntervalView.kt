package com.thesis.sportologia.ui.views

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ViewDateIntervalBinding
import com.thesis.sportologia.utils.parseDate
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


typealias OnDateIntervalActionListener = (OnDateIntervalAction) -> Unit

enum class OnDateIntervalAction {
    POSITIVE
}

class DateIntervalView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: ViewDateIntervalBinding

    private var listener: OnDateIntervalActionListener? = null

    private var dateAndTimeFrom = Calendar.getInstance()
    private var dateAndTimeTo = Calendar.getInstance()

    private var isBefore = false
    private var isAfter= false

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
        inflater.inflate(R.layout.view_date_interval, this, true)
        binding = ViewDateIntervalBinding.bind(this)
        initializeAttributes(attrs, defStyleAttr, defStyleRes)
    }

    private fun initializeAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.DateIntervalView, defStyleAttr, defStyleRes
        )

        val title = typedArray.getString(R.styleable.DateIntervalView_divTitle)
        binding.title.text = title

        val hintLeft = typedArray.getString(R.styleable.DateIntervalView_divHintLeft)
        binding.textBlockLeft.hint = hintLeft

        val hintRight = typedArray.getString(R.styleable.DateIntervalView_divHintRight)
        binding.textBlockRight.hint = hintRight

        isBefore = typedArray.getBoolean(R.styleable.DateIntervalView_divIsBefore, false)
        isAfter = typedArray.getBoolean(R.styleable.DateIntervalView_divIsAfter, false)

        isTimeEnabled = typedArray.getBoolean(
            R.styleable.DateIntervalView_divIsTimeEnabled,
            false
        )

        val dLeft = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            dateAndTimeFrom[Calendar.YEAR] = year
            dateAndTimeFrom[Calendar.MONTH] = monthOfYear
            dateAndTimeFrom[Calendar.DAY_OF_MONTH] = dayOfMonth

            if (validateDate(dateAndTimeFrom)) {
                binding.textBlockLeft.text = parseDate(dateAndTimeFrom, "d M uuuu, H:mm")
            }
        }

        binding.textBlockLeft.setOnClickListener {
            invokeDatePicker(dLeft, dateAndTimeFrom)
        }

        val dRight = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            dateAndTimeTo[Calendar.YEAR] = year
            dateAndTimeTo[Calendar.MONTH] = monthOfYear
            dateAndTimeTo[Calendar.DAY_OF_MONTH] = dayOfMonth

            if (validateDate(dateAndTimeTo)) {
                binding.textBlockRight.text = parseDate(dateAndTimeTo, "d M uuuu, H:mm")
            }
        }

        binding.textBlockRight.setOnClickListener {
            invokeDatePicker(dRight, dateAndTimeTo)
        }

        typedArray.recycle()
    }

    /*// TODO other languages support
    private fun parseDate(): String {
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            .withLocale(Locale("ru"))
            .format(
                LocalDate.of(
                    dateAndTime.get(Calendar.YEAR),
                    dateAndTime.get(Calendar.MONTH) + 1,
                    dateAndTime.get(Calendar.DAY_OF_MONTH)
                )
            )
    }*/

    private fun validateDate(dateAndTime: Calendar): Boolean {
        if (isBefore) {
            val currentDateAndTime = Calendar.getInstance()
            if (currentDateAndTime < dateAndTime) {
                Toast.makeText(context, context.getString(R.string.error_date), Toast.LENGTH_SHORT)
                    .show()
                return false
            }
        }
        if (isAfter) {
            val currentDateAndTime = Calendar.getInstance()
            if (currentDateAndTime > dateAndTime) {
                Toast.makeText(context, context.getString(R.string.error_date), Toast.LENGTH_SHORT)
                    .show()
                return false
            }
        }

        return true
    }

    fun getDateFromMillis(): Long {
        return dateAndTimeFrom.timeInMillis
    }

    fun getDateToMillis(): Long {
        return dateAndTimeTo.timeInMillis
    }

    fun getDateFromText(): String {
        return binding.textBlockLeft.text.toString()
    }

    fun getDateToText(): String {
        return binding.textBlockRight.text.toString()
    }

    private fun invokeDatePicker(d: OnDateSetListener, dateAndTime: Calendar) {
        val datePickerDialog = DatePickerDialog(
            context, R.style.DialogStyleBasic, d,
            dateAndTime.get(Calendar.YEAR),
            dateAndTime.get(Calendar.MONTH),
            dateAndTime.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()

        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            .setTextColor(context.getColor(R.color.text_main))
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
            .setTextColor(context.getColor(R.color.text_main))
    }

    private fun initListeners() {
        binding.root.setOnClickListener {
            this.listener?.invoke(OnDateIntervalAction.POSITIVE)
        }
    }

    fun setListener(listener: OnDateIntervalActionListener?) {
        this.listener = listener
    }

    // SAVE

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()!!
        val savedState = SavedState(superState)
        savedState.savedDateLeft = binding.textBlockLeft.text.toString()
        savedState.savedDateRight = binding.textBlockRight.text.toString()
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)

        val savedDateLeft = savedState.savedDateLeft
        val savedDateRight = savedState.savedDateRight

        binding.textBlockLeft.post {
            binding.textBlockLeft.text = savedDateLeft
        }

        binding.textBlockRight.post {
            binding.textBlockRight.text = savedDateRight
        }
    }

    class SavedState : BaseSavedState {

        var savedDateLeft: String? = null
        var savedDateRight: String? = null

        constructor(superState: Parcelable) : super(superState)

        constructor(parcel: Parcel) : super(parcel) {
            savedDateLeft = parcel.readString()
            savedDateRight = parcel.readString()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeString(savedDateLeft)
            out.writeString(savedDateRight)
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