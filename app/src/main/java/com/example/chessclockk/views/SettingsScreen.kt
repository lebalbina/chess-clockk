package com.example.chessclockk.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import kotlinx.parcelize.Parcelize

@Parcelize
class SettingsScreen(
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content(modifier: Modifier) {
        SettingsContent()
    }
}

@Composable
fun SettingsContent() {

}

@Composable
@Preview
fun SettingsPreview() {
    SettingsScreen().Content(modifier = Modifier)
}
