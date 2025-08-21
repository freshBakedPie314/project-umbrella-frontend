package com.example.project_umbrella.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.LineHeightStyle.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.project_umbrella.dtos.GameResponseDetailed
import com.example.project_umbrella.screens.GameInfoScreen
import com.example.project_umbrella.screens.LoginScreen
import com.example.project_umbrella.screens.MainAppScreen
import com.example.project_umbrella.screens.RegisterScreen
import com.example.project_umbrella.viewModel.AuthViewModel
import com.example.project_umbrella.viewModel.GameViewModel
import com.example.yourapp.SplashScreen


// Define routes for clarity
object Route {
    const val SPLASH = "splash"
    const val AUTH = "auth_flow"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val APP = "app_flow"
    const val INFO = "game_info/{gameId}"
}

@Composable
fun AppNavigation(authViewModel: AuthViewModel, gameViewModel: GameViewModel) {
    val navController = rememberNavController()
    val isSessionValid by authViewModel.isSessionValid.collectAsState()

    NavHost(navController = navController, startDestination = Route.SPLASH) {

        composable(Route.SPLASH) {
            SplashScreen(
                onNavigateToMain = {
                    val destination = if (isSessionValid) Route.APP else Route.AUTH
                    navController.navigate(destination) {
                        popUpTo(Route.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        navigation(startDestination = Route.LOGIN, route = Route.AUTH) {
            composable(Route.LOGIN) {
                LoginScreen(
                    onLoginClick = { email, password ->
                        authViewModel.login(email, password, navController)
                    },
                    onNavigateToRegister = { navController.navigate(Route.REGISTER){
                        launchSingleTop = true
                    } }
                )
            }
            composable(Route.REGISTER) {
                RegisterScreen(
                    onRegisterClick = { email, password ->
                        authViewModel.register(email, password, navController)
                    },
                    onNavigateToLogin = { navController.navigate(Route.LOGIN){
                        launchSingleTop = true
                        popUpTo(Route.REGISTER){inclusive = true}
                    } }
                )
            }
        }

        composable(Route.APP) {
            MainAppScreen(gameViewModel , authViewModel, navController)
        }

        composable (route = Route.INFO, arguments = listOf(navArgument("gameId") { type = NavType.IntType })){ backStackEntry ->

            val gameId = backStackEntry.arguments?.getInt("gameId")

            LaunchedEffect(key1 = gameId) {
                if(gameId != null){
                    gameViewModel.getGameDetailsById(gameId)
                }
            }

            val gameDetails by gameViewModel.gameDetails.collectAsState()

            if (gameDetails != null) {
                GameInfoScreen(game = gameDetails!!)
            } else {
                // Show a loading indicator or an error message
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    // You can replace this with a CircularProgressIndicator
                    Text("Loading...", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
