package com.sbp.metric;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

/**
 *  Inits package responsible for HB measurement
 *
 * @author  Spayker
 * @version 1.0
 * @since   06/01/2019
 */
public class HeartBeatMeasurerPackage implements ReactPackage {

    private HeartBeatMeasurer heartBeatMeasurer;

    @Nonnull
    @Override
    public List<ViewManager> createViewManagers(@Nonnull ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public List<NativeModule> createNativeModules(@Nonnull ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        modules.add(initHeartBeatMeasurer(reactContext));
        return modules;
    }

    private HeartBeatMeasurer initHeartBeatMeasurer(ReactApplicationContext reactContext){
        heartBeatMeasurer = new HeartBeatMeasurer(reactContext);
        return heartBeatMeasurer;
    }

    public HeartBeatMeasurer getHeartBeatMeasurer() {
        return heartBeatMeasurer;
    }


}
