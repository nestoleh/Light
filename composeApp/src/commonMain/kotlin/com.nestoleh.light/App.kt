package com.nestoleh.light

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.touchlab.kermit.Logger
import com.nestoleh.light.presentation.main.MainScreen
import com.nestoleh.light.presentation.place.AddPlaceScreen
import com.nestoleh.light.theme.LightAppTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    LightAppTheme {
        KoinContext {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = Route.Main.route,
                enterTransition = { fadeIn(animationSpec = tween(400)) },
                exitTransition = { fadeOut(animationSpec = tween(400)) },
            ) {
                composable(Route.Main.route) {
                    MainScreen(
                        onAddNewPlace = {
                            navController.navigate(Route.AddNewHome.route)
                        }
                    )
                }
                composable(Route.AddNewHome.route) {
                    AddPlaceScreen(
                        onPlaceAdded = {
                            Logger.d("back stack size = ${navController.currentBackStack.value.size}")
                            if (navController.currentBackStack.value.size > 1) {
                                navController.popBackStack()
                            } else {
                                navController.navigate(Route.Main.route) {
                                    popUpTo(Route.Main.route)
                                    launchSingleTop = true
                                }
                            }
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

sealed class Route(val route: String) {
    data object Main : Route("main")
    data object AddNewHome : Route("addHome")
}