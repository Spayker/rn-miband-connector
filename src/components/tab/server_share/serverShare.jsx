import React from 'react'
import {Text, View, TouchableOpacity} from 'react-native';
import DataScreen from '../../common/dataScreen/dataScreen.jsx';
import DeviceRequests from "../../common/rest/deviceRequests.jsx"
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
            deviceId: ''
        }
    }

    shareDeviceData = () => {
        deviceRequestsObj = new DeviceRequests();
        deviceRequestsObj.registerDevice(this.state.username, this.state.deviceId, this.state.userToken)
        deviceRequestsObj.sendDeviceData(this.state.username, this.state.deviceId, this.state.userToken, this.state.storedHeartBeatRate)
    }

    componentDidMount(){ this.updateStateByAsyncStorage() }

    updateStateByAsyncStorage = async () => {
        try {
          const accessToken = await AsyncStorage.getItem(globals.ACCESS_TOKEN_KEY);
          const username = await AsyncStorage.getItem(globals.USERNAME_TOKEN_KEY);
          const deviceId = await AsyncStorage.getItem(globals.DEVICE_ID_KEY);

          console.log('userToken: ' + accessToken)
          console.log('username: ' + username)
          console.log('deviceId: ' + deviceId)
          
          if (accessToken !== null && username !== null) {
            this.setState({userToken: accessToken})
            this.setState({username: username})
            this.setState({deviceId: this.state.deviceId})
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