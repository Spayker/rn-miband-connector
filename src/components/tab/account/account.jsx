import React from 'react'
import {Button, View, Image, TextInput} from 'react-native';
import styles from "./styles.jsx";

export default class Account extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
        
        }
    }

    saveProfile(){
        console.log('Profile has been signed up...')
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
                        placeholder='Your Name'
                        name="name"
                        type="name"
                        id="name"/>

                    <TextInput
                        style={styles.dataInputText}
                        editable={true}
                        placeholder='Your Password'
                        name="password"
                        type="password"
                        id="password"/>

                </View>

                <Button style={styles.saveButton} title='Sign Up' onPress={() => {
                    this.signUp()
                }}/>

            </View>
        )}



}