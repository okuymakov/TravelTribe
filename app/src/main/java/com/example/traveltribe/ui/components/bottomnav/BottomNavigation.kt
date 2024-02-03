package com.example.traveltribe.ui.components.bottomnav

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.traveltribe.ui.components.favorite.Favorite
import com.example.traveltribe.ui.components.home.Home
import com.example.traveltribe.ui.components.profile.Profile
import com.example.traveltribe.ui.components.routes.Routes
import com.example.traveltribe.R

val items = listOf(
    Screen.Home,
    Screen.Routes,
    Screen.Favorite,
    Screen.Profile
)

@Composable
fun BottomNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                        label = { Text(stringResource(screen.resourceId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.Profile.route, Modifier.padding(innerPadding)) {
            composable(Screen.Home.route) { Home(navController) }
            composable(Screen.Routes.route) { Routes(navController) }
            composable(Screen.Favorite.route) { Favorite(navController) }
            composable(Screen.Profile.route) { Profile(navController) }
        }
    }
}

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
    object Home : Screen("home", R.string.home)
    object Routes : Screen("routes", R.string.routes)
    object Favorite : Screen("favorite", R.string.favorite)
    object Profile : Screen("profile", R.string.profile)
}