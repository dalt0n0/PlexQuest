package com.plexquest.app.di;

import com.plexquest.app.data.api.PlexApi;
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
public final class ServerModule_ProvidePlexApiFactory implements Factory<PlexApi> {
  private final Provider<Retrofit> retrofitProvider;

  public ServerModule_ProvidePlexApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public PlexApi get() {
    return providePlexApi(retrofitProvider.get());
  }

  public static ServerModule_ProvidePlexApiFactory create(Provider<Retrofit> retrofitProvider) {
    return new ServerModule_ProvidePlexApiFactory(retrofitProvider);
  }

  public static PlexApi providePlexApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(ServerModule.INSTANCE.providePlexApi(retrofit));
  }
}
