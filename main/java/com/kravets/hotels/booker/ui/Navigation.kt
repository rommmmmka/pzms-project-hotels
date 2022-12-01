package com.kravets.hotels.booker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.kravets.hotels.booker.ui.screen.view_model.LoginPageViewModel
import com.kravets.hotels.booker.ui.screen.view_model.MainPageViewModel
import com.kravets.hotels.booker.ui.screen.view_model.NavigationDrawerViewModel
import com.kravets.hotels.booker.ui.screen.view_model.RegisterPageViewModel
import com.kravets.hotels.booker.ui.shared.TopBarComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

object Routes {
    const val Start = "start"

    const val MainPage = "main_page"
    const val Register = "register"
    const val Login = "login"
}

object TopBarNames {
    val MainPage: Pair<Int, Int?> = Pair(R.string.app_name, R.string.app_description)
    val Register: Pair<Int, Int?> = Pair(R.string.tab_sign_up, null)
    val Login: Pair<Int, Int?> = Pair(R.string.tab_sign_in, null)
}

@ExperimentalCoroutinesApi
@ExperimentalMaterial3Api
@Composable
fun Navigation() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbarHostState = SnackbarHostState()
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val dataStore = DataStore(LocalContext.current)

    val topBarName = remember { mutableStateOf(TopBarNames.MainPage) }

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
                    title = topBarName.value.first,
                    description = topBarName.value.second,
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
                        navigateWithoutStack(navController, coroutineScope, Routes.MainPage + "?message=0")
                    }

                    composable(
                        route = Routes.MainPage + "?message={message}",
                        arguments = listOf(navArgument("message") { defaultValue = 0 })
                    ) { navBackStackEntry ->
                        topBarName.value = TopBarNames.MainPage
                        MainPage(
                            viewModel = MainPageViewModel(
                                dataStore,
                                navBackStackEntry.arguments?.getInt("message")
                            ),
                            snackbarHostState = snackbarHostState,
                        )
                    }

                    composable(Routes.Register) {
                        topBarName.value = TopBarNames.Register
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
                        topBarName.value = TopBarNames.Login
                        LoginPage(
                            viewModel = LoginPageViewModel(
                                navController, dataStore,
                                navBackStackEntry.arguments?.getString("login"),
                                navBackStackEntry.arguments?.getInt("message")
                            ),
                            snackbarHostState = snackbarHostState
                        )
                    }
                }
            }
        }
    }
}