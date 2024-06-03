package com.nestoleh.light.presentation.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Route(val route: String) {

    data object Main : Route("main")

    data object AddNewPlace : Route("addPlace")

    data object PlaceSettings : Route("placeSettings/{id}") {
        val arguments = listOf(
            navArgument("id") { type = NavType.StringType }
        )

        fun createRoute(id: String) = "placeSettings/$id"

        fun NavBackStackEntry.parseId() = arguments?.getString("id")
    }

    data object PlaceSchedule : Route("placeSchedule/{id}") {
        val arguments = listOf(
            navArgument("id") { type = NavType.StringType }
        )

        fun createRoute(id: String) = "placeSchedule/$id"

        fun NavBackStackEntry.parseId() = arguments?.getString("id")
    }
}