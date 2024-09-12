package com.example.chessclockk

import java.util.concurrent.TimeUnit

//TODO zrobic metody na powtarzajacy sie kod - split
fun String.extractHHMMSSformatToMillis(): Long {
    val splitList = this.split(":")
    val hours = splitList[0].toLong()
    val minutes = splitList[1].toLong()
    val seconds = splitList[2].toLong()

    return TimeUnit.HOURS.toMillis(hours) + TimeUnit.MINUTES.toMillis(minutes) +
            TimeUnit.SECONDS.toMillis(seconds)
}

fun String.extractMMSSformatToMillis(): Long {
    val splitList = this.split(":")
    val minutes = splitList[0].toLong()
    val seconds = splitList[1].toLong()

    return TimeUnit.MINUTES.toMillis(minutes) + TimeUnit.SECONDS.toMillis(seconds)
}

fun Long.millisToFormattedString(): String {
    val hours = TimeUnit.MILLISECONDS.toHours(this)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) % 60

    return if (hours == 0L) {
        "%02d:%02d".format(minutes, seconds)
    } else {
        "%01d:%02d:%02d".format(hours, minutes, seconds)
    }
}

fun Pair<String, String>.extractHHMMSSMMSStoTempo(): String {
    val stringBuilder = StringBuilder()

    val splitGameTime = this.first.split(":")
    val hours = splitGameTime[0].toLong()
    val minutes = splitGameTime[1].toLong()
    val seconds = splitGameTime[2].toLong()

    val gameTempo = if (hours != 0L) hours * 60 + minutes else minutes
    stringBuilder.append("$gameTempo\'")
    if (seconds != 0L) {
        stringBuilder.append("$seconds\"")
    }

    val splitBonus = this.second.split(":")
    val bonusMinutes = splitBonus[0].toLong()
    val bonusSeconds = splitBonus[1].toLong()

    if (bonusSeconds != 0L) {
        stringBuilder.append(" + ")
        val bonusTempo = if (bonusMinutes != 0L) bonusMinutes * 60 + bonusSeconds else bonusSeconds
        stringBuilder.append("$bonusTempo\"")

    }
    return stringBuilder.toString()
}


