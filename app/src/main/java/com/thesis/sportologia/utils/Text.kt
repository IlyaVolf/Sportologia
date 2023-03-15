package com.thesis.sportologia.utils

import com.thesis.sportologia.R
import com.thesis.sportologia.ui.users.entities.UserListItem

fun removeEmptyStrings(text: String): String {
    return text.replace("^\\n+|\\n+\$".toRegex(), "")
}

fun concatMap(map: Map<String, Boolean>, separator: String): String {
    val res = StringBuilder().append("")

    var flag = false
    for (item in map) {
        if (flag) {
            res.append(separator)
            flag = false
        }
        if (item.value) {
            res.append(item.key)
            flag = true
        }
    }

    return res.toString()
}