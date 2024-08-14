package com.example.chessclockk.views

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.chessclockk.GameState

@Composable
fun PlayPauseComposable(
    state: GameState,
    onPlayPauseBtnClicked: () -> Unit
) {
    val isEnabled = state != GameState.NEW_GAME

    Button(
        enabled = isEnabled,
        onClick = { onPlayPauseBtnClicked() },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFBE2578)
        ),
        modifier = Modifier.size(100.dp)
    ) {
        Icon(
            imageVector = when (state) {
                GameState.WHITE_MOVE -> Icons.Filled.Pause
                GameState.BLACK_MOVE -> Icons.Filled.Pause
                GameState.PAUSE -> Icons.Filled.PlayArrow
                GameState.NEW_GAME -> Icons.Filled.PlayArrow
            },
            contentDescription = when (state) {
                GameState.WHITE_MOVE -> "Pause"
                GameState.BLACK_MOVE -> "Pause"
                GameState.PAUSE -> "Play"
                GameState.NEW_GAME -> "Play"
            },
            modifier = Modifier.size(48.dp)
        )
    }
}
