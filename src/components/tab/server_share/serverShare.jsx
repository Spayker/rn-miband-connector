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
        };
    }

    shareDeviceData = () => {
        console.log('share userToken: ' + this.state.userToken);
    }

    componentDidMount(){
        this._updateStateByAsyncStorage()
    }

    _updateStateByAsyncStorage = async () => {
        try {
          const value = await AsyncStorage.getItem(globals.ACCESS_TOKEN_KEY);
          if (value !== null) {
            this.setState({userToken: value})
            console.log('received userToken: ' + this.state.userToken);
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