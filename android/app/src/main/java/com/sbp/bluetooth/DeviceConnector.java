package com.sbp.bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.sbp.metric.HeartBeatMeasurer;

import java.util.ArrayList;

import static android.content.Context.BLUETOOTH_SERVICE;
import static com.sbp.common.ModuleStorage.getModuleStorage;

public class DeviceConnector  extends ReactContextBaseJavaModule {

    // Bluetooth section
    private BluetoothGatt bluetoothGatt;
    private AppBluetoothGattCallback miBandGattCallBack;
    private static final String MI_BAND_3_NAME = "Mi Band 3";

    // Miscellaneous
    private ArrayList<BluetoothDevice> deviceArrayList;

    // Android settings section
    private SharedPreferences sharedPreferences;
    private String sharedPreferencesDeviceMacAddress = "lastMiBandConnectedDeviceMacAddress";

    private BluetoothDevice miBand;

    DeviceConnector(ReactApplicationContext reactContext) {
        super(reactContext);

        Context currentActivity = getReactApplicationContext().getApplicationContext();
        String sharedPreferencesAppName = "MiBandConnectPreferences";
        sharedPreferences = currentActivity
                .getSharedPreferences(sharedPreferencesAppName, Context.MODE_PRIVATE);
        HeartBeatMeasurer heartBeatMeasurer = getModuleStorage().getHeartBeatMeasurerPackage().getHeartBeatMeasurer();
        miBandGattCallBack = new AppBluetoothGattCallback(sharedPreferences, heartBeatMeasurer);
    }

    @ReactMethod
    public void enableBTAndDiscover(Callback successCallback) {
        Context mainContext = getReactApplicationContext().getCurrentActivity();
        final BluetoothAdapter bluetoothAdapter = ((BluetoothManager) mainContext.getSystemService(BLUETOOTH_SERVICE)).getAdapter();

        final ProgressDialog searchProgress = new ProgressDialog(mainContext);
        searchProgress.setIndeterminate(true);
        searchProgress.setTitle("MiBand Bluetooth Scanner");
        searchProgress.setMessage("Searching...");
        searchProgress.setCancelable(false);
        searchProgress.show();

        deviceArrayList = new ArrayList<>();

        if (!bluetoothAdapter.isEnabled()) {
            ((AppCompatActivity)mainContext).startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1);
        }

        final ScanCallback leDeviceScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                String deviceName = result.getDevice().getName();
                if (deviceName != null && deviceName.equalsIgnoreCase(MI_BAND_3_NAME)){
                    Log.d("TAG", "Device found" + " " + result.getDevice().getAddress() + " " + deviceName);
                    if (!deviceArrayList.contains(result.getDevice())) {
                        deviceArrayList.add(result.getDevice());
                        miBand = result.getDevice();
                        bluetoothAdapter.getBluetoothLeScanner().stopScan(this);
                        searchProgress.dismiss();
                        connectDevice(successCallback);
                    }
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        };

        String lastMiBandConnectedDeviceMac = sharedPreferences.getString(sharedPreferencesDeviceMacAddress, null);
        if (lastMiBandConnectedDeviceMac != null) {
            miBand = bluetoothAdapter.getRemoteDevice(lastMiBandConnectedDeviceMac);
            bluetoothGatt = miBand.connectGatt(mainContext, true, miBandGattCallBack);
            getDeviceBondLevel(successCallback);
            getModuleStorage().getHeartBeatMeasurerPackage().getHeartBeatMeasurer().updateBluetoothConfig(bluetoothGatt);
            miBandGattCallBack.updateBluetoothGatt(bluetoothGatt);
            searchProgress.dismiss();
        } else {
            BluetoothLeScanner bluetoothScanner = bluetoothAdapter.getBluetoothLeScanner();
            if(bluetoothScanner != null){
                bluetoothScanner.startScan(leDeviceScanCallback);
            }
        }
        new Handler().postDelayed(() -> {
            bluetoothAdapter.getBluetoothLeScanner().stopScan(leDeviceScanCallback);
            searchProgress.dismiss();
        }, 120000);
    }

    @ReactMethod
    private void connectDevice(Callback successCallback) {
        if (miBand.getBondState() == BluetoothDevice.BOND_NONE) {
            miBand.createBond();
            Log.d("Bond", "Created with Device");
        }
        Context mainContext = getReactApplicationContext().getCurrentActivity();
        bluetoothGatt = miBand.connectGatt(mainContext, true, miBandGattCallBack);
        getModuleStorage().getHeartBeatMeasurerPackage().getHeartBeatMeasurer().updateBluetoothConfig(bluetoothGatt);
        miBandGattCallBack.updateBluetoothGatt(bluetoothGatt);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(sharedPreferencesDeviceMacAddress, miBand.getAddress());
        editor.apply();

        getDeviceBondLevel(successCallback);
    }

    @ReactMethod
    private void getDeviceBondLevel(Callback successCallback){
        if(bluetoothGatt != null){
            successCallback.invoke(null, bluetoothGatt.getDevice().getBondState());
        }
    }

    @Override
    public String getName() {
        return "DeviceConnector";
    }

}
