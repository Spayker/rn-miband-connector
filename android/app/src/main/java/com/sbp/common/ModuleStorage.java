package com.sbp.common;

import com.facebook.react.shell.MainReactPackage;
import com.sbp.bluetooth.DeviceConnectorPackage;
import com.sbp.info.InfoPackage;
import com.sbp.metric.hr.HeartBeatMeasurerPackage;

/**
 *  An utility class that registers native modules in app
 *
 * @author  Spayker
 * @version 1.0
 * @since   06/01/2019
 */
public class ModuleStorage {

    private static ModuleStorage instance;

    private MainReactPackage mainReactPackage;

    private DeviceConnectorPackage deviceConnectorPackage;

    private HeartBeatMeasurerPackage heartBeatMeasurerPackage;

    private InfoPackage infoPackage;

    private ModuleStorage(){
        mainReactPackage = new MainReactPackage();
        deviceConnectorPackage = new DeviceConnectorPackage();
        heartBeatMeasurerPackage = new HeartBeatMeasurerPackage();
        infoPackage = new InfoPackage();
    }

    public static ModuleStorage getModuleStorage(){
        if(instance == null){
            instance = new ModuleStorage();
        }
        return instance;
    }

    public MainReactPackage getMainReactPackage() {
        return mainReactPackage;
    }

    public DeviceConnectorPackage getDeviceConnectorPackage() {
        return deviceConnectorPackage;
    }

    public HeartBeatMeasurerPackage getHeartBeatMeasurerPackage() {
        return heartBeatMeasurerPackage;
    }

    public InfoPackage getInfoPackage() {
        return infoPackage;
    }
}
