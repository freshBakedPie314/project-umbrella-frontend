package com.example.project_umbrella

import android.R
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "session")

class SessionManager(context: Context) {

    private val dataStore = context.dataStore

    companion object{
        val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        val EXPIRES_AT = longPreferencesKey("expires_at")
    }

    suspend fun saveTokens(accessToken : String, refreshToken: String, expiresAt: Long){
        dataStore.edit { prefrences ->
            prefrences[ACCESS_TOKEN_KEY] = accessToken
            prefrences[REFRESH_TOKEN_KEY] = refreshToken
            prefrences[EXPIRES_AT] = expiresAt
        }
    }

    val accessTokenAsFlow : Flow<String?> = dataStore.data.map {
        it[ACCESS_TOKEN_KEY]
    }

    val expiresAtAsFlow : Flow<Long?> = dataStore.data.map {
        it[EXPIRES_AT]
    }

    suspend fun clearTokens()
    {
        dataStore.edit { preferences ->
            preferences.clear()

        }
    }
}