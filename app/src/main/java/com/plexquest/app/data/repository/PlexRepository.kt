package com.plexquest.app.data.repository

import com.plexquest.app.data.api.PlexApi
import com.plexquest.app.data.models.LibraryType
import com.plexquest.app.data.models.MediaItem
import com.plexquest.app.data.models.MediaLibrary
import com.plexquest.app.data.models.MetadataData
import com.plexquest.app.data.models.PlexHub
import com.plexquest.app.data.models.PlexServer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

sealed class PlexResult<out T> {
    data class Success<T>(val data: T) : PlexResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : PlexResult<Nothing>()
    data object Loading : PlexResult<Nothing>()
}

// All flows use .catch { } instead of try/catch inside the builder.
// flow { } builders must NOT catch CancellationException subclasses (like AbortFlowException
// thrown by first { } terminal operators). The .catch operator handles this correctly.
@Singleton
class PlexRepository @Inject constructor(
    private val plexApi: PlexApi,
) {

    fun getLibraries(server: PlexServer): Flow<PlexResult<List<MediaLibrary>>> = flow {
        emit(PlexResult.Loading)
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
    }.catch { e -> emit(PlexResult.Error("Network error: ${e.message}", e)) }

    fun getLibraryContents(
        server: PlexServer,
        sectionId: String,
        start: Int = 0,
        pageSize: Int = 50,
    ): Flow<PlexResult<List<MediaItem>>> = flow {
        emit(PlexResult.Loading)
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
    }.catch { e -> emit(PlexResult.Error("Network error: ${e.message}", e)) }

    fun getRecentlyAdded(server: PlexServer, sectionId: String): Flow<PlexResult<List<MediaItem>>> = flow {
        emit(PlexResult.Loading)
        val url = "${server.baseUrl}/library/sections/$sectionId/recentlyAdded?X-Plex-Container-Size=20"
        val response = plexApi.getRecentlyAdded(url, server.token)
        if (response.isSuccessful) {
            val items = response.body()?.mediaContainer?.metadata
                ?.map { it.toMediaItem(server) } ?: emptyList()
            emit(PlexResult.Success(items))
        } else {
            emit(PlexResult.Error("Server error: ${response.code()}"))
        }
    }.catch { e -> emit(PlexResult.Error("Network error: ${e.message}", e)) }

    fun getOnDeck(server: PlexServer): Flow<PlexResult<List<MediaItem>>> = flow {
        emit(PlexResult.Loading)
        val url = "${server.baseUrl}/library/onDeck?X-Plex-Container-Size=20"
        val response = plexApi.getOnDeck(url, server.token)
        if (response.isSuccessful) {
            val items = response.body()?.mediaContainer?.metadata
                ?.map { it.toMediaItem(server) } ?: emptyList()
            emit(PlexResult.Success(items))
        } else {
            emit(PlexResult.Error("Server error: ${response.code()}"))
        }
    }.catch { e -> emit(PlexResult.Error("Network error: ${e.message}", e)) }

    fun getMetadata(server: PlexServer, ratingKey: String): Flow<PlexResult<MetadataData>> = flow {
        emit(PlexResult.Loading)
        val url = "${server.baseUrl}/library/metadata/$ratingKey"
        val response = plexApi.getMetadata(url, server.token)
        if (response.isSuccessful) {
            val item = response.body()?.mediaContainer?.metadata?.firstOrNull()
            if (item != null) emit(PlexResult.Success(item))
            else emit(PlexResult.Error("No metadata returned"))
        } else {
            emit(PlexResult.Error("Server error: ${response.code()}"))
        }
    }.catch { e -> emit(PlexResult.Error("Network error: ${e.message}", e)) }

    fun search(server: PlexServer, query: String): Flow<PlexResult<List<MediaItem>>> = flow {
        emit(PlexResult.Loading)
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
    }.catch { e -> emit(PlexResult.Error("Network error: ${e.message}", e)) }

    fun getChildren(server: PlexServer, ratingKey: String): Flow<PlexResult<List<MediaItem>>> = flow {
        emit(PlexResult.Loading)
        val url = "${server.baseUrl}/library/metadata/$ratingKey/children"
        val response = plexApi.getChildren(url, server.token)
        if (response.isSuccessful) {
            val items = response.body()?.mediaContainer?.metadata
                ?.map { it.toMediaItem(server) } ?: emptyList()
            emit(PlexResult.Success(items))
        } else {
            emit(PlexResult.Error("Server error: ${response.code()}"))
        }
    }.catch { e -> emit(PlexResult.Error("Network error: ${e.message}", e)) }

    fun getHomeHubs(server: PlexServer): Flow<PlexResult<List<PlexHub>>> = flow {
        emit(PlexResult.Loading)
        val url = "${server.baseUrl}/hubs/home?count=20&includeEmpty=0"
        val response = plexApi.getHomeHubs(url, server.token)
        if (response.isSuccessful) {
            val hubs = response.body()?.mediaContainer?.hubs
                ?.filter { (it.metadata?.size ?: 0) > 0 }
                ?.map { hub -> hub.copy(metadata = hub.metadata?.map { it }) }
                ?: emptyList()
            emit(PlexResult.Success(hubs))
        } else {
            emit(PlexResult.Error("Server error: ${response.code()}"))
        }
    }.catch { e -> emit(PlexResult.Error("Network error: ${e.message}", e)) }

    suspend fun reportTimeline(
        server: PlexServer,
        ratingKey: String,
        partKey: String,
        state: String,
        positionMs: Long,
        durationMs: Long,
    ) {
        try {
            val url = "${server.baseUrl}/:/timeline" +
                "?ratingKey=$ratingKey" +
                "&key=/library/metadata/$ratingKey" +
                "&state=$state" +
                "&time=$positionMs" +
                "&duration=$durationMs" +
                "&hasMDE=1"
            plexApi.reportTimeline(url, server.token)
        } catch (_: Exception) { /* best-effort */ }
    }

    fun buildStreamUrl(server: PlexServer, partKey: String): String =
        "${server.baseUrl}$partKey?X-Plex-Token=${server.token}"

    fun buildTranscodeUrl(server: PlexServer, ratingKey: String, partKey: String, qualityKbps: Int): String {
        val (w, h) = when {
            qualityKbps >= 20_000 -> "3840" to "2160"
            qualityKbps >= 8_000  -> "1920" to "1080"
            qualityKbps >= 4_000  -> "1280" to "720"
            qualityKbps >= 2_000  -> "854"  to "480"
            else                  -> "640"  to "360"
        }
        return "${server.baseUrl}/video/:/transcode/universal/start.m3u8" +
            "?path=/library/metadata/$ratingKey" +
            "&mediaIndex=0&partIndex=0" +
            "&protocol=hls&fastSeek=1&directPlay=0&directStream=0" +
            "&videoResolution=${w}x${h}&maxVideoBitrate=$qualityKbps" +
            "&audioCodec=aac&audioBitrate=256&audioChannels=2" +
            "&X-Plex-Token=${server.token}" +
            "&X-Plex-Client-Identifier=${com.plexquest.app.PlexConstants.CLIENT_ID}" +
            "&X-Plex-Product=PlexQuest" +
            "&X-Plex-Platform=Android" +
            "&X-Plex-Device=MetaQuest"
    }

    fun MetadataData.toMediaItem(server: PlexServer) = MediaItem(
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
