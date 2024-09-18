package com.example.chessclockk.views.clock

import androidx.compose.ui.graphics.Color

data class ClockState(
    val timerValue: String,
    val onClockClicked: () -> Unit,
    val isEnabled: Boolean,
    val rotation: Float,
    val movesCount: String,
    val timeSetting: String,
    val backgroundColor: Color,
    val flagIconVisible: Boolean
)