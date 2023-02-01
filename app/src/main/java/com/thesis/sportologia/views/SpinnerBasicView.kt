package com.thesis.sportologia.views

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
import com.thesis.sportologia.databinding.ViewSpinnerBasicBinding


typealias OnSpinnerBasicActionListener = (String) -> Unit

class SpinnerBasicView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: ViewSpinnerBasicBinding

    private var listener: OnSpinnerBasicActionListener? = null

    private lateinit var adapter: ArrayAdapter<String>

    private lateinit var data: MutableList<String>

    private lateinit var currentValue: String

    private var hint = ""

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
        inflater.inflate(R.layout.view_spinner_basic, this, true)

        binding = ViewSpinnerBasicBinding.bind(this)

        initAttributes(attrs, defStyleAttr, defStyleRes)
    }

    private fun initAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.SpinnerBasicView, defStyleAttr, defStyleRes
        )

        val title = typedArray.getString(R.styleable.SpinnerBasicView_spinnerTitle)
        binding.title.text = title

        val hint = typedArray.getString(R.styleable.SpinnerBasicView_spinnerHint)
        this.hint = hint ?: ""
        this.currentValue = this.hint

        typedArray.recycle()
    }

    fun initAdapter(list: List<String>) {

        //Toast.makeText(context, "AGAIN" + hint, Toast.LENGTH_SHORT).show()
        Log.d("BUGFIX", "INIT ADAPTER $hint")
        data = list.toMutableList()
        data.add(0, hint)

        adapter = object : ArrayAdapter<String>(
            context, R.layout.item_spinner, R.id.dropdown_text, data
        ) {
            override fun isEnabled(position: Int): Boolean {
                // Disable the first item from Spinner
                // First item will be used for hint
                return position != 0
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view =
                    super.getDropDownView(position, convertView, parent)
                if (position == 0) {
                    view.findViewById<TextView>(R.id.dropdown_text).setTextColor(
                        context.getColor(R.color.text_hint)
                    )
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
                    if (value == data[0]) {
                        view.findViewById<TextView>(R.id.dropdown_text)
                            .setTextColor(context.getColor(R.color.text_hint))
                    } else {
                        currentValue = value
                    }

                    Log.d("BUGFIX", "onItemSelected $currentValue")
                    listener?.invoke(currentValue)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d("BUGFIX", "onNothingSelected $currentValue")
                // write code to perform some action
            }

        }

        Log.d("BUGFIX", "leaving initAdapter: $currentValue")
    }

    fun getCurrentAccountType(): String {
        return currentValue
    }

    fun setListener(listener: OnSpinnerBasicActionListener?) {
        this.listener = listener
    }

    fun setAdapter(adapter: ArrayAdapter<String>) {
        this.adapter = adapter
        binding.spinnerBlock.adapter = adapter
    }

    /// Save

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()!!
        val savedState = SavedState(superState)

        savedState.currentValue = currentValue

        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        Log.d("BUGFIX", " onRestoreInstanceState Entered:  $currentValue")

        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)

        currentValue = savedState.currentValue!!

        Log.d("BUGFIX", " onRestoreInstanceState Entered:  $currentValue $hint")

        binding.spinnerBlock.post {
            binding.spinnerBlock.setSelection(
                data.indexOf(
                    currentValue
                ), true
            )

            if (currentValue == hint) {
                binding.spinnerBlock.getChildAt(0).findViewById<TextView>(R.id.dropdown_text)
                    .setTextColor(context.getColor(R.color.text_hint))
            }
        }

        //adapter.notifyDataSetChanged()

        listener?.invoke(currentValue)

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