package com.sbp.metric;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.UUID;

import javax.annotation.Nonnull;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;
import static java.lang.Thread.sleep;

/**
 *  Declares main set of methods which will be used by react UI during data fetching procedure
 * Last one includes only heart beat measurement.
 * @author  Spayker
 * @version 1.0
 * @since   06/01/2019
 */
public class HeartBeatMeasurer extends ReactContextBaseJavaModule {

    private boolean shallStopHRCalculation;

    /**
     * Gatt Service contains a collection of BluetoothGattCharacteristic,
     * as well as referenced services.
     */
    private BluetoothGattService heartRateService;

    /**
     * Public API for the Bluetooth GATT Profile.
     * This class provides Bluetooth GATT functionality to enable communication with Bluetooth
     * Smart or Smart Ready devices.
     * To connect to a remote peripheral device, create a BluetoothGattCallback and call
     * BluetoothDevice#connectGatt to get a instance of this class. GATT capable devices can be
     * discovered using the Bluetooth device discovery or BLE scan process.
     */
    private BluetoothGatt bluetoothGatt;

    /**
     * A GATT characteristic is a basic data element used to construct a GATT service,
     * BluetoothGattService. The characteristic contains a value as well as additional
     * information and optional GATT descriptors, BluetoothGattDescriptor.
     */
    private BluetoothGattCharacteristic heartRateControlPointCharacteristic;

    /**
     * keeps current heart beat value taken from miband device
     */
    private String heartRateValue = "0";

    /**
     * used to get
     */
    private final Object object = new Object();
    private final int DEVICE_PAUSE_COMMUNICATION_IN_MS = 500;

    HeartBeatMeasurer(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    /**
     * Reads recieved data from miband device with current heart beat state.
     * @param characteristic GATT characteristic is a basic data element used
     *                       to construct a GATT service
     */
    public void handleHeartRateData(final BluetoothGattCharacteristic characteristic) {

        Log.i("Heart", String.valueOf(characteristic.getValue()[1]));
        runOnUiThread(() -> {
            if(shallStopHRCalculation){
                heartRateValue = "0";
            } else {
                BluetoothGattCharacteristic heartRateMeasurementCharacteristic
                        = heartRateService.getCharacteristic(
                        UUID.fromString(UUIDs.HEART_RATE_MEASUREMENT_CHARACTERISTIC_STRING));

                bluetoothGatt.readCharacteristic(heartRateMeasurementCharacteristic);
                synchronized (object) {
                    try {
                        object.wait(DEVICE_PAUSE_COMMUNICATION_IN_MS);
                        heartRateControlPointCharacteristic.setValue(new byte[]{0x16});
                        bluetoothGatt.writeCharacteristic(heartRateControlPointCharacteristic);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                heartRateValue = String.valueOf(characteristic.getValue()[1]);
            }
        });
    }

    /**
     * Starts heartBeat data fetching from miband device.
     * @param successCallback - a Callback instance that contains result of native code execution
     */
    @ReactMethod
    private void startHeartRateCalculation(Callback successCallback) {
        shallStopHRCalculation = false;
        heartRateService = bluetoothGatt.getService(UUIDs.HEART_RATE_SERVICE);
        UUID heartRateCharacteristicCode = UUID.fromString(UUIDs.HEART_RATE_MEASUREMENT_CHARACTERISTIC_STRING);

        if(heartRateService != null){
            BluetoothGattCharacteristic heartRateCharacteristic = heartRateService.getCharacteristic(heartRateCharacteristicCode);
            heartRateControlPointCharacteristic = heartRateService.getCharacteristic(UUIDs.HEART_RATE_CONTROL_POINT_CHARACTERISTIC);

            bluetoothGatt.setCharacteristicNotification(heartRateCharacteristic, true);

            heartRateControlPointCharacteristic.setValue(new byte[]{0x15, 0x02, 0x00});
            bluetoothGatt.writeCharacteristic(heartRateControlPointCharacteristic);

            heartRateControlPointCharacteristic.setValue(new byte[]{0x15, 0x01, 0x00});
            bluetoothGatt.writeCharacteristic(heartRateControlPointCharacteristic);
            makePause();

            heartRateControlPointCharacteristic.setValue(new byte[]{0x01, 0x00});
            bluetoothGatt.writeCharacteristic(heartRateControlPointCharacteristic);
            makePause();

            heartRateControlPointCharacteristic.setValue(new byte[]{0x15, 0x01, 0x01});
            bluetoothGatt.writeCharacteristic(heartRateControlPointCharacteristic);
            getHeartRate(successCallback);
        }
    }

    @ReactMethod
    private void stopHeartRateCalculation(Callback successCallback) {
        shallStopHRCalculation = true;
        getHeartRate(successCallback);
    }

    /**
     * Returns current heart beat value.
     * @param successCallback - a Callback instance that contains result of native code execution
     */
    @ReactMethod
    private void getHeartRate(Callback successCallback) {
        successCallback.invoke(null, heartRateValue);
    }

    /**
     * A weird method that must be used during communication process with miband device. Unfortunately
     * last one can not process requests immediately. That is why our application must wait for some
     * time.
     */
    private void makePause(){
        try {
            sleep(DEVICE_PAUSE_COMMUNICATION_IN_MS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Re-inits BluetoothGatt instance in case bluetooth connection was interrupted somehow.
     * @param bluetoothGatt instance to be re-initialized
     */
    public void updateBluetoothConfig(BluetoothGatt bluetoothGatt){
        this.bluetoothGatt = bluetoothGatt;
    }


    @Nonnull
    @Override
    public String getName() {
        return HeartBeatMeasurer.class.getSimpleName();
    }
}
