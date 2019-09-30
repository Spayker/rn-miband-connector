package com.sbp.common;

import java.util.UUID;

/**
 *  Declares main set of charasteristics for communication between miband device and android
 *  application. Contains codes for device paring, heart measurement
 *
 * @author  Spayker
 * @version 1.0
 * @since   06/01/2019
 */
public interface UUIDs {

    String BASE = "0000%s-0000-1000-8000-00805f9b34fb";

    String SERVICE1 = "0000fee0-0000-1000-8000-00805f9b34fb";
    String SERVICE2 = "0000fee1-0000-1000-8000-00805f9b34fb";

    String SERVICE_HEART_RATE = "0000180d-0000-1000-8000-00805f9b34fb";

    String CHAR_AUTH = "00000009-0000-3512-2118-0009af100700";

    String NOTIFICATION_DESC = "00002902-0000-1000-8000-00805f9b34fb";

    String CHAR_HEART_RATE_CONTROL = "00002a39-0000-1000-8000-00805f9b34fb";
    String CHAR_HEART_RATE_MEASURE = "00002a37-0000-1000-8000-00805f9b34fb";

    String CHAR_FETCH = "00000004-0000-3512-2118-0009af100700";
    String CHAR_ACTIVITY_DATA = "00000005-0000-3512-2118-0009af100700";

    String CHAR_SENSOR = "00000001-0000-3512-2118-0009af100700";

    String SERVICE_ALERT = String.format(BASE, "1802");
    String SERVICE_ALERT_NOTIFICATION = String.format(BASE, "1811");
    String SERVICE_DEVICE_INFO = String.format(BASE, "180a");



    String CHAR_HZ = "00000002-0000-3512-2118-0009af100700";
    String CHAR_ALERT = "00002a06-0000-1000-8000-00805f9b34fb";
    String CHAR_BATTERY = "00000006-0000-3512-2118-0009af100700";
    String CHAR_STEPS = "00000007-0000-3512-2118-0009af100700";
    String CHAR_LE_PARAMS = String.format(BASE, "FF09");
    int CHAR_REVISION = 0x2a28;
    int CHAR_SERIAL = 0x2a25;
    int CHAR_HRDW_REVISION = 0x2a27;
    String CHAR_CONFIGURATION = "00000003-0000-3512-2118-0009af100700";
    String CHAR_DEVICEEVENT = "00000010-0000-3512-2118-0009af100700";

    String CHAR_CURRENT_TIME = String.format(BASE, "2A2B");
    String CHAR_AGE = String.format(BASE, "2A80");
    String CHAR_USER_SETTINGS = "00000008-0000-3512-2118-0009af100700";



}
