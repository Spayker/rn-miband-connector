import React from 'react'
import {Button, View, Image, TextInput, Text} from 'react-native';
import AccountRequests from "../../common/rest/accountRequests.jsx"
import globals from "../../common/globals.jsx";
import {AsyncStorage} from 'react-native';
import styles from "./styles.jsx";

export default class Account extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            username: 'spayker',
            password: 'qwerty',
            userToken: '',
            status: globals.UNAUTHORIZED_STATE
        }
    }

    signUpUser = () => {
        accountRequestsObj = new AccountRequests();
        accountRequestsObj.signUp(this.state.username, this.state.password)
        this.updateAuthStatus()
    }

    updateAuthStatus = async () => {
        try {
            const accessToken = await AsyncStorage.getItem(globals.ACCESS_TOKEN_KEY);
            console.log('AceesToken: ' + accessToken)
            if (accessToken !== null) {
                this.setState({status: globals.AUTHORIZED_STATE})
            } else {
                this.setState({status: globals.UNAUTHORIZED_STATE})
            }
        } catch (error) { console.log(error) }
    }

    render() {
        return (
            <View style={styles.container}>

                <View style={styles.editPhotoPackage}>
                    <Image style={styles.image} source={require('../../../resources/account.png')} />
                    <Text style={styles.contentTextHeader}>{this.state.username}</Text>
                    <Text style={styles.textHeaderDescription}>{this.state.status}</Text>
                </View>

                <View style={styles.inputPackage}>

                    <TextInput
                        style={styles.dataInputText}
                        editable={true}
                        placeholder='Enter Your Name'
                        name="name"
                        type="name"
                        id="username"
                        value={this.state.username}
                        onChangeText={(username) => this.setState({username})}/>

                    <TextInput
                        style={styles.dataInputText}
                        editable={true}
                        secureTextEntry={true}
                        placeholder='Enter Your Password'
                        name="password"
                        type="password"
                        id="password"
                        value={this.state.password}
                        onChangeText={(password) => this.setState({password})}/>
                </View>

                <Button style={styles.saveButton} title='Sign Up' onPress={() => { this.signUpUser() }}/>

            </View>
        )}

}