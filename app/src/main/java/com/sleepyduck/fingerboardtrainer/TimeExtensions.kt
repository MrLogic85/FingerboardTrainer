package com.sleepyduck.fingerboardtrainer

import java.util.*

fun Long.toMillisString(): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    return when {
        this > 60000L -> "${calendar.get(Calendar.MINUTE)}m ${calendar.get(Calendar.SECOND)}s"
        else -> "${calendar.get(Calendar.SECOND)}s"
    }
}