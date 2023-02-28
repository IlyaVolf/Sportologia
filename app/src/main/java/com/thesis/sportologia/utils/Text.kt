package com.thesis.sportologia.utils

fun removeEmptyStrings(text: String): String {
    return text.replace("^\\n+|\\n+\$".toRegex(), "")
}