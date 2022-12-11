package com.kravets.hotels.booker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kravets.hotels.booker.R
import com.kravets.hotels.booker.misc.navigateWithoutStack
import com.kravets.hotels.booker.service.other.DataStore
import com.kravets.hotels.booker.ui.screen.composable.*
import com.kravets.hotels.booker.ui.screen.view_model.*
import com.kravets.hotels.booker.ui.shared.TopBarComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

object Routes {
    const val Start = "start"

    const val MainPage = "main_page"
    const val Register = "register"
    const val Login = "login"
    const val Orders = "orders"

    const val HotelsList = "list_hotels"
    const val RoomsList = "list_rooms"
}

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@ExperimentalMaterial3Api
@Composable
fun Navigation() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbarHostState = SnackbarHostState()
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val dataStore = DataStore(LocalContext.current)

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawer(NavigationDrawerViewModel(navController, dataStore), drawerState)
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopBarComponent(
                    navController = navController,
                    onNavigationIconClick = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                NavHost(
                    navController = navController,
                    startDestination = Routes.Start,
                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
                ) {
                    composable(Routes.Start) {
                        navigateWithoutStack(navController, coroutineScope, Routes.MainPage)
                    }

                    composable(
                        route = Routes.MainPage + "?message={message}",
                        arguments = listOf(navArgument("message") { defaultValue = 0 })
                    ) { navBackStackEntry ->
//                        topBarName.value = TopBarNames.MainPage
                        MainPage(
                            viewModel = MainPageViewModel(
                                navController, dataStore,
                                navBackStackEntry.arguments?.getInt("message")
                            ),
                            snackbarHostState = snackbarHostState,
                        )
                    }

                    composable(Routes.Register) {
//                        topBarName.value = TopBarNames.Register
                        RegisterPage(
                            viewModel = RegisterPageViewModel(navController),
                            snackbarHostState = snackbarHostState
                        )
                    }

                    composable(
                        route = Routes.Login + "?login={login}&message={message}",
                        arguments = listOf(
                            navArgument("login") { defaultValue = "" },
                            navArgument("message") { defaultValue = 0 }
                        )
                    ) { navBackStackEntry ->
                        LoginPage(
                            viewModel = LoginPageViewModel(
                                navController, dataStore,
                                navBackStackEntry.arguments?.getString("login"),
                                navBackStackEntry.arguments?.getInt("message")
                            ),
                            snackbarHostState = snackbarHostState
                        )
                    }

                    composable(
                        route = Routes.Orders + "?message={message}",
                        arguments = listOf(
                            navArgument("message") { defaultValue = 0 }
                        )
                    ) { navBackStackEntry ->
                        OrdersPage(
                            viewModel = OrdersPageViewModel(
                                dataStore,
                                navBackStackEntry.arguments?.getInt("message")
                            ),
                            snackbarHostState = snackbarHostState
                        )
                    }

                    composable(
                        route = Routes.HotelsList
                    ) { navBackStackEntry ->
                        HotelsListPage(
                            HotelsListViewModel(navController),
                            snackbarHostState
                        )
                    }

                    composable(
                        route = Routes.RoomsList + "?hotel={hotel}&city={city}",
                        arguments = listOf(
                            navArgument("hotel") { defaultValue = 0L },
                            navArgument("city") { defaultValue = 0L }
                        )
                    ) { navBackStackEntry ->
                        RoomsListPage(
                            RoomsListViewModel(
                                navBackStackEntry.arguments?.getLong("hotel"),
                                navBackStackEntry.arguments?.getLong("city")
                            ),
                            snackbarHostState
                        )
                    }
                }
            }
        }
    }
}