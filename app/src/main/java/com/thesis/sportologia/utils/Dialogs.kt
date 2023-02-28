package com.thesis.sportologia.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_NEUTRAL
import com.thesis.sportologia.R

typealias DialogOnClickAction = (DialogInterface, Int) -> Unit

fun createSimpleDialog(
    context: Context,
    titleText: String?,
    messageText: String?,
    negativeButtonText: String?,
    negativeButtonAction: DialogOnClickAction?,
    neutralButtonText: String?,
    neutralButtonAction: DialogOnClickAction?,
    positiveButtonText: String?,
    positiveButtonAction: DialogOnClickAction?,
) {

    val builder = AlertDialog.Builder(context, R.style.DialogStyleBasic)

    if (titleText != null) {
        builder.setTitle(messageText)
    }

    if (messageText != null) {
        builder.setMessage(messageText)
    }

    if (negativeButtonText != null && negativeButtonAction != null) {
        builder.setNegativeButton(negativeButtonText) { dialog, which ->
            negativeButtonAction(dialog, which)
        }
    }

    if (negativeButtonText != null && neutralButtonAction != null) {
        builder.setNeutralButton(neutralButtonText) { dialog, which ->
            neutralButtonAction(dialog, which)
        }
    }

    if (positiveButtonText != null && positiveButtonAction != null) {
        builder.setPositiveButton(positiveButtonText) { dialog, which ->
            positiveButtonAction(dialog, which)
        }
    }

    val dialog: AlertDialog = builder.create()

    dialog.show()

    dialog.getButton(BUTTON_NEGATIVE)
        .setTextColor(context.getColor(R.color.purple_medium))
    dialog.getButton(BUTTON_NEUTRAL)
        .setTextColor(context.getColor(R.color.purple_medium))

    dialog.getButton(BUTTON_NEGATIVE).isAllCaps = false
    dialog.getButton(BUTTON_NEUTRAL).isAllCaps = false
}

fun createSpinnerDialog(
    context: Context,
    titleText: String?,
    messageText: String?,
    items: Array<Pair<String, DialogOnClickAction?>>,
) {

    val builder = AlertDialog.Builder(context, R.style.DialogStyleBasic)

    if (titleText != null) {
        builder.setTitle(messageText)
    }

    if (messageText != null) {
        builder.setMessage(messageText)
    }

    builder.setItems(
        items.map { it.first }.toTypedArray()
    ) { dialog, which ->
        items[which].second?.invoke(dialog, which)
    }

    val dialog: AlertDialog = builder.create()

    dialog.show()
}