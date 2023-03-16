package com.thesis.sportologia.utils

import android.content.Context
import androidx.annotation.StringRes
import com.thesis.sportologia.R

fun getCurrencyAbbreviation(context: Context, currency: String): String? {
    return when (currency) {
        "Rubles" -> context.getString(R.string.ruble_abbreviation)
        else -> null
    }
}

fun getCurrencyByAbbreviation(context: Context, @StringRes currencyAbbr: Int): String? {
    return when (currencyAbbr) {
        R.string.ruble_abbreviation -> "Rubles"
        else -> null
    }
}

fun formatPrice(priceString: String): String {
    val priceSplit = priceString.split(".")
    return if (priceSplit.last() == "0") {
        priceSplit.first()
    } else {
        priceString
    }
}

fun getPriceWithCurrency(context: Context, price: Float, currency: String): String {
    return if (price == 0f) {
        context.getString(R.string.free)
    } else {
        val priceString = formatPrice(price.toString())
        "$priceString ${getCurrencyAbbreviation(context, currency)}"
    }
}