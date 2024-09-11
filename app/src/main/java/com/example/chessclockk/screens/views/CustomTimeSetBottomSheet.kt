package com.example.chessclockk.screens.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CustomTimeSetBottomSheetContent(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onSheetClose: (String, String) -> Unit,
    modifier: Modifier
) {
    Surface {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var hours by remember { mutableStateOf("00") }
            var minutes by remember { mutableStateOf("00") }
            var seconds by remember { mutableStateOf("00") }

            var bonusMinutes by remember { mutableStateOf("00") }
            var bonusSeconds by remember { mutableStateOf("00") }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DigitInputTextField(
                    value = hours,
                    onInputValidated = { hours = it },
                    inputType = DigitInputType.HOURS
                )
                Colon()
                DigitInputTextField(
                    value = minutes,
                    onInputValidated = { minutes = it },
                    inputType = DigitInputType.MINUTES
                )
                Colon()
                DigitInputTextField(
                    value = seconds,
                    onInputValidated = { seconds = it },
                    inputType = DigitInputType.SECONDS
                )
            }
            Row(
                modifier = Modifier
                    .padding(bottom = 16.dp, top = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = "BONUS"
                )
                DigitInputTextField(
                    value = bonusMinutes,
                    onInputValidated = { bonusMinutes = it },
                    inputType = DigitInputType.MINUTES
                )
                Colon()
                DigitInputTextField(
                    value = bonusSeconds,
                    onInputValidated = { bonusSeconds = it },
                    inputType = DigitInputType.SECONDS
                )
            }
            Button(onClick = {
                onSheetClose(
                    "$hours:$minutes:$seconds",
                    "$bonusMinutes:$bonusSeconds"
                )
            }) {
                Text(text = "Close meee")
            }
        }
    }
}

@Composable
private fun Colon() {
    Text(
        modifier = Modifier.padding(4.dp),
        text = ":"
    )
}

enum class DigitInputType {
    HOURS, MINUTES, SECONDS
}

@Composable
private fun DigitInputTextField(
    value: String,
    onInputValidated: (String) -> Unit,
    inputType: DigitInputType
) {
    val textFieldSize = 64.dp
    TextField(
        value = value,
        onValueChange = {
            when (inputType) {
                DigitInputType.HOURS -> {
                    if (it.length <= 2) {
                        onInputValidated(it)
                    }
                }

                DigitInputType.MINUTES, DigitInputType.SECONDS -> {
                    if (it.length <= 2) {
                        val input = if (it.isNotEmpty() && it.isDigitsOnly()) it.toInt() else 0
                        onInputValidated(if (input > 59) "59" else it)
                    }
                }
            }
        },
        Modifier.width(textFieldSize),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = TextStyle(textAlign = TextAlign.Center)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    name = "Small phone",
    widthDp = 360,
    heightDp = 640
)
@Composable
fun LargePhonePreview() {
    CustomTimeSetBottomSheetContent(
        sheetState = rememberModalBottomSheetState(),
        onSheetClose = { _, _ -> },
        onDismissRequest = {},
        modifier = Modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    name = "Medium Phone",
    widthDp = 427,
    heightDp = 948
)
@Composable
fun MediumPhonePreview() {
    CustomTimeSetBottomSheetContent(
        sheetState = rememberModalBottomSheetState(),
        onSheetClose = { _, _ -> },
        onDismissRequest = {},
        modifier = Modifier
    )
}

