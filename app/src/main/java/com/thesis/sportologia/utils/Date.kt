package com.thesis.sportologia.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

fun parseDate(dateAndTime: Calendar): String {
    return DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(Locale("ru"))
        .format(
            LocalDate.of(
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH) + 1,
                dateAndTime.get(Calendar.DAY_OF_MONTH)
            )
        )
}