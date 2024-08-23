package com.example.chessclockk.views

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

//TODO how to make it less ugly?
@Composable
fun PlusMinusPause(
    isEnabled: Boolean,
    onPlusBtnClicked: (Boolean) -> Unit,
    onMinusBtnClicked: (Boolean) -> Unit,
    onPlsBtnReleased: () -> Unit,
    onMinusBtnReleased: () -> Unit,
) {
    val viewConfiguration = LocalViewConfiguration.current

    val interactionSourceIncrement = remember { MutableInteractionSource() }
    val isIncrementPressed by interactionSourceIncrement.collectIsPressedAsState()
    var isIncrementLongPressActive by remember { mutableStateOf(false) }

    val interactionSourceDecrement = remember { MutableInteractionSource() }
    val isDecreasePressed by interactionSourceDecrement.collectIsPressedAsState()
    var isDecreaseLongPressActive by remember { mutableStateOf(false) }

    LaunchedEffect(isIncrementPressed) {
        if (isIncrementPressed) {
            isIncrementLongPressActive = false
            onPlusBtnClicked(false)
        } else {
            onPlsBtnReleased()
        }
    }

    LaunchedEffect(isIncrementPressed) {
        if (isIncrementPressed) {
            isIncrementLongPressActive = false
            delay(viewConfiguration.longPressTimeoutMillis)
            isIncrementLongPressActive = true
            onPlusBtnClicked(true)
        }
    }

    LaunchedEffect(isDecreasePressed) {
        if (isDecreasePressed) {
            isDecreaseLongPressActive = false
            onMinusBtnClicked(false)
        } else {
            onMinusBtnReleased()
        }
    }

    LaunchedEffect(isDecreasePressed) {
        if (isDecreasePressed) {
            isDecreaseLongPressActive = false
            delay(viewConfiguration.longPressTimeoutMillis)
            isDecreaseLongPressActive = true
            onMinusBtnClicked(true)
        }
    }

    Row(modifier = Modifier.padding(12.dp)) {
        Button(
            modifier = Modifier.size(100.dp),
            onClick = { },
            interactionSource = interactionSourceIncrement,
            enabled = isEnabled,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBE2578))
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Plus"
            )
        }
        Button(
            modifier = Modifier.size(100.dp),
            onClick = { },
            enabled = isEnabled,
            interactionSource = interactionSourceDecrement,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBE2578))
        )
        {
            Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = "Minus"
            )
        }
    }
}
