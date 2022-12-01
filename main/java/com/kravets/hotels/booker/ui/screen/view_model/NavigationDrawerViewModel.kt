package com.kravets.hotels.booker.ui.screen.view_model

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.kravets.hotels.booker.R
import com.kravets.hotels.booker.misc.navigateWithoutStack
import com.kravets.hotels.booker.service.other.DataStore
import com.kravets.hotels.booker.ui.Routes
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
class NavigationDrawerViewModel(
    private val navController: NavHostController,
    private val dataStore: DataStore
) : ViewModel() {

    val sessionKey: StateFlow<String> =
        dataStore.sessionKey.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val shortName: StateFlow<String> =
        dataStore.shortName.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val isAdmin: StateFlow<Boolean> =
        dataStore.isAdmin.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun onItemClicked(destination: String) {
        navigateWithoutStack(navController, viewModelScope, destination)
    }

    fun onLogoutClicked() {
        viewModelScope.launch {
            dataStore.clear()
            navigateWithoutStack(
                navController, viewModelScope,
                destination = "${Routes.MainPage}?message=${R.string.message_successful_signing_out}",
                redraw = true
            )
        }
    }
}