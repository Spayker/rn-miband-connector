import React from 'react'
import globals from "../../common/globals.jsx";

export default class DeviceRequests extends React.Component {

    constructor(props) { super(props) }

    registerDevice = (username, deviceId, userToken) => {
        console.log('Device register: ' + username + ' ' + deviceId + ' ' + userToken)
        return fetch('http://' + globals.SERVER_DEVICE_URL_ADDRESS + '/devices/', {
            method: 'POST',
            headers: this.deviceHeaderBuilder(userToken),
            body:    this.deviceJsonBodyBuilder(deviceId, username, new Date(), 0)
        })
        .then((response) => response.json())
        .then((responseJson) => { console.log(responseJson) })
        .catch((error) => { console.error(error) });
    }

    sendDeviceData = (username, deviceId, userToken, storedHeartBeatRate) => {
        console.log('Device sendDeviceData: ' + username + ' ' + deviceId + ' ' + userToken + ' ' + storedHeartBeatRate)
        return fetch('http://' + globals.SERVER_DEVICE_URL_ADDRESS + '/devices/', {
            method: 'PUT',
            headers: this.deviceHeaderBuilder(userToken),
            body:    this.deviceJsonBodyBuilder(deviceId, username, new Date(), storedHeartBeatRate)
        })
        .catch((error) => { console.error(error) });
    }

    getDeviceData = (deviceId, userToken, cb) => {
        console.log('Device getDeviceData: ' + deviceId + ' ' + userToken)
        return fetch('http://' + globals.SERVER_DEVICE_URL_ADDRESS + '/devices/' + deviceId, {
            method: 'GET',
            headers: this.deviceHeaderBuilder(userToken)
        })
        .then((response) => response.json())
        .then((responseJson) => { cb(responseJson)})
        .catch((error) => { console.error(error) });
    }

    deviceHeaderBuilder = (userToken) => {
        return {
            Accept: 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + userToken
        }
    }

    deviceJsonBodyBuilder = (deviceId, username, date, hrdata) => {
        return JSON.stringify({
            deviceId:   deviceId,
            username:   username,
            date:       date,
            hrData:     hrdata
        })
    }

}