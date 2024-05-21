package com.nestoleh.light

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nestoleh.light.presentation.home.AddNewHomeScreen
import com.nestoleh.light.presentation.main.MainScreen
import com.nestoleh.light.theme.LightAppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext

@Composable
@Preview
fun App() {
    LightAppTheme {
        KoinContext {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = Route.Main.route,
            ) {
                composable(Route.Main.route) {
                    MainScreen(
                        onAddNewHome = {
                            navController.navigate(Route.AddNewHome.route)
                        }
                    )
                }
                composable(Route.AddNewHome.route) {
                    AddNewHomeScreen(
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