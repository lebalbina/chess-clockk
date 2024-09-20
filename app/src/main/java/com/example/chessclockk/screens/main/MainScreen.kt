package com.example.chessclockk.screens.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.MaterialTheme
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

    val colors = MaterialTheme.colorScheme

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
                GameState.END_GAME_WHITE -> colors.error
                GameState.BLACK_MOVE -> colors.primary.copy(alpha = 0.5F)
                GameState.PAUSE -> colors.primary.copy(alpha = 0.5f)
                GameState.END_GAME_BLACK, GameState.WHITE_MOVE, GameState.NEW_GAME -> colors.primary
            }
        }
    }

    val clockBlackColor by remember {
        derivedStateOf {
            when (state.value.gameState) {
                GameState.END_GAME_BLACK -> colors.error
                GameState.WHITE_MOVE -> colors.primary.copy(alpha = 0.5F)
                GameState.PAUSE -> colors.primary.copy(alpha = 0.5f)
                GameState.END_GAME_WHITE, GameState.BLACK_MOVE, GameState.NEW_GAME -> colors.primary
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

    val textColorWhite by remember {
        derivedStateOf {
            when (state.value.gameState) {
                GameState.END_GAME_WHITE -> colors.onError
                GameState.WHITE_MOVE -> colors.onPrimary
                GameState.PAUSE, GameState.BLACK_MOVE -> colors.onPrimary.copy(alpha = 0.5F)
                GameState.END_GAME_BLACK, GameState.NEW_GAME -> colors.onPrimary
            }
        }
    }

    val textColorBlack by remember {
        derivedStateOf {
            when (state.value.gameState) {
                GameState.END_GAME_BLACK -> colors.onError
                GameState.BLACK_MOVE -> colors.onPrimary
                GameState.PAUSE, GameState.WHITE_MOVE -> colors.onPrimary.copy(alpha = 0.5F)
                GameState.END_GAME_WHITE, GameState.NEW_GAME -> colors.onPrimary
            }
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
            flagIconVisible = flagIconWhite,
            textColor = textColorWhite,
            flagColor = colors.onError
        ),
        clockBlackState = ClockState(
            timerValue = clockBlackValue.value,
            onClockClicked = { viewModel.onClockBlackPressed() },
            isEnabled = isBlackEnabled,
            movesCount = state.value.blackMovesCount.toString(),
            timeSetting = state.value.timeFormat,
            rotation = 180f,
            backgroundColor = Color(clockBlackColor.value),
            flagIconVisible = flagIconBlack,
            textColor = textColorBlack,
            flagColor = colors.onError
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
        onCustomTimeSetClick = { viewModel.onRestartClicked() },
    )
}

