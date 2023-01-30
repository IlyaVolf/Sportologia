package com.thesis.sportologia.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.SpinnerBasicBinding


typealias OnSpinnerBasicActionListener = (OnSpinnerBasicAction) -> Unit

enum class OnSpinnerBasicAction {
    POSITIVE
}

class SpinnerBasicView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: SpinnerBasicBinding

    private var listener: OnSpinnerBasicActionListener? = null

    private lateinit var adapter: ArrayAdapter<String>

    private lateinit var data: MutableList<String>

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
        inflater.inflate(R.layout.spinner_basic, this, true)
        binding = SpinnerBasicBinding.bind(this)
        initAttributes(attrs, defStyleAttr, defStyleRes)
    }

    private fun initAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.SpinnerBasicView, defStyleAttr, defStyleRes
        )

        val title = typedArray.getString(R.styleable.SpinnerBasicView_spinnerTitle)
        binding.title.text = title

        typedArray.recycle()
    }

    fun initAdapter(list: List<String>, string: String) {
        data = list.toMutableList()
        data.add(0, string)

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
        //spinner.dropDownWidth = 500

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
                        /*Toast.makeText(
                        context,
                        "Selected Item" + " " +
                                "" + data[position], Toast.LENGTH_SHORT
                    ).show()*/
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }

        }
    }

    private fun initListeners() {
        binding.root.setOnClickListener {
            this.listener?.invoke(OnSpinnerBasicAction.POSITIVE)
        }
    }

    fun setListener(listener: OnSpinnerBasicActionListener?) {
        this.listener = listener
    }

    fun setAdapter(adapter: ArrayAdapter<String>) {
        this.adapter = adapter
        binding.spinnerBlock.adapter = adapter
    }

    fun getSpinner(): AppCompatSpinner {
        return binding.spinnerBlock
    }

}