package com.thesis.sportologia.utils

import android.content.Context
import android.util.Log
import android.widget.Toast

fun toast(context: Context?, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

fun logBugFix(text: String) {
    Log.d("BUGFIX", text)
}