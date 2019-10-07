package com.sbp.info;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

/**
 *  Inits package responsible for general info gathering
 *
 * @author  Spayker
 * @version 1.0
 * @since   10/06/2019
 */
public class InfoPackage implements ReactPackage {

    private InfoReceiver infoReceiver;

    @Nonnull
    @Override
    public List<ViewManager> createViewManagers(@Nonnull ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public List<NativeModule> createNativeModules(@Nonnull ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        modules.add(initInfoMeasurer(reactContext));
        return modules;
    }

    private InfoReceiver initInfoMeasurer(ReactApplicationContext reactContext){
        infoReceiver = new InfoReceiver(reactContext);
        return infoReceiver;
    }

    public InfoReceiver getInfoReceiver() {
        return infoReceiver;
    }


}
