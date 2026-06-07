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

    // Base URL irrelevant — all calls use @Url full URLs built in PlexRepository
    @Provides
    @Singleton
    @Named("server")
    fun provideServerRetrofit(client: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl("https://plex.tv/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    fun providePlexApi(@Named("server") retrofit: Retrofit): PlexApi =
        retrofit.create(PlexApi::class.java)
}
