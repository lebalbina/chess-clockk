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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chessclockk.ui.theme.ChessClockkTheme
import kotlinx.coroutines.delay

//TODO extract actual values from viewModel to child views
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

    @Composable
    fun PlusMinusPause(viewModel: MainActivityVM?) {
        val viewConfiguration = LocalViewConfiguration.current
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        var isLongPressActive by remember { mutableStateOf(false) }
        val isPlaying = viewModel?.isPlayingLiveData?.observeAsState()

        LaunchedEffect(isPressed) {
            if (isPressed) {
                isLongPressActive = false
                viewModel?.startIncrement(false)
            } else {
                viewModel?.stopIncrement()
            }
        }

        LaunchedEffect(isPressed) {
            if (isPressed) {
                isLongPressActive = false
                delay(viewConfiguration.longPressTimeoutMillis)
                isLongPressActive = true
                viewModel?.startIncrement(true)
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
                interactionSource = interactionSource,
                modifier = Modifier.size(100.dp)
            ) {
                Text(text = "+")
            }
            Button(onClick = { viewModel?.stopIncrement() }) {
                Text(text = "-")
            }
            Button(
                onClick = { viewModel?.onPlayPauseBtnClicked(false) },
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.Red)
            ) {
                Icon(
                    imageVector = if (isPlaying?.value!!) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying.value!!) "Pause" else "Play",
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

