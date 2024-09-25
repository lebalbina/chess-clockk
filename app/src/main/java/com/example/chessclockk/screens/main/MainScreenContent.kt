package com.example.chessclockk.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chessclockk.R
import com.example.chessclockk.ui.theme.getDarkColorScheme
import com.example.chessclockk.ui.theme.getLightColorScheme
import com.example.chessclockk.views.clock.ClockState
import com.example.chessclockk.views.clock.ClockWidget
import com.example.chessclockk.views.customtimebottomsheet.CustomTimeSetBottomSheetContent
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

    val playPauseIconDescription = remember {
        when (playPauseState.icon) {
            Icons.Filled.Pause -> "Pause"
            Icons.Filled.PlayArrow -> "Play"
            else -> "Unrecognized"
        }
    }

    if (restartState.showRestartDialog) {
        RestartDialog(
            onRestartConfirmedClick = restartState.onRestartConfirmedClick,
            onRestartDismissedClick = restartState.onRestartDismissedClick
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
            .padding(24.dp)
    ) {
        ClockWidget(
            modifier = Modifier.weight(1f),
            clockState = clockBlackState
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp, top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            ClickableIcon(
                icon = playPauseState.icon,
                onIconClicked = playPauseState.onPlayPauseBtnClicked,
                description = playPauseIconDescription,
                isEnabled = playPauseState.isEnabled
            )
            ClickableIcon(
                onIconClicked = {
                    onCustomTimeSetClick()
                    showBottomSheet = true
                },
                icon = Icons.Filled.Alarm,
                description = "Timer",
            )
            ClickableIcon(
                onIconClicked = restartState.onRestartClicked,
                icon = Icons.Filled.Refresh,
                isEnabled = restartState.isEnabled,
                description = "Restart"
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
    val colors = MaterialTheme.colorScheme
    Button(
        onClick = onIconClicked,
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.tertiary,
            contentColor = colors.onTertiary,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.LightGray
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = colors.onTertiary
        )
    }
}

@Composable
fun RestartDialog(
    onRestartConfirmedClick: () -> Unit,
    onRestartDismissedClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onRestartDismissedClick,
        dismissButton = {
            TextButton(onClick = onRestartDismissedClick) {
                Text(stringResource(id = R.string.reset_dialog_dismiss_btn))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onRestartConfirmedClick()
                onRestartDismissedClick()
            }) {
                Text(text = stringResource(id = R.string.reset_dialog_confirm_btn))
            }
        },
        title = { Text(stringResource(id = R.string.reset_dialog_title)) },
        text = { Text(stringResource(id = R.string.reset_dialog_text)) }
    )
}

@Preview(name = "Dark Theme", showBackground = true)
@Composable
fun MainScreenDarkThemePreview() {
    MaterialTheme(
        colorScheme = getDarkColorScheme()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            MainScreenContent(
                modifier = Modifier,
                clockWhiteState = ClockState(
                    timerValue = "3:02",
                    timeSetting = "3\"2'",
                    movesCount = "3",
                    rotation = 0f,
                    isEnabled = true,
                    onClockClicked = {},
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    flagIconVisible = false,
                    textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    flagColor = MaterialTheme.colorScheme.onError,
                    frameColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                clockBlackState = ClockState(
                    timerValue = "01:01:02",
                    timeSetting = "3\"2'",
                    movesCount = "3",
                    rotation = 180f,
                    isEnabled = true,
                    onClockClicked = {},
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    flagIconVisible = false,
                    textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    flagColor = MaterialTheme.colorScheme.onError,
                    frameColor = MaterialTheme.colorScheme.outline
                ),
                playPauseState = PlayPauseState(
                    icon = Icons.Filled.Refresh,
                    isEnabled = false,
                    onPlayPauseBtnClicked = {}
                ),
                restartState = RestartState(
                    isEnabled = true,
                    onRestartClicked = {},
                    onRestartConfirmedClick = {},
                    onRestartDismissedClick = {},
                    showRestartDialog = false
                ),
                onSettingsClicked = {},
                onCustomTimeSet = { _, _ -> },
                onCustomTimeSetClick = {},
            )
        }
    }
}

@Preview(name = "Light Theme", showBackground = true)
@Composable
fun MainScreenLightThemePreview() {
    MaterialTheme(
        colorScheme = getLightColorScheme()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            MainScreenContent(
                modifier = Modifier,
                clockWhiteState = ClockState(
                    timerValue = "3:02",
                    timeSetting = "3\"2'",
                    movesCount = "3",
                    rotation = 0f,
                    isEnabled = true,
                    onClockClicked = {},
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    flagIconVisible = false,
                    textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    flagColor = MaterialTheme.colorScheme.onError,
                    frameColor = MaterialTheme.colorScheme.outline
                ),
                clockBlackState = ClockState(
                    timerValue = "01:01:02",
                    timeSetting = "3\"2'",
                    movesCount = "3",
                    rotation = 180f,
                    isEnabled = true,
                    onClockClicked = {},
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    flagIconVisible = false,
                    textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    flagColor = MaterialTheme.colorScheme.onError,
                    frameColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                playPauseState = PlayPauseState(
                    icon = Icons.Filled.Refresh,
                    isEnabled = true,
                    onPlayPauseBtnClicked = {}
                ),
                restartState = RestartState(
                    isEnabled = true,
                    onRestartClicked = {},
                    onRestartConfirmedClick = {},
                    onRestartDismissedClick = {},
                    showRestartDialog = false
                ),
                onSettingsClicked = {},
                onCustomTimeSet = { _, _ -> },
                onCustomTimeSetClick = {},
            )
        }
    }
}

@Composable
@Preview
fun DialogPreview() {
    RestartDialog(
        onRestartConfirmedClick = {},
        onRestartDismissedClick = {}
    )
}


