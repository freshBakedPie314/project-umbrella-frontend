package com.example.project_umbrella.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CatalogueAddRequest(
    @SerialName("game_id") val gameId : Int
)
