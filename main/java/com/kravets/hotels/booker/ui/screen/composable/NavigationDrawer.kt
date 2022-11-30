package com.kravets.hotels.booker.ui.screen.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import com.kravets.hotels.booker.model.other.MenuItem
import com.kravets.hotels.booker.ui.theme.Purple40
import com.kravets.hotels.booker.ui.theme.Purple80
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun NavigationDrawer(navController: NavHostController, drawerState: DrawerState) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    if (drawerState.isAnimationRunning) {
        focusManager.clearFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        DrawerHeader(navController, drawerState)
        DrawerBody(
            menuItems = listOf(
                MenuItem(
                    "main_page",
                    "Find a room for yourself",
                    Icons.Default.Search,
                    false
                ),
                MenuItem(
                    "divider",
                    "Divider",
                    null,
                    true
                ),
                MenuItem(
                    "orders",
                    "Orders",
                    Icons.Default.List,
                    false
                ),
            ),
            onItemClick = {
                if (navController.currentBackStackEntry?.destination?.route != it.id) {
                    navController.navigate(
                        route = it.id,
                        navOptions = navOptions {
                            this.launchSingleTop = true
                            this.restoreState = true
                        }
                    )
                }


                coroutineScope.launch {
                    drawerState.close()
                }
            }
        )
    }

}

@ExperimentalMaterial3Api
@Composable
fun DrawerHeader(navController: NavHostController, drawerState: DrawerState) {
    val coroutineScope = rememberCoroutineScope()

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
            text = "Вы не ўвайшлі ў акаўнт",
            style = TextStyle(
                color = Purple40,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ElevatedButton(
                onClick = {
                    if (navController.currentBackStackEntry?.destination?.route != "login") {
                        navController.navigate(
                            route = "login",
                            navOptions = navOptions {
                                this.launchSingleTop = true
                                this.restoreState = true
                            }
                        )
                    }
                    coroutineScope.launch {
                        drawerState.close()
                    }
                },
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 5.dp
                )
            ) {
                Text("Уваход")
            }
            ElevatedButton(
                onClick = {
                    if (navController.currentBackStackEntry?.destination?.route != "register") {
                        navController.navigate(
                            route = "register",
                            navOptions = navOptions {
                                this.launchSingleTop = true
                                this.restoreState = true
                            }
                        )
                    }
                    coroutineScope.launch {
                        drawerState.close()
                    }
                },
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 5.dp
                )
            ) {
                Text("Рэгістрацыя")
            }
        }
    }
}

@Composable
fun DrawerBody(
    menuItems: List<MenuItem>,
    onItemClick: (MenuItem) -> Unit
) {
    LazyColumn() {
        items(menuItems) {
            TextButton(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = ShapeDefaults.ExtraSmall,
                enabled = !it.disabled,
                onClick = {
                    onItemClick(it)
                }
            ) {
                Row(modifier = Modifier.weight(1F)) {
                    Box(modifier = Modifier.width(40.dp)) {
                        if (it.icon != null) {
                            Icon(imageVector = it.icon, "")
                        }
                    }
                    Text(
                        text = it.name,
                        fontSize = 16.sp,
                        color = if (it.disabled) Color.Gray else Color.Black
                    )
                }
            }
        }
    }
}