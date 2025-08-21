package com.example.project_umbrella.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun GameCard(
    coverUrl : String,
    name: String,
    onClick: () -> Unit,
    onClickAdd: () -> Unit,
    showAddButton: Boolean = true
) {

    Card (
        modifier = Modifier
            .padding(8.dp)
            .width(150.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ){
        Box(modifier = Modifier.fillMaxWidth())
        {
            Column {
                AsyncImage(
                    model = coverUrl,
                    contentDescription = name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.75f),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = name,
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if(showAddButton)
            {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add to list",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable(onClick = onClickAdd)
                        .padding(6.dp)
                )
            }

        }
    }
}

@Preview
@Composable
private fun GameCardPrev() {
    GameCard("https://picsum.photos/id/237/200/300",
        "Dogesh Bhai",
        {},
        {},
        false)
}