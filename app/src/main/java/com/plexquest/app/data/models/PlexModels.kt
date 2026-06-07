package com.plexquest.app.data.models

import com.google.gson.annotations.SerializedName

data class PlexServer(
    val name: String,
    val address: String,
    val port: Int = 32400,
    val token: String,
    val machineIdentifier: String,
    val local: Boolean = true,
) {
    val baseUrl: String get() = "http://$address:$port"
}

data class MediaLibrary(
    val key: String,
    val title: String,
    val type: LibraryType,
    val thumb: String?,
)

enum class LibraryType(val raw: String) {
    MOVIES("movie"),
    SHOWS("show"),
    MUSIC("artist"),
    PHOTOS("photo"),
    UNKNOWN("unknown");

    companion object {
        fun from(raw: String) = entries.firstOrNull { it.raw == raw } ?: UNKNOWN
    }
}

data class MediaItem(
    val ratingKey: String,
    val title: String,
    val year: Int?,
    val summary: String?,
    val thumb: String?,
    val art: String?,
    val type: String,
    val duration: Long?,
    val viewOffset: Long?,
    val grandparentTitle: String?,  // show title for episodes
    val parentIndex: Int?,          // season number
    val index: Int?,                // episode number
    val contentRating: String?,
    val rating: Double?,
    val audienceRating: Double?,
    val addedAt: Long?,
) {
    val isWatched: Boolean get() = viewOffset != null && duration != null && viewOffset >= duration * 0.9
    val progressPercent: Float get() {
        if (viewOffset == null || duration == null || duration == 0L) return 0f
        return (viewOffset.toFloat() / duration).coerceIn(0f, 1f)
    }
}

// Plex API response wrappers
data class PlexMediaContainer(
    @SerializedName("MediaContainer") val mediaContainer: MediaContainerData,
)

data class MediaContainerData(
    @SerializedName("Directory") val directories: List<DirectoryData>?,
    @SerializedName("Metadata") val metadata: List<MetadataData>?,
    val size: Int,
    val title1: String?,
    val title2: String?,
)

data class DirectoryData(
    val key: String,
    val title: String,
    val type: String?,
    val thumb: String?,
)

data class MetadataData(
    val ratingKey: String,
    val title: String,
    val year: Int?,
    val summary: String?,
    val thumb: String?,
    val art: String?,
    val type: String,
    val duration: Long?,
    val viewOffset: Long?,
    val grandparentTitle: String?,
    val parentIndex: Int?,
    val index: Int?,
    val contentRating: String?,
    val rating: Double?,
    val audienceRating: Double?,
    val addedAt: Long?,
    @SerializedName("Media") val media: List<MediaData>?,
)

data class MediaData(
    val id: Long,
    val duration: Long?,
    val bitrate: Int?,
    val width: Int?,
    val height: Int?,
    val videoResolution: String?,
    val videoCodec: String?,
    val audioCodec: String?,
    val audioChannels: Int?,
    @SerializedName("Part") val parts: List<PartData>?,
)

data class PartData(
    val id: Long,
    val key: String,
    val duration: Long?,
    val file: String?,
    val size: Long?,
)
