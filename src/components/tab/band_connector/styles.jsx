import { StyleSheet } from 'react-native';

export default styles = StyleSheet.create({

    container: {
        flex: 1,
        flexDirection: "column",
        alignContent: "center",
        justifyContent: "center",
        padding: 15
    },
    package:{
        flexDirection: "row",
        justifyContent: "space-between",
        margin: 5
    },
    sensorField: {
        fontSize: 20
    },
    buttonContainer: {
        flexDirection: "column",
        justifyContent: "space-around"
    },
    spacing: {
        padding: 5
    },
    buttonEnabled: {
        height: 35,
        borderRadius: 2,
        backgroundColor: "dodgerblue",
        shadowColor: 'rgba(0, 0, 0, 0.1)',
        shadowOpacity: 0.8,
        elevation: 6,
        shadowRadius: 15 ,
        shadowOffset : { width: 1, height: 5}
    },
    buttonDisabled: {
        height: 35,
        borderRadius: 2,
        backgroundColor: "grey",
        shadowColor: 'rgba(0, 0, 0, 0.1)',
        shadowOpacity: 0.8,
        elevation: 6,
        shadowRadius: 15 ,
        shadowOffset : { width: 1, height: 5}
    },
    buttonText: {
        fontSize: 14,
        color: "white",
        textAlign: "center",
        paddingTop: 7
    },

    listTrainingContainer:{
        flex: 1,
        flexDirection: "row",
        alignItems: 'center',
        justifyContent: "space-around",
    },


});