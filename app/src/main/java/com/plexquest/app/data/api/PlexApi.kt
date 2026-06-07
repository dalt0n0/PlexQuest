package com.plexquest.app.data.api

import com.plexquest.app.data.models.PlexMediaContainer
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url

interface PlexApi {

    @GET
    suspend fun getLibraries(
        @Url url: String,
        @Header("X-Plex-Token") token: String,
    ): Response<PlexMediaContainer>

    @GET
    suspend fun getLibraryContents(
        @Url url: String,
        @Header("X-Plex-Token") token: String,
    ): Response<PlexMediaContainer>

    @GET
    suspend fun getRecentlyAdded(
        @Url url: String,
        @Header("X-Plex-Token") token: String,
    ): Response<PlexMediaContainer>

    @GET
    suspend fun getOnDeck(
        @Url url: String,
        @Header("X-Plex-Token") token: String,
    ): Response<PlexMediaContainer>

    @GET
    suspend fun getHomeHubs(
        @Url url: String,
        @Header("X-Plex-Token") token: String,
    ): Response<PlexMediaContainer>

    @GET
    suspend fun getMetadata(
        @Url url: String,
        @Header("X-Plex-Token") token: String,
    ): Response<PlexMediaContainer>

    @GET
    suspend fun getChildren(
        @Url url: String,
        @Header("X-Plex-Token") token: String,
    ): Response<PlexMediaContainer>

    @GET
    suspend fun search(
        @Url url: String,
        @Header("X-Plex-Token") token: String,
    ): Response<PlexMediaContainer>

    // Scrobble / timeline progress — Plex ignores the response body
    @GET
    suspend fun reportTimeline(
        @Url url: String,
        @Header("X-Plex-Token") token: String,
    ): Response<Unit>
}
