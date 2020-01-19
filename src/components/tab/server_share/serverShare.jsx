import React from 'react'
import {Text, View, TouchableOpacity} from 'react-native';
import DataScreen from '../../common/dataScreen/dataScreen.jsx';
import {AsyncStorage} from 'react-native';
import globals from "../../common/globals.jsx";
import styles from "./styles.jsx";

export default class ServerShare extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            storedDeviceBondLevel: 0,
            storedHeartBeatRate: 0,
            storedSteps: 0,
            storedBattery: 0,
            userToken: '',
            username: '',
            deviceId: '123412342999'
        };
    }

    shareDeviceData = () => {
        console.log('userToken: ' + this.state.userToken)
        console.log('username: ' + this.state.username)
        this.registerDevice()
        this.sendDeviceData()
    }

    registerDevice = () => {
        return fetch('http://' + globals.SERVER_DEVICE_URL_ADDRESS + '/devices/', {
            method: 'POST',
            headers: {
                Accept: 'application/json',
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + this.state.userToken
            },
            body: JSON.stringify({
                username: this.state.username,
                deviceId: this.state.deviceId
            }),
        })
        .then((response) => response.json())
        .then((responseJson) => {
            console.log(responseJson)
        })
        .catch((error) => { console.error(error) });
    }

    sendDeviceData = () => {
        return fetch('http://' + globals.SERVER_DEVICE_URL_ADDRESS + '/devices/', {
            method: 'PUT',
            headers: {
                Accept: 'application/json',
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + this.state.userToken
            },
            body: JSON.stringify({
                deviceId:   this.state.deviceId,
                username:   this.state.username,
                date:       new Date().getDate(),
                hrData:     this.state.storedHeartBeatRate,
            }),
        })
        .catch((error) => { console.error(error) });
    }

    componentDidMount(){
        this.updateStateByAsyncStorage()
    }

    updateStateByAsyncStorage = async () => {
        try {
          const accessToken = await AsyncStorage.getItem(globals.ACCESS_TOKEN_KEY);
          const username = await AsyncStorage.getItem(globals.USERNAME_TOKEN_KEY);

          console.log('userToken: ' + accessToken)
          console.log('username: ' + username)
          
          if (accessToken !== null && username !== null) {
            this.setState({userToken: accessToken})
            this.setState({username: username})
          }

        } catch (error) {
            console.log(error)
        }
    };

    render() {
        return (
            <View style={styles.container}>
                <View style={styles.package}>
                    <Text style={styles.tabHeader}>Collected Data</Text>

                    <DataScreen deviceBondLevel={this.state.storedDeviceBondLevel} 
                                heartBeatRate={this.state.storedHeartBeatRate} 
                                steps={this.state.storedSteps} 
                                battery={this.state.storedBattery}/>

                </View>

                <View style={styles.spacing}/>

                <View style={styles.package}>
                    <Text style={styles.tabHeader}>Data Transfer Status</Text>
                </View>
                
                <View style={styles.package}>
                    <Text style={styles.tabHeader}></Text>
                </View>

                <View style={styles.buttonContainer}>
                        <TouchableOpacity style={styles.buttonEnabled} onPress={() => {this.shareDeviceData()}}>
                            <Text style={styles.buttonText}>Share Data</Text>
                        </TouchableOpacity>
                </View>
            </View>
        );
    }

}