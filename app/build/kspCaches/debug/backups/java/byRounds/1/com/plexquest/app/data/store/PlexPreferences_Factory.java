package com.plexquest.app.data.store;

import android.content.Context;
import com.google.gson.Gson;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class PlexPreferences_Factory implements Factory<PlexPreferences> {
  private final Provider<Context> contextProvider;

  private final Provider<Gson> gsonProvider;

  public PlexPreferences_Factory(Provider<Context> contextProvider, Provider<Gson> gsonProvider) {
    this.contextProvider = contextProvider;
    this.gsonProvider = gsonProvider;
  }

  @Override
  public PlexPreferences get() {
    return newInstance(contextProvider.get(), gsonProvider.get());
  }

  public static PlexPreferences_Factory create(Provider<Context> contextProvider,
      Provider<Gson> gsonProvider) {
    return new PlexPreferences_Factory(contextProvider, gsonProvider);
  }

  public static PlexPreferences newInstance(Context context, Gson gson) {
    return new PlexPreferences(context, gson);
  }
}
