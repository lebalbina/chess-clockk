package com.example.chessclockk.screens.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PlayPause(
    isEnabled: Boolean,
    onPlayPauseBtnClicked: () -> Unit,
    icon: ImageVector
) {
    Button(
        enabled = isEnabled,
        onClick = onPlayPauseBtnClicked,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFBE2578)
        ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = when (icon) {
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
    PlayPause(isEnabled = true, onPlayPauseBtnClicked = { }, icon = Icons.Filled.Pause)
}
