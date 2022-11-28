import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kravets.hotels.booker.ui.screen.view.MainPageView
import com.kravets.hotels.booker.ui.screen.view_model.MainPageViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

object Routes {
    const val MainPage = "main_page"
//    const val GameDetails = "game_details/{gameId}"
//    const val NewGames = "new_games/{minReleaseTimestamp}/{subtitle}"
//    const val UpcomingReleases = "upcoming_releases"
//    const val PopularGames = "popular_games/{minReleaseTimestamp}/{subtitle}"
//    const val Search = "search"
//
//    fun gameDetails(gameId: Long) = "game_details/$gameId"
//    fun newGames(minReleaseTimestamp: Long, subtitle: String) =
//        "new_games/$minReleaseTimestamp/$subtitle"
//
//    fun popularGames(minReleaseTimestamp: Long, subtitle: String) =
//        "popular_games/$minReleaseTimestamp/$subtitle"
}

@ExperimentalCoroutinesApi
@ExperimentalMaterial3Api
@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.MainPage,
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        composable(Routes.MainPage) {
            MainPageView(viewModel = MainPageViewModel(), navController = navController)
        }
    }

}