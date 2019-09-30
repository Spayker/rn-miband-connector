package com.sbp.bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.sbp.R;
import com.sbp.common.AppBluetoothGattCallback;
import com.sbp.metric.HeartBeatMeasurer;
import com.sbp.metric.HeartBeatMeasurerPackage;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.BLUETOOTH_SERVICE;
import static com.sbp.common.ModuleStorage.getModuleStorage;

/**
 *  Declares main set of methods which will be used by react UI during data fetching procedure.
 *  Last one includes only device connection. Make sure your miband device has
 *  "Allow 3-rd party connect" option ON
 * @author  Spayker
 * @version 1.0
 * @since   06/01/2019
 */
public class DeviceConnector  extends ReactContextBaseJavaModule {

    // Bluetooth variable section
    private BluetoothGatt bluetoothGatt;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private AppBluetoothGattCallback appBluetoothGattCallback;
    private ArrayList<BluetoothDevice> deviceArrayList = new ArrayList<>();
    private ProgressDialog searchProgressDialog;

    // Android settings section
    private SharedPreferences sharedPreferences;

    private Context applicationContext;

    DeviceConnector(ReactApplicationContext reactContext) {
        super(reactContext);

        applicationContext = getReactApplicationContext().getApplicationContext();
        HeartBeatMeasurerPackage hBMeasurerPackage = getModuleStorage().getHeartBeatMeasurerPackage();

        String sharedPreferencesAppName =
                applicationContext.getString(R.string.app_mi_band_connect_preferences);
        sharedPreferences = applicationContext
                .getSharedPreferences(sharedPreferencesAppName, Context.MODE_PRIVATE);
        HeartBeatMeasurer heartBeatMeasurer = hBMeasurerPackage.getHeartBeatMeasurer();
        appBluetoothGattCallback = new AppBluetoothGattCallback(sharedPreferences, heartBeatMeasurer);
    }

    /**
     * Enables Bluetooth module on smart phone and starts device discovering process.
     * @param successCallback - a Callback instance that will be needed in the end of discovering
     *                        process to send back a result of work.
     */
    @ReactMethod
    public void enableBTAndDiscover(Callback successCallback) {
        Context mainContext = getReactApplicationContext().getCurrentActivity();
        bluetoothAdapter = ((BluetoothManager) mainContext.
                getSystemService(BLUETOOTH_SERVICE)).getAdapter();

        searchProgressDialog = new ProgressDialog(mainContext);
        searchProgressDialog.setIndeterminate(true);
        searchProgressDialog.setTitle("MiBand Bluetooth Scanner");
        searchProgressDialog.setMessage("Searching...");
        searchProgressDialog.setCancelable(false);
        searchProgressDialog.show();

        if (!bluetoothAdapter.isEnabled()) {
            ((AppCompatActivity)mainContext).
                    startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                            1);
        }

        final ScanCallback leDeviceScanCallback = new DeviceScanCallback(this, successCallback);
        String sharedPreferencesDeviceMacAddress = Objects.requireNonNull(getCurrentActivity())
                .getString(R.string.app_mi_band_last_connected_device_mac_address_key);
        String lastMiBandConnectedDeviceMac =
                sharedPreferences.getString(sharedPreferencesDeviceMacAddress, null);

        if (lastMiBandConnectedDeviceMac != null) {
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(lastMiBandConnectedDeviceMac);
            bluetoothGatt = bluetoothDevice.connectGatt(mainContext, true, appBluetoothGattCallback);
            getDeviceBondLevel(successCallback);
            getModuleStorage().getHeartBeatMeasurerPackage().getHeartBeatMeasurer()
                    .updateBluetoothConfig(bluetoothGatt);
            searchProgressDialog.dismiss();
        } else {
            BluetoothLeScanner bluetoothScanner = bluetoothAdapter.getBluetoothLeScanner();
            if(bluetoothScanner != null){
                bluetoothScanner.startScan(leDeviceScanCallback);
            }
        }

        final int DISCOVERY_TIME_DELAY_IN_MS = 120000;
        new Handler().postDelayed(() -> {
            bluetoothAdapter.getBluetoothLeScanner().stopScan(leDeviceScanCallback);
            searchProgressDialog.dismiss();
        }, DISCOVERY_TIME_DELAY_IN_MS);
    }

    /**
     * Tries to connect a found miband device with tha app. In case of succeed a bound level value
     * will be send back to be displayed on UI.
     * @param successCallback - a Callback instance that will be needed in the end of discovering
     *                        process to send back a result of work.
     */
    void connectDevice(Callback successCallback) {
        Context mainContext = getReactApplicationContext().getCurrentActivity();
        bluetoothGatt = bluetoothDevice.connectGatt(mainContext, true, appBluetoothGattCallback);
        getModuleStorage().getHeartBeatMeasurerPackage()
                .getHeartBeatMeasurer()
                .updateBluetoothConfig(bluetoothGatt);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Objects.requireNonNull(getCurrentActivity())
                .getString(R.string.app_mi_band_last_connected_device_mac_address_key), bluetoothDevice.getAddress());
        editor.apply();

        getDeviceBondLevel(successCallback);
    }

    @ReactMethod
    void disconnectDevice(Callback successCallback) {
        bluetoothGatt.disconnect();
        bluetoothGatt = null;
        bluetoothDevice = null;
        bluetoothAdapter = null;
        getDeviceBondLevel(successCallback);
    }

    /**
     * Returns a bluetooth bound level of connection between miband device and android app.
     * Used by react UI part when connection has been established.
     * @param successCallback - a Callback instance that will be needed in the end of discovering
     *                        process to send back a result of work.
     */
    @ReactMethod
    private void getDeviceBondLevel(Callback successCallback){
        if(bluetoothGatt != null){
            successCallback.invoke(null, bluetoothGatt.getDevice().getBondState());
        }
    }

    @Override
    public String getName() {
        return DeviceConnector.class.getSimpleName();
    }

    Context getApplicationContext() {
        return applicationContext;
    }

    void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    ProgressDialog getSearchProgressDialog() {
        return searchProgressDialog;
    }

    ArrayList<BluetoothDevice> getDeviceArrayList() {
        return deviceArrayList;
    }

}
