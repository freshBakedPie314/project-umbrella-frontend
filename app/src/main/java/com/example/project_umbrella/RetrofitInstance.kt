package com.example.project_umbrella.network

import com.example.project_umbrella.services.AuthApiService
import com.example.project_umbrella.services.GameApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

object RetrofitInstance {

    private const val BASE_URL = "https://project-umbrella-backend.onrender.com/"

    private val httpCleint = OkHttpClient.Builder()
        .connectTimeout(30L, TimeUnit.SECONDS)
        .readTimeout(30L, TimeUnit.SECONDS)
        .writeTimeout(30L, TimeUnit.SECONDS)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(Json { ignoreUnknownKeys = true }.asConverterFactory("application/json".toMediaType()))
            .client(httpCleint)
            .build()
    }

    val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val gameApi: GameApiService by lazy {
        retrofit.create(GameApiService::class.java)
    }
}
