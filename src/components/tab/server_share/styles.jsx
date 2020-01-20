import { StyleSheet } from 'react-native';

export default styles = StyleSheet.create({

    container: {
        flex: 1,
        flexDirection: "column",
        alignContent: "center",
        justifyContent: "center",
        padding: 15
    },

    dataContainer: {
        flexDirection: "column",
        alignContent: "center",
        justifyContent: "center",
        padding: 15
    },
    dataPackage:{
        flexDirection: "row",
        justifyContent: "space-between",
        margin: 5
    },
    
    dataField: {
        fontSize: 20
    },

    package:{
        flex: 1,
        flexDirection: "column",
    },

    tabHeader: {
        alignContent: "center",
        textAlign: "center",
        justifyContent: "center",
        fontWeight: "bold",
        fontSize: 20,
        color: "black",
        marginTop: 10
    },

    buttonContainer: {
        flexDirection: "column",
        justifyContent: "space-around"
    },

    buttonEnabled: {
        height: 35,
        borderRadius: 2,
        backgroundColor: "dodgerblue",
        shadowColor: 'rgba(0, 0, 0, 0.1)',
        shadowOpacity: 0.8,
        elevation: 6,
        shadowRadius: 15,
        shadowOffset : { width: 1, height: 5}
    },

    buttonText: {
        fontSize: 14,
        color: "white",
        textAlign: "center",
        paddingTop: 7
    },

    spacing: { 
        padding: 15
    }

});