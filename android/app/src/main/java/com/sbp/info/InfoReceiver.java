package com.sbp.info;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import java.util.UUID;

import javax.annotation.Nonnull;

import static com.sbp.common.UUIDs.CHAR_STEPS;
import static com.sbp.common.UUIDs.SERVICE1;

/**
 *  Inits package responsible for main info gathering
 *
 * @author  Spayker
 * @version 1.0
 * @since   10/06/2019
 */
public class InfoReceiver extends ReactContextBaseJavaModule {

    private BluetoothGattCharacteristic stepsChar;

    /**
     * Public API for the Bluetooth GATT Profile.
     * This class provides Bluetooth GATT functionality to enable communication with Bluetooth
     * Smart or Smart Ready devices.
     * To connect to a remote peripheral device, create a BluetoothGattCallback and call
     * BluetoothDevice#connectGatt to get a instance of this class. GATT capable devices can be
     * discovered using the Bluetooth device discovery or BLE scan process.
     */
    private BluetoothGatt btGatt;

    private String steps = "0";
    private String battery = "0";


    InfoReceiver(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    public void updateInfoChars(BluetoothGatt gatt){
        this.btGatt = gatt;
        BluetoothGattService service1 = btGatt.getService(UUID.fromString(SERVICE1));
        stepsChar = service1.getCharacteristic(UUID.fromString(CHAR_STEPS));
        btGatt.readCharacteristic(stepsChar);
    }

    /**
     * Returns main info from device including steps, battery level.
     * @param successCallback - a Callback instance that contains result of native code execution
     */
    @ReactMethod
    private void getInfo(Callback successCallback) {
        if(btGatt != null && stepsChar != null){
            btGatt.readCharacteristic(stepsChar);
        }
        successCallback.invoke(null, steps, battery);
    }

    /**
     * Updates steps variable with current step value on device side
     * @param value - an array with step value
     *
     */
    public void handleInfoData(final byte[] value) {
        if(value != null){
            byte receivedSteps = value[1];
            steps = String.valueOf(receivedSteps);
        }
    }

    /**
     * Updates steps variable with current battery value on device side
     * @param value - an array with battery value
     *
     */
    public void handleBatteryData(final byte[] value) {
        if(value != null){
            byte currentSteps = value[1];
            battery = String.valueOf(currentSteps);
        }
    }

    @Nonnull
    @Override
    public String getName() {
        return InfoReceiver.class.getSimpleName();
    }
}
