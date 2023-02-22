package com.thesis.sportologia.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.thesis.sportologia.R

class CancelDialog(
    var negativeAction: ((Unit) -> Unit)?,
    var neutralAction: ((Unit) -> Unit)?,
) : DialogFragment() {

    private var title = "Title"
    private var text = "Text"

    fun setTitle(title: String) {
        this.title = title
    }

    fun setText(text: String) {
        this.text = text
    }

    /*  если положительная (BUTTON_POSITIVE), то сохраняем данные и закрываем приложение
        если отрицательная (BUTTON_NEGATIVE), то закрываем приложение без сохранения
        если нейтральная (BUTTON_NEUTRAL), то не делаем ничего */

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder
                .setTitle(title)
                .setMessage(text)

            if (negativeAction != null) {
                builder.setNegativeButton(R.string.action_delete) { dialog, id ->
                    negativeAction
                }
            }

            if (neutralAction != null) {
                builder.setNeutralButton(R.string.action_cancel) { dialog, id ->
                    neutralAction
                }
            }

                builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}