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
import com.thesis.sportologia.databinding.ViewSpinnerMoreBinding


typealias OnSpinnerMoreActionListener = (String) -> Unit

class SpinnerMoreView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: ViewSpinnerMoreBinding

    private var listeners = mutableListOf<OnSpinnerMoreActionListener?>()

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
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.view_spinner_only_outlined, this, true)

        binding = ViewSpinnerMoreBinding.bind(this)
    }

    fun initAdapter(list: List<String>) {

        //Toast.makeText(context, "AGAIN" + hint, Toast.LENGTH_SHORT).show()
        //Log.d("BUGFIX", "INIT ADAPTER $hint")
        data = list.toMutableList()

        adapter =
            ArrayAdapter<String>(context, R.layout.item_spinner_short, R.id.dropdown_text, data)

        adapter.setDropDownViewResource(R.layout.item_spinner)
        setAdapter(adapter)

        binding.spinnerMore.onItemSelectedListener = object :
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
    }

    fun setListener(listener: OnSpinnerMoreActionListener?) {
        listeners.add(listener)
    }


    fun removeListener(listener: OnSpinnerMoreActionListener?) {
        listeners.remove(listener)
    }

    fun setAdapter(adapter: ArrayAdapter<String>) {
        this.adapter = adapter
        binding.spinnerMore.adapter = adapter
    }

}