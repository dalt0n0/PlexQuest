package com.plexquest.app.di;

import com.google.gson.Gson;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;
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
public final class ServerModule_ProvideServerRetrofitFactory implements Factory<Retrofit> {
  private final Provider<OkHttpClient> clientProvider;

  private final Provider<Gson> gsonProvider;

  public ServerModule_ProvideServerRetrofitFactory(Provider<OkHttpClient> clientProvider,
      Provider<Gson> gsonProvider) {
    this.clientProvider = clientProvider;
    this.gsonProvider = gsonProvider;
  }

  @Override
  public Retrofit get() {
    return provideServerRetrofit(clientProvider.get(), gsonProvider.get());
  }

  public static ServerModule_ProvideServerRetrofitFactory create(
      Provider<OkHttpClient> clientProvider, Provider<Gson> gsonProvider) {
    return new ServerModule_ProvideServerRetrofitFactory(clientProvider, gsonProvider);
  }

  public static Retrofit provideServerRetrofit(OkHttpClient client, Gson gson) {
    return Preconditions.checkNotNullFromProvides(ServerModule.INSTANCE.provideServerRetrofit(client, gson));
  }
}
