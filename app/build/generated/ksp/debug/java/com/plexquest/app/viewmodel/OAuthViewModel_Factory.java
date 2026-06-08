package com.plexquest.app.viewmodel;

import com.plexquest.app.data.api.PlexAuthApi;
import com.plexquest.app.data.store.PlexPreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class OAuthViewModel_Factory implements Factory<OAuthViewModel> {
  private final Provider<PlexAuthApi> authApiProvider;

  private final Provider<PlexPreferences> preferencesProvider;

  public OAuthViewModel_Factory(Provider<PlexAuthApi> authApiProvider,
      Provider<PlexPreferences> preferencesProvider) {
    this.authApiProvider = authApiProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public OAuthViewModel get() {
    return newInstance(authApiProvider.get(), preferencesProvider.get());
  }

  public static OAuthViewModel_Factory create(Provider<PlexAuthApi> authApiProvider,
      Provider<PlexPreferences> preferencesProvider) {
    return new OAuthViewModel_Factory(authApiProvider, preferencesProvider);
  }

  public static OAuthViewModel newInstance(PlexAuthApi authApi, PlexPreferences preferences) {
    return new OAuthViewModel(authApi, preferences);
  }
}
