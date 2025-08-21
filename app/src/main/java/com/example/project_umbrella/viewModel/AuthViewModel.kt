package com.example.project_umbrella.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.project_umbrella.SessionManager
import com.example.project_umbrella.dtos.AuthRequest
import com.example.project_umbrella.navigation.Route
import com.example.project_umbrella.network.RetrofitInstance
import com.example.project_umbrella.services.AuthApiService
import com.example.project_umbrella.services.GameApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

// Represents the state of the login UI
data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val sessionManager: SessionManager,
) : ViewModel() {

    val authApiService = RetrofitInstance.authApi

    val authToken: StateFlow<String?> = sessionManager.accessTokenAsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _authUiState = MutableStateFlow(AuthUiState())
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    val isSessionValid : StateFlow<Boolean> = sessionManager.accessTokenAsFlow.combine(sessionManager.expiresAtAsFlow) { token, expiresAt ->
        val tokemExists = token != null
        val isTokenExpired = expiresAt?.let { expSec ->
            (expSec * 1000) < System.currentTimeMillis()
        } ?: true

        return@combine tokemExists && !isTokenExpired
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun login(email: String, password: String, navController: NavHostController) {
        viewModelScope.launch {
            Log.d("auth", "login")
            _authUiState.value = AuthUiState(isLoading = true)
            try {
                val request = AuthRequest(email.trim(), password)
                val response = authApiService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    sessionManager.saveTokens(
                        authResponse.accessToken,
                        authResponse.refreshToken,
                        authResponse.expiresAt.epochSeconds
                    )
                    _authUiState.value = AuthUiState(isLoading = false)
                    Log.d("auth", "login succesful")
                    navController.navigate(Route.APP) {
                        popUpTo(Route.AUTH) { inclusive = true }
                    }
                } else {
                    // Handle server error (e.g., wrong password)

                    val errorBody = response.errorBody()?.string() ?: "Login failed"
                    _authUiState.value = AuthUiState(isLoading = false, error = errorBody)
                    Log.d("auth", errorBody)
                }
            } catch (e: Exception) {
                Log.d("auth", e.message.toString())
                // Handle network error (e.g., no internet)
                _authUiState.value = AuthUiState(isLoading = false, error = e.message ?: "An unknown error occurred")
            }
        }
    }

    fun register(email: String, password: String, navController: NavHostController) {
        viewModelScope.launch {
            Log.d("auth", "register")
            _authUiState.value = AuthUiState(isLoading = true)
            try {
                val request = AuthRequest(email.trim(), password)
                val response = authApiService.register(request)

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    sessionManager.saveTokens(
                        authResponse.accessToken,
                        authResponse.refreshToken,
                        authResponse.expiresAt.epochSeconds
                    )
                    _authUiState.value = AuthUiState(isLoading = false)
                    Log.d("auth", "register succesful")
                    navController.navigate(Route.APP) {
                        popUpTo(Route.AUTH) { inclusive = true }
                    }
                } else {
                    // Handle server error (e.g., wrong password)

                    val errorBody = response.errorBody()?.string() ?: "register failed"
                    _authUiState.value = AuthUiState(isLoading = false, error = errorBody)
                    Log.d("auth", errorBody)
                }
            } catch (e: Exception) {
                Log.d("auth", e.message.toString())
                // Handle network error (e.g., no internet)
                _authUiState.value = AuthUiState(isLoading = false, error = e.message ?: "An unknown error occurred")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authApiService.logout()
                Log.d("auth" , "Logout successful")
            }
            catch (e : Exception){
                Log.d("auth" , "$e.message")
            }
            finally {
                sessionManager.clearTokens()
            }
        }
    }
}
