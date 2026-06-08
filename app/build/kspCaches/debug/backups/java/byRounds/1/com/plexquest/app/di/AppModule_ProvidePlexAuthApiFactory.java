package com.plexquest.app.di;

import com.plexquest.app.data.api.PlexAuthApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("javax.inject.Named")
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
public final class AppModule_ProvidePlexAuthApiFactory implements Factory<PlexAuthApi> {
  private final Provider<Retrofit> retrofitProvider;

  public AppModule_ProvidePlexAuthApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public PlexAuthApi get() {
    return providePlexAuthApi(retrofitProvider.get());
  }

  public static AppModule_ProvidePlexAuthApiFactory create(Provider<Retrofit> retrofitProvider) {
    return new AppModule_ProvidePlexAuthApiFactory(retrofitProvider);
  }

  public static PlexAuthApi providePlexAuthApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.providePlexAuthApi(retrofit));
  }
}
