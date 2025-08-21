package com.example.project_umbrella.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.getValue
import com.example.project_umbrella.nav.AppNavHost
import com.example.project_umbrella.viewModel.AuthViewModel
import com.example.project_umbrella.viewModel.GameViewModel

@Composable
fun MainAppScreen(viewModel : GameViewModel, authViewModel: AuthViewModel, mainNavController: NavHostController)
{
   val navController = rememberNavController()
   Scaffold(
      bottomBar = { BottomNavigationBar(navController)}
   )
   { ineerPadding ->
      Box(
         modifier = Modifier.padding(ineerPadding)
      )
      {
         AppNavHost(
            navController = navController,
            viewModel = viewModel,
            authViewModel = authViewModel,
            mainNavController = mainNavController
         )
      }
   }
}

@Composable
fun BottomNavigationBar(
   navController : NavHostController
) {
   
   val subScreens = listOf(
      SubScreens.Home,
      SubScreens.Games,
      SubScreens.Catalog,
      SubScreens.Profile
   )

   val navBackStackEntry by navController.currentBackStackEntryAsState()
   val currentDestination = navBackStackEntry?.destination

   NavigationBar(modifier = Modifier.background(Color.Red)) {
      subScreens.forEach { subScreen ->
         NavigationBarItem(
            label = { Text(subScreen.title) },
            icon = { Icon (subScreen.icon , contentDescription = subScreen.title) },
            selected = currentDestination?.hierarchy?.any { it.route == subScreen.route } == true,
            onClick = {
               navController.navigate(subScreen.route){
                  popUpTo(navController.graph.startDestinationId)
                  launchSingleTop = true
               }
            }
         )
      }
   }
}

