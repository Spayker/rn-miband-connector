import React from 'react'
import {Button, View, Image, TextInput, Text} from 'react-native';
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
            status: 'unauthorized'
        }
    }

    signUp = () => {
        console.log(globals.SERVER_ACCOUNT_URL_ADDRESS)
        return fetch('http://' + globals.SERVER_ACCOUNT_URL_ADDRESS + '/accounts/', {
            method: 'POST',
            headers: {
                Accept: 'application/json',
                        'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                username: this.state.username,
                password: this.state.password
            }),
        })
        .then((response) => response.json())
        .then((responseJson) => {
            console.log(responseJson)
            this.getAccessToken()
        })
        .catch((error) => { 
            console.error(error)
            this.getAccessToken() 
        });
    }

    getAccessToken = () => {
        var details = {
            "scope": "ui",
            "username": this.state.username,
            "password": this.state.password,
            "grant_type": "client_credentials"
        };
        
        var formBody = [];
        for (var property in details) {
          var encodedKey = encodeURIComponent(property);
          var encodedValue = encodeURIComponent(details[property]);
          formBody.push(encodedKey + "=" + encodedValue);
        }
        formBody = formBody.join("&");

        return fetch('http://' + globals.SERVER_AUTH_URL_ADDRESS + '/mservicet/oauth/token', {
            method: 'POST',
            headers: {
                Authorization: "Basic YnJvd3Nlcjo=",
                Accept: "*/*", 
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: formBody
        })
        .then((response) => response.json())
        .then((responseJson) => {
            this.setState({userToken: responseJson.access_token})
            this.setState({status: 'authorized'})
            console.log(this.state.userToken)
            this._storeData()

        })
        .catch((error) => { 
            this._storeData()
            console.error(error)
        });
    }

    _storeData = async () => {
        try {
            console.log('username111: ' + this.state.username)
            let multiDataSet = [
                [globals.ACCESS_TOKEN_KEY, this.state.userToken],
                [globals.USERNAME_TOKEN_KEY, this.state.username],
            ];

            await AsyncStorage.multiSet(multiDataSet);
        } catch (error) {
            console.log('couldn\'t save user access token to storage...')
        }
    };

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

                <Button style={styles.saveButton} title='Sign Up' onPress={() => {
                    this.signUp()
                }}/>

            </View>
        )}

}