package com.kravets.hotels.booker.model.other

import androidx.compose.ui.graphics.vector.ImageVector

data class MenuItem(
    val id: String,
    val name: String,
    val icon: ImageVector?,
    val disabled: Boolean
)
