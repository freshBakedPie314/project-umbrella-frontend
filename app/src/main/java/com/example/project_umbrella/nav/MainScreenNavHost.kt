package com.example.project_umbrella.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.project_umbrella.screens.CatalogScreen
import com.example.project_umbrella.screens.GamesScreen
import com.example.project_umbrella.screens.HomeScreen
import com.example.project_umbrella.screens.ProfileScreen
import com.example.project_umbrella.screens.SubScreens
import com.example.project_umbrella.viewModel.AuthViewModel
import com.example.project_umbrella.viewModel.GameViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: GameViewModel,
    authViewModel: AuthViewModel,
    mainNavController : NavHostController
) {
    NavHost(
        startDestination = SubScreens.Home.route,
        navController = navController
    )
    {
        composable(route = SubScreens.Home.route) {
            HomeScreen(viewModel, mainNavController)
        }

        composable(route = SubScreens.Games.route) {
            GamesScreen(viewModel, mainNavController)
        }

        composable(route = SubScreens.Catalog.route) {
            CatalogScreen(viewModel, mainNavController)
        }

        composable(route = SubScreens.Profile.route) {
            ProfileScreen(authViewModel)
        }
    }
}