package com.example.chessclockk.views.customtimebottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.example.chessclockk.R

@Composable
fun CustomTimeSetBottomSheetContent(
    onSheetClose: (String, String) -> Unit,
    modifier: Modifier
) {
    var hours by remember { mutableStateOf("00") }
    var minutes by remember { mutableStateOf("00") }
    var seconds by remember { mutableStateOf("00") }

    var bonusMinutes by remember { mutableStateOf("00") }
    var bonusSeconds by remember { mutableStateOf("00") }

    Surface {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 24.dp, bottom = 24.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                text = stringResource(id = R.string.custom_time_modal_title).uppercase(),
            )
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
                    text = stringResource(id = R.string.bonus)
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
            Button(
                modifier = Modifier.padding(bottom = 48.dp),
                onClick = {
                    onSheetClose(
                        "$hours:$minutes:$seconds",
                        "$bonusMinutes:$bonusSeconds"
                    )
                }) {
                Text(text = stringResource(id = R.string.close_time_modal_btn))
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

@Composable
private fun DigitInputTextField(
    value: String,
    onInputValidated: (String) -> Unit,
    inputType: DigitInputType
) {
    TextField(
        modifier = Modifier.width(64.dp),
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
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = TextStyle(textAlign = TextAlign.Center)
    )
}

@Preview
@Composable
fun MediumPhonePreview() {
    CustomTimeSetBottomSheetContent(
        onSheetClose = { _, _ -> },
        modifier = Modifier
    )
}
