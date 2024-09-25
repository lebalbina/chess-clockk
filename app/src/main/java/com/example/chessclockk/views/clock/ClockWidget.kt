package com.example.chessclockk.views.clock

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.delay

@Composable
fun ClockWidget(
    modifier: Modifier,
    clockState: ClockState
) {

    val borderWidth by animateDpAsState(
        targetValue = if (clockState.isEnabled) 8.dp else 0.dp,
        label = ""
    )

    val borderColor by animateColorAsState(
        targetValue = if (clockState.isEnabled) clockState.frameColor else clockState.backgroundColor,
        label = ""
    )

    val boxColor by animateColorAsState(
        targetValue = clockState.backgroundColor,
        label = ""
    )

    var clicked by remember { mutableStateOf(false) }
    var targetFontSize by remember { mutableFloatStateOf(72f) }
    val fontSize by animateFloatAsState(
        targetValue = targetFontSize,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = ""
    )

    LaunchedEffect(clicked) {
        if (clicked) {
            targetFontSize = 82f
            delay(150)
            targetFontSize = 72f
            clicked = false
        }
    }

    Box(
        modifier
            .fillMaxSize()
            .background(
                color = boxColor,
                shape = RoundedCornerShape(16.dp)
            )
            .then(
                if (clockState.isEnabled) {
                    Modifier
                        .clickable {
                            clicked = !clicked
                            clockState.onClockClicked.invoke()
                        }
                        .border(borderWidth, borderColor, RoundedCornerShape(16.dp))
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
                fontSize = fontSize.sp,
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