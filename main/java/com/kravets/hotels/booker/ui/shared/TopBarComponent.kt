package com.kravets.hotels.booker.ui.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kravets.hotels.booker.R
import com.kravets.hotels.booker.ui.Routes

object TopBarNames {
    val MainPage: Pair<Int, Int?> = Pair(R.string.app_name, R.string.app_description)
    val Register: Pair<Int, Int?> = Pair(R.string.tab_sign_up, null)
    val Login: Pair<Int, Int?> = Pair(R.string.tab_sign_in, null)
    val Orders: Pair<Int, Int?> = Pair(R.string.tab_orders, null)

    fun getTitleByRoute(route: String?): Pair<Int, Int?> {
        var currentRoute = route ?: ""
        if (currentRoute.indexOf('?') != -1) {
            currentRoute = currentRoute.substring(0, currentRoute.indexOf('?'))
        }
        return when (currentRoute) {
            Routes.MainPage -> MainPage
            Routes.Register -> Register
            Routes.Login -> Login
            Routes.Orders -> Orders
            else -> MainPage
        }
    }
}


@ExperimentalMaterial3Api
@Composable
fun TopBarComponent(
    navController: NavController,
    onNavigationIconClick: () -> Unit
) {
    val title = remember { mutableStateOf(TopBarNames.MainPage.first) }
    val description = remember { mutableStateOf(TopBarNames.MainPage.second) }

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            // You can map the title based on the route using:
            title.value = TopBarNames.getTitleByRoute(backStackEntry.destination.route).first
            description.value = TopBarNames.getTitleByRoute(backStackEntry.destination.route).second
        }
    }
    CenterAlignedTopAppBar(
        modifier = Modifier.shadow(2.dp),
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(title.value),
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                if (description.value != null) {
                    Text(
                        text = stringResource(description.value!!),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }

        },
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = stringResource(id = R.string.menu),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}