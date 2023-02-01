package com.thesis.sportologia.views

import android.content.Context
import android.graphics.Typeface
import android.os.Parcel
import android.os.Parcelable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ViewContentTabsBinding


typealias OnContentTabsActionListener = (OnContentTabsAction) -> Unit

enum class OnContentTabsAction {
    POSITIVE
}

class ContentTabsView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: ViewContentTabsBinding

    private var buttonNumber = 4
    private var activatedButtonId = 1
    private var isSpinnerSupported = false

    private var listener: OnContentTabsActionListener? = null

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
        inflater.inflate(R.layout.view_content_tabs, this, true)
        binding = ViewContentTabsBinding.bind(this)
        initializeAttributes(attrs, defStyleAttr, defStyleRes)
    }

    private fun initializeAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.ContentTabsView, defStyleAttr, defStyleRes
        )

        val text1 = typedArray.getString(R.styleable.ContentTabsView_contentTabsText1) ?: "text 1"
        val text2 = typedArray.getString(R.styleable.ContentTabsView_contentTabsText2) ?: "text 2"
        val text3 = typedArray.getString(R.styleable.ContentTabsView_contentTabsText3) ?: "text 3"
        val text4 = typedArray.getString(R.styleable.ContentTabsView_contentTabsText4) ?: "text 4"

        binding.text1.text = text1
        binding.text2.text = text2
        binding.text3.text = text3
        binding.text4.text = text4

        val textSize =
            typedArray.getInt(R.styleable.ContentTabsView_contentTabsTextSize, 14)
        binding.text1.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
        binding.text2.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
        binding.text3.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
        binding.text4.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())

        buttonNumber =
            typedArray.getInt(R.styleable.ContentTabsView_contentTabsButtonsNumber, 4)

        when (buttonNumber) {
            4 -> {
                binding.button1.visibility = VISIBLE
                binding.button2.visibility = VISIBLE
                binding.button3.visibility = VISIBLE
                binding.button4.visibility = VISIBLE
            }

            3 -> {
                binding.button1.visibility = VISIBLE
                binding.button2.visibility = VISIBLE
                binding.button3.visibility = VISIBLE
                binding.button4.visibility = GONE
            }

            2 -> {
                binding.button1.visibility = VISIBLE
                binding.button2.visibility = VISIBLE
                binding.button3.visibility = GONE
                binding.button4.visibility = GONE
            }

            1 -> {
                binding.button1.visibility = VISIBLE
                binding.button2.visibility = GONE
                binding.button3.visibility = GONE
                binding.button4.visibility = GONE
            }
        }

        val isCounterSupported =
            typedArray.getBoolean(R.styleable.ContentTabsView_contentTabsCounterSupport, false)

        if (isCounterSupported) {
            binding.count1.visibility = VISIBLE
            binding.count2.visibility = VISIBLE
            binding.count3.visibility = VISIBLE
            binding.count4.visibility = VISIBLE
        } else {
            binding.count1.visibility = GONE
            binding.count2.visibility = GONE
            binding.count3.visibility = GONE
            binding.count4.visibility = GONE
        }

        isSpinnerSupported =
            typedArray.getBoolean(R.styleable.ContentTabsView_contentTabsSpinnerSupport, false)
        binding.arrow1.visibility = GONE
        binding.arrow2.visibility = GONE
        binding.arrow3.visibility = GONE
        binding.arrow4.visibility = GONE

        activatedButtonId =
            typedArray.getInt(R.styleable.ContentTabsView_contentTabsActiveByDefault, 1)
        activateButton(activatedButtonId)

        typedArray.recycle()
    }

    private fun disactivateButton(buttonId: Int) {
        when (buttonId) {
            1 -> {
                binding.bar1.setBackgroundColor(context.getColor(R.color.element_secondary))
                binding.text1.setTextColor(context.getColor(R.color.text_secondary))
                binding.text1.typeface = Typeface.DEFAULT
                binding.arrow1.visibility = GONE
            }
            2 -> {
                binding.bar2.setBackgroundColor(context.getColor(R.color.element_secondary))
                binding.text2.setTextColor(context.getColor(R.color.text_secondary))
                binding.text2.typeface = Typeface.DEFAULT
                binding.arrow2.visibility = GONE
            }
            3 -> {
                binding.bar3.setBackgroundColor(context.getColor(R.color.element_secondary))
                binding.text3.setTextColor(context.getColor(R.color.text_secondary))
                binding.text3.typeface = Typeface.DEFAULT
                binding.arrow3.visibility = GONE
            }
            4 -> {
                binding.bar4.setBackgroundColor(context.getColor(R.color.element_secondary))
                binding.text4.setTextColor(context.getColor(R.color.text_secondary))
                binding.text4.typeface = Typeface.DEFAULT
                binding.arrow4.visibility = GONE
            }
        }
    }

    private fun activateButton(buttonId: Int) {
        disactivateButton(activatedButtonId)

        when (buttonId) {
            1 -> {
                binding.bar1.setBackgroundColor(context.getColor(R.color.purple_dark))
                binding.text1.setTextColor(context.getColor(R.color.text_main))
                binding.text1.typeface = Typeface.DEFAULT_BOLD
                if (isSpinnerSupported) {
                    binding.arrow1.visibility = VISIBLE
                }
            }
            2 -> {
                binding.bar2.setBackgroundColor(context.getColor(R.color.purple_dark))
                binding.text2.setTextColor(context.getColor(R.color.text_secondary))
                binding.text2.typeface = Typeface.DEFAULT_BOLD
                if (isSpinnerSupported) {
                    binding.arrow2.visibility = VISIBLE
                }
            }
            3 -> {
                binding.bar3.setBackgroundColor(context.getColor(R.color.purple_dark))
                binding.text3.setTextColor(context.getColor(R.color.text_main))
                binding.text3.typeface = Typeface.DEFAULT_BOLD
                if (isSpinnerSupported) {
                    binding.arrow3.visibility = VISIBLE
                }
            }
            4 -> {
                binding.bar4.setBackgroundColor(context.getColor(R.color.purple_dark))
                binding.text4.setTextColor(context.getColor(R.color.text_main))
                binding.text4.typeface = Typeface.DEFAULT_BOLD
                if (isSpinnerSupported) {
                    binding.arrow4.visibility = VISIBLE
                }
            }
        }
    }

    private fun initListeners() {
        binding.root.setOnClickListener {
            this.listener?.invoke(OnContentTabsAction.POSITIVE)
        }
    }

    fun setListener(listener: OnContentTabsActionListener?) {
        this.listener = listener
    }

    fun setCount(buttonId: Int, count: Int) {
        if (buttonId > 4 || buttonId < 1) {
            return
        }

        /*for (i in 1..4) {
            if (buttonId == (i - 1) && buttonNumber < i) {
                return
            }
        }*/

        when (buttonId) {
            1 -> binding.count1.text = " ($count)"
            2 -> binding.count2.text = " ($count)"
            3 -> binding.count3.text = " ($count)"
            4 -> binding.count4.text = " ($count)"
        }
    }

    // SAVE

    /* override fun onSaveInstanceState(): Parcelable {
         val superState = super.onSaveInstanceState()!!
         val savedState = SavedState(superState)
         savedState.enteredText = binding.textBlock.text.toString()
         return savedState
     }

     override fun onRestoreInstanceState(state: Parcelable?) {
         val savedState = state as SavedState
         super.onRestoreInstanceState(savedState.superState)

         val enteredText = savedState.enteredText

         Log.d("BUGFIX", "HELLO")

         binding.textBlock.post {
             binding.textBlock.setText(enteredText)
         }
     }

     class SavedState : BaseSavedState {

         var enteredText: String? = null

         constructor(superState: Parcelable) : super(superState)

         constructor(parcel: Parcel) : super(parcel) {
             enteredText = parcel.readString()
         }

         override fun writeToParcel(out: Parcel, flags: Int) {
             super.writeToParcel(out, flags)
             out.writeString(enteredText)
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
     }*/

}