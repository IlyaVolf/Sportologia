package com.thesis.sportologia.utils

import android.content.Context
import com.thesis.sportologia.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

fun parseDate(dateAndTime: Calendar, pattern: String): String {
    return DateTimeFormatter.ofPattern(pattern)
        .withLocale(Locale("ru"))
        .format(
            LocalDateTime.of(
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH) + 1,
                dateAndTime.get(Calendar.DAY_OF_MONTH),
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE),
                dateAndTime.get(Calendar.SECOND),
            )
        )
}

fun parseDatePublication(context: Context, dateAndTime: Calendar): String {
    val dateAndTimeNow = Calendar.getInstance()

    val isTheSameYear = dateAndTimeNow.get(Calendar.YEAR) == dateAndTime.get(Calendar.YEAR) &&
            dateAndTimeNow.get(Calendar.YEAR) == dateAndTime.get(Calendar.YEAR)

    val res = StringBuilder()
    if (dateAndTime.get(Calendar.DAY_OF_MONTH) == dateAndTimeNow.get(Calendar.DAY_OF_MONTH) &&
        dateAndTime.get(Calendar.MONTH) == dateAndTimeNow.get(Calendar.MONTH) &&
        dateAndTime.get(Calendar.YEAR) == dateAndTimeNow.get(Calendar.YEAR)
    ) {
        res.append("${context.getString(R.string.date_today)} ${context.getString(R.string.date_at)} ")
            .append(parseDate(dateAndTime, "H:mm"))
    } else if (dateAndTime.get(Calendar.DAY_OF_MONTH) == (dateAndTimeNow.get(Calendar.DAY_OF_MONTH) - 1) &&
        dateAndTime.get(Calendar.MONTH) == dateAndTimeNow.get(Calendar.MONTH) &&
        dateAndTime.get(Calendar.YEAR) == dateAndTimeNow.get(Calendar.YEAR)
    ) {
        res.append("${context.getString(R.string.date_yesterday)} ${context.getString(R.string.date_at)} ")
            .append(parseDate(dateAndTime, "H:mm"))
    } else {
        res.append(parseDate(dateAndTime, "H:mm"))
        if (isTheSameYear) {
            res.append(parseDate(dateAndTime, "d MMMM"))
        } else {
            res.append(parseDate(dateAndTime, "d MMMM uuuu"))
        }
        res.append(" ${context.getString(R.string.date_at)} ")
            .append(parseDate(dateAndTime, "H:mm"))
    }

    return res.toString()
}

fun parseDateRange(dateAndTimeFrom: Calendar, dateAndTimeTo: Calendar): String {
    val dateAndTimeNow = Calendar.getInstance()
    val isTheSameYear = dateAndTimeNow.get(Calendar.YEAR) == dateAndTimeFrom.get(Calendar.YEAR) &&
            dateAndTimeNow.get(Calendar.YEAR) == dateAndTimeTo.get(Calendar.YEAR)

    val res = StringBuilder()
    if (dateAndTimeFrom.get(Calendar.DAY_OF_MONTH) == dateAndTimeTo.get(Calendar.DAY_OF_MONTH) &&
        dateAndTimeFrom.get(Calendar.MONTH) == dateAndTimeTo.get(Calendar.MONTH) &&
        dateAndTimeFrom.get(Calendar.YEAR) == dateAndTimeTo.get(Calendar.YEAR)
    ) {
        if (isTheSameYear) {
            res.append(parseDate(dateAndTimeFrom, "d MMMM"))
        } else {
            res.append(parseDate(dateAndTimeFrom, "d MMMM uuuu"))
        }
        res.append(", ")
            .append(parseDate(dateAndTimeFrom, "H:mm"))
            .append("-")
            .append(parseDate(dateAndTimeTo, "H:mm"))
    } else {
        if (isTheSameYear) {
            res.append(parseDate(dateAndTimeFrom, "d MMMM, H:mm"))
                .append(" - ")
            res.append(parseDate(dateAndTimeTo, "d MMMM, H:mm"))
        } else {
            res.append(parseDate(dateAndTimeFrom, "d MMMM uuuu, H:mm"))
                .append(" - ")
            res.append(parseDate(dateAndTimeTo, "d MMMM uuuu, H:mm"))
        }
    }

    return res.toString()
}
