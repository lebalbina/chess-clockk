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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.chessclockk.R
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
    onCustomTimeSetClick: () -> Unit,
    onSettingsClicked: () -> Unit
) {

    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    val settingsVisible by remember { mutableStateOf(false) }
    val showRestartDialog = remember { mutableStateOf(false) }

    if (showRestartDialog.value) {
        RestartDialog(
            onRestartConfirmedClick = restartState.onRestartConfirmedClick,
            showDialog = showRestartDialog
        )
    }

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
                onIconClicked = {
                    onCustomTimeSetClick()
                    showBottomSheet = true
                },
                icon = Icons.Filled.Alarm,
                description = "Timer",
            )
            Restart(
                showDialog = { showRestartDialog.value = it },
                restartState = restartState
            )
            if (settingsVisible)
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
    isEnabled: Boolean = true
) {
    Button(
        onClick = onIconClicked,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFBE2578)
        ),
        enabled = isEnabled
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description
        )
    }
}

@Composable
fun RestartDialog(
    onRestartConfirmedClick: () -> Unit,
    showDialog: MutableState<Boolean>
) {
    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        dismissButton = {
            TextButton(onClick = { showDialog.value = false }) {
                Text(stringResource(id = R.string.reset_dialog_dismiss_btn))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onRestartConfirmedClick()
                showDialog.value = false
            }) {
                Text(text = stringResource(id = R.string.reset_dialog_confirm_btn))
            }
        },
        title = { Text(stringResource(id = R.string.reset_dialog_title)) },
        text = { Text(stringResource(id = R.string.reset_dialog_text)) }
    )
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreenContent(
        modifier = Modifier,
        clockWhiteState = ClockState(
            timerValue = "3:02",
            timeSetting = "3\"2'",
            movesCount = "3",
            rotation = 0f,
            isEnabled = true,
            onClockClicked = {},
            backgroundColor = Color.Blue,
            flagIconVisible = false
        ),
        clockBlackState = ClockState(
            timerValue = "3:02",
            timeSetting = "3\"2'",
            movesCount = "3",
            rotation = 180f,
            isEnabled = true,
            onClockClicked = {},
            backgroundColor = Color.Blue,
            flagIconVisible = true
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
        onCustomTimeSet = { _, _ -> },
        onCustomTimeSetClick = {}
    )
}

@Composable
@Preview
fun DialogPreview() {
    val showDialog = remember { mutableStateOf(true) }
    RestartDialog(
        onRestartConfirmedClick = {},
        showDialog = showDialog
    )
}

