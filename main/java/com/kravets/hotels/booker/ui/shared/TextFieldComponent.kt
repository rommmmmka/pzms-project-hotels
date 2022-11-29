package com.kravets.hotels.booker.ui.shared

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.kravets.hotels.booker.ui.theme.DarkRed
import com.kravets.hotels.booker.ui.theme.Purple40

@ExperimentalMaterial3Api
@Composable
fun TextFieldDecimalComponent(
    @StringRes descriptionStringId: Int,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    isError: Boolean
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp),
        value = value,
        textStyle = MaterialTheme.typography.labelLarge,
        onValueChange = onValueChange,
        label = {
            Text(
                style = MaterialTheme.typography.labelSmall,
                text = stringResource(id = descriptionStringId)
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        ),
        isError = isError,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = Purple40,
            unfocusedBorderColor = Purple40,
            focusedBorderColor = Purple40,
            unfocusedLabelColor = Purple40,
            focusedLabelColor = Purple40,
            errorBorderColor = DarkRed
        )
    )
}