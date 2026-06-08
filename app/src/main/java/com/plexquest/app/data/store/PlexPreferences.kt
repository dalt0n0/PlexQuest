package com.plexquest.app.data.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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
    // Auth
    private val AUTH_TOKEN = stringPreferencesKey("auth_token")
    private val USER_THUMB = stringPreferencesKey("user_thumb")
    private val USERNAME = stringPreferencesKey("username")
    private val SERVERS_JSON = stringPreferencesKey("servers_json_v2")
    private val ACTIVE_SERVER_ID = stringPreferencesKey("active_server_id")

    // Playback settings
    private val VIDEO_QUALITY_KBPS = intPreferencesKey("video_quality_kbps")
    private val DIRECT_PLAY_ENABLED = booleanPreferencesKey("direct_play_enabled")
    private val AUTO_PLAY_NEXT = booleanPreferencesKey("auto_play_next")
    private val PREFERRED_AUDIO_LANG = stringPreferencesKey("preferred_audio_lang")
    private val PREFERRED_SUBTITLE_LANG = stringPreferencesKey("preferred_subtitle_lang")

    val authToken: Flow<String?> = context.dataStore.data.map { it[AUTH_TOKEN] }
    val username: Flow<String?> = context.dataStore.data.map { it[USERNAME] }
    val userThumb: Flow<String?> = context.dataStore.data.map { it[USER_THUMB] }

    val servers: Flow<List<PlexServer>> = context.dataStore.data.map { prefs ->
        val json = prefs[SERVERS_JSON] ?: return@map emptyList()
        try {
            val type = object : TypeToken<List<PlexServer>>() {}.type
            (gson.fromJson<List<PlexServer>>(json, type) ?: emptyList())
                .filter { runCatching { it.baseUrl.isNotEmpty() }.getOrDefault(false) }
        } catch (_: Exception) { emptyList() }
    }

    val activeServerId: Flow<String?> = context.dataStore.data.map { it[ACTIVE_SERVER_ID] }

    // Playback pref flows
    val videoQualityKbps: Flow<Int> = context.dataStore.data.map { it[VIDEO_QUALITY_KBPS] ?: 8_000 }
    val directPlayEnabled: Flow<Boolean> = context.dataStore.data.map { it[DIRECT_PLAY_ENABLED] ?: true }
    val autoPlayNext: Flow<Boolean> = context.dataStore.data.map { it[AUTO_PLAY_NEXT] ?: false }
    val preferredAudioLang: Flow<String> = context.dataStore.data.map { it[PREFERRED_AUDIO_LANG] ?: "" }
    val preferredSubtitleLang: Flow<String> = context.dataStore.data.map { it[PREFERRED_SUBTITLE_LANG] ?: "" }

    suspend fun saveAuthInfo(token: String, username: String, thumb: String?) {
        context.dataStore.edit { prefs ->
            prefs[AUTH_TOKEN] = token
            prefs[USERNAME] = username
            if (thumb != null) prefs[USER_THUMB] = thumb
        }
    }

    suspend fun saveServers(serverList: List<PlexServer>) {
        context.dataStore.edit { prefs -> prefs[SERVERS_JSON] = gson.toJson(serverList) }
    }

    suspend fun setActiveServer(machineId: String) {
        context.dataStore.edit { prefs -> prefs[ACTIVE_SERVER_ID] = machineId }
    }

    suspend fun setVideoQuality(kbps: Int) {
        context.dataStore.edit { it[VIDEO_QUALITY_KBPS] = kbps }
    }

    suspend fun setDirectPlay(enabled: Boolean) {
        context.dataStore.edit { it[DIRECT_PLAY_ENABLED] = enabled }
    }

    suspend fun setAutoPlayNext(enabled: Boolean) {
        context.dataStore.edit { it[AUTO_PLAY_NEXT] = enabled }
    }

    suspend fun setPreferredAudioLang(lang: String) {
        context.dataStore.edit { it[PREFERRED_AUDIO_LANG] = lang }
    }

    suspend fun setPreferredSubtitleLang(lang: String) {
        context.dataStore.edit { it[PREFERRED_SUBTITLE_LANG] = lang }
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
