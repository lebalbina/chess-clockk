package com.example.chessclockk.views.clock

data class ClockState(
    val timerValue: String,
    val onClockClicked: () -> Unit,
    val playerLabel: String,
    val isEnabled: Boolean,
    val rotation: Float,
    val movesCount: String,
    val timeSetting: String
)