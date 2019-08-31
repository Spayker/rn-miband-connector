import React from 'react'
import {Text, View, Button, NativeModules, TouchableOpacity} from 'react-native';
import styles from "./styles.jsx";

export default class Dashboard extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            deviceBondLevel: 0,
            heartBeatRate: 0,
            isConnectedWithMiBand: false,
            isHeartRateCalculating: false,
            bluetoothSearchInterval: null,
            hrRateInterval: null
        };
    }

    searchBluetoothDevices = () => {
        this.setState({ isConnectedWithMiBand: true})
        NativeModules.DeviceConnector.enableBTAndDiscover( (error, deviceBondLevel) => {
            this.setState({ deviceBondLevel: deviceBondLevel});
        })
        this.setState({ bluetoothSearchInterval: setInterval(this.getDeviceBondLevel, 2000) })
    }

    unlinkBluetoothDevice = () => {
        this.setState({ deviceBondLevel: 0});
        this.setState({ isConnectedWithMiBand: false})
        clearInterval(this.state.bluetoothSearchInterval);
        this.setState({ bluetoothSearchInterval: null});
        this.deactivateHeartRateCalculation();
    }

    getDeviceBondLevel = () => {
        NativeModules.DeviceConnector.getDeviceBondLevel( (error, deviceBondLevel) => {
            this.setState({ deviceBondLevel: deviceBondLevel}, () => {
                this.getDeviceBondLevel
            });
        })
    }

    activateHeartRateCalculation = () => {
        NativeModules.HeartBeatMeasurer.startHeartRateCalculation( (error, heartBeatRate) => {
            this.setState({ heartBeatRate: heartBeatRate});
            this.setState({ isHeartRateCalculating: true});
        })
        this.setState({ hrRateInterval: setInterval(this.getHeartRate, 2000)})
    }

    deactivateHeartRateCalculation = () => {
        this.setState({ heartBeatRate: 0});
        clearInterval(this.state.hrRateInterval);
        this.setState({ hrRateInterval: null});
        this.setState({ isHeartRateCalculating: false});
    }

    getHeartRate = () => {
        NativeModules.HeartBeatMeasurer.getHeartRate( (error, heartBeatRate) => {
            this.setState({ heartBeatRate: heartBeatRate});
        })
    }

    render() {
        return (
            <View style={styles.container}>
                <View style={styles.package}>
                    <Text style={styles.sensorField}>Heart Beat:</Text>
                    <Text style={styles.sensorField}>{this.state.heartBeatRate + ' Bpm'}</Text>
                </View>

                <View style={styles.package}>
                    <Text style={styles.sensorField}>Device BL:</Text>
                    <Text style={styles.sensorField}>{this.state.deviceBondLevel}</Text>
                </View>

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
                                <Text style={styles.buttonText}>Stop HR Calculation</Text>
                            </TouchableOpacity>
                        ) : (
                            <TouchableOpacity style={styles.buttonEnabled} onPress={this.activateHeartRateCalculation} disabled={false}>
                                <Text style={styles.buttonText}>Start HR Calculation</Text>
                            </TouchableOpacity>
                        )
                    ) : (
                        <TouchableOpacity style={styles.buttonDisabled} onPress={this.activateHeartRateCalculation} disabled={true}>
                            <Text style={styles.buttonText}>Start HR Calculation</Text>
                        </TouchableOpacity>
                    )}
                        
                </View>
            </View>
        );
    }
}