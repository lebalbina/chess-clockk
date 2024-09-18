package com.example.chessclockk.views.restart

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun Restart(
    modifier: Modifier = Modifier,
    restartState: RestartState,
    showDialog: (Boolean) -> Unit,
) {
    Button(
        onClick = {
            restartState.onRestartClicked()
            showDialog(true)
        },
        enabled = restartState.isEnabled
    ) {
        Icon(
            imageVector = restartState.icon,
            contentDescription = "restart"
        )
    }
}

@Composable
@Preview
fun RestartPreview() {
    Restart(
        modifier = Modifier,
        showDialog = {},
        restartState = RestartState(
            onRestartConfirmedClick = { },
            icon = Icons.Filled.Refresh,
            onRestartClicked = {},
            isEnabled = true
        )
    )
}


