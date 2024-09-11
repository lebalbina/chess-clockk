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
    timerValue: String,
    onClockClicked: () -> Unit,
    title: String,
    isEnabled: Boolean,
    rotation: Float = 0f,
    movesCount: String,
    timeSetting: String
) {
    Box(
        modifier
            .fillMaxSize()
            .background(Color(0xFF25BE78))
            .alpha(if (isEnabled) 1f else 0.5f)
            .clickable { onClockClicked.invoke() }
    ) {
        Text(
            text = "moves $movesCount",
            modifier = Modifier
                .graphicsLayer(rotationZ = rotation)
                .padding(top = 12.dp)
                .align(Alignment.TopEnd)
        )
        Text(
            text = timeSetting,
            modifier = Modifier
                .graphicsLayer(rotationZ = rotation)
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
                text = title,
                modifier = Modifier
                    .graphicsLayer(rotationZ = rotation)
            )
            Text(
                text = timerValue,
                fontSize = 24.sp,
                modifier = Modifier
                    .graphicsLayer(rotationZ = rotation)
            )
        }
    }
}

@Composable
@Preview
fun ClockPreview() {
    ClockWidget(
        timerValue = "00:00:00",
        onClockClicked = { },
        title = "PLAYAA",
        isEnabled = true,
        movesCount = "3",
        modifier = Modifier,
        timeSetting = "3 | 2"
    )
}