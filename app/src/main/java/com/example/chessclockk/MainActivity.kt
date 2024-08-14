package com.example.chessclockk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.chessclockk.ui.theme.ChessClockkTheme
import com.example.chessclockk.views.MainView

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ChessClockkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    MainView()
                }
            }
        }
    }

    @Composable
    @Preview(showSystemUi = true, showBackground = true)
    fun DefaultPreview() {
        ChessClockkTheme {
            MainView()
        }
    }
}

