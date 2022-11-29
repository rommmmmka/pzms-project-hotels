package com.kravets.hotels.booker.ui.shared

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.em
import com.kravets.hotels.booker.ui.theme.DarkRed

@Composable
fun ValidationErrorComponent(errorMessageId: Int) {
    Text(
        fontSize = 2.4.em,
        color = DarkRed,
        text = stringResource(id = errorMessageId)
    )
}