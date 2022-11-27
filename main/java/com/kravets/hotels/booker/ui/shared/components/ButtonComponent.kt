package com.kravets.hotels.booker.ui.shared.components

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.kravets.hotels.booker.ui.theme.DarkRed
import com.kravets.hotels.booker.ui.theme.Purple40

@Composable
fun PickerButtonComponent(
    @StringRes descriptionStringId: Int,
    isError: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, if (isError) DarkRed else Purple40),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                fontSize = 2.4.em,
                text = stringResource(id = descriptionStringId),
                color = if (isError) DarkRed else Purple40
            )
            content.invoke()
        }
    }
}
