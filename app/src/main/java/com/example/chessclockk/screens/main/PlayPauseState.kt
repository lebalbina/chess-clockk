package com.example.chessclockk.screens.main

import androidx.compose.ui.graphics.vector.ImageVector

data class PlayPauseState(
    val isEnabled: Boolean,
    val onPlayPauseBtnClicked: () -> Unit,
    val icon: ImageVector,
//    val color: Color
)
