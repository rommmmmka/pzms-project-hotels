package com.kravets.hotels.booker.ui.screen.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kravets.hotels.booker.R
import com.kravets.hotels.booker.model.other.MenuItem
import com.kravets.hotels.booker.ui.screen.view_model.NavigationDrawerViewModel
import com.kravets.hotels.booker.ui.theme.Purple40
import com.kravets.hotels.booker.ui.theme.Purple80
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun NavigationDrawer(viewModel: NavigationDrawerViewModel, drawerState: DrawerState) {
    val coroutineScope = rememberCoroutineScope()

    if (drawerState.isAnimationRunning) {
        LocalFocusManager.current.clearFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        DrawerHeader(viewModel, coroutineScope, drawerState)
        DrawerBody(
            viewModel = viewModel,
            coroutineScope = coroutineScope,
            drawerState = drawerState,
            menuItems = listOf(
                MenuItem(
                    id = "main_page?success=0",
                    name = stringResource(R.string.drawer_item_main),
                    icon = Icons.Default.Search,
                    disabled = false,
                    role = "any"
                ),
                MenuItem(
                    id = "orders",
                    name = stringResource(R.string.drawer_item_orders),
                    icon = Icons.Default.List,
                    disabled = false,
                    role = "user"
                ),

                MenuItem(
                    id = "",
                    name = stringResource(R.string.drawer_item_lists),
                    icon = Icons.Default.LibraryBooks,
                    disabled = true,
                    role = "any"
                ),
                MenuItem(
                    id = "hotels",
                    name = stringResource(R.string.drawer_item_hotels),
                    icon = null,
                    disabled = false,
                    role = "any"
                ),
                MenuItem(
                    id = "rooms",
                    name = stringResource(R.string.drawer_item_rooms),
                    icon = null,
                    disabled = false,
                    role = "any"
                ),

                MenuItem(
                    id = "",
                    name = stringResource(R.string.drawer_item_admin),
                    icon = Icons.Default.Handyman,
                    disabled = true,
                    role = "admin"
                ),
                MenuItem(
                    id = "admin_hotels",
                    name = stringResource(R.string.drawer_item_admin_hotels),
                    icon = null,
                    disabled = false,
                    role = "admin"
                ),
                MenuItem(
                    id = "admin_rooms",
                    name = stringResource(R.string.drawer_item_admin_rooms),
                    icon = null,
                    disabled = false,
                    role = "admin"
                ),
                MenuItem(
                    id = "admin_orders",
                    name = stringResource(R.string.drawer_item_admin_orders),
                    icon = null,
                    disabled = false,
                    role = "admin"
                ),
                MenuItem(
                    id = "admin_users",
                    name = stringResource(R.string.drawer_item_admin_users),
                    icon = null,
                    disabled = false,
                    role = "admin"
                )
            ),
        )
    }

}

@ExperimentalMaterial3Api
@Composable
fun DrawerHeader(
    viewModel: NavigationDrawerViewModel,
    coroutineScope: CoroutineScope,
    drawerState: DrawerState
) {
    val shortName by viewModel.shortName.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Purple80)
            .height(150.dp)
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = shortName.ifEmpty { stringResource(R.string.drawer_you_have_not_signed_in) },
            style = TextStyle(
                color = Purple40,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            overflow = TextOverflow.Ellipsis
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            if (shortName == "") {
                ElevatedButton(
                    onClick = {
                        closeDrawer(coroutineScope, drawerState)
                        viewModel.onItemClicked("login")
                    },
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 5.dp
                    )
                ) {
                    Text(stringResource(R.string.tab_sign_in))
                }
                ElevatedButton(
                    onClick = {
                        closeDrawer(coroutineScope, drawerState)
                        viewModel.onItemClicked("register")
                    },
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 5.dp
                    )
                ) {
                    Text(stringResource(R.string.tab_sign_up))
                }
            } else {
                ElevatedButton(
                    onClick = {
                        closeDrawer(coroutineScope, drawerState)
                        viewModel.onLogoutClicked()
                    },
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 5.dp
                    )
                ) {
                    Text(stringResource(R.string.tab_sign_out))
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun DrawerBody(
    viewModel: NavigationDrawerViewModel,
    coroutineScope: CoroutineScope,
    drawerState: DrawerState,
    menuItems: List<MenuItem>
) {
    val sessionKey by viewModel.sessionKey.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()



    LazyColumn {
        items(menuItems) {
            if (
                it.role == "any" ||
                (it.role == "user" && sessionKey.isNotEmpty()) ||
                (it.role == "admin" && isAdmin)
            ) {
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    shape = ShapeDefaults.ExtraSmall,
                    enabled = !it.disabled,
                    onClick = {
                        closeDrawer(coroutineScope, drawerState)
                        viewModel.onItemClicked(it.id)
                    }
                ) {
                    Row(
                        modifier = Modifier.weight(1F),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.width(40.dp)) {
                            if (it.icon != null) {
                                Icon(
                                    imageVector = it.icon,
                                    contentDescription = "",
                                    tint = Color.Black
                                )
                            }
                        }
                        Text(
                            text = it.name,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
fun closeDrawer(coroutineScope: CoroutineScope, drawerState: DrawerState) {
    coroutineScope.launch {
        drawerState.close()
    }
}