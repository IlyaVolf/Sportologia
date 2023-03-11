package com.thesis.sportologia.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

fun parseDate(dateAndTime: Calendar): String {
    return DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
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