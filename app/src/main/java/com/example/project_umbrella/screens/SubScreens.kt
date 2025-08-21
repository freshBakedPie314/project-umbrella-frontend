package com.example.project_umbrella.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.project_umbrella.composables.Carousel
import com.example.project_umbrella.viewModel.GameViewModel
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.project_umbrella.SessionManager
import com.example.project_umbrella.composables.FilterDropDownMenu
import com.example.project_umbrella.composables.GameCard
import com.example.project_umbrella.navigation.Route
import com.example.project_umbrella.viewModel.AuthViewModel

sealed class SubScreens(
    val route : String,
    val title : String,
    val icon : ImageVector
){
    object Home : SubScreens(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )

    object Games: SubScreens (
        route = "games",
        title = "Games",
        icon = Icons.Default.Star
    )

    object Catalog: SubScreens (
        route = "catalog",
        title = "Catalog",
        icon = Icons.Default.Favorite
    )

    object Profile: SubScreens (
        route = "profile",
        title = "Profile",
        icon = Icons.Default.Person
    )
}

@Composable
fun HomeScreen(viewModel : GameViewModel, mainNavController : NavHostController) {

    val homeScreenState by viewModel.homeScreenState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ){
        LazyColumn(
        ) {
            item {
                Carousel("Most Anticipated" , homeScreenState.mostAnticipatedGames , viewModel, mainNavController)
            }

            item {
                Carousel("Recently Released", homeScreenState.recentlyReleasedGames, viewModel, mainNavController)
            }

            item {
                Carousel("Most Played", homeScreenState.mostPlayedGames, viewModel, mainNavController)
            }
        }
    }
}

@Composable
fun GamesScreen(viewModel : GameViewModel, mainNavController : NavHostController) {
    LaunchedEffect(key1 = viewModel.gameScreenState.collectAsState().value.selectedSorting) {
        if (viewModel.gameScreenState.value.games.isEmpty()) {
            viewModel.loadNextGamesPage()
        }
    }

    val state by viewModel.gameScreenState.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            FilterDropDownMenu(viewModel)
        }

        itemsIndexed(state.games) { index, game ->

            if (index == state.games.lastIndex && !state.isLoading && !state.endReached) {
                viewModel.loadNextGamesPage()
            }

            GameCard(
                name = game.name,
                coverUrl = game.coverUrl,
                onClick = { mainNavController.navigate("game_info/${game.id}") },
                onClickAdd = { viewModel.addGameToCatalogue(game.id) }
            )
        }

        if (state.isLoading) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun CatalogScreen(viewModel: GameViewModel,mainNavController : NavHostController) {

    viewModel.loadNextCataloguePage()
    Log.d("cat:" , "Catalogue refreshed")
    val state by viewModel.catalogueScreenState.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        itemsIndexed(state.games) { index, game ->

            if (index == state.games.lastIndex && !state.isLoading && !state.endReached) {
                viewModel.loadNextCataloguePage()
            }

            GameCard(
                name = game.name,
                coverUrl = game.coverUrl,
                onClick = { mainNavController.navigate("game_info/${game.id}") },
                onClickAdd = { viewModel.addGameToCatalogue(game.id)},
                showAddButton = false
            )
        }

        if (state.isLoading) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(authViewModel: AuthViewModel) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Button(
            onClick = {authViewModel.logout()}
        ) {
            Text("Logout")
        }
    }
}
