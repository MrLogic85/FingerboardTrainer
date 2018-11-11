package com.sleepyduck.fingerboardtrainer.data

import androidx.annotation.DrawableRes
import com.sleepyduck.fingerboardtrainer.R

// *** *** *** DO NOT CHANGE THE ID'S, THEY ARE HARD LINKED IN THE DATABASE TO THE ICON RESOURCE *** *** ***
enum class Icon(val id: Int) {
    GIRL_EASY_CLIMB(1),
    GIRL_NORMAL_CLIMB(2),
    GIRL_MEDIUM_CLIMB(3),
    GIRL_HARD_CLIMB(4);

    @DrawableRes
    fun iconRes() = when (this) {
        GIRL_EASY_CLIMB -> R.drawable.icon_climb_1
        GIRL_NORMAL_CLIMB -> R.drawable.icon_climb_2
        GIRL_MEDIUM_CLIMB -> R.drawable.icon_climb_3
        GIRL_HARD_CLIMB -> R.drawable.icon_climb_4
    }
}

fun Int.toIcon() = when (this) {
    1 -> Icon.GIRL_EASY_CLIMB
    2 -> Icon.GIRL_NORMAL_CLIMB
    3 -> Icon.GIRL_MEDIUM_CLIMB
    4 -> Icon.GIRL_HARD_CLIMB

    else -> Icon.GIRL_EASY_CLIMB
}

fun Int.toIconRes() = toIcon().iconRes()