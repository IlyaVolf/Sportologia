package com.thesis.sportologia.ui.views

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ViewDateIntervalOutlinedBinding
import com.thesis.sportologia.utils.parseDate
import java.util.*

// TODO дата мнимая: при сохранении берется текущая. При отменевы бора берется текущая
typealias OnDateIntervalOutlinedActionListener = (OnDateIntervalOutlinedAction) -> Unit

enum class OnDateIntervalOutlinedAction {
    POSITIVE
}

class DateIntervalOutlinedView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private lateinit var dateAndTimePattern: String
    private val binding: ViewDateIntervalOutlinedBinding

    private var listener: OnDateIntervalOutlinedActionListener? = null

    private val chosenDataAndTime = Calendar.getInstance()
    private var dateAndTimeFrom: Calendar? = null
    private var dateAndTimeTo: Calendar? = null

    private var isBefore = false
    private var isAfter = false
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
        inflater.inflate(R.layout.view_date_interval_outlined, this, true)
        binding = ViewDateIntervalOutlinedBinding.bind(this)
        initAttributes(attrs, defStyleAttr, defStyleRes)
        initDates()
    }

    private fun initAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.DateIntervalOutlinedView, defStyleAttr, defStyleRes
        )

        val title = typedArray.getString(R.styleable.DateIntervalOutlinedView_diovTitle)
        binding.title.text = title

        val hintLeft = typedArray.getString(R.styleable.DateIntervalOutlinedView_diovHintLeft)
        binding.textBlockLeft.hint = hintLeft

        val hintRight = typedArray.getString(R.styleable.DateIntervalOutlinedView_diovHintRight)
        binding.textBlockRight.hint = hintRight

        isBefore = typedArray.getBoolean(R.styleable.DateIntervalOutlinedView_diovIsBefore, false)
        isAfter = typedArray.getBoolean(R.styleable.DateIntervalOutlinedView_diovIsAfter, false)

        isTimeEnabled = typedArray.getBoolean(
            R.styleable.DateIntervalOutlinedView_diovIsTimeEnabled,
            false
        )
        dateAndTimePattern = if (isTimeEnabled) {
            DATE_TIME_PATTERN
        } else {
            DATE_PATTERN
        }

        typedArray.recycle()
    }

    private fun initDates() {
        val tLeft = getTimeSetListener(DateBlock.LEFT)
        val dLeft = getDateSetListener(DateBlock.LEFT, tLeft)
        binding.textBlockLeft.setOnClickListener {
            if (dateAndTimeFrom != null) {
                chosenDataAndTime.timeInMillis = dateAndTimeFrom!!.timeInMillis
            }
            invokeDatePicker(dLeft)
        }

        val tRight = getTimeSetListener(DateBlock.RIGHT)
        val dRight = getDateSetListener(DateBlock.RIGHT, tRight)
        binding.textBlockRight.setOnClickListener {
            if (dateAndTimeTo != null) {
                chosenDataAndTime.timeInMillis = dateAndTimeTo!!.timeInMillis
            }
            invokeDatePicker(dRight)
        }
    }

    fun setDateAndTime(dateFromMillis: Long?, dateToMillis: Long?) {
        if (dateAndTimeFrom == null) {
            dateAndTimeFrom = Calendar.getInstance()
        }

        if (dateFromMillis != null) {
            if (dateAndTimeFrom == null) {
                dateAndTimeFrom = Calendar.getInstance()
            }
            dateAndTimeFrom!!.timeInMillis = dateFromMillis
            binding.textBlockLeft.text = parseDate(dateAndTimeFrom!!, dateAndTimePattern)
        }

        if (dateToMillis != null) {
            if (dateAndTimeTo == null) {
                dateAndTimeTo = Calendar.getInstance()
            }
            dateAndTimeTo!!.timeInMillis = dateToMillis
            binding.textBlockRight.text = parseDate(dateAndTimeTo!!, dateAndTimePattern)
        }
    }

    private fun updateDateAndTime(dateBlock: DateBlock) {
        when (dateBlock) {
            DateBlock.LEFT -> {
                if (dateAndTimeFrom == null) {
                    dateAndTimeFrom = Calendar.getInstance()
                }
                dateAndTimeFrom!!.timeInMillis = chosenDataAndTime.timeInMillis
                binding.textBlockLeft.text = parseDate(dateAndTimeFrom!!, dateAndTimePattern)
            }
            DateBlock.RIGHT -> {
                if (dateAndTimeTo == null) {
                    dateAndTimeTo = Calendar.getInstance()
                }
                dateAndTimeTo!!.timeInMillis = chosenDataAndTime.timeInMillis
                binding.textBlockRight.text = parseDate(dateAndTimeTo!!, dateAndTimePattern)
            }
        }
    }

    private fun getTimeSetListener(dateBlock: DateBlock): OnTimeSetListener {
        return OnTimeSetListener { view, hourOfDay, minute ->
            chosenDataAndTime[Calendar.HOUR_OF_DAY] = hourOfDay
            chosenDataAndTime[Calendar.MINUTE] = minute

            Log.d("CALENDAR", "chosenTime: $chosenDataAndTime")
            if (validateDate(chosenDataAndTime)) {
                updateDateAndTime(dateBlock)
            }
        }
    }

    private fun getDateSetListener(dateBlock: DateBlock, t: OnTimeSetListener): OnDateSetListener {
        return OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            chosenDataAndTime[Calendar.YEAR] = year
            chosenDataAndTime[Calendar.MONTH] = monthOfYear
            chosenDataAndTime[Calendar.DAY_OF_MONTH] = dayOfMonth

            Log.d("CALENDAR", "chosenData: $chosenDataAndTime")
            if (isTimeEnabled) {
                invokeTimePicker(t)
            } else {
                if (validateDate(chosenDataAndTime)) {
                    updateDateAndTime(dateBlock)
                }
            }
        }
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

    fun getDateFromMillis(): Long? {
        return dateAndTimeFrom?.timeInMillis
    }

    fun getDateToMillis(): Long? {
        return dateAndTimeTo?.timeInMillis
    }

    private fun invokeDatePicker(d: OnDateSetListener) {
        val datePickerDialog = DatePickerDialog(
            context, R.style.DialogStyleBasic, d,
            chosenDataAndTime.get(Calendar.YEAR),
            chosenDataAndTime.get(Calendar.MONTH),
            chosenDataAndTime.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()

        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            .setTextColor(context.getColor(R.color.text_main))
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
            .setTextColor(context.getColor(R.color.text_main))
    }

    private fun invokeTimePicker(t: OnTimeSetListener) {
        val timePickerDialog = TimePickerDialog(
            context, R.style.DialogStyleBasic, t,
            chosenDataAndTime[Calendar.HOUR_OF_DAY],
            chosenDataAndTime[Calendar.MINUTE],
            true
        )

        timePickerDialog.show()

        timePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            .setTextColor(context.getColor(R.color.text_main))
        timePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
            .setTextColor(context.getColor(R.color.text_main))
    }

    private fun initListeners() {
        binding.root.setOnClickListener {
            this.listener?.invoke(OnDateIntervalOutlinedAction.POSITIVE)
        }
    }

    fun setListener(listener: OnDateIntervalOutlinedActionListener?) {
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

    companion object {
        enum class DateBlock {
            LEFT, RIGHT
        }

        const val DATE_TIME_PATTERN = "d MMM uuuu, H:mm"
        const val DATE_PATTERN = "d MMM uuuu"
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