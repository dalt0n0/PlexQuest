package com.plexquest.app.data.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plexquest.app.data.models.PlexServer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "plexquest_prefs")

@Singleton
class PlexPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson,
) {
    private val AUTH_TOKEN = stringPreferencesKey("auth_token")
    private val USER_THUMB = stringPreferencesKey("user_thumb")
    private val USERNAME = stringPreferencesKey("username")
    private val SERVERS_JSON = stringPreferencesKey("servers_json")
    private val ACTIVE_SERVER_ID = stringPreferencesKey("active_server_id")

    val authToken: Flow<String?> = context.dataStore.data.map { it[AUTH_TOKEN] }
    val username: Flow<String?> = context.dataStore.data.map { it[USERNAME] }
    val userThumb: Flow<String?> = context.dataStore.data.map { it[USER_THUMB] }

    val servers: Flow<List<PlexServer>> = context.dataStore.data.map { prefs ->
        val json = prefs[SERVERS_JSON] ?: return@map emptyList()
        val type = object : TypeToken<List<PlexServer>>() {}.type
        gson.fromJson(json, type) ?: emptyList()
    }

    val activeServerId: Flow<String?> = context.dataStore.data.map { it[ACTIVE_SERVER_ID] }

    suspend fun saveAuthInfo(token: String, username: String, thumb: String?) {
        context.dataStore.edit { prefs ->
            prefs[AUTH_TOKEN] = token
            prefs[USERNAME] = username
            if (thumb != null) prefs[USER_THUMB] = thumb
        }
    }

    suspend fun saveServers(serverList: List<PlexServer>) {
        context.dataStore.edit { prefs ->
            prefs[SERVERS_JSON] = gson.toJson(serverList)
        }
    }

    suspend fun setActiveServer(machineId: String) {
        context.dataStore.edit { prefs ->
            prefs[ACTIVE_SERVER_ID] = machineId
        }
    }

    suspend fun clearAuth() {
        context.dataStore.edit { prefs ->
            prefs.remove(AUTH_TOKEN)
            prefs.remove(USERNAME)
            prefs.remove(USER_THUMB)
            prefs.remove(SERVERS_JSON)
            prefs.remove(ACTIVE_SERVER_ID)
        }
    }
}
