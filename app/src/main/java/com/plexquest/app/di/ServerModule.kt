package com.plexquest.app.di

import com.google.gson.Gson
import com.plexquest.app.data.api.PlexApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServerModule {

    // Base Retrofit with a placeholder URL — actual server URL is set per-request
    // via a dynamic OkHttp interceptor or by rebuilding with the active server's URL.
    // For simplicity in v0.1 we use full URLs passed at call-site via @Url.
    @Provides
    @Singleton
    @Named("server")
    fun provideServerRetrofit(client: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:32400/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    fun providePlexApi(@Named("server") retrofit: Retrofit): PlexApi =
        retrofit.create(PlexApi::class.java)
}
