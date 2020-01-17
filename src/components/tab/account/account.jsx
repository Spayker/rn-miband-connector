import React from 'react'
import {Button, View, Image, TextInput} from 'react-native';
import styles from "./styles.jsx";

export default class Account extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            username: "",
            password: ""
        }
    }

    signUp = () => {
        return fetch('http://192.168.0.101:6000/accounts/', {
            method: 'POST',
            headers: {
              Accept: 'application/json',
              'Content-Type': 'application/json',
            },
            body: JSON.stringify({
              username: this.state.username,
              password: this.state.password
            }),
        });
    }

    render() {
        return (
            <View style={styles.container}>

                <View style={styles.editPhotoPackage}>

                    <View style={styles.spacing}/>

                    <Image style={styles.image} source={require('../../../resources/galaxy_scaled.png')} />

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