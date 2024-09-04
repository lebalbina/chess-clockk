package com.example.chessclockk.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chessclockk.GameState
import com.example.chessclockk.MainActivityVM
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainScreenContent(
    viewModel: MainActivityVM = viewModel(),
    openNextScreen: () -> Unit,
    modifier: Modifier
) {

    val clockBlackValue = viewModel.clockBlackLiveData.observeAsState("111")
    val clockWhiteValue = viewModel.clockWhiteLiveData.observeAsState("111")
    val gameState = viewModel.gameStateLiveData.observeAsState(GameState.NEW_GAME)
    val blackMovesCounter = viewModel.blackMovesCountLiveData.observeAsState("0")
    val whiteMovesCounter = viewModel.whiteMovesCountLiveData.observeAsState("0")

    val isWhiteEnabled by remember {
        derivedStateOf {
            gameState.value == GameState.WHITE_MOVE || gameState.value == GameState.NEW_GAME
        }
    }

    val isBlackEnabled by remember {
        derivedStateOf {
            gameState.value == GameState.BLACK_MOVE || gameState.value == GameState.NEW_GAME
        }
    }

    val playPauseIcon by remember {
        derivedStateOf {
            when (gameState.value) {
                GameState.WHITE_MOVE -> Icons.Filled.Pause
                GameState.BLACK_MOVE -> Icons.Filled.Pause
                GameState.PAUSE -> Icons.Filled.PlayArrow
                GameState.NEW_GAME -> Icons.Filled.PlayArrow
            }
        }
    }

    val isPlayPauseBtnEnabled by remember {
        derivedStateOf { gameState.value != GameState.NEW_GAME }
    }

    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            CustomTimeSetBottomSheetContent(
                sheetState = sheetState,
                onDismissRequest = { showBottomSheet = false },
                onSheetClose = { time, bonus ->
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                    viewModel.onCustomTimeSet(time, bonus)
                },
                modifier = Modifier
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
            timerValue = clockBlackValue.value,
            onClockClicked = { viewModel.onClockBlackPressed() },
            title = "PLAYER BLACK",
            isEnabled = isBlackEnabled,
            rotation = 180f,
            movesCount = blackMovesCounter.value
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            PlusMinus(
                isEnabled = gameState.value == GameState.NEW_GAME,
                onPlusBtnClicked = { isLongPress -> viewModel.onPlusBtnClicked(isLongPress) },
                onPlsBtnReleased = { viewModel.onPlusBtnReleased() },
                onMinusBtnClicked = { isLongPress -> viewModel.onMinusBtnClicked(isLongPress) },
                onMinusBtnReleased = { viewModel.onMinusBtnReleased() }
            )
            ClickableIcon(
                onIconClicked = { showBottomSheet = true },
                icon = Icons.Filled.Alarm,
                description = "Timer",
            )
            PlayPause(
                isEnabled = isPlayPauseBtnEnabled,
                onPlayPauseBtnClicked = { viewModel.onPlayPauseBtnClicked() },
                icon = playPauseIcon
            )
            Restart(
                isEnabled = isPlayPauseBtnEnabled,
                onRestartClicked = { viewModel.onRestartClicked() },
                onRestartConfirmedClick = { viewModel.onRestartConfirmedClicked() },
                icon = Icons.Filled.Refresh
            )
            ClickableIcon(
                onIconClicked = openNextScreen,
                icon = Icons.Filled.Settings,
                description = "Settings"
            )
        }
        ClockWidget(
            modifier = Modifier.weight(1f),
            timerValue = clockWhiteValue.value,
            onClockClicked = { viewModel.onClockWhitePressed() },
            title = "PLAYER WHITE",
            isEnabled = isWhiteEnabled,
            movesCount = whiteMovesCounter.value
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

@Composable
@Preview
fun MainViewPreview() {
    MainScreenContent(
        modifier = Modifier,
        openNextScreen = {}
    )
}