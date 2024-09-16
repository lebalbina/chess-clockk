package com.example.chessclockk.views.playpause

import androidx.compose.ui.graphics.vector.ImageVector

data class PlayPauseState(
    val isEnabled: Boolean,
    val onPlayPauseBtnClicked: () -> Unit,
    val icon: ImageVector
)
