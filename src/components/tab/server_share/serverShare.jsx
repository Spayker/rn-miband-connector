import React from 'react'
import { Text, View, TouchableOpacity } from 'react-native';
import { AsyncStorage } from 'react-native';
import DeviceRequests from "../../common/rest/deviceRequests.jsx"
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
            storedDateTime: '',
            userToken: '',
            username: '',
            deviceId: 'unknown',
            serverStoredDeviceId: ''
        }
        deviceRequestsObj = new DeviceRequests()
    }

    shareDeviceData = () => {
        deviceRequestsObj.registerDevice(this.state.username, this.state.deviceId, this.state.userToken)
    }

    sendDataToServer = () => {
        deviceRequestsObj.sendDeviceData(this.state.username, this.state.deviceId, this.state.userToken, this.state.storedHeartBeatRate)
    }

    getServerData = () => {
        deviceRequestsObj.getDeviceData(this.state.deviceId, this.state.userToken, ((data) => {
            this.setState({serverStoredDeviceId: data.deviceId})
            this.setState({storedHeartBeatRate: data.hrData})
        }))
    }

    componentDidMount(){ this.updateStateByAsyncStorage() }

    updateStateByAsyncStorage = async () => {
        try {
          const accessToken = await AsyncStorage.getItem(globals.ACCESS_TOKEN_KEY);
          const username = await AsyncStorage.getItem(globals.USERNAME_TOKEN_KEY);
          const deviceId = await AsyncStorage.getItem(globals.DEVICE_ID_KEY);

          if (accessToken !== null && username !== null) {
            this.setState({userToken: accessToken})
            this.setState({username: username})
            this.setState({deviceId: deviceId})
          }
        } catch (error) { console.log(error) }
    };

    render() {
        return (
            <View style={styles.container}>
                <View style={styles.package}>
                    <Text style={styles.tabHeader}>Data From Server</Text>

                    <View style={styles.dataContainer}>
                        <View style={styles.dataPackage}>
                            <Text style={styles.dataField}>Device Id:</Text>
                            <Text style={styles.dataField}>{this.state.serverStoredDeviceId}</Text>
                        </View>
                        
                        <View style={styles.dataPackage}>
                            <Text style={styles.dataField}>Heart Beat:</Text>
                            <Text style={styles.dataField}>{this.state.storedHeartBeatRate + ' Bpm'}</Text>
                        </View>

                        <View style={styles.dataPackage}>
                            <Text style={styles.dataField}>Server Date Mark:</Text>
                            <Text style={styles.dataField}>{this.state.storedDateTime}</Text>
                        </View>

                        <View style={styles.dataPackage}>
                            <Text style={styles.dataField}>Steps:</Text>
                            <Text style={styles.dataField}>{this.state.storedSteps}</Text>
                        </View>

                        <View style={styles.dataPackage}>
                            <Text style={styles.dataField}>Battery:</Text>
                            <Text style={styles.dataField}>{this.state.storedBattery + ' %'}</Text>
                        </View>

                        <View style={styles.dataPackage}>
                            <Text style={styles.dataField}>Device Bond Level:</Text>
                            <Text style={styles.dataField}>{this.state.storedDeviceBondLevel}</Text>
                        </View>
                    </View>
                </View>
                
                <View style={styles.package}>
                    <Text style={styles.tabHeader}></Text>
                </View>

                <View style={styles.buttonContainer}>
                        <TouchableOpacity style={styles.buttonEnabled} onPress={() => {this.shareDeviceData()}}>
                            <Text style={styles.buttonText}>Register Device</Text>
                        </TouchableOpacity>
                </View>

                <View style={styles.spacing}/>

                <View style={styles.buttonContainer}>
                        <TouchableOpacity style={styles.buttonEnabled} onPress={() => {this.sendDataToServer()}}>
                            <Text style={styles.buttonText}>Send Data</Text>
                        </TouchableOpacity>
                </View>

                <View style={styles.spacing}/>

                <View style={styles.buttonContainer}>
                        <TouchableOpacity style={styles.buttonEnabled} onPress={() => {this.getServerData()}}>
                            <Text style={styles.buttonText}>Get Server Data</Text>
                        </TouchableOpacity>
                </View>
            </View>
        );
    }

}