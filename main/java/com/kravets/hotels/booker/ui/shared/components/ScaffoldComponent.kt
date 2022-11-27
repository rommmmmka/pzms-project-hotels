package com.kravets.hotels.booker.ui.shared.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@ExperimentalMaterial3Api
@Composable
fun ScaffoldComponent(
    @StringRes topBarStringId: Int,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopBarComposable(name = stringResource(id = topBarStringId))
        },
        content = { padding ->
            Box (
                modifier = Modifier.padding(padding)
            ) {
                content()
            }
        }

    )
}