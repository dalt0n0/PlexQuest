package com.plexquest.app.data.repository

import com.plexquest.app.data.api.PlexApi
import com.plexquest.app.data.models.LibraryType
import com.plexquest.app.data.models.MediaItem
import com.plexquest.app.data.models.MediaLibrary
import com.plexquest.app.data.models.MetadataData
import com.plexquest.app.data.models.PlexServer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

sealed class PlexResult<out T> {
    data class Success<T>(val data: T) : PlexResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : PlexResult<Nothing>()
    data object Loading : PlexResult<Nothing>()
}

@Singleton
class PlexRepository @Inject constructor(
    private val plexApi: PlexApi,
) {

    fun getLibraries(server: PlexServer): Flow<PlexResult<List<MediaLibrary>>> = flow {
        emit(PlexResult.Loading)
        try {
            val url = "${server.baseUrl}/library/sections"
            val response = plexApi.getLibraries(url, server.token)
            if (response.isSuccessful) {
                val libraries = response.body()?.mediaContainer?.directories
                    ?.map { dir ->
                        MediaLibrary(
                            key = dir.key,
                            title = dir.title,
                            type = LibraryType.from(dir.type ?: ""),
                            thumb = dir.thumb?.let { "${server.baseUrl}$it" },
                        )
                    } ?: emptyList()
                emit(PlexResult.Success(libraries))
            } else {
                emit(PlexResult.Error("Server error: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(PlexResult.Error("Network error: ${e.message}", e))
        }
    }

    fun getLibraryContents(
        server: PlexServer,
        sectionId: String,
        start: Int = 0,
        pageSize: Int = 50,
    ): Flow<PlexResult<List<MediaItem>>> = flow {
        emit(PlexResult.Loading)
        try {
            val url = "${server.baseUrl}/library/sections/$sectionId/all" +
                "?X-Plex-Container-Start=$start&X-Plex-Container-Size=$pageSize&sort=addedAt:desc"
            val response = plexApi.getLibraryContents(url, server.token)
            if (response.isSuccessful) {
                val items = response.body()?.mediaContainer?.metadata
                    ?.map { it.toMediaItem(server) } ?: emptyList()
                emit(PlexResult.Success(items))
            } else {
                emit(PlexResult.Error("Server error: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(PlexResult.Error("Network error: ${e.message}", e))
        }
    }

    fun getRecentlyAdded(server: PlexServer, sectionId: String): Flow<PlexResult<List<MediaItem>>> = flow {
        emit(PlexResult.Loading)
        try {
            val url = "${server.baseUrl}/library/sections/$sectionId/recentlyAdded?X-Plex-Container-Size=20"
            val response = plexApi.getRecentlyAdded(url, server.token)
            if (response.isSuccessful) {
                val items = response.body()?.mediaContainer?.metadata
                    ?.map { it.toMediaItem(server) } ?: emptyList()
                emit(PlexResult.Success(items))
            } else {
                emit(PlexResult.Error("Server error: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(PlexResult.Error("Network error: ${e.message}", e))
        }
    }

    // Global on-deck across all libraries
    fun getOnDeck(server: PlexServer): Flow<PlexResult<List<MediaItem>>> = flow {
        emit(PlexResult.Loading)
        try {
            val url = "${server.baseUrl}/library/onDeck?X-Plex-Container-Size=20"
            val response = plexApi.getOnDeck(url, server.token)
            if (response.isSuccessful) {
                val items = response.body()?.mediaContainer?.metadata
                    ?.map { it.toMediaItem(server) } ?: emptyList()
                emit(PlexResult.Success(items))
            } else {
                emit(PlexResult.Error("Server error: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(PlexResult.Error("Network error: ${e.message}", e))
        }
    }

    fun getMetadata(server: PlexServer, ratingKey: String): Flow<PlexResult<MetadataData>> = flow {
        emit(PlexResult.Loading)
        try {
            val url = "${server.baseUrl}/library/metadata/$ratingKey"
            val response = plexApi.getMetadata(url, server.token)
            if (response.isSuccessful) {
                val item = response.body()?.mediaContainer?.metadata?.firstOrNull()
                if (item != null) emit(PlexResult.Success(item))
                else emit(PlexResult.Error("No metadata returned"))
            } else {
                emit(PlexResult.Error("Server error: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(PlexResult.Error("Network error: ${e.message}", e))
        }
    }

    fun search(server: PlexServer, query: String): Flow<PlexResult<List<MediaItem>>> = flow {
        emit(PlexResult.Loading)
        try {
            val encoded = java.net.URLEncoder.encode(query, "UTF-8")
            val url = "${server.baseUrl}/search?query=$encoded&limit=30"
            val response = plexApi.search(url, server.token)
            if (response.isSuccessful) {
                val items = response.body()?.mediaContainer?.metadata
                    ?.map { it.toMediaItem(server) } ?: emptyList()
                emit(PlexResult.Success(items))
            } else {
                emit(PlexResult.Error("Server error: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(PlexResult.Error("Network error: ${e.message}", e))
        }
    }

    fun getChildren(server: PlexServer, ratingKey: String): Flow<PlexResult<List<MediaItem>>> = flow {
        emit(PlexResult.Loading)
        try {
            val url = "${server.baseUrl}/library/metadata/$ratingKey/children"
            val response = plexApi.getChildren(url, server.token)
            if (response.isSuccessful) {
                val items = response.body()?.mediaContainer?.metadata
                    ?.map { it.toMediaItem(server) } ?: emptyList()
                emit(PlexResult.Success(items))
            } else {
                emit(PlexResult.Error("Server error: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(PlexResult.Error("Network error: ${e.message}", e))
        }
    }

    fun buildStreamUrl(server: PlexServer, partKey: String): String =
        "${server.baseUrl}$partKey?X-Plex-Token=${server.token}"

    private fun MetadataData.toMediaItem(server: PlexServer) = MediaItem(
        ratingKey = ratingKey,
        title = title,
        year = year,
        summary = summary,
        thumb = thumb?.let { "${server.baseUrl}$it?X-Plex-Token=${server.token}" },
        art = art?.let { "${server.baseUrl}$it?X-Plex-Token=${server.token}" },
        type = type,
        duration = duration,
        viewOffset = viewOffset,
        grandparentTitle = grandparentTitle,
        parentIndex = parentIndex,
        index = index,
        contentRating = contentRating,
        rating = rating,
        audienceRating = audienceRating,
        addedAt = addedAt,
    )
}
