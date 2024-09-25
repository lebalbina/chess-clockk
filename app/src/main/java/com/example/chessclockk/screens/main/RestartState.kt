package com.example.chessclockk.screens.main

data class RestartState(
    val isEnabled: Boolean,
    val onRestartClicked: () -> Unit,
    val onRestartConfirmedClick: () -> Unit,
    val onRestartDismissedClick: () -> Unit,
    val showRestartDialog: Boolean
)
