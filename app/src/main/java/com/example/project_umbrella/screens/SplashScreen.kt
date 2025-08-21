package com.example.yourapp // Replace with your actual package name

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_umbrella.viewModel.SplashScreenViewModel

@Composable
fun SplashScreen(
    splashViewModel: SplashScreenViewModel = viewModel(),
    onNavigateToMain: () -> Unit
) {

    val isBackendLive by splashViewModel.isBackendLive.collectAsState()


    LaunchedEffect(isBackendLive) {
        if (isBackendLive) {
            onNavigateToMain()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1C2E)), // Dark blue background
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Waking up the server...",
            color = Color.White,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        CircularProgressIndicator(color = Color.White)
    }
}
