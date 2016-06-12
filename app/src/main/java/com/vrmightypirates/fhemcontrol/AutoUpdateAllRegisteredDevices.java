package com.vrmightypirates.fhemcontrol;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Boke on 09.06.2016.
 */
public class AutoUpdateAllRegisteredDevices implements CommunicateWithFhemTelnet.OnMassageFromFhem{

    private static final String TAG = AutoUpdateAllRegisteredDevices.class.getSimpleName();
    private boolean autoUpdateIsRunning = false;
    private ArrayList<FhemDevice> deviceList;
    private  FhemMessageParser fhemParser = new FhemMessageParser();
    private FhemCommunication  fhemCommunication = new FhemCommunication();

    public AutoUpdateAllRegisteredDevices() {
        deviceList = new ArrayList<FhemDevice>();
    }

    public boolean addDeviceToUpdateListener(FhemDevice device) {

        deviceList.add(device);
        return true;
    }


    public boolean autoUpdateAllRegisteredDevices(ArrayList<FhemDevice> deviceList, ConnectionType connectionType) {

        this.deviceList = deviceList;
        StringBuilder message = new StringBuilder();

        if(autoUpdateIsRunning == true){
            fhemCommunication.communicateWithFhemTelnet.cancel(true);
        }

        message.append("inform on ");

        for (FhemDevice object: deviceList) {
            message.append(object.getDeviceName());
        }

        Log.i(TAG, "autoUpdateAllDevices: "+ message);
        switch (connectionType) {
            case http:
                break;
            case telnet:
                fhemCommunication.communicateWithFhemTelnet = new CommunicateWithFhemTelnet(this, message.toString(),false);
                break;
            default:
                return false;
        }

        autoUpdateIsRunning = true;
        return true;
    }

    private boolean parseFhemMessage(String messageFromFhem, boolean singleResponse){

            fhemParser.parseMessage(messageFromFhem,this.deviceList);


        return true;
    }

    public FhemMessageParser getFhemParser() {
        return fhemParser;
    }

    @Override
    public void onMessageFromFhemReceived(String messageFromFhem, boolean singleResponse) {
        parseFhemMessage(messageFromFhem, singleResponse);
        Log.i(TAG, "onMessageFromFhemReceived: "+messageFromFhem);

    }

}
