package com.plexquest.app.data.repository;

import com.plexquest.app.data.api.PlexApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
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
public final class PlexRepository_Factory implements Factory<PlexRepository> {
  private final Provider<PlexApi> plexApiProvider;

  public PlexRepository_Factory(Provider<PlexApi> plexApiProvider) {
    this.plexApiProvider = plexApiProvider;
  }

  @Override
  public PlexRepository get() {
    return newInstance(plexApiProvider.get());
  }

  public static PlexRepository_Factory create(Provider<PlexApi> plexApiProvider) {
    return new PlexRepository_Factory(plexApiProvider);
  }

  public static PlexRepository newInstance(PlexApi plexApi) {
    return new PlexRepository(plexApi);
  }
}
