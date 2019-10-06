package com.sbp.bluetooth;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.sbp.R;

import java.util.Objects;

public class DeviceScanCallback extends ScanCallback {

    private DeviceConnector deviceConnector;
    private Callback successCallback;

    DeviceScanCallback(DeviceConnector deviceConnector, Callback successCallback){
        this.deviceConnector = deviceConnector;
        this.successCallback = successCallback;
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        String deviceName = result.getDevice().getName();
        if (deviceName != null && deviceName.equalsIgnoreCase(
                Objects.requireNonNull(
                        deviceConnector.getApplicationContext())
                        .getString(R.string.mi_band_device_name))){
            Log.d("TAG", "Device found " + result.getDevice().getAddress() +
                    " " + deviceName);
            deviceConnector.setBluetoothDevice(result.getDevice());
            deviceConnector.getBluetoothAdapter()
                    .getBluetoothLeScanner()
                    .stopScan(this);
            deviceConnector.getSearchProgressDialog().dismiss();
            deviceConnector.connectDevice(successCallback);
        }
    }

}
