package com.example.project_umbrella.services

import com.example.project_umbrella.dtos.CatalogueAddRequest
import com.example.project_umbrella.dtos.GameResponseDetailed
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.project_umbrella.dtos.GameResponseShort
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface GameApiService {
    @GET("games/most-played")
    suspend fun GetMostPlayedGames(
        @Query("limit") limit : Int,
        @Query("offset") offset : Int
    ):List<GameResponseShort>

    @GET("games/most-anticipated")
    suspend fun GetMostAnticipated(
        @Query("limit") limit : Int,
        @Query("offset") offset : Int
    ):List<GameResponseShort>

    @GET("games/recently-released-most-rated")
    suspend fun GetRecentlyReleased(
        @Query("limit") limit : Int,
        @Query("offset") offset : Int
    ):List<GameResponseShort>

    @GET("games/release-date")
    suspend fun GetRecentlyReleasedGames(
        @Query("limit") limit : Int,
        @Query("offset") offset : Int
    ):List<GameResponseShort>

    @GET("games/highest-rated")
    suspend fun GetHighestRatedGames(
        @Query("limit") limit : Int,
        @Query("offset") offset : Int
    ):List<GameResponseShort>

    @GET("/games/{id}")
    suspend fun GetGameInfoById(
        @Path("id") id : Int
    ):List<GameResponseDetailed>


    //-----------------CATALOGUE STUFF--------------------

    @POST("catalogue/add")
    suspend fun AddGameToCatalogue(
        @Header("Authorization") token: String,
        @Body request: CatalogueAddRequest
    )

    @GET("catalogue")
    suspend fun GetCatalogueGames(
        @Query("limit") limit : Int,
        @Query("offset") offset : Int,
        @Header("Authorization") token: String
    ):List<GameResponseShort>
}