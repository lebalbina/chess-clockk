package com.example.chessclockk.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ClockWidget(
    modifier: Modifier = Modifier,
    timerValue: String,
    onClockClicked: () -> Unit,
    title: String,
    isEnabled: Boolean,
    rotation: Float = 0f
) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFF25BE78))
            .alpha(if (isEnabled) 1f else 0.5f)
            .clickable { onClockClicked.invoke() },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            modifier = Modifier.graphicsLayer(rotationZ = rotation)
        )
        Text(
            text = timerValue,
            fontSize = 24.sp,
            modifier = Modifier.graphicsLayer(rotationZ = rotation)
        )
    }
}