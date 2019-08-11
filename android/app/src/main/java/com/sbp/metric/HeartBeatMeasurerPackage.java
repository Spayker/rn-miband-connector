package com.sbp.metric;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HeartBeatMeasurerPackage implements ReactPackage {

    private HeartBeatMeasurer heartBeatMeasurer;

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

    @Override
    public List<NativeModule> createNativeModules(
            ReactApplicationContext reactContext) {
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
