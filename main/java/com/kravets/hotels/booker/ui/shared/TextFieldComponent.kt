package com.kravets.hotels.booker.ui.shared

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.kravets.hotels.booker.ui.theme.DarkRed
import com.kravets.hotels.booker.ui.theme.Purple40

@ExperimentalMaterial3Api
@Composable
fun TextFieldPasswordComponent(
    @StringRes descriptionStringId: Int,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    isError: Boolean
) {
    var passwordVisible by remember { mutableStateOf(false) }

    TextFieldBase(
        descriptionStringId = descriptionStringId,
        value = value,
        onValueChange = onValueChange,
        isError = isError,
        keyboardType = KeyboardType.Password,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Outlined.Visibility
                    else Icons.Outlined.VisibilityOff,
                    contentDescription = "",
                    tint = if (isError) DarkRed else Purple40
                )
            }
        }
    )
}

@ExperimentalMaterial3Api
@Composable
fun TextFieldDecimalComponent(
    @StringRes descriptionStringId: Int,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    isError: Boolean
) {
    TextFieldBase(
        descriptionStringId = descriptionStringId,
        value = value,
        onValueChange = onValueChange,
        isError = isError,
        keyboardType = KeyboardType.Decimal
    )
}

@ExperimentalMaterial3Api
@Composable
fun TextFieldTextComponent(
    @StringRes descriptionStringId: Int,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    isError: Boolean,
    capitalize: Boolean = false
) {
    TextFieldBase(
        descriptionStringId = descriptionStringId,
        value = value,
        onValueChange = onValueChange,
        isError = isError,
        keyboardType = KeyboardType.Text,
        capitalize = capitalize
    )
}

@ExperimentalMaterial3Api
@Composable
fun TextFieldBase(
    @StringRes descriptionStringId: Int,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    isError: Boolean,
    keyboardType: KeyboardType,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable() (() -> Unit)? = null,
    capitalize: Boolean = false
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp),
        value = value,
        textStyle = TextStyle(
            color = if (isError) DarkRed else Purple40,
            fontWeight = FontWeight.Medium
        ),
        onValueChange = onValueChange,
        label = {
            Text(
                style = MaterialTheme.typography.labelSmall,
                text = stringResource(id = descriptionStringId)
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            capitalization = if (capitalize) KeyboardCapitalization.Sentences else KeyboardCapitalization.None
        ),
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
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