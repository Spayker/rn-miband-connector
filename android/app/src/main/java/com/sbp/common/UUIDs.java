package com.sbp.common;

/**
 *  Declares main set of chars for communication between miband device and android
 *  application. Contains codes for device paring, heart measurement
 *
 * @author  Spayker
 * @version 1.0
 * @since   06/01/2019
 */
public interface UUIDs {

    byte[] AUTH_CHAR_KEY = new byte[]{
            (byte) 0xf5, (byte) 0xd2, 0x29, (byte) 0x87, 0x65, 0x0a, 0x1d, (byte) 0x82, 0x05,
            (byte) 0xab, (byte) 0x82, (byte) 0xbe, (byte) 0xb9, 0x38, 0x59, (byte) 0xcf
    };

    String SERVICE1 = "0000fee0-0000-1000-8000-00805f9b34fb";
    String SERVICE2 = "0000fee1-0000-1000-8000-00805f9b34fb";
    String SERVICE_HEART_RATE = "0000180d-0000-1000-8000-00805f9b34fb";

    String CHAR_AUTH = "00000009-0000-3512-2118-0009af100700";
    String CHAR_HEART_RATE_CONTROL = "00002a39-0000-1000-8000-00805f9b34fb";
    String CHAR_HEART_RATE_MEASURE = "00002a37-0000-1000-8000-00805f9b34fb";
    String CHAR_SENSOR = "00000001-0000-3512-2118-0009af100700";
    String CHAR_BATTERY = "00000006-0000-3512-2118-0009af100700";
    String CHAR_STEPS = "00000007-0000-3512-2118-0009af100700";

    String NOTIFICATION_DESC = "00002902-0000-1000-8000-00805f9b34fb";
}
