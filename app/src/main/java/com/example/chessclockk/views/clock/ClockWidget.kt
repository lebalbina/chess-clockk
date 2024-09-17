package com.example.chessclockk.views.clock

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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chessclockk.R

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
            .then(
                if (clockState.isEnabled) {
                    Modifier.clickable { clockState.onClockClicked.invoke() }
                } else Modifier
            )
            .rotate(clockState.rotation)
    ) {
        Text(
            modifier = Modifier
                .padding(top = 12.dp, end = 12.dp)
                .align(Alignment.TopEnd),
            text = "${stringResource(id = R.string.clock_moves)} ${clockState.movesCount}",
            fontSize = 18.sp
        )
        Text(
            modifier = Modifier
                .padding(bottom = 24.dp)
                .align(Alignment.BottomCenter),
            text = clockState.timeSetting,
            fontSize = 18.sp
        )
        Column(modifier = Modifier.align(Alignment.Center)) {
            Text(
                text = clockState.timerValue,
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
@Preview
fun ClockPreview() {
    ClockWidget(
        modifier = Modifier,
        clockState = ClockState(
            timerValue = "00:00:00",
            onClockClicked = { },
            playerLabel = "PLAYAA",
            isEnabled = true,
            movesCount = "3",
            timeSetting = "3 + 2",
            rotation = 0f
        )
    )
}