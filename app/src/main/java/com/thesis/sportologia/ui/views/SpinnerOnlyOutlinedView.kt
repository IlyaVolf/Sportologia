package com.thesis.sportologia.ui.views

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ViewSpinnerOnlyOutlinedBinding


typealias OnSpinnerOnlyOutlinedActionListener = (String) -> Unit

class SpinnerOnlyOutlinedView @JvmOverloads constructor(
    readyBinding: ViewSpinnerOnlyOutlinedBinding? = null,
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: ViewSpinnerOnlyOutlinedBinding

    private var listeners = mutableListOf<OnSpinnerOnlyOutlinedActionListener?>()

    private lateinit var adapter: ArrayAdapter<String>

    private var data = mutableListOf<String>()

    private lateinit var currentValue: String

    private lateinit var hint: String

    private var isHintEnabled = false

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        null,
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)


    constructor(
        readyBinding: ViewSpinnerOnlyOutlinedBinding?,
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : this(
        readyBinding,
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(readyBinding: ViewSpinnerOnlyOutlinedBinding?, context: Context, attrs: AttributeSet?) : this(
        readyBinding,
        context,
        attrs,
        0
    )

    constructor(readyBinding: ViewSpinnerOnlyOutlinedBinding?, context: Context) : this(
        readyBinding,
        context,
        null
    )


    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.view_spinner_only_outlined, this, true)

        binding = readyBinding ?: ViewSpinnerOnlyOutlinedBinding.bind(this)

        initAttributes(attrs, defStyleAttr, defStyleRes)
    }

    private fun initAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.SpinnerOnlyOutlinedView, defStyleAttr, defStyleRes
        )

        isHintEnabled =
            typedArray.getBoolean(
                R.styleable.SpinnerOnlyOutlinedView_spinnerOnlyIsHintEnabled,
                false
            )

        val spinnerHilt = typedArray.getString(R.styleable.SpinnerOnlyOutlinedView_spinnerOnlyHint)
        if (spinnerHilt == null) {
            isHintEnabled = false
        }
        hint = spinnerHilt ?: ""
        if (isHintEnabled) {
            currentValue = hint
        }

        typedArray.recycle()
    }

    fun initAdapter(list: List<String>) {

        //Toast.makeText(context, "AGAIN" + hint, Toast.LENGTH_SHORT).show()
        //Log.d("BUGFIX", "INIT ADAPTER $hint")
        data = list.toMutableList()
        if (isHintEnabled) {
            data.add(0, hint)
        }

        adapter = object : ArrayAdapter<String>(
            context, R.layout.item_spinner_short, R.id.dropdown_text, data
        ) {
            override fun isEnabled(position: Int): Boolean {
                // Disable the first item from Spinner
                // First item will be used for hint
                return if (isHintEnabled) {
                    position != 0
                } else {
                    true
                }
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view =
                    super.getDropDownView(position, convertView, parent)
                if (isHintEnabled) {
                    if (position == 0) {
                        view.findViewById<TextView>(R.id.dropdown_text).setTextColor(
                            context.getColor(R.color.text_hint)
                        )
                    }
                }
                return view
            }
        }

        adapter.setDropDownViewResource(R.layout.item_spinner)
        setAdapter(adapter)

        binding.spinnerBlock.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?, position: Int, id: Long
            ) {
                if (view != null) {

                    val value = parent.getItemAtPosition(position).toString()
                    if (isHintEnabled) {
                        if (value == data[0]) {
                            view.findViewById<TextView>(R.id.dropdown_text)
                                .setTextColor(context.getColor(R.color.text_hint))
                        } else {
                            currentValue = value
                        }
                    } else {
                        currentValue = value
                    }

                    listeners.forEach {
                        it?.invoke(currentValue)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }

        }
    }

    fun getCurrentAccountType(): String {
        return currentValue
    }

    fun setListener(listener: OnSpinnerOnlyOutlinedActionListener?) {
        listeners.add(listener)
    }


    fun removeListener(listener: OnSpinnerOnlyOutlinedActionListener?) {
        listeners.remove(listener)
    }

    fun setAdapter(adapter: ArrayAdapter<String>) {
        this.adapter = adapter
        binding.spinnerBlock.adapter = adapter
    }

    /// Save

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()!!
        val savedState = SavedState(superState)

        if (::currentValue.isInitialized) {
            savedState.currentValue = currentValue
        }
        if (::hint.isInitialized) {
            savedState.hint = hint
        }

        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)

        hint = savedState.hint ?: ""
        currentValue = savedState.currentValue ?: hint

        binding.spinnerBlock.post {
            binding.spinnerBlock.setSelection(
                data.indexOf(
                    currentValue
                ), true
            )

            if (currentValue == hint && binding.spinnerBlock.getChildAt(0) != null) {
                binding.spinnerBlock.getChildAt(0).findViewById<TextView>(R.id.dropdown_text)
                    .setTextColor(context.getColor(R.color.text_hint))
            }
        }

        //adapter.notifyDataSetChanged()

        listeners.forEach {
            it?.invoke(currentValue)
        }

    }

    class SavedState : BaseSavedState {

        var hint: String? = null
        var currentValue: String? = null

        constructor(superState: Parcelable) : super(superState)

        constructor(parcel: Parcel) : super(parcel) {
            hint = parcel.readString()
            currentValue = parcel.readString()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeString(currentValue)
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