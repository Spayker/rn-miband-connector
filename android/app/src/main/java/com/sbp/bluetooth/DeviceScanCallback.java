package com.sbp.bluetooth;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.util.Log;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

import java.util.HashMap;
import java.util.Map;

public class DeviceScanCallback extends ScanCallback {

    private Map<String, String> discoveredDevices;

    private static final String UNKNOWN_DEVICE = "Unknown Device";

    DeviceScanCallback(){
        discoveredDevices = new HashMap<>();
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        String deviceName = result.getDevice().getName();
        String deviceMacAddress = result.getDevice().getAddress();

        if(deviceName == null){
            deviceName = UNKNOWN_DEVICE;
        }

        if(discoveredDevices.containsValue(deviceMacAddress)){
            Log.d("TAG", "Device with mac: " + deviceMacAddress + " already discovered");
        } else {
            discoveredDevices.put(deviceName, deviceMacAddress);
        }
    }

    public WritableMap getDiscoveredDevices() {
        WritableMap mappedDevices = new WritableNativeMap();
        for (String deviceName : discoveredDevices.keySet()) {
            mappedDevices.putString("deviceName", deviceName);
            mappedDevices.putString("deviceMac", discoveredDevices.get(deviceName));
        }
        return mappedDevices;
    }

}
