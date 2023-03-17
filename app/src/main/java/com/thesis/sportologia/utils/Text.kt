package com.thesis.sportologia.utils

fun removeEmptyStrings(text: String): String {
    return text.replace("^\\n+|\\n+\$".toRegex(), "")
}

fun concatMap(map: Map<String, Boolean>, separator: String): String {
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