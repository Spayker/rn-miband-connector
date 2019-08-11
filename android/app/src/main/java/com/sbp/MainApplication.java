package com.sbp;

import android.app.Application;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.soloader.SoLoader;
import com.sbp.common.ModuleStorage;

import java.util.Arrays;
import java.util.List;

import static com.sbp.common.ModuleStorage.getModuleStorage;

public class MainApplication extends Application implements ReactApplication {

  private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
    @Override
    public boolean getUseDeveloperSupport() {
      return BuildConfig.DEBUG;
    }

    @Override
    protected List<ReactPackage> getPackages() {
      ModuleStorage appModuleStorage = getModuleStorage();
      return Arrays.asList(
              appModuleStorage.getMainReactPackage(),
              appModuleStorage.getDeviceConnectorPackage(),
              appModuleStorage.getHeartBeatMeasurerPackage()
      );
    }

    @Override
    protected String getJSMainModuleName() {
      return "index";
    }
  };

  @Override
  public ReactNativeHost getReactNativeHost() {
    return mReactNativeHost;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    SoLoader.init(this, false);
  }
}
