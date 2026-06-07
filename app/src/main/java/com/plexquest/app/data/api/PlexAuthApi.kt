package com.plexquest.app.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

// Talks to plex.tv for auth + server discovery
interface PlexAuthApi {

    @FormUrlEncoded
    @POST("users/sign_in.json")
    suspend fun signIn(
        @Field("user[login]") login: String,
        @Field("user[password]") password: String,
        @Header("X-Plex-Client-Identifier") clientId: String,
        @Header("X-Plex-Product") product: String = "PlexQuest",
        @Header("X-Plex-Version") version: String = "0.1.0",
        @Header("X-Plex-Device") device: String = "Meta Quest",
        @Header("X-Plex-Platform") platform: String = "Android",
    ): Response<PlexUserResponse>

    @GET("pins/{pinId}.json")
    suspend fun checkPin(
        @Path("pinId") pinId: Long,
        @Header("X-Plex-Client-Identifier") clientId: String,
    ): Response<PlexPinResponse>

    @POST("pins.json")
    suspend fun createPin(
        @Header("X-Plex-Client-Identifier") clientId: String,
        @Header("X-Plex-Product") product: String = "PlexQuest",
    ): Response<PlexPinResponse>

    @GET("resources.json")
    suspend fun getResources(
        @Header("X-Plex-Token") token: String,
        @Header("X-Plex-Client-Identifier") clientId: String,
    ): Response<List<PlexResourceResponse>>
}

data class PlexUserResponse(
    val user: PlexUser?,
)

data class PlexUser(
    val id: Long,
    val username: String,
    val email: String,
    val thumb: String?,
    @SerializedName("authToken") val authToken: String,
)

data class PlexPinResponse(
    val id: Long,
    val code: String,
    @SerializedName("authToken") val authToken: String?,
    val expiresAt: String?,
)

data class PlexResourceResponse(
    val name: String,
    @SerializedName("clientIdentifier") val machineIdentifier: String,
    val provides: String,
    @SerializedName("Connection") val connections: List<PlexConnection>?,
    val accessToken: String?,
)

data class PlexConnection(
    val uri: String,
    val local: Boolean,
    val relay: Boolean,
)
