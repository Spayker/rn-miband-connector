package com.sbp.metric;

import java.util.UUID;

/**
 *  Declares main set of charasteristics for communication between miband device and android
 *  application. Contains codes for device paring, heart measurement
 *
 * @author  Spayker
 * @version 1.0
 * @since   06/01/2019
 */
public class UUIDs {

    //Custom service 3 components
    public static UUID CUSTOM_SERVICE_FEE1 = UUID.fromString("0000fee1-0000-1000-8000-00805f9b34fb");
    public static UUID CUSTOM_SERVICE_AUTH_CHARACTERISTIC = UUID.fromString("00000009-0000-3512-2118-0009af100700");
    public static UUID CUSTOM_SERVICE_AUTH_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    //Heart rate monitoring section
    static UUID HEART_RATE_SERVICE = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
    static UUID HEART_RATE_MEASURMENT_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    static UUID HEART_RATE_CONTROL_POINT_CHARACTERISTIC = UUID.fromString("00002a39-0000-1000-8000-00805f9b34fb");

    // Sensor section
    static UUID SENSOR_SERVICE = UUID.fromString("0000fee0-0000-1000-8000-00805f9b34fb");
    static UUID CHARACTER_SENSOR_CHARACTERISTIC = UUID.fromString("00000001-0000-3512-2118-0009af100700");

    /*----------------UUID strings for the switch statements----------*/
    public static final String CUSTOM_SERVICE_AUTH_CHARACTERISTIC_STRING = "00000009-0000-3512-2118-0009af100700";
    public static final String HEART_RATE_MEASUREMENT_CHARACTERISTIC_STRING = "00002a37-0000-1000-8000-00805f9b34fb";
}
