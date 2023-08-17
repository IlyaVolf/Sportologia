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
import androidx.constraintlayout.widget.ConstraintLayout
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ViewSpinnerOnlyOutlinedBinding

typealias OnSpinnerOnlyOutlinedActionListener = (String) -> Unit

class SpinnerOnlyOutlinedView @JvmOverloads constructor(
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

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    init {
        Log.d("FFFF", "hi")
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.view_spinner_only_outlined, this, true)

        binding = ViewSpinnerOnlyOutlinedBinding.bind(this)

        initAttributes(attrs, defStyleAttr, defStyleRes)
    }

    private fun initAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.SpinnerOnlyOutlinedView, defStyleAttr, defStyleRes
        )

        typedArray.recycle()
    }

    fun initAdapter(list: List<String>, savedValue: String) {
        currentValue = savedValue
        //Toast.makeText(context, "AGAIN" + hint, Toast.LENGTH_SHORT).show()
        //Log.d("BUGFIX", "INIT ADAPTER $hint")
        data = list.toMutableList()

        //binding.spinnerBlock.setSelection(data.indexOf(savedValue))

        adapter = object : ArrayAdapter<String>(
            context, R.layout.item_spinner_short, R.id.dropdown_text, data
        ) {
            override fun isEnabled(position: Int): Boolean {
                // Disable the first item from Spinner
                // First item will be used for hint
                return true
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                return super.getDropDownView(position, convertView, parent)
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
                    currentValue = value

                    listeners.forEach {
                        it?.invoke(currentValue)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }

        }

        binding.spinnerBlock.setSelection(data.indexOf(savedValue))
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
        Log.d("FFFF", "save")
        val superState = super.onSaveInstanceState()!!
        val savedState = SavedState(superState)

        if (::currentValue.isInitialized) {
            savedState.currentValue = currentValue
        }

        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        Log.d("FFFF", "restore")
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)

        currentValue = savedState.currentValue ?: ""

        binding.spinnerBlock.post {
            binding.spinnerBlock.setSelection(
                data.indexOf(
                    currentValue
                ), true
            )
        }

        //adapter.notifyDataSetChanged()

        listeners.forEach {
            it?.invoke(currentValue)
        }

    }

    class SavedState : BaseSavedState {

        var currentValue: String? = null

        constructor(superState: Parcelable) : super(superState)

        constructor(parcel: Parcel) : super(parcel) {
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