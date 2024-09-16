package com.example.chessclockk.screens.views

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun Restart(restartState: RestartState) {

    val showDialog = remember { mutableStateOf(false) }

    Button(
        onClick = {
            restartState.onRestartClicked()
            showDialog.value = true
        },
        enabled = restartState.isEnabled
    ) {
        Icon(
            imageVector = restartState.icon,
            contentDescription = "restart"
        )
    }

    if (showDialog.value) {
        RestartDialog(
            onRestartConfirmedClick = restartState.onRestartConfirmedClick,
            showDialog = showDialog
        )
    }
}

data class RestartState(
    val isEnabled: Boolean,
    val onRestartClicked: () -> Unit,
    val onRestartConfirmedClick: () -> Unit,
    val icon: ImageVector
)

@Composable
fun RestartDialog(
    onRestartConfirmedClick: () -> Unit,
    showDialog: MutableState<Boolean>
) {
    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        dismissButton = {
            TextButton(onClick = { showDialog.value = false }) {
                Text("Dismiss")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onRestartConfirmedClick()
                showDialog.value = false
            }) {
                Text(text = "Confirm")
            }
        },
        title = { Text("Restart") },
        text = { Text("Do you really want to restart?") }
    )
}

@Composable
@Preview
fun RestartPreview() {
    Restart(
        RestartState(
            onRestartConfirmedClick = { },
            icon = Icons.Filled.Refresh,
            onRestartClicked = {},
            isEnabled = true
        )
    )
}

@Composable
@Preview
fun DialogPreview() {
    val showDialog = remember { mutableStateOf(true) }
    RestartDialog(
        onRestartConfirmedClick = {},
        showDialog = showDialog
    )
}
