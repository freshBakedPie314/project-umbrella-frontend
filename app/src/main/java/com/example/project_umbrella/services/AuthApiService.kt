package com.example.project_umbrella.services

import com.example.project_umbrella.dtos.AuthRequest
import com.example.project_umbrella.dtos.AuthResponse
import com.example.project_umbrella.dtos.RefreshRequest
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("/auth/login")
    suspend fun login(
        @Body request: AuthRequest
    ): retrofit2.Response<AuthResponse>

    @POST("/auth/register")
    suspend fun register(
        @Body request: AuthRequest
    ): retrofit2.Response<AuthResponse>

    @POST("/auth/logout")
    suspend fun logout(): retrofit2.Response<Any>

    @POST("/auth/refresh")
    suspend fun refresh(
        @Body request: RefreshRequest
    ) : retrofit2.Response<Any>
}