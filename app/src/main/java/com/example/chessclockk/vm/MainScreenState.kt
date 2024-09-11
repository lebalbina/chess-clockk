package com.example.chessclockk.vm

data class MainScreenState(
    val timeFormat: String,
    val gameState: GameState,
    val blackMovesCount: Int,
    val whiteMovesCount: Int,
)