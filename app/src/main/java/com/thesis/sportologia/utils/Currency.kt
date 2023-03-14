package com.thesis.sportologia.utils

import android.content.Context
import com.thesis.sportologia.R

fun getCurrencyAbbreviation(context: Context, code: String): String {
    return when (code) {
        "Rubles" -> context.getString(R.string.ruble_abbreviation)
        else -> ""
    }
}

fun convertPrice(context: Context, price: Float, currency: String): String {
    return if (price == 0f) {
        context.getString(R.string.free)
    } else {
        var priceString = price.toString()
        val priceSplit = priceString.split(".")
        priceString = if (priceSplit.last() == "0") {
            priceSplit.first()
        } else {
            price.toString()
        }
        "$priceString ${getCurrencyAbbreviation(context, currency)}"
    }
}