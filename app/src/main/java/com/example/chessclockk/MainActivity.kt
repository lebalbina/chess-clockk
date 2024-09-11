package com.example.chessclockk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.chessclockk.screens.main.MainScreen
import com.example.chessclockk.ui.theme.ChessClockkTheme
import com.github.terrakok.modo.Modo.rememberRootScreen
import com.github.terrakok.modo.stack.DefaultStackScreen
import com.github.terrakok.modo.stack.StackNavModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ChessClockkTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    val rootScreen = rememberRootScreen {
                        DefaultStackScreen(
                            StackNavModel(MainScreen())
                        )
                    }
                    rootScreen.Content(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}



