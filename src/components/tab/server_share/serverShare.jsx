import React from 'react'
import {Text, View, TouchableOpacity} from 'react-native';
import DataScreen from '../../common/dataScreen/index.js';
import styles from "./styles.jsx";

export default class ServerShare extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            storedDeviceBondLevel: 0,
            storedHeartBeatRate: 0,
            storedSteps: 0,
            storedBattery: 0
        };
    }

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
                        <TouchableOpacity style={styles.buttonEnabled} onPress={this.searchBluetoothDevices}>
                            <Text style={styles.buttonText}>Share Data</Text>
                        </TouchableOpacity>
                </View>
            </View>
        );
    }

}