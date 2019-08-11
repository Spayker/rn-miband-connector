package com.sbp.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.SharedPreferences;
import android.util.Log;

import com.sbp.metric.HeartBeatMeasurer;
import com.sbp.metric.UUIDs;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import static com.sbp.common.ModuleStorage.getModuleStorage;
import static com.sbp.metric.UUIDs.CUSTOM_SERVICE_AUTH_CHARACTERISTIC_STRING;
import static com.sbp.metric.UUIDs.HEART_RATE_MEASUREMENT_CHARACTERISTIC_STRING;

public class AppBluetoothGattCallback extends BluetoothGattCallback {

    private BluetoothGatt bluetoothGatt;
    private SharedPreferences sharedPreferences;
    private HeartBeatMeasurer heartBeatMeasurer;

    AppBluetoothGattCallback(SharedPreferences sharedPreferences,
                             HeartBeatMeasurer heartBeatMeasurer){
        this.sharedPreferences = sharedPreferences;
        this.heartBeatMeasurer = heartBeatMeasurer;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        switch (newState) {
            case BluetoothGatt.STATE_DISCONNECTED:
                Log.d("Info", "Device disconnected");

                break;
            case BluetoothGatt.STATE_CONNECTED: {
                Log.d("Info", "Connected with device");
                Log.d("Info", "Discovering services");
                gatt.discoverServices();
            }
            break;
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (!sharedPreferences.getBoolean("isAuthenticated", false)) {
            authoriseMiBand();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isAuthenticated", true);
            editor.apply();
        } else {
            Log.i("Device", "Already authenticated");
        }
    }

    /*------Methods to send requests to the device------*/
    private void authoriseMiBand() {
        BluetoothGattService service = bluetoothGatt.getService(UUIDs.CUSTOM_SERVICE_FEE1);

        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUIDs.CUSTOM_SERVICE_AUTH_CHARACTERISTIC);
        bluetoothGatt.setCharacteristicNotification(characteristic, true);
        for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
            if (descriptor.getUuid().equals(UUIDs.CUSTOM_SERVICE_AUTH_DESCRIPTOR)) {
                Log.d("INFO", "Found NOTIFICATION BluetoothGattDescriptor: " + descriptor.getUuid().toString());
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            }
        }

        characteristic.setValue(new byte[]{0x01, 0x8, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x40, 0x41, 0x42, 0x43, 0x44, 0x45});
        bluetoothGatt.writeCharacteristic(characteristic);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        heartBeatMeasurer = getModuleStorage().getHeartBeatMeasurerPackage().getHeartBeatMeasurer();
        switch (characteristic.getUuid().toString()) {
            case CUSTOM_SERVICE_AUTH_CHARACTERISTIC_STRING:
                executeAuthorisationSequence(characteristic);
                break;
            case HEART_RATE_MEASUREMENT_CHARACTERISTIC_STRING:
                heartBeatMeasurer.handleHeartRateData(characteristic);
                break;
        }
    }

    private void executeAuthorisationSequence(BluetoothGattCharacteristic characteristic) {
        byte[] value = characteristic.getValue();
        if (value[0] == 0x10 && value[1] == 0x01 && value[2] == 0x01) {
            characteristic.setValue(new byte[]{0x02, 0x8});
            bluetoothGatt.writeCharacteristic(characteristic);
        } else if (value[0] == 0x10 && value[1] == 0x02 && value[2] == 0x01) {
            try {
                byte[] tmpValue = Arrays.copyOfRange(value, 3, 19);
                Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");

                SecretKeySpec key = new SecretKeySpec(new byte[]{0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x40, 0x41, 0x42, 0x43, 0x44, 0x45}, "AES");

                cipher.init(Cipher.ENCRYPT_MODE, key);
                byte[] bytes = cipher.doFinal(tmpValue);


                byte[] rq = ArrayUtils.addAll(new byte[]{0x03, 0x8}, bytes);
                characteristic.setValue(rq);
                bluetoothGatt.writeCharacteristic(characteristic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        Log.d("Descriptor", descriptor.getUuid().toString() + " Read");
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        Log.d("Descriptor", descriptor.getUuid().toString() + " Written");
    }


    void updateBluetoothGatt(BluetoothGatt bluetoothGatt){
        this.bluetoothGatt = bluetoothGatt;
    }

}
