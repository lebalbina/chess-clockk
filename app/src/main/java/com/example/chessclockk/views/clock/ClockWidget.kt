package com.example.chessclockk.views.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chessclockk.R
import com.example.chessclockk.ui.theme.getDarkColorScheme
import com.example.chessclockk.ui.theme.getLightColorScheme

@Composable
fun ClockWidget(
    modifier: Modifier,
    clockState: ClockState
) {
    Box(
        modifier
            .fillMaxSize()
            .background(
                color = clockState.backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .then(
                if (clockState.isEnabled) {
                    Modifier
                        .clickable { clockState.onClockClicked.invoke() }
                        .border(8.dp, clockState.frameColor, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                } else Modifier
            )
            .rotate(clockState.rotation)

    ) {
        Text(
            modifier = Modifier
                .padding(top = 12.dp, end = 12.dp)
                .align(Alignment.TopEnd),
            text = "${stringResource(id = R.string.clock_moves)} ${clockState.movesCount}",
            color = clockState.textColor,
            fontSize = 18.sp
        )
        Text(
            modifier = Modifier
                .padding(bottom = 24.dp)
                .align(Alignment.BottomCenter),
            text = clockState.timeSetting,
            color = clockState.textColor,
            fontSize = 18.sp
        )
        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = clockState.timerValue,
                fontSize = 72.sp,
                color = clockState.textColor,
                fontWeight = FontWeight.Bold,
            )
            if (clockState.flagIconVisible) {
                Icon(
                    painter = painterResource(id = R.drawable.flag_regular),
                    contentDescription = "flag",
                    tint = clockState.flagColor,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

@Composable
@Preview(name = "Light Theme", showBackground = true)
fun ClockLightThemePreview() {
    MaterialTheme(colorScheme = getLightColorScheme()) {
        ClockWidget(
            modifier = Modifier,
            clockState = ClockState(
                timerValue = "00:00:00",
                onClockClicked = { },
                isEnabled = true,
                movesCount = "3",
                timeSetting = "3\" + 2'",
                rotation = 0f,
                backgroundColor = colorScheme.primaryContainer,
                flagIconVisible = false,
                textColor = colorScheme.onPrimaryContainer,
                flagColor = colorScheme.onError,
                frameColor = colorScheme.outline
            )
        )
    }
}

@Composable
@Preview(name = "Dark Theme", showBackground = true)
fun ClockDarkThemePreview() {
    MaterialTheme(colorScheme = getDarkColorScheme()) {
        ClockWidget(
            modifier = Modifier,
            clockState = ClockState(
                timerValue = "00:00:00",
                onClockClicked = { },
                isEnabled = true,
                movesCount = "3",
                timeSetting = "3\" + 2'",
                rotation = 0f,
                backgroundColor = colorScheme.primaryContainer,
                flagIconVisible = false,
                textColor = colorScheme.onPrimaryContainer,
                flagColor = colorScheme.onError,
                frameColor = colorScheme.outline
            )
        )
    }
}