package com.example.chessclockk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
            Root()
        }
    }

    @Composable
    fun Root() {
        Column(
            modifier = Modifier
                .background(Color.Magenta)
                .padding(Dp(24f))
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TimerControl()
        }
    }

    @Composable
    private fun TimerControl() {
        var value by remember { mutableIntStateOf(0) }
        var btnText by remember { mutableStateOf("START") }

        Text(
            text = value.toString(),
            modifier = Modifier.padding(24.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (btnText == "START") {
                    btnText = "STOP"
                    while (true) {
                        value++
                    }
                } else {
                    btnText = "START"
                }
            },
            modifier = Modifier.background(Color.Yellow)
        ) {
            Text(text = btnText)
        }
    }

    @Preview
    @Composable
    fun PreviewClock() {
        Root()
    }
}
