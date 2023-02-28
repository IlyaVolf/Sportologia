package com.thesis.sportologia.ui.views

import android.content.Context
import android.graphics.Typeface
import android.os.Parcel
import android.os.Parcelable
import android.transition.ChangeBounds
import android.transition.Scene
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.*
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ViewContentTabsBinding


typealias OnContentTabsActionListener = (OnContentTabsAction) -> Unit

enum class OnContentTabsAction {
    FIRST,
    SECOND,
    THIRD,
    FOURTH
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

    /*private val adaptersList = mutableListOf<ArrayAdapter<String>?>(null, null, null, null)
    private var dataList = mutableListOf<MutableList<String>>(mutableListOf(), mutableListOf(),
        mutableListOf(), mutableListOf())*/

    private var listeners = mutableListOf<OnContentTabsActionListener?>()

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
        initAttributes(attrs, defStyleAttr, defStyleRes)
        initListeners()
    }

    private fun initAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
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
            typedArray.getInt(R.styleable.ContentTabsView_contentTabsTextSizeSp, 14)
        binding.text1.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
        binding.text2.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
        binding.text3.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
        binding.text4.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
        binding.count1.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
        binding.count2.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
        binding.count3.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
        binding.count4.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())

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
                binding.count1.setTextColor(context.getColor(R.color.text_secondary))
                binding.text1.typeface = Typeface.DEFAULT
                binding.count1.typeface = Typeface.DEFAULT
                binding.arrow1.visibility = GONE
            }
            2 -> {
                binding.bar2.setBackgroundColor(context.getColor(R.color.element_secondary))
                binding.text2.setTextColor(context.getColor(R.color.text_secondary))
                binding.count2.setTextColor(context.getColor(R.color.text_secondary))
                binding.text2.typeface = Typeface.DEFAULT
                binding.count2.typeface = Typeface.DEFAULT
                binding.arrow2.visibility = GONE
            }
            3 -> {
                binding.bar3.setBackgroundColor(context.getColor(R.color.element_secondary))
                binding.text3.setTextColor(context.getColor(R.color.text_secondary))
                binding.count3.setTextColor(context.getColor(R.color.text_secondary))
                binding.text3.typeface = Typeface.DEFAULT
                binding.count3.typeface = Typeface.DEFAULT
                binding.arrow3.visibility = GONE
            }
            4 -> {
                binding.bar4.setBackgroundColor(context.getColor(R.color.element_secondary))
                binding.text4.setTextColor(context.getColor(R.color.text_secondary))
                binding.count4.setTextColor(context.getColor(R.color.text_secondary))
                binding.text4.typeface = Typeface.DEFAULT
                binding.count4.typeface = Typeface.DEFAULT
                binding.arrow4.visibility = GONE
            }
        }
    }

    // TODO анимации https://developer.android.com/develop/ui/views/animations/transitions
    private fun activateButton(buttonId: Int) {
        disactivateButton(activatedButtonId)

        when (buttonId) {
            1 -> {
                binding.bar1.setBackgroundColor(context.getColor(R.color.purple_dark))
                binding.text1.setTextColor(context.getColor(R.color.text_main))
                binding.count1.setTextColor(context.getColor(R.color.text_main))
                binding.text1.typeface = Typeface.DEFAULT_BOLD
                binding.count1.typeface = Typeface.DEFAULT_BOLD
                if (isSpinnerSupported) {
                    binding.arrow1.visibility = VISIBLE
                }
                activatedButtonId = buttonId
                val sceneRoot = findViewById<ViewGroup>(R.id.button_1)
                val bar = sceneRoot.findViewById<View>(R.id.bar_1)
                TransitionManager.beginDelayedTransition(sceneRoot)

                /*scene2 = Scene.getSceneForLayout(sceneRoot, R.layout.b, context)
                val set = TransitionSet()
                set.addTransition(ChangeBounds())
                set.setDuration(500)
                set.interpolator = AccelerateInterpolator()
                TransitionManager.go(bar, set)*/

            }
            2 -> {
                binding.bar2.setBackgroundColor(context.getColor(R.color.purple_dark))
                binding.text2.setTextColor(context.getColor(R.color.text_main))
                binding.count2.setTextColor(context.getColor(R.color.text_main))
                binding.text2.typeface = Typeface.DEFAULT_BOLD
                binding.count2.typeface = Typeface.DEFAULT_BOLD
                if (isSpinnerSupported) {
                    binding.arrow2.visibility = VISIBLE
                }
                activatedButtonId = buttonId
            }
            3 -> {
                binding.bar3.setBackgroundColor(context.getColor(R.color.purple_dark))
                binding.text3.setTextColor(context.getColor(R.color.text_main))
                binding.count3.setTextColor(context.getColor(R.color.text_main))
                binding.text3.typeface = Typeface.DEFAULT_BOLD
                binding.count3.typeface = Typeface.DEFAULT_BOLD
                if (isSpinnerSupported) {
                    binding.arrow3.visibility = VISIBLE
                }
                activatedButtonId = buttonId
            }
            4 -> {
                binding.bar4.setBackgroundColor(context.getColor(R.color.purple_dark))
                binding.text4.setTextColor(context.getColor(R.color.text_main))
                binding.count4.setTextColor(context.getColor(R.color.text_main))
                binding.text4.typeface = Typeface.DEFAULT_BOLD
                binding.count4.typeface = Typeface.DEFAULT_BOLD
                if (isSpinnerSupported) {
                    binding.arrow4.visibility = VISIBLE
                }
                activatedButtonId = buttonId
            }
        }
    }

    private fun initListeners() {
        binding.button1.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnContentTabsAction.FIRST)
                activateButton(1)
            }
        }
        binding.button2.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnContentTabsAction.SECOND)
                activateButton(2)
            }
        }
        binding.button3.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnContentTabsAction.THIRD)
                activateButton(3)
            }
        }
        binding.button4.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnContentTabsAction.FOURTH)
                activateButton(4)
            }
        }
    }

    fun setListener(listener: OnContentTabsActionListener?) {
        listeners.add(listener)
    }

    fun removeListener(listener: OnContentTabsActionListener?) {
        listeners.remove(listener)
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

    fun setButtonText(buttonId: Int, text: String) {
        if (buttonId > 4 || buttonId < 1) {
            return
        }

        when (buttonId) {
            1 -> binding.text1.text = text
            2 -> binding.text2.text = text
            3 -> binding.text3.text = text
            4 -> binding.text4.text = text
        }
    }

    fun set(buttonId: Int, text: String) {
        if (buttonId > 4 || buttonId < 1) {
            return
        }

        when (buttonId) {
            1 -> binding.text1.text = text
            2 -> binding.text2.text = text
            3 -> binding.text3.text = text
            4 -> binding.text4.text = text
        }
    }

// SAVE

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()!!
        val savedState = SavedState(superState)
        savedState.savedText1 = binding.text1.text.toString()
        savedState.savedText2 = binding.text2.text.toString()
        savedState.savedText3 = binding.text3.text.toString()
        savedState.savedText4 = binding.text4.text.toString()
        savedState.savedActivatedButtonId = activatedButtonId
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)

        val savedText1 = savedState.savedText1 ?: ""
        val savedText2 = savedState.savedText2 ?: ""
        val savedText3 = savedState.savedText3 ?: ""
        val savedText4 = savedState.savedText4 ?: ""

        val savedActivatedButtonId = savedState.savedActivatedButtonId ?: 1

        binding.text1.post {
            binding.text1.text = savedText1
        }
        binding.text2.post {
            binding.text2.text = savedText2
        }
        binding.text3.post {
            binding.text3.text = savedText3
        }
        binding.text4.post {
            binding.text4.text = savedText4
        }

        //Toast.makeText(context, "$savedActivatedButtonId", Toast.LENGTH_SHORT).show()
        activateButton(savedActivatedButtonId)
    }

    class SavedState : BaseSavedState {

        var savedText1: String? = null
        var savedText2: String? = null
        var savedText3: String? = null
        var savedText4: String? = null
        var savedCount1: String? = null
        var savedCount2: String? = null
        var savedCount3: String? = null
        var savedCount4: String? = null
        var savedActivatedButtonId: Int? = null

        constructor(superState: Parcelable) : super(superState)

        constructor(parcel: Parcel) : super(parcel) {
            savedText1 = parcel.readString()
            savedText2 = parcel.readString()
            savedText3 = parcel.readString()
            savedText4 = parcel.readString()
            savedCount1 = parcel.readString()
            savedCount2 = parcel.readString()
            savedCount3 = parcel.readString()
            savedCount4 = parcel.readString()
            savedActivatedButtonId = parcel.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeString(savedText1)
            out.writeString(savedText2)
            out.writeString(savedText3)
            out.writeString(savedText4)
            out.writeString(savedCount1)
            out.writeString(savedCount2)
            out.writeString(savedCount3)
            out.writeString(savedCount4)
            out.writeInt(savedActivatedButtonId!!)
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