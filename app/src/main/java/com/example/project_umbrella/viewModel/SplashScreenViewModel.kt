package com.example.project_umbrella.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers // Import Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay // Correct import
import kotlinx.coroutines.withContext // Import withContext
import java.net.HttpURLConnection
import java.net.URL

class SplashScreenViewModel : ViewModel() {
    private val _isBackendLive = MutableStateFlow(false)
    val isBackendLive = _isBackendLive.asStateFlow()

    init {
        pollBackEndStatus()
    }

    private fun pollBackEndStatus() {
        viewModelScope.launch {
            while (isActive && !_isBackendLive.value) {
                withContext(Dispatchers.IO) {
                    var connection: HttpURLConnection? = null
                    try {
                        val url = URL("https://project-umbrella-backend.onrender.com/test")
                        connection = url.openConnection() as HttpURLConnection
                        connection.requestMethod = "GET"
                        connection.connectTimeout = 5000
                        connection.readTimeout = 5000

                        val responseCode = connection.responseCode
                        Log.d("Splash", "Backend Response Code: $responseCode")

                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            _isBackendLive.value = true
                            Log.d("Splash", "Success: Backend is live.")
                        }
                    } catch (e: Exception) {
                        Log.e("Splash", "Error polling backend: ${e.message}")
                    } finally {
                        connection?.disconnect()
                    }
                }

                if (!_isBackendLive.value) {
                    delay(2000)
                }
            }
        }
    }
}
