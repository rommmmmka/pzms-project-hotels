import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kravets.hotels.booker.R
import com.kravets.hotels.booker.ui.screen.composable.MainPage
import com.kravets.hotels.booker.ui.screen.composable.NavigationDrawer
import com.kravets.hotels.booker.ui.screen.composable.TopBar
import com.kravets.hotels.booker.ui.screen.view_model.MainPageViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

object Routes {
    const val MainPage = "main_page"

}

@ExperimentalCoroutinesApi
@ExperimentalMaterial3Api
@Composable
fun Navigation() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbarHostState = SnackbarHostState()
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawer(navController, drawerState)
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopBar(
                    name = stringResource(id = R.string.app_name),
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
                    startDestination = Routes.MainPage,
                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
                ) {
                    composable(Routes.MainPage) {
                        MainPage(
                            viewModel = MainPageViewModel(),
                            navController = navController,
                            snackbarHostState = snackbarHostState
                        )
                    }
                }
            }
        }
    }


}