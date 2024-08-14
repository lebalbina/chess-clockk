package com.example.chessclockk.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chessclockk.GameState
import com.example.chessclockk.MainActivityVM

@Composable
fun MainView(viewModel: MainActivityVM = viewModel()) {

    val clockBlackValue = viewModel.clockBlackLiveData.observeAsState("111")
    val clockWhiteValue = viewModel.clockWhiteLiveData.observeAsState("111")
    val gameState = viewModel.gameStateFlow.collectAsState()

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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(24.dp)
    ) {
        ClockWidget(
            modifier = Modifier.weight(1f),
            timerValue = clockBlackValue.value,
            onClockClicked = { viewModel.onClockBlackPressed() },
            title = "PLAYER BLACK",
            isEnabled = isBlackEnabled
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            PlusMinusPause(
                state = gameState.value,
                onPlusBtnClicked = { isLongPress -> viewModel.onPlusBtnClicked(isLongPress) },
                onPlsBtnReleased = { viewModel.onPlusBtnReleased() },
                onMinusBtnClicked = { isLongPress -> viewModel.onMinusBtnClicked(isLongPress) },
                onMinusBtnReleased = { viewModel.onMinusBtnReleased() }
            )
            PlayPauseComposable(
                state = gameState.value,
                onPlayPauseBtnClicked = { viewModel.onPlayPauseBtnClicked() }
            )
        }
        ClockWidget(
            modifier = Modifier.weight(1f),
            timerValue = clockWhiteValue.value,
            onClockClicked = { viewModel.onClockWhitePressed() },
            title = "PLAYER WHITE",
            isEnabled = isWhiteEnabled
        )
    }
}