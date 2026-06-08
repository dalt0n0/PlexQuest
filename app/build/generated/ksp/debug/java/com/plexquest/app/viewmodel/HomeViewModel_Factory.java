package com.plexquest.app.viewmodel;

import com.plexquest.app.data.repository.PlexRepository;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<PlexRepository> repositoryProvider;

  private final Provider<PlexPreferences> preferencesProvider;

  public HomeViewModel_Factory(Provider<PlexRepository> repositoryProvider,
      Provider<PlexPreferences> preferencesProvider) {
    this.repositoryProvider = repositoryProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(repositoryProvider.get(), preferencesProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<PlexRepository> repositoryProvider,
      Provider<PlexPreferences> preferencesProvider) {
    return new HomeViewModel_Factory(repositoryProvider, preferencesProvider);
  }

  public static HomeViewModel newInstance(PlexRepository repository, PlexPreferences preferences) {
    return new HomeViewModel(repository, preferences);
  }
}
