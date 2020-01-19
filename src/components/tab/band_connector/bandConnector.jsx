import React from 'react'
import {Text, View, NativeModules, TouchableOpacity} from 'react-native';
import DataScreen from '../../common/dataScreen/dataScreen.jsx';
import globals from "../../common/globals.jsx";
import {AsyncStorage} from 'react-native';
import styles from "./styles.jsx";

export default class Bandconnector extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            deviceBondLevel: 0,
            heartBeatRate: 0,
            steps: 0,
            battery: 0,
            isConnectedWithMiBand: false,
            isHeartRateCalculating: false,
            bluetoothSearchInterval: null,
            hrRateInterval: null
        };
    }

    searchBluetoothDevices = () => {
        this.setState({ isConnectedWithMiBand: true})
        NativeModules.DeviceConnector.enableBTAndDiscover( (error, deviceBondLevel) => {
            this.setState({ deviceBondLevel: deviceBondLevel})
            //toDo: get real deviceId instead of what's transferring to storage below...
            AsyncStorage.setItem(globals.DEVICE_ID_KEY, (((1+Math.random())*0x10000)|0).toString(16).substring(1));
        })
        this.setState({ bluetoothSearchInterval: setInterval(this.getDeviceInfo, 5000) })
    }

    unlinkBluetoothDevice = () => {
        this.deactivateHeartRateCalculation()
        NativeModules.DeviceConnector.disconnectDevice( (error, deviceBondLevel) => {
            this.setState({ deviceBondLevel: deviceBondLevel});
        })
        clearInterval(this.state.bluetoothSearchInterval)
        this.setState({ bluetoothSearchInterval: null})
        this.setState({ deviceBondLevel: 0})
        this.setState({ steps: 0})
        this.setState({ battery: 0})
        this.setState({ isConnectedWithMiBand: false})
    }

    getDeviceInfo = () => {
        NativeModules.DeviceConnector.getDeviceBondLevel( (error, deviceBondLevel) => {
            this.setState({ deviceBondLevel: deviceBondLevel}, () => {
                this.getDeviceBondLevel
            });
        })
        NativeModules.InfoReceiver.getInfo((error, steps, battery) => {
            this.setState({ steps: steps})
            this.setState({ battery: battery})
        })
    }

    activateHeartRateCalculation = () => {
        NativeModules.HeartBeatMeasurer.startHrCalculation((error, heartBeatRate) => {
            this.setState({ isHeartRateCalculating: true})
            this.setState({ heartBeatRate: heartBeatRate})
        })
        this.setState({ hrRateInterval: setInterval(this.getHeartRate, 5000)})
    }

    deactivateHeartRateCalculation = () => {
        NativeModules.HeartBeatMeasurer.stopHrCalculation()
        this.setState({ isHeartRateCalculating: false})
        this.setState({ hrRateInterval: null})
        this.setState({ heartBeatRate: 0})
        clearInterval(this.state.hrRateInterval)
    }

    getHeartRate = () => {
        NativeModules.HeartBeatMeasurer.getHeartRate( this.state.heartBeatRate, (error, heartBeatRate) => {
            this.setState({ heartBeatRate: heartBeatRate})
        })
    }

    render() {
        return (
            <View style={styles.container}>

                <DataScreen deviceBondLevel={this.state.deviceBondLevel} 
                            heartBeatRate={this.state.heartBeatRate} 
                            steps={this.state.steps} 
                            battery={this.state.battery}/>
                
                <View style={styles.buttonContainer}>

                    {this.state.isConnectedWithMiBand ? (
                        <TouchableOpacity style={styles.buttonEnabled} onPress={this.unlinkBluetoothDevice}>
                            <Text style={styles.buttonText}>Unlink With MiBand</Text>
                        </TouchableOpacity>
                    ) : (
                        <TouchableOpacity style={styles.buttonEnabled} onPress={this.searchBluetoothDevices}>
                            <Text style={styles.buttonText}>Link With MiBand</Text>
                        </TouchableOpacity>
                    )}

                    <View style={styles.spacing}/>

                    {this.state.isConnectedWithMiBand ? (
                        this.state.isHeartRateCalculating ? (
                            <TouchableOpacity style={styles.buttonEnabled} onPress={this.deactivateHeartRateCalculation} disabled={false}>
                                <Text style={styles.buttonText}>Stop HR Measurement</Text>
                            </TouchableOpacity>
                        ) : (
                            <TouchableOpacity style={styles.buttonEnabled} onPress={this.activateHeartRateCalculation} disabled={false}>
                                <Text style={styles.buttonText}>Start HR Measurement</Text>
                            </TouchableOpacity>
                        )
                    ) : (
                        <TouchableOpacity style={styles.buttonDisabled} disabled={true}>
                            <Text style={styles.buttonText}>Start HR Measurement</Text>
                        </TouchableOpacity>
                    )}
                </View>
            </View>
        );
    }

}