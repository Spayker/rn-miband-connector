package com.sbp.metric;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
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

public class HeartBeatMeasurer extends ReactContextBaseJavaModule {

    private BluetoothGattService variableService;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic heartRateControlPointCharacteristic;

    private String heartRateValue = "0";
    private final Object object = new Object();

    HeartBeatMeasurer(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    public void handleHeartRateData(final BluetoothGattCharacteristic characteristic) {

        Log.e("Heart", String.valueOf(characteristic.getValue()[1]));
        runOnUiThread(() -> {
            BluetoothGattCharacteristic heartRateMeasurementCharacteristic
                    = variableService.getCharacteristic(UUID.fromString(UUIDs.HEART_RATE_MEASUREMENT_CHARACTERISTIC_STRING));

            bluetoothGatt.readCharacteristic(heartRateMeasurementCharacteristic);
            synchronized (object) {
                try {
                    object.wait(250);
                    heartRateControlPointCharacteristic.setValue(new byte[]{0x16});
                    bluetoothGatt.writeCharacteristic(heartRateControlPointCharacteristic);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            heartRateValue = String.valueOf(characteristic.getValue()[1]);
        });
    }

    @ReactMethod
    private void startHeartRateCalculation(Callback successCallback) {
        variableService = bluetoothGatt.getService(UUIDs.HEART_RATE_SERVICE);
        UUID heartRateCharacteristicCode = UUID.fromString(UUIDs.HEART_RATE_MEASUREMENT_CHARACTERISTIC_STRING);

        if(variableService != null){
            BluetoothGattCharacteristic heartRateCharacteristic = variableService.getCharacteristic(heartRateCharacteristicCode);
            BluetoothGattDescriptor heartRateDescriptor = heartRateCharacteristic.getDescriptor(UUIDs.HEART_RATE_MEASURMENT_DESCRIPTOR);

            bluetoothGatt.setCharacteristicNotification(heartRateCharacteristic, true);
            heartRateDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            bluetoothGatt.writeDescriptor(heartRateDescriptor);

            heartRateControlPointCharacteristic = variableService
                    .getCharacteristic(UUIDs.HEART_RATE_CONTROL_POINT_CHARACTERISTIC);
            pause();

            BluetoothGattService variableSensorService = bluetoothGatt.getService(UUIDs.SENSOR_SERVICE);
            BluetoothGattCharacteristic heartCharacteristicSensor
                    = variableSensorService.getCharacteristic(UUIDs.CHARACTER_SENSOR_CHARACTERISTIC);
            pause();

            heartRateControlPointCharacteristic.setValue(new byte[]{0x15, 0x02, 0x00});
            bluetoothGatt.writeCharacteristic(heartRateControlPointCharacteristic);
            pause();


            heartRateControlPointCharacteristic.setValue(new byte[]{0x15, 0x01, 0x00});
            bluetoothGatt.writeCharacteristic(heartRateControlPointCharacteristic);
            pause();

            heartCharacteristicSensor.setValue(new byte[]{0x01, 0x03, 0x19});
            bluetoothGatt.writeCharacteristic(heartRateControlPointCharacteristic);
            pause();

            heartRateControlPointCharacteristic.setValue(new byte[]{0x01, 0x00});
            bluetoothGatt.writeCharacteristic(heartRateControlPointCharacteristic);
            pause();

            heartRateControlPointCharacteristic.setValue(new byte[]{0x15, 0x01, 0x01});
            bluetoothGatt.writeCharacteristic(heartRateControlPointCharacteristic);

            heartCharacteristicSensor.setValue(new byte[]{0x2});
            bluetoothGatt.writeCharacteristic(heartRateControlPointCharacteristic);
            getHeartRate(successCallback);
        }

    }

    @ReactMethod
    private void getHeartRate(Callback successCallback) {
        successCallback.invoke(null, heartRateValue);
    }

    private void pause(){
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void updateBluetoothConfig(BluetoothGatt bluetoothGatt){
        this.bluetoothGatt = bluetoothGatt;
    }


    @Nonnull
    @Override
    public String getName() {
        return "HeartBeatMeasurer";
    }
}
