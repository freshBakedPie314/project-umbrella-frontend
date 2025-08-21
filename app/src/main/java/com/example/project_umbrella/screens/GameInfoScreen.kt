package com.example.project_umbrella.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.project_umbrella.dtos.GameResponseDetailed
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun GameInfoScreen(game: GameResponseDetailed) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }

    var dragOffset by remember { mutableFloatStateOf(0f) }
    val maxDragOffset = screenHeight * 0.8f

    val progress = (dragOffset / maxDragOffset).coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 200),
        label = "progress"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        dragOffset = if (dragOffset > maxDragOffset * 0.3f) {
                            maxDragOffset
                        } else {
                            0f
                        }
                    }
                ) { _, dragAmount ->
                    val newOffset = dragOffset - dragAmount.y
                    dragOffset = newOffset.coerceIn(0f, maxDragOffset)
                }
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color(0xFF1A1A1A).copy(alpha = 0.3f + animatedProgress * 0.7f)
                )
        )

        // Main content container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset {
                    IntOffset(
                        x = 0,
                        y = -animatedProgress.roundToInt()
                    )
                }
        ) {
            TransformingImageView(
                game = game,
                progress = animatedProgress,
                screenWidth = configuration.screenWidthDp.dp
            )
            GameDetailsPanel(
                game = game,
                progress = animatedProgress,
                screenWidth = configuration.screenWidthDp.dp
            )
        }
    }
}

// Keep the TransformingImageView and GameDetailsPanel composables as they were
// (with the changes mentioned above for alignment)
// Just ensure they are defined in the same file or imported correctly.

@Composable
private fun TransformingImageView(
    game: GameResponseDetailed,
    progress: Float,
    screenWidth: androidx.compose.ui.unit.Dp
) {
    val imageWidth = lerp(screenWidth, screenWidth * 0.4f, progress)
    val imageHeight = lerp(screenWidth * 1.2f, screenWidth * 0.4f * 1.5f, progress)
    val imageOffsetX = lerp(0.dp, screenWidth * 0.6f, progress)
    val imageOffsetY = lerp(0.dp, 100.dp, progress)
    val cornerRadius = lerp(0.dp, 16.dp, progress)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .width(imageWidth)
                .height(imageHeight)
                .offset(x = imageOffsetX, y = imageOffsetY)
                .align(
                    if (progress < 0.3f) Alignment.Center
                    else Alignment.TopStart
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(Color.Gray)
            )

            AsyncImage(
                model = game.coverUrl.takeIf { it.isNotBlank() }
                    ?: "https://picsum.photos/400/600",
                contentDescription = "Game cover",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(cornerRadius))
                    .graphicsLayer {
                        shadowElevation = (progress * 8f).dp.toPx()
                    },
                contentScale = ContentScale.Crop
            )

            if (progress > 0.7f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(
                            MaterialTheme.colorScheme.primary.copy(
                                alpha = (progress - 0.7f) * 0.3f
                            )
                        )
                )
            }
        }

        if (progress < 0.5f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(alpha = 0.3f * (1f - progress * 2f))
                    )
            )

            Text(
                text = game.name,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
                    .graphicsLayer {
                        alpha = 1f - (progress * 3f).coerceIn(0f, 1f)
                    },
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun GameDetailsPanel(
    game: GameResponseDetailed,
    progress: Float,
    screenWidth: androidx.compose.ui.unit.Dp
) {
    if (progress > 0.2f) {
        val panelAlpha = ((progress - 0.2f) / 0.8f).coerceIn(0f, 1f)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = panelAlpha
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .fillMaxHeight()
                    .align(Alignment.TopStart)
                    .padding(
                        start = 16.dp,
                        end = 8.dp,
                        top = 100.dp,
                        bottom = 16.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = game.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Genres",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = game.genres.joinToString(" • "),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Rating",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${game.igdbRating}/10",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Green
                        )
                        Text(
                            text = "${game.igdbRatingCount} votes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = game.status,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Release Date",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = game.releaseDate,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Swipe down to return",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}