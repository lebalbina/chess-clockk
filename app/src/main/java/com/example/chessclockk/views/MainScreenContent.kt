package com.example.chessclockk.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chessclockk.GameState
import com.example.chessclockk.MainActivityVM

@Composable
fun MainScreenContent(
    viewModel: MainActivityVM = viewModel(),
    openNextScreen: () -> Unit,
    modifier: Modifier
) {

    val clockBlackValue = viewModel.clockBlackLiveData.observeAsState("111")
    val clockWhiteValue = viewModel.clockWhiteLiveData.observeAsState("111")
    val gameState = viewModel.gameStateLiveData.observeAsState(GameState.NEW_GAME)

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
            isEnabled = isBlackEnabled,
            rotation = 180f
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
            SettingsIcon(
                onSettingsClicked = openNextScreen,
                modifier = Modifier
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


@Composable
fun SettingsIcon(
    onSettingsClicked: () -> Unit,
    modifier: Modifier
) {
    Button(
        onClick = onSettingsClicked,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFBE2578)
        )
    ) {
        Icon(
            imageVector = Icons.Filled.Settings,
            contentDescription = "Settings"
        )
    }
}


@Composable
@Preview(showSystemUi = true, showBackground = true)
fun SettingsIconPreview() {
    SettingsIcon(onSettingsClicked = {}, modifier = Modifier)
}

@Composable
@Preview
fun MainViewPreview() {
    MainScreenContent(
        modifier = Modifier,
        openNextScreen = {}
    )
}