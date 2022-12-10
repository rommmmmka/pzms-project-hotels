package com.kravets.hotels.booker.misc

import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun navigateWithoutStack(
    navController: NavController,
    scope: CoroutineScope,
    destination: String,
    redraw: Boolean = false
) {
    if (!redraw) {
        var currentRoute = navController.currentBackStackEntry?.destination?.route ?: ""
        if (currentRoute.indexOf('?') != -1) {
            currentRoute = currentRoute.substring(0, currentRoute.indexOf('?'))
        }
        var newRoute = destination
        if (newRoute.indexOf('?') != -1) {
            newRoute = newRoute.substring(0, newRoute.indexOf('?'))
        }

        if (currentRoute == newRoute) {
            return
        }
    }



    scope.launch {
        navController.popBackStack()
        navController.navigate(destination)
    }
}