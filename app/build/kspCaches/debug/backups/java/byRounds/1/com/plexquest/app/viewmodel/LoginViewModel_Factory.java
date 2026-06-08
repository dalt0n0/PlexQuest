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
public final class LoginViewModel_Factory implements Factory<LoginViewModel> {
  private final Provider<PlexAuthApi> authApiProvider;

  private final Provider<PlexPreferences> preferencesProvider;

  public LoginViewModel_Factory(Provider<PlexAuthApi> authApiProvider,
      Provider<PlexPreferences> preferencesProvider) {
    this.authApiProvider = authApiProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public LoginViewModel get() {
    return newInstance(authApiProvider.get(), preferencesProvider.get());
  }

  public static LoginViewModel_Factory create(Provider<PlexAuthApi> authApiProvider,
      Provider<PlexPreferences> preferencesProvider) {
    return new LoginViewModel_Factory(authApiProvider, preferencesProvider);
  }

  public static LoginViewModel newInstance(PlexAuthApi authApi, PlexPreferences preferences) {
    return new LoginViewModel(authApi, preferences);
  }
}
