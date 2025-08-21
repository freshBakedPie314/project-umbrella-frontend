package com.example.project_umbrella.viewModel

import android.util.Log
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_umbrella.dtos.GameResponseShort
import com.example.project_umbrella.services.GameApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.datastore.dataStore
import com.example.project_umbrella.SessionManager
import com.example.project_umbrella.dtos.CatalogueAddRequest
import com.example.project_umbrella.dtos.GameResponseDetailed
import com.example.project_umbrella.network.RetrofitInstance
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.flow
import java.time.Instant

data class HomeScreenState(
    val mostAnticipatedGames : List<GameResponseShort> = emptyList<GameResponseShort>(),
    val recentlyReleasedGames : List<GameResponseShort> = emptyList<GameResponseShort>(),
    val mostPlayedGames : List<GameResponseShort> = emptyList<GameResponseShort>(),

    val isLoading : Boolean = false,
    val error : String? = null
)

data class GameScreenState(
    val games: List<GameResponseShort> = emptyList(),
    val selectedSorting: String = "Most Played",
    val isLoading: Boolean = false,
    val endReached: Boolean = false,
    val error: String? = null
)

data class CatalogueScreenState(
    val games : List<GameResponseShort> = emptyList(),
    val isLoading: Boolean = false,
    val endReached: Boolean = false,
    val error : String? = null
)

val TAG = "viewModel"
class GameViewModel(sessionManager: SessionManager) : ViewModel() {
    private val _homeScreenState = MutableStateFlow(HomeScreenState())
    val homeScreenState = _homeScreenState.asStateFlow()

    private val _gameScreenState = MutableStateFlow(GameScreenState())
    val gameScreenState = _gameScreenState.asStateFlow()

    val authToken: StateFlow<String?> = sessionManager.accessTokenAsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val apiService = RetrofitInstance.gameApi

    private var _selectedSorting = MutableStateFlow("Most Rated")
    val selectedSorting = _selectedSorting.asStateFlow()

    private var _gameDetails = MutableStateFlow<GameResponseDetailed?>(null)
    val gameDetails = _gameDetails.asStateFlow()
    fun onSortingChanged(selected : String)
    {
        _gameScreenState.update {
            it.copy(
                selectedSorting = selected,
                games = emptyList(),
                endReached = false,
                error = null
            )
        }
        loadNextGamesPage()
    }
    init {
        loadHomeScreen()
    }

    fun loadHomeScreen()
    {
        viewModelScope.launch {
            _homeScreenState.update { it.copy(isLoading = true) }
            try {
                val mostAnticipated = apiService.GetMostAnticipated(limit = 15, offset = 0)
                val recentlyReleased = apiService.GetRecentlyReleased(limit = 15, offset = 0)
                val mostPlayed = apiService.GetMostPlayedGames(limit = 15, offset = 0)

                _homeScreenState.update {
                    it.copy(
                        mostAnticipatedGames = mostAnticipated,
                        recentlyReleasedGames = recentlyReleased,
                        mostPlayedGames = mostPlayed,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching home screen games: ${e.message}")
                _homeScreenState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun loadNextGamesPage() {
        viewModelScope.launch {
            val currentState = _gameScreenState.value
            if (currentState.isLoading || currentState.endReached) return@launch

            _gameScreenState.update { it.copy(isLoading = true) }

            try {
                val newGames = when (currentState.selectedSorting) {
                    "Highest Rated" -> apiService.GetHighestRatedGames(limit = 10, offset = currentState.games.size)
                    "Recently Released" -> apiService.GetRecentlyReleasedGames(limit = 10, offset = currentState.games.size)
                    else -> apiService.GetMostPlayedGames(limit = 10, offset = currentState.games.size) // Default to Most Played
                }

                _gameScreenState.update {
                    it.copy(
                        games = it.games + newGames,
                        isLoading = false,
                        endReached = newGames.isEmpty()
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching games page: ${e.message}")
                _gameScreenState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun addGameToCatalogue(gameId : Int){
        viewModelScope.launch {
            val currentToken = authToken.value

            if(currentToken == null){
                Log.e(TAG, "Cannot add game to catalogue: Auth token is null.")
                return@launch
            }

            Log.d(TAG, "Trying To Add Game ID: $gameId")
            try {
                apiService.AddGameToCatalogue(
                    token = "Bearer $currentToken",
                    request = CatalogueAddRequest(gameId)
                )
                Log.d(TAG, "Successfully added game to catalogue.")
                _catalogueScreenState.update {
                    it.copy(
                        endReached = false
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error adding game to catalogue: ${e.message}")
            }
        }
    }

    private val _catalogueScreenState = MutableStateFlow(CatalogueScreenState())
    val catalogueScreenState = _catalogueScreenState.asStateFlow()

    fun loadNextCataloguePage()
    {
        viewModelScope.launch {
            val currentState = _catalogueScreenState.value
            if (currentState.isLoading || currentState.endReached) return@launch

            _catalogueScreenState.update {
                it.copy(isLoading = true)
            }

            try {
                val currentToken = "Bearer " + authToken.value
                val newGames = apiService.GetCatalogueGames(limit = 10, offset = currentState.games.size,
                    token = currentToken)

                _catalogueScreenState.update {
                    it.copy(
                        games = it.games + newGames,
                        isLoading = false,
                        endReached = newGames.isEmpty(),
                    )
                }
            }
            catch (e : Exception)
            {
                Log.e(TAG, "Error fetching catalogue page: ${e.message}")
                _catalogueScreenState.update {
                    it.copy(
                        error = e.message
                    )
                }
            }
        }
    }

    fun getGameDetailsById(gameId : Int) {
        viewModelScope.launch {
             try {
                val gamesList = apiService.GetGameInfoById(gameId)
                if(gamesList.isNotEmpty()){
                     _gameDetails.value = gamesList[0]
                }else{
                    Log.d("viewModel" , "Unable to fetch game details")
                    _gameDetails.value = null
                }
            }catch (e : Exception) {
                 Log.d("viewModel" , e.message.toString())
                 _gameDetails.value = null
            }
        }
    }
}