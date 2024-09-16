package com.example.chessclockk.screens.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ClockWidget(
    modifier: Modifier,
    clockState: ClockState
) {
    Box(
        modifier
            .fillMaxSize()
            .background(Color(0xFF25BE78))
            .alpha(if (clockState.isEnabled) 1f else 0.5f)
            .clickable { clockState.onClockClicked.invoke() }
    ) {
        Text(
            text = "moves: ${clockState.movesCount}",
            modifier = Modifier
                .graphicsLayer(rotationZ = clockState.rotation)
                .padding(top = 12.dp)
                .align(Alignment.TopEnd)
        )
        Text(
            text = clockState.timeSetting,
            modifier = Modifier
                .graphicsLayer(rotationZ = clockState.rotation)
                .padding(bottom = 24.dp)
                .align(Alignment.BottomCenter)
        )

        Column(
            modifier = Modifier
                .padding(top = 24.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(modifier = Modifier.size(48.dp))
            Text(
                text = clockState.title,
                modifier = Modifier
                    .graphicsLayer(rotationZ = clockState.rotation)
            )
            Text(
                text = clockState.timerValue,
                fontSize = 24.sp,
                modifier = Modifier
                    .graphicsLayer(rotationZ = clockState.rotation)
            )
        }
    }
}

data class ClockState(
    val timerValue: String,
    val onClockClicked: () -> Unit,
    val title: String,
    val isEnabled: Boolean,
    val rotation: Float = 0f,
    val movesCount: String,
    val timeSetting: String
)

@Composable
@Preview
fun ClockPreview() {
    ClockWidget(
        modifier = Modifier,
        clockState = ClockState(
            timerValue = "00:00:00",
            onClockClicked = { },
            title = "PLAYAA",
            isEnabled = true,
            movesCount = "3",
            timeSetting = "3 | 2"
        )
    )
}