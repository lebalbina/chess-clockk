package com.example.chessclockk.screens.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.chessclockk.views.clock.ClockState
import com.example.chessclockk.views.playpause.PlayPauseState
import com.example.chessclockk.views.restart.RestartState
import com.example.chessclockk.vm.GameState
import com.example.chessclockk.vm.IMainActivityVM.MainScreenState
import com.example.chessclockk.vm.MainActivityVM

@Composable

fun MainScreen(
    viewModel: MainActivityVM,
    modifier: Modifier
) {

    val clockBlackValue = viewModel.clockBlackLiveData.observeAsState("111")
    val clockWhiteValue = viewModel.clockWhiteLiveData.observeAsState("111")

    val state = viewModel.stateLiveData.observeAsState(
        MainScreenState(
            timeFormat = "3\" + 2'",
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

    val clockWhiteColor by remember {
        derivedStateOf {
            when (state.value.gameState) {
                GameState.END_GAME_WHITE -> Color.Red
                GameState.BLACK_MOVE -> Color.Gray
                GameState.PAUSE -> Color.Gray
                GameState.END_GAME_BLACK, GameState.WHITE_MOVE, GameState.NEW_GAME -> Color.Green
            }
        }
    }

    val clockBlackColor by remember {
        derivedStateOf {
            when (state.value.gameState) {
                GameState.END_GAME_BLACK -> Color.Red
                GameState.WHITE_MOVE -> Color.Gray
                GameState.PAUSE -> Color.Gray
                GameState.END_GAME_WHITE, GameState.BLACK_MOVE, GameState.NEW_GAME -> Color.Green
            }
        }
    }

    val playPauseIcon by remember {
        derivedStateOf {
            when (state.value.gameState) {
                GameState.WHITE_MOVE -> Icons.Filled.Pause
                GameState.BLACK_MOVE -> Icons.Filled.Pause
                GameState.PAUSE -> Icons.Filled.PlayArrow
                GameState.NEW_GAME -> Icons.Filled.PlayArrow
                GameState.END_GAME_BLACK, GameState.END_GAME_WHITE -> Icons.Filled.PlayArrow
            }
        }
    }

    val isPlayPauseBtnEnabled by remember {
        derivedStateOf {
            state.value.gameState != GameState.NEW_GAME &&
                    state.value.gameState != GameState.END_GAME_BLACK &&
                    state.value.gameState != GameState.END_GAME_WHITE
        }
    }

    val isRestartEnabled by remember {
        derivedStateOf {
            state.value.gameState != GameState.NEW_GAME
        }
    }

    val flagIconWhite by remember {
        derivedStateOf {
            state.value.gameState == GameState.END_GAME_WHITE
        }
    }

    val flagIconBlack by remember {
        derivedStateOf {
            state.value.gameState == GameState.END_GAME_BLACK
        }
    }

    MainScreenContent(
        modifier = modifier,
        clockWhiteState = ClockState(
            timerValue = clockWhiteValue.value,
            onClockClicked = { viewModel.onClockWhitePressed() },
            isEnabled = isWhiteEnabled,
            rotation = 0f,
            movesCount = state.value.whiteMovesCount.toString(),
            timeSetting = state.value.timeFormat,
            backgroundColor = Color(clockWhiteColor.value),
            flagIconVisible = flagIconWhite
        ),
        clockBlackState = ClockState(
            timerValue = clockBlackValue.value,
            onClockClicked = { viewModel.onClockBlackPressed() },
            isEnabled = isBlackEnabled,
            movesCount = state.value.blackMovesCount.toString(),
            timeSetting = state.value.timeFormat,
            rotation = 180f,
            backgroundColor = Color(clockBlackColor.value),
            flagIconVisible = flagIconBlack
        ),
        playPauseState = PlayPauseState(
            isEnabled = isPlayPauseBtnEnabled,
            onPlayPauseBtnClicked = { viewModel.onPlayPauseBtnClicked() },
            icon = playPauseIcon
        ),
        restartState = RestartState(
            isEnabled = isRestartEnabled,
            onRestartClicked = { viewModel.onRestartClicked() },
            onRestartConfirmedClick = { viewModel.onRestartConfirmedClicked() },
            icon = Icons.Filled.Refresh
        ),
        onSettingsClicked = {},
        onCustomTimeSet = { time, bonus -> viewModel.onCustomTimeSet(time, bonus) },
        onCustomTimeSetClick = { viewModel.onRestartClicked() }
    )
}
