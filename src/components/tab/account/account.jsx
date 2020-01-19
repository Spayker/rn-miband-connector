import React from 'react'
import {Button, View, Image, TextInput, Text} from 'react-native';
import globals from "../../common/globals.jsx";
import styles from "./styles.jsx";

export default class Account extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            username: 'Spayker',
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
        .catch((error) => { console.error(error) });
    }

    getAccessToken = () => {
        console.log(globals.SERVER_AUTH_URL_ADDRESS)
        console.log(this.state.username)
        console.log(this.state.password)


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
        })
        .catch((error) => { console.error(error) });
    }

    render() {
        return (
            <View style={styles.container}>

                <View style={styles.editPhotoPackage}>
                    <Image style={styles.image} source={require('../../../resources/galaxy_scaled.png')} />
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
                        onChangeText={(username) => this.setState({username})}/>

                    <TextInput
                        style={styles.dataInputText}
                        editable={true}
                        placeholder='Enter Your Password'
                        name="password"
                        type="password"
                        id="password"
                        secureTextEntry={true}
                        value={this.state.password}
                        onChangeText={(password) => this.setState({password})}/>
                </View>

                <Button style={styles.saveButton} title='Sign Up' onPress={() => {
                    this.signUp()
                }}/>

            </View>
        )}

}