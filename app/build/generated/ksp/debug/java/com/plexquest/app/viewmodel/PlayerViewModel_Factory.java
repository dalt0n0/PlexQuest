package com.plexquest.app.viewmodel;

import android.content.Context;
import com.plexquest.app.data.repository.PlexRepository;
import com.plexquest.app.data.store.PlexPreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class PlayerViewModel_Factory implements Factory<PlayerViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<PlexRepository> repositoryProvider;

  private final Provider<PlexPreferences> preferencesProvider;

  public PlayerViewModel_Factory(Provider<Context> contextProvider,
      Provider<PlexRepository> repositoryProvider, Provider<PlexPreferences> preferencesProvider) {
    this.contextProvider = contextProvider;
    this.repositoryProvider = repositoryProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public PlayerViewModel get() {
    return newInstance(contextProvider.get(), repositoryProvider.get(), preferencesProvider.get());
  }

  public static PlayerViewModel_Factory create(Provider<Context> contextProvider,
      Provider<PlexRepository> repositoryProvider, Provider<PlexPreferences> preferencesProvider) {
    return new PlayerViewModel_Factory(contextProvider, repositoryProvider, preferencesProvider);
  }

  public static PlayerViewModel newInstance(Context context, PlexRepository repository,
      PlexPreferences preferences) {
    return new PlayerViewModel(context, repository, preferences);
  }
}
