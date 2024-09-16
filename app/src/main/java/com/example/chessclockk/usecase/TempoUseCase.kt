package com.example.chessclockk.usecase

import android.content.SharedPreferences
import javax.inject.Inject

class TempoUseCase @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    fun saveTempo(gameTempo: String, bonusTempo: String) {
        sharedPreferences
            .edit()
            .putString("game_tempo", gameTempo)
            .putString("bonus_tempo", bonusTempo)
            .apply()
    }

    fun retrieveTempo(): Pair<String, String> {
        val gameTempo = sharedPreferences.getString("game_tempo", "00:03:00") ?: "00:03:00"
        val bonusTempo = sharedPreferences.getString("bonus_tempo", "00:02") ?: "00:02"
        return Pair(gameTempo, bonusTempo)
    }
}
