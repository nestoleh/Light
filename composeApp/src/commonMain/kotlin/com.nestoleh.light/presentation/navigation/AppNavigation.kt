package com.nestoleh.light.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nestoleh.light.presentation.main.MainScreen
import com.nestoleh.light.presentation.navigation.Route.PlaceSettings.parseId
import com.nestoleh.light.presentation.place.add.AddPlaceScreen
import com.nestoleh.light.presentation.place.settings.PlaceSettingsScreen
import com.nestoleh.light.presentation.schedule.PlaceScheduleScreen

@Composable
fun LightAppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Route.Main.route,
        enterTransition = { fadeIn(animationSpec = tween(400)) },
        exitTransition = { fadeOut(animationSpec = tween(400)) },
    ) {
        composable(Route.Main.route) {
            MainScreen(
                onNavigateToAddPLace = {
                    navController.navigate(Route.AddNewPlace.route)
                },
                onNavigateToPlaceSettings = {
                    navController.navigate(
                        Route.PlaceSettings.createRoute(id = it.id)
                    )
                },
                onNavigateToPlaceSchedule = {
                    navController.navigate(
                        Route.PlaceSchedule.createRoute(id = it.id)
                    )
                }
            )
        }
        composable(Route.AddNewPlace.route) {
            AddPlaceScreen(
                onNavigateToPlaceSettings = { place ->
                    navController.navigate(Route.PlaceSettings.createRoute(place.id)) {
                        popUpTo(Route.Main.route)
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Route.PlaceSettings.route,
            arguments = Route.PlaceSettings.arguments
        ) {
            val id = it.parseId()
            navController.protectPlaceRelatedNavigation(id) { placeId ->
                PlaceSettingsScreen(
                    id = placeId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
        composable(
            route = Route.PlaceSchedule.route,
            arguments = Route.PlaceSchedule.arguments
        ) {
            val id = it.parseId()
            navController.protectPlaceRelatedNavigation(id) { placeId ->
                PlaceScheduleScreen(
                    id = placeId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private inline fun NavHostController.protectPlaceRelatedNavigation(
    placeId: String?,
    navigation: @Composable (placeId: String) -> Unit
) {
    if (placeId == null) {
        this.navigate(Route.Main.route) {
            popUpTo(Route.Main.route)
            launchSingleTop = true
        }
    } else {
        navigation(placeId)
    }
}

