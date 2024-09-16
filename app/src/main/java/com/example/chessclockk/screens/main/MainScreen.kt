package com.example.chessclockk.screens.main

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
import com.example.chessclockk.screens.views.ClockState
import com.example.chessclockk.screens.views.ClockWidget
import com.example.chessclockk.screens.views.CustomTimeSetBottomSheetContent
import com.example.chessclockk.screens.views.PlayPause
import com.example.chessclockk.screens.views.PlayPauseState
import com.example.chessclockk.screens.views.Restart
import com.example.chessclockk.screens.views.RestartState
import com.example.chessclockk.vm.GameState
import com.example.chessclockk.vm.IMainActivityVM.MainScreenState
import com.example.chessclockk.vm.MainActivityVM
import kotlinx.coroutines.launch
import java.time.Clock

//TODO separate concerns - screen class with state and children
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainScreen(
    viewModel: MainActivityVM,
    modifier: Modifier
) {

    val clockBlackValue = viewModel.clockBlackLiveData.observeAsState("111")
    val clockWhiteValue = viewModel.clockWhiteLiveData.observeAsState("111")

    val state = viewModel.stateLiveData.observeAsState(
        MainScreenState(
            timeFormat = "",
            whiteMovesCount = 0,
            blackMovesCount = 0,
            gameState = GameState.NEW_GAME
        )
    )

    val isWhiteEnabled by remember {
        derivedStateOf {
            state.value.gameState == GameState.WHITE_MOVE || state.value.gameState == GameState.NEW_GAME
        }
    }

    val isBlackEnabled by remember {
        derivedStateOf {
            state.value.gameState == GameState.BLACK_MOVE || state.value.gameState == GameState.NEW_GAME
        }
    }

    val playPauseIcon by remember {
        derivedStateOf {
            when (state.value.gameState) {
                GameState.WHITE_MOVE -> Icons.Filled.Pause
                GameState.BLACK_MOVE -> Icons.Filled.Pause
                GameState.PAUSE -> Icons.Filled.PlayArrow
                GameState.NEW_GAME -> Icons.Filled.PlayArrow
            }
        }
    }

    val isPlayPauseBtnEnabled by remember {
        derivedStateOf { state.value.gameState != GameState.NEW_GAME }
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
            clockState = ClockState(
                timerValue = clockBlackValue.value,
                onClockClicked = { viewModel.onClockBlackPressed() },
                title = "PLAYER BLACK",
                isEnabled = isBlackEnabled,
                rotation = 180f,
                movesCount = state.value.blackMovesCount.toString(),
                timeSetting = state.value.timeFormat
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            PlayPause(
                PlayPauseState(
                    isEnabled = isPlayPauseBtnEnabled,
                    onPlayPauseBtnClicked = { viewModel.onPlayPauseBtnClicked() },
                    icon = playPauseIcon
                )
            )
            ClickableIcon(
                onIconClicked = { showBottomSheet = true },
                icon = Icons.Filled.Alarm,
                description = "Timer",
            )
            Restart(
                RestartState(
                    isEnabled = isPlayPauseBtnEnabled,
                    onRestartClicked = { viewModel.onRestartClicked() },
                    onRestartConfirmedClick = { viewModel.onRestartConfirmedClicked() },
                    icon = Icons.Filled.Refresh
                )
            )
            ClickableIcon(
                onIconClicked = { },
                icon = Icons.Filled.Settings,
                description = "Settings"
            )
        }
        ClockWidget(
            modifier = Modifier.weight(1f),
            clockState = ClockState(
                timerValue = clockWhiteValue.value,
                onClockClicked = { viewModel.onClockWhitePressed() },
                title = "PLAYER WHITE",
                isEnabled = isWhiteEnabled,
                movesCount = state.value.whiteMovesCount.toString(),
                timeSetting = state.value.timeFormat
            )
        )
    }
}


//@Composable
//@Preview
//fun MainViewPreview() {
//    MainScreen(
//        modifier = Modifier,
//        null
//    )
//}