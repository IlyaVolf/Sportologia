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
import com.thesis.sportologia.databinding.ViewDateBasicBinding
import com.thesis.sportologia.utils.parseDate
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


typealias OnDateBasicActionListener = (OnDateBasicAction) -> Unit

enum class OnDateBasicAction {
    POSITIVE
}

class DateBasicView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: ViewDateBasicBinding

    private var listener: OnDateBasicActionListener? = null

    private var dateAndTime = Calendar.getInstance()

    private var isBefore = false

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
        inflater.inflate(R.layout.view_date_basic, this, true)
        binding = ViewDateBasicBinding.bind(this)
        initializeAttributes(attrs, defStyleAttr, defStyleRes)
    }

    fun getDateMillis(): Long? {
        return binding.textBlock.text.toString().toLongOrNull()
    }

    private fun initializeAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.DateBasicView, defStyleAttr, defStyleRes
        )

        val title = typedArray.getString(R.styleable.DateBasicView_dateTitle)
        binding.title.text = title

        val hint = typedArray.getString(R.styleable.DateBasicView_dateHint)
        binding.textBlock.hint = hint

        isBefore = typedArray.getBoolean(R.styleable.DateBasicView_dateIsBefore, false)

        isTimeEnabled = typedArray.getBoolean(
            R.styleable.DateBasicView_dateIsTimeEnabled,
            false
        )

        val d = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            dateAndTime[Calendar.YEAR] = year
            dateAndTime[Calendar.MONTH] = monthOfYear
            dateAndTime[Calendar.DAY_OF_MONTH] = dayOfMonth

            if (validateDate()) {
                binding.textBlock.text = parseDate(dateAndTime, "d MMMM uuuu")
            }
        }

        binding.textBlock.setOnClickListener {
            invokeDatePicker(d)
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

    private fun validateDate(): Boolean {
        if (isBefore) {
            val currentDateAndTime = Calendar.getInstance()
            if (currentDateAndTime < dateAndTime) {
                Toast.makeText(context, context.getString(R.string.error_date), Toast.LENGTH_SHORT)
                    .show()
                return false
            }
        }

        return true
    }

    private fun invokeDatePicker(d: OnDateSetListener) {
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
            this.listener?.invoke(OnDateBasicAction.POSITIVE)
        }
    }

    fun setListener(listener: OnDateBasicActionListener?) {
        this.listener = listener
    }

    // SAVE

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()!!
        val savedState = SavedState(superState)
        savedState.savedDate = binding.textBlock.text.toString()
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)

        val savedDate = savedState.savedDate

        binding.textBlock.post {
            binding.textBlock.text = savedDate
        }
    }

    class SavedState : BaseSavedState {

        var savedDate: String? = null

        constructor(superState: Parcelable) : super(superState)

        constructor(parcel: Parcel) : super(parcel) {
            savedDate = parcel.readString()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeString(savedDate)
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