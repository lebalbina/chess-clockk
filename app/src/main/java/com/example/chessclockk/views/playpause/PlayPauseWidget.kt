package com.example.chessclockk.views.playpause

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PlayPause(
    modifier: Modifier = Modifier,
    playPauseState: PlayPauseState
) {
    Button(
        enabled = playPauseState.isEnabled,
        onClick = playPauseState.onPlayPauseBtnClicked,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFBE2578)
        ),
    ) {
        Icon(
            imageVector = playPauseState.icon,
            contentDescription = when (playPauseState.icon) {
                Icons.Filled.Pause -> "Pause"
                Icons.Filled.PlayArrow -> "Play"
                else -> "Unrecognized"
            }
        )
    }
}

@Composable
@Preview
fun PlayPausePreview() {
    PlayPause(
        modifier = Modifier,
        PlayPauseState(
            isEnabled = true,
            onPlayPauseBtnClicked = { },
            icon = Icons.Filled.Pause
        )
    )
}
