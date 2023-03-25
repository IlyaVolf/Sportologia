package com.thesis.sportologia.utils

fun removeEmptyStrings(text: String): String {
    return text.replace("^\\n+|\\n+\$".toRegex(), "")
}

fun formatFloat(number: Float, accuracy: Int, removeLeadingZeros: Boolean): String {
    val numberString = "%.${accuracy}f".format(number)
    val numberSplit = numberString.split(".")
    var fraction = numberSplit.last()
    return if (removeLeadingZeros) {
        while (fraction.lastOrNull() == '0') {
            fraction = fraction.substring(0, fraction.length - 1)
        }
        if (fraction == "") {
            numberSplit.first()
        } else {
            numberSplit.first() + "." + fraction
        }
    } else numberString
}

fun concatMap(map: Map<String, Boolean>?, separator: String): String {
    map ?: return ""

    val text = StringBuilder().append("")

    var flag = false
    for (item in map) {
        if (item.value) {
            text.append(item.key).append(separator)
            flag = true
        }
    }

    if (flag) {
        text.delete(text.length - separator.length, text.length)
    }

    return text.toString()
}

fun concatList(list: List<String>?, separator: String): String {
    list ?: return ""

    val text = StringBuilder().append("")

    var flag = false
    for (item in list) {
        text.append(item).append(separator)
        flag = true
    }

    if (flag) {
        text.delete(text.length - separator.length, text.length)
    }

    return text.toString()
}

fun containsAnyCase(text: String, cantainedText: String): Boolean {
    return text.lowercase().contains(cantainedText.lowercase())
}