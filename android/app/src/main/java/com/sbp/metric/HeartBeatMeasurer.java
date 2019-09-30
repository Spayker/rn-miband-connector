package com.sbp.metric;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.UUID;

import javax.annotation.Nonnull;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;
import static com.sbp.common.UUIDs.CHAR_HEART_RATE_CONTROL;
import static com.sbp.common.UUIDs.CHAR_HEART_RATE_MEASURE;
import static com.sbp.common.UUIDs.CHAR_SENSOR;
import static com.sbp.common.UUIDs.NOTIFICATION_DESC;
import static com.sbp.common.UUIDs.SERVICE_HEART_RATE;
import static com.sbp.common.UUIDs.SERVICE1;
import static com.sbp.common.UUIDs.SERVICE2;
import static java.lang.Thread.sleep;

/**
 *  Declares main set of methods which will be used by react UI during data fetching procedure
 * Last one includes only heart beat measurement.
 * @author  Spayker
 * @version 1.0
 * @since   06/01/2019
 */
public class HeartBeatMeasurer extends ReactContextBaseJavaModule {

    private BluetoothGattService heartService;
    private BluetoothGattCharacteristic hrCtrlChar;
    private BluetoothGattCharacteristic hrMeasureChar;
    private BluetoothGattDescriptor hrDescChar;
    private BluetoothGattCharacteristic sensorChar;
    private BluetoothGattService service1;
    private BluetoothGattService service2;

    /**
     * Public API for the Bluetooth GATT Profile.
     * This class provides Bluetooth GATT functionality to enable communication with Bluetooth
     * Smart or Smart Ready devices.
     * To connect to a remote peripheral device, create a BluetoothGattCallback and call
     * BluetoothDevice#connectGatt to get a instance of this class. GATT capable devices can be
     * discovered using the Bluetooth device discovery or BLE scan process.
     */
    private BluetoothGatt btGatt;

    /**
     * keeps current heart beat value taken from miband device
     */
    private String heartRateValue = "0";

    HeartBeatMeasurer(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    public void initHrChars(BluetoothGatt gatt){
        this.btGatt = gatt;
        service1 = btGatt.getService(UUID.fromString(SERVICE1));
        service2 = btGatt.getService(UUID.fromString(SERVICE2));
        heartService = btGatt.getService(UUID.fromString(SERVICE_HEART_RATE));
        hrCtrlChar = heartService.getCharacteristic(UUID.fromString(CHAR_HEART_RATE_CONTROL));
        hrMeasureChar = heartService.getCharacteristic(UUID.fromString(CHAR_HEART_RATE_MEASURE));
        hrDescChar = hrMeasureChar.getDescriptor(UUID.fromString(NOTIFICATION_DESC));
        sensorChar = service1.getCharacteristic(UUID.fromString(CHAR_SENSOR));
        btGatt.setCharacteristicNotification(hrCtrlChar, true);
        btGatt.setCharacteristicNotification(hrMeasureChar, true);
    }

    /**
     * Reads recieved data from miband device with current heart beat state.
     * @param characteristic GATT characteristic is a basic data element used
     *                       to construct a GATT service
     */
    public void handleHeartRateData(final BluetoothGattCharacteristic characteristic) {
        byte currentHrValue = characteristic.getValue()[1];
        heartRateValue = String.valueOf(currentHrValue);
    }

    /**
     * Starts heartBeat data fetching from miband device.
     * @param successCallback - a Callback instance that contains result of native code execution
     */
    @ReactMethod
    private void startHeartRateCalculation(Callback successCallback) {
        sensorChar.setValue(new byte[]{0x01, 0x03, 0x19});
        System.out.println("sensorChar: " + btGatt.writeCharacteristic(sensorChar));

        hrDescChar.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        System.out.println("hrDescChar: " + btGatt.writeDescriptor(hrDescChar));
        makePause();

        hrCtrlChar.setValue(new byte[]{0x15, 0x01, 0x01});
        System.out.println("hrCtrlChar: " + btGatt.writeCharacteristic(hrCtrlChar));

        successCallback.invoke(null, heartRateValue);
    }

    @ReactMethod
    private void stopHeartRateCalculation(Callback successCallback) {
        successCallback.invoke(null, heartRateValue);
    }

    /**
     * Returns current heart beat value.
     * @param successCallback - a Callback instance that contains result of native code execution
     */
    @ReactMethod
    private void getHeartRate(String currentHeartBeat, Callback successCallback) {
        if(Integer.valueOf(heartRateValue).equals(Integer.valueOf(currentHeartBeat))){
            hrCtrlChar.setValue(new byte[]{0x16});
            btGatt.writeCharacteristic(hrCtrlChar);
        }
        successCallback.invoke(null, heartRateValue);
    }

    /**
     * A weird method that must be used during communication process with miband device. Unfortunately
     * last one can not process requests immediately. That is why our application must wait for some
     * time.
     */
    private void makePause(){
        try {
            int CHAR_WRITE_PAUSE_COMMUNICATION_IN_MS = 200;
            sleep(CHAR_WRITE_PAUSE_COMMUNICATION_IN_MS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Re-inits BluetoothGatt instance in case bluetooth connection was interrupted somehow.
     * @param bluetoothGatt instance to be re-initialized
     */
    public void updateBluetoothConfig(BluetoothGatt bluetoothGatt){
        this.btGatt = bluetoothGatt;
    }


    @Nonnull
    @Override
    public String getName() {
        return HeartBeatMeasurer.class.getSimpleName();
    }
}
