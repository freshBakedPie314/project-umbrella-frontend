package com.example.project_umbrella

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.project_umbrella.navigation.AppNavigation
import com.example.project_umbrella.ui.theme.Project_umbrellaTheme
import com.example.project_umbrella.viewModel.AuthViewModel
import com.example.project_umbrella.viewModel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sessionManager = SessionManager(applicationContext)

        val authViewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return AuthViewModel(sessionManager) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        val gameViewModelFactory = object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if(modelClass.isAssignableFrom(GameViewModel::class.java))
                {
                    @Suppress("UNCHECKED_CAST")
                    return GameViewModel(sessionManager) as T
                }
                throw IllegalArgumentException("Unknown Game View Model Class")
            }
        }

        setContent {
            Project_umbrellaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
                    val gameViewModel: GameViewModel = viewModel(factory = gameViewModelFactory)
                    AppNavigation(authViewModel = authViewModel, gameViewModel)
                }
            }
        }
    }
}
