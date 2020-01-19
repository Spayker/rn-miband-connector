import React from 'react'
import globals from "../../common/globals.jsx";
import {AsyncStorage} from 'react-native';

export default class DeviceRequests extends React.Component {

    registerDevice = (username, deviceId, userToken) => {
        return fetch('http://' + globals.SERVER_DEVICE_URL_ADDRESS + '/devices/', {
            method: 'POST',
            headers: {
                Accept: 'application/json',
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + userToken
            },
            body: JSON.stringify({
                username: username,
                deviceId: deviceId
            })
        })
        .then((response) => response.json())
        .then((responseJson) => { console.log(responseJson) })
        .catch((error) => { console.error(error) });
    }

    sendDeviceData = (username, deviceId, userToken, storedHeartBeatRate) => {
        return fetch('http://' + globals.SERVER_DEVICE_URL_ADDRESS + '/devices/', {
            method: 'PUT',
            headers: {
                Accept: 'application/json',
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + userToken
            },
            body: JSON.stringify({
                deviceId:   deviceId,
                username:   username,
                date:       new Date().getDate(),
                hrData:     storedHeartBeatRate,
            })
        })
        .catch((error) => { console.error(error) });
    }

}