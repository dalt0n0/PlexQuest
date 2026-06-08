package com.plexquest.app.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// Talks to plex.tv for auth + server discovery
interface PlexAuthApi {

    @POST("api/v2/users/signin")
    suspend fun signIn(@Body body: SignInBody): Response<PlexUserResponse>

    @GET("api/v2/pins/{pinId}")
    suspend fun checkPin(@Path("pinId") pinId: Long): Response<PlexPinResponse>

    @POST("api/v2/pins")
    suspend fun createPin(@Query("strong") strong: Boolean = true): Response<PlexPinResponse>

    @GET("api/v2/resources")
    suspend fun getResources(
        @Header("X-Plex-Token") token: String,
    ): Response<List<PlexResourceResponse>>
}

data class SignInBody(
    val login: String,
    val password: String,
    val rememberMe: Boolean = false,
)

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
