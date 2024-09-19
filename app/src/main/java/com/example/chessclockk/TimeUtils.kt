package com.example.chessclockk

import java.util.concurrent.TimeUnit

fun String.convertHHMMSSToMillis(): Long {
    val splitList = this.split(":").map { it.toLong() }
    return when (splitList.size) {
        3 -> TimeUnit.HOURS.toMillis(splitList[0]) +
                TimeUnit.MINUTES.toMillis(splitList[1]) +
                TimeUnit.SECONDS.toMillis(splitList[2])

        2 -> TimeUnit.MINUTES.toMillis(splitList[0]) +
                TimeUnit.SECONDS.toMillis(splitList[1])

        else -> throw IllegalArgumentException("Invalid time format; HH:MM(:SS) accepted")
    }
}

fun Long.millisToHHMMSS(): String {
    val hours = TimeUnit.MILLISECONDS.toHours(this)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) % 60

    return if (hours == 0L) {
        "%d:%02d".format(minutes, seconds)
    } else {
        "%d:%02d:%02d".format(hours, minutes, seconds)
    }
}

fun Pair<String, String>.convertGameAndBonusTimeToTempo(): String {
    val (gameTime, bonusTime) = this

    val splitGameTime = gameTime.split(":").map { it.toLong() }
    val gameMinutes =
        if (splitGameTime[0] != 0L) "${splitGameTime[0] * 60 + splitGameTime[1]}\'" else "${splitGameTime[1]}\'"
    val gameSeconds = if (splitGameTime[2] != 0L) " ${splitGameTime[2]}\"" else ""

    val gameTempo = gameMinutes + gameSeconds

    val splitBonus = bonusTime.split(":").map { it.toLong() }
    val bonusSeconds =
        if (splitBonus[0] != 0L) splitBonus[0] * 60 + splitBonus[1] else splitBonus[1]
    val bonusTempo = if (bonusSeconds != 0L) " + $bonusSeconds\"" else ""
    return gameTempo + bonusTempo
}



