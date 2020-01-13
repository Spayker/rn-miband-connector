import React from 'react'
import {Text, View} from 'react-native';
import styles from "./styles.jsx";


export default class DataScreen extends React.Component {

    render() {
        return (
            <View style={styles.container}>
                <View style={styles.package}>
                    <Text style={styles.sensorField}>Heart Beat:</Text>
                    <Text style={styles.sensorField}>{this.props.heartBeatRate + ' Bpm'}</Text>
                </View>

                <View style={styles.package}>
                    <Text style={styles.sensorField}>Steps:</Text>
                    <Text style={styles.sensorField}>{this.props.steps}</Text>
                </View>

                <View style={styles.package}>
                    <Text style={styles.sensorField}>Battery:</Text>
                    <Text style={styles.sensorField}>{this.props.battery + ' %'}</Text>
                </View>

                <View style={styles.package}>
                    <Text style={styles.sensorField}>Device Bond Level:</Text>
                    <Text style={styles.sensorField}>{this.props.deviceBondLevel}</Text>
                </View>
            </View>
        );
    }
}