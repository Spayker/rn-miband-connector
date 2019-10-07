package com.sbp.common;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.sbp.info.InfoReceiver;
import com.sbp.metric.hr.HeartBeatMeasurer;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import static com.sbp.common.ModuleStorage.getModuleStorage;
import static com.sbp.common.UUIDs.AUTH_CHAR_KEY;
import static com.sbp.common.UUIDs.CHAR_AUTH;
import static com.sbp.common.UUIDs.CHAR_BATTERY;
import static com.sbp.common.UUIDs.CHAR_HEART_RATE_CONTROL;
import static com.sbp.common.UUIDs.CHAR_HEART_RATE_MEASURE;
import static com.sbp.common.UUIDs.CHAR_SENSOR;
import static com.sbp.common.UUIDs.CHAR_STEPS;
import static com.sbp.common.UUIDs.NOTIFICATION_DESC;
import static com.sbp.common.UUIDs.SERVICE1;
import static com.sbp.common.UUIDs.SERVICE2;
import static com.sbp.common.UUIDs.SERVICE_HEART_RATE;

/**
 * Declares logic for connection establishment between android app and miband by Bluetooth protocol
 *
 * @author  Spayker
 * @version 1.0
 * @since   06/01/2019
 */
public class GattCallback extends BluetoothGattCallback {

    private HeartBeatMeasurer heartBeatMeasurer;
    private InfoReceiver infoReceiver;

    private BluetoothGattCharacteristic authChar;
    private BluetoothGattDescriptor authDesc;

    private BluetoothGattCharacteristic hrCtrlChar;
    private BluetoothGattDescriptor hrDescChar;
    private BluetoothGattCharacteristic batteryChar;

    public GattCallback(HeartBeatMeasurer heartBeatMeasurer){
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
        authoriseMiBand(gatt);
    }

    private void init(BluetoothGatt gatt) {
        BluetoothGattService service1 = gatt.getService(UUID.fromString(SERVICE1));
        BluetoothGattService service2 = gatt.getService(UUID.fromString(SERVICE2));
        BluetoothGattService heartService = gatt.getService(UUID.fromString(SERVICE_HEART_RATE));

        authChar = service2.getCharacteristic(UUID.fromString(CHAR_AUTH));
        hrCtrlChar = heartService.getCharacteristic(UUID.fromString(CHAR_HEART_RATE_CONTROL));
        BluetoothGattCharacteristic hrMeasureChar = heartService.getCharacteristic(UUID.fromString(CHAR_HEART_RATE_MEASURE));
        batteryChar = service1.getCharacteristic(UUID.fromString(CHAR_BATTERY));
        hrDescChar = hrMeasureChar.getDescriptor(UUID.fromString(NOTIFICATION_DESC));
        authDesc = authChar.getDescriptor(UUID.fromString(NOTIFICATION_DESC));
    }

    private void authoriseMiBand(BluetoothGatt gatt) {
        Log.d("INFO","Enabling Auth Service notifications status...");
        gatt.setCharacteristicNotification(authChar, true);                 // 1
        Log.d("INFO", "Found NOTIFICATION BluetoothGattDescriptor: " + authDesc.toString());
        authDesc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);       // 2
        gatt.writeDescriptor(authDesc);                                             // 2
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
                        authChar.setValue(new byte[]{0x02, 0x00}); //4
                        gatt.writeCharacteristic(authChar); //4
                        break;
                    }
                    case "[16, 3, 4]":
                    case "[16, 2, 1]": {
                        executeAuthorisationSequence(gatt, characteristic); //5
                        break;
                    }
                    case "[16, 3, 1]":{
                        Log.d("INFO", "Authentication has been passed successfully"); // 7
                        ModuleStorage moduleStorage = getModuleStorage();
                        heartBeatMeasurer = moduleStorage.getHeartBeatMeasurerPackage().getHeartBeatMeasurer();
                        heartBeatMeasurer.updateHrChars(gatt);
                        infoReceiver = moduleStorage.getInfoPackage().getInfoReceiver();
                        infoReceiver.updateInfoChars(gatt);
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
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.i("INFO", "onCharacteristicRead uuid: " + characteristic.getUuid().toString()
                + " value: " + Arrays.toString(characteristic.getValue()) + " status: " + status);
        switch (characteristic.getUuid().toString()) {
            case CHAR_STEPS: {
                infoReceiver.handleInfoData(characteristic.getValue());
                gatt.readCharacteristic(batteryChar);
                break;
            }
            case CHAR_BATTERY: {
                infoReceiver.handleBatteryData(characteristic.getValue());
            }
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){
        switch (characteristic.getUuid().toString()) {
            case CHAR_SENSOR: {
                switch (Arrays.toString(characteristic.getValue())){
                    // for real time HR measurement [1, 3, 19] was sent actually but [1, 3, 25]
                    // is written. Magic?
                    case "[1, 3, 25]":{
                        hrDescChar.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        gatt.writeDescriptor(hrDescChar);
                    }
                }
            }
            default:
                Log.d("INFO", "onCharacteristicWrite uuid: " + characteristic.getUuid().toString()
                        + " value: " + Arrays.toString(characteristic.getValue()) + " status: " + status);
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        switch (descriptor.getCharacteristic().getUuid().toString()){
            case CHAR_AUTH:{
                byte[] authKey = ArrayUtils.addAll(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, AUTH_CHAR_KEY);
                authChar.setValue(authKey);             // 3
                gatt.writeCharacteristic(authChar);     // 3
                break;
            }
            case CHAR_HEART_RATE_MEASURE:{
                switch (Arrays.toString(descriptor.getValue())){
                    case "[1, 0]":{
                        hrCtrlChar.setValue(new byte[]{0x15, 0x01, 0x01});
                        Log.d("INFO","hrCtrlChar: " + gatt.writeCharacteristic(hrCtrlChar));
                    }
                    default:
                        Log.d("INFO", "onDescriptorWrite uuid: " + descriptor.getUuid().toString()
                                + " value: " + Arrays.toString(descriptor.getValue()));
                }
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
                String CIPHER_TYPE = "AES/ECB/NoPadding";
                Cipher cipher = Cipher.getInstance(CIPHER_TYPE);

                String CIPHER_NAME = "AES";
                SecretKeySpec key = new SecretKeySpec(AUTH_CHAR_KEY, CIPHER_NAME);
                cipher.init(Cipher.ENCRYPT_MODE, key);
                byte[] bytes = cipher.doFinal(tmpValue);
                byte[] rq = ArrayUtils.addAll(new byte[]{0x03, 0x00}, bytes);
                characteristic.setValue(rq);
                gatt.writeCharacteristic(characteristic); // 6
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
