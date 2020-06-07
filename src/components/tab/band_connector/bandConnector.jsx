import React from 'react'
import {Text, View, NativeModules, TouchableOpacity, TouchableWithoutFeedback, FlatList} from 'react-native';
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
            isConnectedWithMiBand: null,
            isHeartRateCalculating: false,
            bluetoothSearchInterval: null,
            foundDevices: [],
            hrRateInterval: null
        };
    }

    searchBluetoothDevices = () => {
        NativeModules.DeviceConnector.discoverDevices( (error, data) => {
            this.setState({ foundDevices: [data]})
            console.debug(this.state.foundDevices)
        })
    }

    linkWithDevice = (macAddress) => {
        NativeModules.DeviceConnector.linkWithDevice(macAddress, (error, data) => {
            this.setState({ deviceBondLevel: data})
            // toDo: get real deviceId instead of what's transferring to storage below...
            AsyncStorage.setItem(globals.DEVICE_ID_KEY, (((1+Math.random())*0x10000)|0).toString(16).substring(1));
        })
        this.setState({ bluetoothSearchInterval: setInterval(this.getDeviceInfo, 15000) })
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
            this.setState({ deviceBondLevel: deviceBondLevel}, () => { this.getDeviceBondLevel })
        })
        NativeModules.InfoReceiver.getInfo((error, steps, battery) => {
            this.setState({ steps: steps})
            this.setState({ battery: battery})
            this.setState({ isConnectedWithMiBand: true})
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
                    <TouchableOpacity style={styles.buttonEnabled} onPress={this.searchBluetoothDevices}>
                        <Text style={styles.buttonText}>Search Devices</Text>
                    </TouchableOpacity>
                    <View style={styles.spacing}/>
                </View>

                <FlatList
                    data={this.state.foundDevices}
                    extraData={this.state}
                    renderItem={
                        ({item}) => 
                            item.deviceName === undefined ? (
                                <View/>
                            ) : (
                                <View style={styles.listTrainingContainer}>
                                    <View style={styles.listTrainingColumnData}>
                                        <Text style={styles.item}>{item.deviceName}</Text>
                                        <Text style={styles.item}>{item.deviceMac}</Text>
                                    </View>

                                    {this.state.isConnectedWithMiBand ? (
                                        <TouchableOpacity style={styles.buttonEnabled} onPress={() => this.unlinkBluetoothDevice()}>
                                            <Text style={styles.buttonText}>Unlink</Text>
                                        </TouchableOpacity>
                                    ) : (
                                        item.deviceName === 'Unknown Device' ? (
                                            <TouchableOpacity style={styles.buttonDisabled} disabled={true}>
                                                <Text style={styles.buttonText}>Link</Text>
                                            </TouchableOpacity>
                                        ) : (
                                            <TouchableOpacity style={styles.buttonEnabled} onPress={() => this.linkWithDevice(item.deviceMac)}>
                                                <Text style={styles.buttonText}>Link</Text>
                                            </TouchableOpacity>
                                        )
                                    )}

                                    {this.state.isConnectedWithMiBand ? (
                                        this.state.isHeartRateCalculating ? (
                                            <TouchableOpacity style={styles.buttonEnabled} onPress={() => this.deactivateHeartRateCalculation()} disabled={false}>
                                                <Text style={styles.buttonText}>Stop HR</Text>
                                            </TouchableOpacity>
                                        ) : (
                                            <TouchableOpacity style={styles.buttonEnabled} onPress={() => this.activateHeartRateCalculation()} disabled={false}>
                                                <Text style={styles.buttonText}>Start HR</Text>
                                            </TouchableOpacity>
                                        )
                                    ) : (
                                        <TouchableOpacity style={styles.buttonDisabled} disabled={true}>
                                            <Text style={styles.buttonText}>Start HR</Text>
                                        </TouchableOpacity>
                                    )}
                                </View>
                            )
                    }
                    keyExtractor={item => item.deviceMac}/>
            </View>
        );
    }

}