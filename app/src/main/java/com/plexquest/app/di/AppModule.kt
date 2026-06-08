package com.plexquest.app.di

import com.google.gson.Gson
import com.plexquest.app.data.api.PlexAuthApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Deterministic UUID — same across app launches on the same install
    private val CLIENT_ID: String = UUID.nameUUIDFromBytes("plexquest-meta-quest".toByteArray()).toString()

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("Accept", "application/json")
                .header("X-Plex-Product", "PlexQuest")
                .header("X-Plex-Version", "0.1.0")
                .header("X-Plex-Platform", "Android")
                .header("X-Plex-Device", "Meta Quest")
                .header("X-Plex-Device-Name", "PlexQuest")
                .header("X-Plex-Client-Identifier", CLIENT_ID)
                .build()
            chain.proceed(request)
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthRetrofit(client: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl("https://plex.tv/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    fun providePlexAuthApi(@Named("auth") retrofit: Retrofit): PlexAuthApi =
        retrofit.create(PlexAuthApi::class.java)
}
