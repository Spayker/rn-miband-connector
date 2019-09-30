package com.sbp.common;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.SharedPreferences;
import android.util.Log;

import com.sbp.metric.HeartBeatMeasurer;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import static com.sbp.common.ModuleStorage.getModuleStorage;
import static com.sbp.common.UUIDs.CHAR_ACTIVITY_DATA;
import static com.sbp.common.UUIDs.CHAR_AUTH;
import static com.sbp.common.UUIDs.CHAR_FETCH;
import static com.sbp.common.UUIDs.CHAR_HEART_RATE_MEASURE;
import static com.sbp.common.UUIDs.NOTIFICATION_DESC;
import static com.sbp.common.UUIDs.SERVICE1;
import static com.sbp.common.UUIDs.SERVICE2;

/**
 * Declares logic for connection establishment between android app and miband by Bluetooth protocol
 *
 * @author  Spayker
 * @version 1.0
 * @since   06/01/2019
 */
public class AppBluetoothGattCallback extends BluetoothGattCallback {

    private SharedPreferences sharedPreferences;
    private HeartBeatMeasurer heartBeatMeasurer;

    private BluetoothGattService service1;
    private BluetoothGattService service2;

    private BluetoothGattCharacteristic authChar;
    private BluetoothGattDescriptor authDesc;

    private BluetoothGattCharacteristic fetchChar;
    private BluetoothGattDescriptor fetchDesc;

    private BluetoothGattCharacteristic activityChar;

    private BluetoothGattCharacteristic serviceAuthChar;
    private BluetoothGattDescriptor activityDesc;

    private byte[] authCharValue = new byte[]{(byte) 0xf5, (byte) 0xd2, 0x29, (byte) 0x87, 0x65,
            0x0a, 0x1d, (byte) 0x82, 0x05, (byte) 0xab, (byte) 0x82, (byte) 0xbe, (byte) 0xb9, 0x38, 0x59, (byte) 0xcf};

    public AppBluetoothGattCallback(SharedPreferences sharedPreferences,
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
        init(gatt);

        /*if (!sharedPreferences.getBoolean("isAuthenticated", false)) {*/
            authoriseMiBand(gatt);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isAuthenticated", true);
            editor.apply();
        /*} else {
            Log.i("Device", "Already authenticated");
        }*/
    }

    private void init(BluetoothGatt gatt) {
        service1 = gatt.getService(UUID.fromString(SERVICE1));
        service2 = gatt.getService(UUID.fromString(SERVICE2));

        authChar = service2.getCharacteristic(UUID.fromString(CHAR_AUTH));
        authDesc = authChar.getDescriptor(UUID.fromString(NOTIFICATION_DESC));

        fetchChar = service1.getCharacteristic(UUID.fromString(CHAR_FETCH));
        fetchDesc = fetchChar.getDescriptor(UUID.fromString(NOTIFICATION_DESC));

        activityChar = service1.getCharacteristic(UUID.fromString(CHAR_ACTIVITY_DATA));
        activityDesc = activityChar.getDescriptor(UUID.fromString(NOTIFICATION_DESC));
        heartBeatMeasurer = getModuleStorage().getHeartBeatMeasurerPackage().getHeartBeatMeasurer();
        heartBeatMeasurer.initHrChars(gatt);
    }

    private void authoriseMiBand(BluetoothGatt gatt) {
        Log.d("INFO","Enabling Auth Service notifications status...");
        gatt.setCharacteristicNotification(authChar, true);
        Log.d("INFO", "Found NOTIFICATION BluetoothGattDescriptor: " +
                    authDesc.toString());
        authDesc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(authDesc);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        Log.d("INFO", "onCharacteristicChanged char uuid: " + characteristic.getUuid().toString()
                + " value: " + Arrays.toString(characteristic.getValue()));

        byte[] charValue = Arrays.copyOfRange(characteristic.getValue(), 0, 3);

        switch (characteristic.getUuid().toString()){
            case CHAR_AUTH:{
                switch (Arrays.toString(charValue)){
                    case "[16, 1, 1]":{
                        authChar.setValue(new byte[]{0x02, 0x00});
                        gatt.writeCharacteristic(authChar);
                        break;
                    }
                    case "[16, 3, 4]":
                    case "[16, 2, 1]": {
                        executeAuthorisationSequence(gatt, characteristic);
                        break;
                    }
                    case "[16, 3, 1]":{
                        Log.d("INFO", "Authentication has been passed successfully");
                        break;
                    }
                }
                break;
            }
            case CHAR_HEART_RATE_MEASURE:{
                heartBeatMeasurer.handleHeartRateData(characteristic);
                break;
            }
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){
        Log.d("INFO", "onCharacteristicWrite uuid: " + characteristic.getUuid().toString()
                + " value: " + Arrays.toString(characteristic.getValue()) + " status: " + status);
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        switch (descriptor.getCharacteristic().getUuid().toString()){
            case CHAR_AUTH:{
                byte[] authKey = ArrayUtils.addAll(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, authCharValue);
                authChar.setValue(authKey);
                gatt.writeCharacteristic(authChar);
                break;
            }
            case CHAR_HEART_RATE_MEASURE:{
                Log.d("INFO", "onDescriptorWrite uuid: " + descriptor.getUuid().toString()
                        + " value: " + Arrays.toString(descriptor.getValue()));
            }
        }
    }

    private void executeAuthorisationSequence(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        byte[] value = characteristic.getValue();
        if (value[0] == 16 && value[1] == 1 && value[2] == 1) {
            characteristic.setValue(new byte[]{0x02, 0x8});
            gatt.writeCharacteristic(characteristic);
        } else if (value[0] == 16 && value[1] == 2 && value[2] == 1) {
            try {
                byte[] tmpValue = Arrays.copyOfRange(value, 3, 19);
                Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");

                SecretKeySpec key = new SecretKeySpec(authCharValue, "AES");
                cipher.init(Cipher.ENCRYPT_MODE, key);
                byte[] bytes = cipher.doFinal(tmpValue);
                byte[] rq = ArrayUtils.addAll(new byte[]{0x03, 0x00}, bytes);
                characteristic.setValue(rq);
                gatt.writeCharacteristic(characteristic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (value[0] == 16 && value[1] == 3 && value[2] == 4) {
            authoriseMiBand(gatt);
        }
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        Log.d("onDescriptorRead", descriptor.getUuid().toString() + " Read" + "status: " + status);
    }

}
