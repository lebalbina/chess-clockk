package com.example.chessclockk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val mainActivityVM: MainActivityVM by viewModels()
            Root(mainActivityVM)
        }
    }

    @Composable
    fun Root(viewModel: MainActivityVM) {
        Column(
            modifier = Modifier
                .background(Color.Magenta)
                .padding(Dp(24f))
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TimerControl(
                viewModel,
                { viewModel.startTimer() },
                { viewModel.stopTimer() }
            )
        }
    }

    @Composable
    private fun TimerControl(
        viewModel: MainActivityVM,
        onStartClick: () -> Unit,
        onStopClick: () -> Unit
    ) {
        var btnText by remember { mutableStateOf("START") }
        val timerValue by viewModel.timerLiveData.observeAsState()

        timerValue?.let {
            Text(
                text = it,
                modifier = Modifier.padding(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (btnText == "START") {
                    onStartClick()
                    btnText = "STOP"
                } else {
                    onStopClick()
                    btnText = "START"
                }
            },
            modifier = Modifier.background(Color.Yellow)
        ) {
            Text(text = btnText)
        }
    }

//    @Preview
//    @Composable
//    fun PreviewClock() {
//        Root()
//    }
}
