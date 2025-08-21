package com.example.project_umbrella.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import java.time.Instant

@Serializable
data class AuthRequest(
    val email: String,
    val password: String
)

@Serializable
data class RefreshRequest(
    val refreshToken : String
)

@Serializable
data class ExpiresAtInfo(
    @SerialName("value\$kotlinx_datetime") val datetimeValue: String,
    val nanosecondsOfSecond: Int,
    val epochSeconds: Long
)

@Serializable
data class AuthResponse(
    val userId: String,
    val accessToken: String,
    val refreshToken: String,
    @SerialName("expiresAt") val expiresAt: ExpiresAtInfo
)