package com.example.chessclockk

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.chessclockk.ui.theme.ChessClockkTheme
import com.example.chessclockk.views.MainScreenContent
import com.example.chessclockk.views.SettingsScreen
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.stack.LocalStackNavigation
import com.github.terrakok.modo.stack.forward
import kotlinx.parcelize.Parcelize

@Parcelize
class MainScreen(
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content(modifier: Modifier) {
        val stackNavigation = LocalStackNavigation.current
        MainScreenContent(
            modifier = modifier,
            openNextScreen = {
                stackNavigation.forward(SettingsScreen())
            }
        )
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun DefaultPreview() {
    MainScreenContent(openNextScreen = {}, modifier = Modifier)
}
