package com.example.project_umbrella.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.project_umbrella.dtos.GameResponseShort
import com.example.project_umbrella.navigation.Route
import com.example.project_umbrella.viewModel.GameViewModel

@Composable
fun Carousel(
    title : String = "Carousel Title",
    games : List<GameResponseShort> = emptyList<GameResponseShort>(),
    viewModel: GameViewModel,
    mainNavController : NavHostController
) {
    Column(

    ) {
        Text(text = title,
            Modifier.padding(horizontal = 16.dp),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow (
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){

            itemsIndexed(games) { index, game ->
                GameCard(
                    coverUrl = game.coverUrl,
                    name = game.name,
                    onClick = {mainNavController.navigate("game_info/${game.id}")},
                    onClickAdd = {viewModel.addGameToCatalogue(game.id)}
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}