package com.plexquest.app;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.plexquest.app.data.api.PlexApi;
import com.plexquest.app.data.api.PlexAuthApi;
import com.plexquest.app.data.repository.PlexRepository;
import com.plexquest.app.data.store.PlexPreferences;
import com.plexquest.app.di.AppModule_ProvideAuthRetrofitFactory;
import com.plexquest.app.di.AppModule_ProvideGsonFactory;
import com.plexquest.app.di.AppModule_ProvideOkHttpClientFactory;
import com.plexquest.app.di.AppModule_ProvidePlexAuthApiFactory;
import com.plexquest.app.di.ServerModule_ProvidePlexApiFactory;
import com.plexquest.app.di.ServerModule_ProvideServerRetrofitFactory;
import com.plexquest.app.viewmodel.DetailViewModel;
import com.plexquest.app.viewmodel.DetailViewModel_HiltModules;
import com.plexquest.app.viewmodel.HomeViewModel;
import com.plexquest.app.viewmodel.HomeViewModel_HiltModules;
import com.plexquest.app.viewmodel.LibraryViewModel;
import com.plexquest.app.viewmodel.LibraryViewModel_HiltModules;
import com.plexquest.app.viewmodel.LoginViewModel;
import com.plexquest.app.viewmodel.LoginViewModel_HiltModules;
import com.plexquest.app.viewmodel.OAuthViewModel;
import com.plexquest.app.viewmodel.OAuthViewModel_HiltModules;
import com.plexquest.app.viewmodel.PlayerViewModel;
import com.plexquest.app.viewmodel.PlayerViewModel_HiltModules;
import com.plexquest.app.viewmodel.SearchViewModel;
import com.plexquest.app.viewmodel.SearchViewModel_HiltModules;
import com.plexquest.app.viewmodel.ServerPickerViewModel;
import com.plexquest.app.viewmodel.ServerPickerViewModel_HiltModules;
import com.plexquest.app.viewmodel.SettingsViewModel;
import com.plexquest.app.viewmodel.SettingsViewModel_HiltModules;
import com.plexquest.app.viewmodel.SplashViewModel;
import com.plexquest.app.viewmodel.SplashViewModel_HiltModules;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

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
public final class DaggerPlexQuestApp_HiltComponents_SingletonC {
  private DaggerPlexQuestApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public PlexQuestApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements PlexQuestApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public PlexQuestApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements PlexQuestApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public PlexQuestApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements PlexQuestApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public PlexQuestApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements PlexQuestApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public PlexQuestApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements PlexQuestApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public PlexQuestApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements PlexQuestApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public PlexQuestApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements PlexQuestApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public PlexQuestApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends PlexQuestApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends PlexQuestApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends PlexQuestApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends PlexQuestApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity arg0) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(ImmutableMap.<String, Boolean>builderWithExpectedSize(10).put(LazyClassKeyProvider.com_plexquest_app_viewmodel_DetailViewModel, DetailViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_plexquest_app_viewmodel_HomeViewModel, HomeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_plexquest_app_viewmodel_LibraryViewModel, LibraryViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_plexquest_app_viewmodel_LoginViewModel, LoginViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_plexquest_app_viewmodel_OAuthViewModel, OAuthViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_plexquest_app_viewmodel_PlayerViewModel, PlayerViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_plexquest_app_viewmodel_SearchViewModel, SearchViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_plexquest_app_viewmodel_ServerPickerViewModel, ServerPickerViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_plexquest_app_viewmodel_SettingsViewModel, SettingsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_plexquest_app_viewmodel_SplashViewModel, SplashViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_plexquest_app_viewmodel_DetailViewModel = "com.plexquest.app.viewmodel.DetailViewModel";

      static String com_plexquest_app_viewmodel_LoginViewModel = "com.plexquest.app.viewmodel.LoginViewModel";

      static String com_plexquest_app_viewmodel_OAuthViewModel = "com.plexquest.app.viewmodel.OAuthViewModel";

      static String com_plexquest_app_viewmodel_PlayerViewModel = "com.plexquest.app.viewmodel.PlayerViewModel";

      static String com_plexquest_app_viewmodel_HomeViewModel = "com.plexquest.app.viewmodel.HomeViewModel";

      static String com_plexquest_app_viewmodel_LibraryViewModel = "com.plexquest.app.viewmodel.LibraryViewModel";

      static String com_plexquest_app_viewmodel_SearchViewModel = "com.plexquest.app.viewmodel.SearchViewModel";

      static String com_plexquest_app_viewmodel_SplashViewModel = "com.plexquest.app.viewmodel.SplashViewModel";

      static String com_plexquest_app_viewmodel_SettingsViewModel = "com.plexquest.app.viewmodel.SettingsViewModel";

      static String com_plexquest_app_viewmodel_ServerPickerViewModel = "com.plexquest.app.viewmodel.ServerPickerViewModel";

      @KeepFieldType
      DetailViewModel com_plexquest_app_viewmodel_DetailViewModel2;

      @KeepFieldType
      LoginViewModel com_plexquest_app_viewmodel_LoginViewModel2;

      @KeepFieldType
      OAuthViewModel com_plexquest_app_viewmodel_OAuthViewModel2;

      @KeepFieldType
      PlayerViewModel com_plexquest_app_viewmodel_PlayerViewModel2;

      @KeepFieldType
      HomeViewModel com_plexquest_app_viewmodel_HomeViewModel2;

      @KeepFieldType
      LibraryViewModel com_plexquest_app_viewmodel_LibraryViewModel2;

      @KeepFieldType
      SearchViewModel com_plexquest_app_viewmodel_SearchViewModel2;

      @KeepFieldType
      SplashViewModel com_plexquest_app_viewmodel_SplashViewModel2;

      @KeepFieldType
      SettingsViewModel com_plexquest_app_viewmodel_SettingsViewModel2;

      @KeepFieldType
      ServerPickerViewModel com_plexquest_app_viewmodel_ServerPickerViewModel2;
    }
  }

  private static final class ViewModelCImpl extends PlexQuestApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<DetailViewModel> detailViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<LibraryViewModel> libraryViewModelProvider;

    private Provider<LoginViewModel> loginViewModelProvider;

    private Provider<OAuthViewModel> oAuthViewModelProvider;

    private Provider<PlayerViewModel> playerViewModelProvider;

    private Provider<SearchViewModel> searchViewModelProvider;

    private Provider<ServerPickerViewModel> serverPickerViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private Provider<SplashViewModel> splashViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.detailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.libraryViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.loginViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.oAuthViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.playerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.searchViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.serverPickerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
      this.splashViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 9);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(ImmutableMap.<String, javax.inject.Provider<ViewModel>>builderWithExpectedSize(10).put(LazyClassKeyProvider.com_plexquest_app_viewmodel_DetailViewModel, ((Provider) detailViewModelProvider)).put(LazyClassKeyProvider.com_plexquest_app_viewmodel_HomeViewModel, ((Provider) homeViewModelProvider)).put(LazyClassKeyProvider.com_plexquest_app_viewmodel_LibraryViewModel, ((Provider) libraryViewModelProvider)).put(LazyClassKeyProvider.com_plexquest_app_viewmodel_LoginViewModel, ((Provider) loginViewModelProvider)).put(LazyClassKeyProvider.com_plexquest_app_viewmodel_OAuthViewModel, ((Provider) oAuthViewModelProvider)).put(LazyClassKeyProvider.com_plexquest_app_viewmodel_PlayerViewModel, ((Provider) playerViewModelProvider)).put(LazyClassKeyProvider.com_plexquest_app_viewmodel_SearchViewModel, ((Provider) searchViewModelProvider)).put(LazyClassKeyProvider.com_plexquest_app_viewmodel_ServerPickerViewModel, ((Provider) serverPickerViewModelProvider)).put(LazyClassKeyProvider.com_plexquest_app_viewmodel_SettingsViewModel, ((Provider) settingsViewModelProvider)).put(LazyClassKeyProvider.com_plexquest_app_viewmodel_SplashViewModel, ((Provider) splashViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_plexquest_app_viewmodel_SettingsViewModel = "com.plexquest.app.viewmodel.SettingsViewModel";

      static String com_plexquest_app_viewmodel_LibraryViewModel = "com.plexquest.app.viewmodel.LibraryViewModel";

      static String com_plexquest_app_viewmodel_OAuthViewModel = "com.plexquest.app.viewmodel.OAuthViewModel";

      static String com_plexquest_app_viewmodel_DetailViewModel = "com.plexquest.app.viewmodel.DetailViewModel";

      static String com_plexquest_app_viewmodel_LoginViewModel = "com.plexquest.app.viewmodel.LoginViewModel";

      static String com_plexquest_app_viewmodel_ServerPickerViewModel = "com.plexquest.app.viewmodel.ServerPickerViewModel";

      static String com_plexquest_app_viewmodel_PlayerViewModel = "com.plexquest.app.viewmodel.PlayerViewModel";

      static String com_plexquest_app_viewmodel_SplashViewModel = "com.plexquest.app.viewmodel.SplashViewModel";

      static String com_plexquest_app_viewmodel_HomeViewModel = "com.plexquest.app.viewmodel.HomeViewModel";

      static String com_plexquest_app_viewmodel_SearchViewModel = "com.plexquest.app.viewmodel.SearchViewModel";

      @KeepFieldType
      SettingsViewModel com_plexquest_app_viewmodel_SettingsViewModel2;

      @KeepFieldType
      LibraryViewModel com_plexquest_app_viewmodel_LibraryViewModel2;

      @KeepFieldType
      OAuthViewModel com_plexquest_app_viewmodel_OAuthViewModel2;

      @KeepFieldType
      DetailViewModel com_plexquest_app_viewmodel_DetailViewModel2;

      @KeepFieldType
      LoginViewModel com_plexquest_app_viewmodel_LoginViewModel2;

      @KeepFieldType
      ServerPickerViewModel com_plexquest_app_viewmodel_ServerPickerViewModel2;

      @KeepFieldType
      PlayerViewModel com_plexquest_app_viewmodel_PlayerViewModel2;

      @KeepFieldType
      SplashViewModel com_plexquest_app_viewmodel_SplashViewModel2;

      @KeepFieldType
      HomeViewModel com_plexquest_app_viewmodel_HomeViewModel2;

      @KeepFieldType
      SearchViewModel com_plexquest_app_viewmodel_SearchViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.plexquest.app.viewmodel.DetailViewModel 
          return (T) new DetailViewModel(singletonCImpl.plexRepositoryProvider.get(), singletonCImpl.plexPreferencesProvider.get());

          case 1: // com.plexquest.app.viewmodel.HomeViewModel 
          return (T) new HomeViewModel(singletonCImpl.plexRepositoryProvider.get(), singletonCImpl.plexPreferencesProvider.get());

          case 2: // com.plexquest.app.viewmodel.LibraryViewModel 
          return (T) new LibraryViewModel(singletonCImpl.plexRepositoryProvider.get(), singletonCImpl.plexPreferencesProvider.get());

          case 3: // com.plexquest.app.viewmodel.LoginViewModel 
          return (T) new LoginViewModel(singletonCImpl.providePlexAuthApiProvider.get(), singletonCImpl.plexPreferencesProvider.get());

          case 4: // com.plexquest.app.viewmodel.OAuthViewModel 
          return (T) new OAuthViewModel(singletonCImpl.providePlexAuthApiProvider.get(), singletonCImpl.plexPreferencesProvider.get());

          case 5: // com.plexquest.app.viewmodel.PlayerViewModel 
          return (T) new PlayerViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.plexRepositoryProvider.get(), singletonCImpl.plexPreferencesProvider.get());

          case 6: // com.plexquest.app.viewmodel.SearchViewModel 
          return (T) new SearchViewModel(singletonCImpl.plexRepositoryProvider.get(), singletonCImpl.plexPreferencesProvider.get());

          case 7: // com.plexquest.app.viewmodel.ServerPickerViewModel 
          return (T) new ServerPickerViewModel(singletonCImpl.providePlexAuthApiProvider.get(), singletonCImpl.plexPreferencesProvider.get());

          case 8: // com.plexquest.app.viewmodel.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.plexPreferencesProvider.get());

          case 9: // com.plexquest.app.viewmodel.SplashViewModel 
          return (T) new SplashViewModel(singletonCImpl.plexPreferencesProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends PlexQuestApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends PlexQuestApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends PlexQuestApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<OkHttpClient> provideOkHttpClientProvider;

    private Provider<Gson> provideGsonProvider;

    private Provider<Retrofit> provideServerRetrofitProvider;

    private Provider<PlexApi> providePlexApiProvider;

    private Provider<PlexRepository> plexRepositoryProvider;

    private Provider<PlexPreferences> plexPreferencesProvider;

    private Provider<Retrofit> provideAuthRetrofitProvider;

    private Provider<PlexAuthApi> providePlexAuthApiProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 3));
      this.provideGsonProvider = DoubleCheck.provider(new SwitchingProvider<Gson>(singletonCImpl, 4));
      this.provideServerRetrofitProvider = DoubleCheck.provider(new SwitchingProvider<Retrofit>(singletonCImpl, 2));
      this.providePlexApiProvider = DoubleCheck.provider(new SwitchingProvider<PlexApi>(singletonCImpl, 1));
      this.plexRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<PlexRepository>(singletonCImpl, 0));
      this.plexPreferencesProvider = DoubleCheck.provider(new SwitchingProvider<PlexPreferences>(singletonCImpl, 5));
      this.provideAuthRetrofitProvider = DoubleCheck.provider(new SwitchingProvider<Retrofit>(singletonCImpl, 7));
      this.providePlexAuthApiProvider = DoubleCheck.provider(new SwitchingProvider<PlexAuthApi>(singletonCImpl, 6));
    }

    @Override
    public void injectPlexQuestApp(PlexQuestApp plexQuestApp) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.plexquest.app.data.repository.PlexRepository 
          return (T) new PlexRepository(singletonCImpl.providePlexApiProvider.get());

          case 1: // com.plexquest.app.data.api.PlexApi 
          return (T) ServerModule_ProvidePlexApiFactory.providePlexApi(singletonCImpl.provideServerRetrofitProvider.get());

          case 2: // @javax.inject.Named("server") retrofit2.Retrofit 
          return (T) ServerModule_ProvideServerRetrofitFactory.provideServerRetrofit(singletonCImpl.provideOkHttpClientProvider.get(), singletonCImpl.provideGsonProvider.get());

          case 3: // okhttp3.OkHttpClient 
          return (T) AppModule_ProvideOkHttpClientFactory.provideOkHttpClient();

          case 4: // com.google.gson.Gson 
          return (T) AppModule_ProvideGsonFactory.provideGson();

          case 5: // com.plexquest.app.data.store.PlexPreferences 
          return (T) new PlexPreferences(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideGsonProvider.get());

          case 6: // com.plexquest.app.data.api.PlexAuthApi 
          return (T) AppModule_ProvidePlexAuthApiFactory.providePlexAuthApi(singletonCImpl.provideAuthRetrofitProvider.get());

          case 7: // @javax.inject.Named("auth") retrofit2.Retrofit 
          return (T) AppModule_ProvideAuthRetrofitFactory.provideAuthRetrofit(singletonCImpl.provideOkHttpClientProvider.get(), singletonCImpl.provideGsonProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
