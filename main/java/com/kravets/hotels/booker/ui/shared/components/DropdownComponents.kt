package com.kravets.hotels.booker.ui.shared.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DropdownMenuComponent(
    isActive: Boolean,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    DropdownMenu(
        modifier = Modifier.requiredSizeIn(maxHeight = 200.dp),
        expanded = isActive,
        onDismissRequest = onDismiss,
    ) { content.invoke() }
}

@Composable
fun DropdownMenuItemComponent(
    text: String,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        modifier = Modifier.height(40.dp),
        text = { Text(text) },
        onClick = onClick
    )
}

@Composable
fun DropdownMenuTextComponent(
    text: String
) {
    DropdownMenuItem(
        modifier = Modifier.height(40.dp),
        enabled = false,
        text = {
            Text(
                color = Color.Gray,
                text = text
            )
        },
        onClick = {}
    )
}