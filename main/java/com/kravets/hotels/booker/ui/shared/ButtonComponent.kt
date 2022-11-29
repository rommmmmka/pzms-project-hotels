package com.kravets.hotels.booker.ui.shared

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kravets.hotels.booker.ui.theme.DarkRed
import com.kravets.hotels.booker.ui.theme.Purple40

@Composable
fun PickerButtonComponent(
    @StringRes descriptionStringId: Int,
    isError: Boolean = false,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(bottom = 5.dp),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, if (isError) DarkRed else Purple40),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                style = MaterialTheme.typography.labelSmall,
                text = stringResource(id = descriptionStringId),
                color = if (isError) DarkRed else Purple40
            )
            content.invoke()
        }
    }
}
