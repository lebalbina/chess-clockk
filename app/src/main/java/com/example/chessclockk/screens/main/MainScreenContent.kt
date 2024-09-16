package com.example.chessclockk.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.chessclockk.views.clock.ClockState
import com.example.chessclockk.views.clock.ClockWidget
import com.example.chessclockk.views.customtimebottomsheet.CustomTimeSetBottomSheetContent
import com.example.chessclockk.views.playpause.PlayPause
import com.example.chessclockk.views.playpause.PlayPauseState
import com.example.chessclockk.views.restart.Restart
import com.example.chessclockk.views.restart.RestartState
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainScreenContent(
    modifier: Modifier,
    clockWhiteState: ClockState,
    clockBlackState: ClockState,
    playPauseState: PlayPauseState,
    restartState: RestartState,
    onCustomTimeSet: (String, String) -> Unit,
    onSettingsClicked: () -> Unit
) {

    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            CustomTimeSetBottomSheetContent(
                modifier = Modifier,
                onSheetClose = { time, bonus ->
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                    onCustomTimeSet(time, bonus)
                }
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        ClockWidget(
            modifier = Modifier.weight(1f),
            clockState = clockBlackState
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            PlayPause(playPauseState = playPauseState)
            ClickableIcon(
                onIconClicked = { showBottomSheet = true },
                icon = Icons.Filled.Alarm,
                description = "Timer",
            )
            Restart(restartState = restartState)
            ClickableIcon(
                onIconClicked = onSettingsClicked,
                icon = Icons.Filled.Settings,
                description = "Settings"
            )
        }
        ClockWidget(
            modifier = Modifier.weight(1f),
            clockState = clockWhiteState
        )
    }
}

@Composable
fun ClickableIcon(
    icon: ImageVector,
    description: String,
    onIconClicked: () -> Unit,
) {
    Button(
        onClick = onIconClicked,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFBE2578)
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description
        )
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreenContent(
        modifier = Modifier,
        clockWhiteState = ClockState(
            timerValue = "3:02",
            timeSetting = "3\"2'",
            playerLabel = "Player black",
            movesCount = "3",
            rotation = 0f,
            isEnabled = true,
            onClockClicked = {}
        ),
        clockBlackState = ClockState(
            timerValue = "3:02",
            timeSetting = "3\"2'",
            playerLabel = "Player black",
            movesCount = "3",
            rotation = 180f,
            isEnabled = true,
            onClockClicked = {}
        ),
        playPauseState = PlayPauseState(
            icon = Icons.Filled.Refresh,
            isEnabled = true,
            onPlayPauseBtnClicked = {}
        ),
        restartState = RestartState(
            icon = Icons.Filled.Refresh,
            isEnabled = true,
            onRestartClicked = {},
            onRestartConfirmedClick = {}
        ),
        onSettingsClicked = {},
        onCustomTimeSet = { _, _ -> }
    )
}

