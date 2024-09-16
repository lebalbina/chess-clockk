package com.example.chessclockk.screens.main

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.chessclockk.screens.views.ClockState
import com.example.chessclockk.screens.views.PlayPauseState
import com.example.chessclockk.screens.views.RestartState
import java.time.Clock

@Composable
fun MainScreenContent(
    modifier: Modifier,
    clockWhiteState: ClockState,
    clockBlackState: ClockState,
    playPauseState: PlayPauseState,
    restartState: RestartState

) {


}

@Composable
fun ClickableIcon(
    icon: ImageVector,
    description: String,
    onIconClicked: () -> Unit,
) {
    Button(
        onClick = onIconClicked,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFBE2578)
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description
        )
    }
}
