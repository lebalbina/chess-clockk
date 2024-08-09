package com.example.chessclockk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chessclockk.ui.theme.ChessClockkTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ChessClockkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    MainView(viewModel = viewModel)
                }
            }
        }
    }

    @Composable
    fun MainView(viewModel: MainActivityVM?) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(24.dp)
        ) {
            CombinedClocks(viewModel)
            PlusMinusPause(viewModel)
        }
    }

    //TODO cos trzeba bedzie wyodrebnic
    @Composable
    fun PlusMinusPause(viewModel: MainActivityVM?) {
        val viewConfiguration = LocalViewConfiguration.current

        val interactionSourceIncrement = remember { MutableInteractionSource() }
        val isIncrementPressed by interactionSourceIncrement.collectIsPressedAsState()
        var isIncrementLongPressActive by remember { mutableStateOf(false) }

        val interactionSourceDecrement = remember { MutableInteractionSource() }
        val isDecreasePressed by interactionSourceDecrement.collectIsPressedAsState()
        var isDecreaseLongPressActive by remember { mutableStateOf(false) }

        val gameState = viewModel?.gameStateLiveData?.observeAsState()

        LaunchedEffect(isIncrementPressed) {
            if (isIncrementPressed) {
                isIncrementLongPressActive = false
                viewModel?.startIncrement(false)
            } else {
                viewModel?.stopIncrement()
            }
        }

        LaunchedEffect(isIncrementPressed) {
            if (isIncrementPressed) {
                isIncrementLongPressActive = false
                delay(viewConfiguration.longPressTimeoutMillis)
                isIncrementLongPressActive = true
                viewModel?.startIncrement(true)
            }
        }

        LaunchedEffect(isDecreasePressed) {
            if (isDecreasePressed) {
                isDecreaseLongPressActive = false
                viewModel?.startDecrease(false)
            } else {
                viewModel?.stopDecrease()
            }
        }

        LaunchedEffect(isDecreasePressed) {
            if (isDecreasePressed) {
                isDecreaseLongPressActive = false
                delay(viewConfiguration.longPressTimeoutMillis)
                isDecreaseLongPressActive = true
                viewModel?.startDecrease(true)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { },
                interactionSource = interactionSourceIncrement,
                modifier = Modifier.size(100.dp)
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Plus")
            }
            Button(
                onClick = { },
                interactionSource = interactionSourceDecrement,
                modifier = Modifier.size(100.dp)
            )
            {
                Icon(imageVector = Icons.Filled.Remove, contentDescription = "Minus")
            }
            Button(
                onClick = { viewModel?.onPlayPauseBtnClicked(false) },
                modifier = Modifier
                    .size(100.dp)
            ) {
                Icon(
                    imageVector = when (gameState?.value) {
                        MainActivityVM.GameState.WHITE_MOVE -> Icons.Filled.Pause
                        MainActivityVM.GameState.BLACK_MOVE -> Icons.Filled.Pause
                        MainActivityVM.GameState.PAUSE -> Icons.Filled.PlayArrow
                        MainActivityVM.GameState.NEW_GAME -> Icons.Filled.PlayArrow
                        null -> Icons.Filled.Pause
                    },
                    contentDescription = when (gameState?.value) {
                        MainActivityVM.GameState.WHITE_MOVE -> "Pause"
                        MainActivityVM.GameState.BLACK_MOVE -> "Pause"
                        MainActivityVM.GameState.PAUSE -> "Play"
                        MainActivityVM.GameState.NEW_GAME -> "Play"
                        null -> "Pause"
                    },
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }

    @Composable
    fun CombinedClocks(viewModel: MainActivityVM?) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            ClockBlackWidget(viewModel)
            ClockWhiteWidget(viewModel)
        }
    }

    //TODO parametryzacja tych widokow
    @Composable
    fun ClockBlackWidget(viewModel: MainActivityVM?) {
        val count = viewModel?.clockBlackLiveData?.observeAsState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "PLAYER BLACK")
            Text(text = "${count?.value}")
            Button(onClick = { viewModel?.clockBlackPressed() }) {
                Text(text = "START")
            }
        }
    }

    @Composable
    fun ClockWhiteWidget(viewModel: MainActivityVM?) {
        val count = viewModel?.clockWhiteLiveData?.observeAsState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "PLAYER WHITE")
            Text(text = "${count?.value}")
            Button(onClick = { viewModel?.clockWhitePressed() }) {
                Text(text = "START")
            }
        }
    }

    @Composable
    @Preview(showSystemUi = true, showBackground = true)
    fun DefaultPreview() {
        ChessClockkTheme {
            MainView(null)
        }
    }
}

