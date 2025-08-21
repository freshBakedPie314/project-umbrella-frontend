package com.example.project_umbrella.dtos

import kotlinx.serialization.Serializable

@Serializable
data class GameResponseShort(
    val id : Int,
    val name: String,
    val genres : List<String>,
    val coverUrl: String
)

@Serializable
data class GameResponseDetailed(
    val id : Int,
    val name: String,
    val genres: List<String>,
    val coverUrl: String,
    val status: String,
    val igdbRating : Double,
    val igdbRatingCount : Int,
    val scrrenshots : List<String>,
    val releaseDate : String
)
