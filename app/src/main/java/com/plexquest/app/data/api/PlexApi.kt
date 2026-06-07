package com.plexquest.app.data.api

import com.plexquest.app.data.models.PlexMediaContainer
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface PlexApi {

    @GET("library/sections")
    suspend fun getLibraries(
        @Header("X-Plex-Token") token: String,
    ): Response<PlexMediaContainer>

    @GET("library/sections/{sectionId}/all")
    suspend fun getLibraryContents(
        @Path("sectionId") sectionId: String,
        @Header("X-Plex-Token") token: String,
        @Query("X-Plex-Container-Start") start: Int = 0,
        @Query("X-Plex-Container-Size") size: Int = 50,
        @Query("sort") sort: String = "addedAt:desc",
    ): Response<PlexMediaContainer>

    @GET("library/sections/{sectionId}/recentlyAdded")
    suspend fun getRecentlyAdded(
        @Path("sectionId") sectionId: String,
        @Header("X-Plex-Token") token: String,
        @Query("X-Plex-Container-Size") size: Int = 20,
    ): Response<PlexMediaContainer>

    @GET("library/sections/{sectionId}/onDeck")
    suspend fun getOnDeck(
        @Path("sectionId") sectionId: String,
        @Header("X-Plex-Token") token: String,
    ): Response<PlexMediaContainer>

    @GET("hubs/home")
    suspend fun getHomeHubs(
        @Header("X-Plex-Token") token: String,
    ): Response<PlexMediaContainer>

    @GET("library/metadata/{ratingKey}")
    suspend fun getMetadata(
        @Path("ratingKey") ratingKey: String,
        @Header("X-Plex-Token") token: String,
    ): Response<PlexMediaContainer>

    @GET("library/metadata/{ratingKey}/children")
    suspend fun getChildren(
        @Path("ratingKey") ratingKey: String,
        @Header("X-Plex-Token") token: String,
    ): Response<PlexMediaContainer>

    @GET("search")
    suspend fun search(
        @Query("query") query: String,
        @Header("X-Plex-Token") token: String,
        @Query("limit") limit: Int = 20,
    ): Response<PlexMediaContainer>

    // Dynamic URL for thumbnail/art proxying through the selected server
    @GET
    suspend fun getResource(@Url url: String): Response<okhttp3.ResponseBody>
}
