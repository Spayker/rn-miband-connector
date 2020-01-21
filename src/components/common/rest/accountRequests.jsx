import React from 'react'
import globals from "../../common/globals.jsx";
import {AsyncStorage} from 'react-native';

export default class AccountRequests extends React.Component {

    signUp = (username, password) => {
        console.log('Account signUp: ' + username + ' ' + password)
        return fetch('http://' + globals.SERVER_ACCOUNT_URL_ADDRESS + '/accounts/', {
            method: 'POST',
            headers: {
                Accept: 'application/json',
                        'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        })
        .then((response) => response.json())
        .then((responseJson) => {
            console.log('account signUp: ' + responseJson)
            this.getAccessToken(username, password)
        })
        .catch((error) => { console.error(error) });
    }

    getAccessToken = (username, password) => {
        console.log('Account getAccessToken: ' + username + ' ' + password)
        var details = {
            "scope": "ui",
            "username": username,
            "password": password,
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
            console.log('account getAccessToken: ' + responseJson)
            this.storeData(responseJson.access_token, username) 
        })
        .catch((error) => { console.error(error) });
    }

    storeData = async (userToken, username) => {
        try {
            let multiDataSet = [
                [globals.ACCESS_TOKEN_KEY, userToken],
                [globals.USERNAME_TOKEN_KEY, username],
            ];
            await AsyncStorage.multiSet(multiDataSet);
        } catch (error) { console.log('couldn\'t save user access token to storage because of: ' + error) }
    };
}