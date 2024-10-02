package com.example.chessclockk

import com.example.chessclockk.vm.GameState

interface IClockk {
    fun startClock(
        updateWhite: (Long) -> Unit,
        updateBlack: (Long) -> Unit,
        onGameEnd: (GameState) -> Unit
    )

    fun stopClock()
    fun updateGameState(gameState: GameState)
    fun setTempo(tempo: String, bonus: String)
}

