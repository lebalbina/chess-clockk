package com.example.chessclockk.views.restart

import androidx.compose.ui.graphics.vector.ImageVector

data class RestartState(
    val isEnabled: Boolean,
    val onRestartClicked: () -> Unit,
    val onRestartConfirmedClick: () -> Unit,
    val icon: ImageVector
)
