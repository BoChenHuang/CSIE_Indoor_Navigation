package com.example.csie_indoor_navigation.Signal_Processing;

import android.content.Context;
import android.util.Log;
import com.example.csie_indoor_navigation.Node;
import org.altbeacon.beacon.Beacon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Signal_Processor {
    private List<String> reserchData = new ArrayList<>();
    private Signal_analyzer signalAnalyzer;
    private List<List<String>> beaconDataQueue = new LinkedList<>();
    int counter;
    String lastNodeID = "";
    private long startTime = System.currentTimeMillis();
    private DeviceParameter deviceParameter;
    private final String listOfUUID[] = {  "00010015-0000-0010-1001-000000101001"
            ,"00010015-0000-0010-1002-000000101002"
            ,"00010015-0000-0010-1003-000000101003"
            ,"00010015-0000-0010-1004-000000101004"
            ,"00020015-0000-0010-1001-000000101001"
            ,"00020015-0000-0010-1002-000000101002"
            ,"00020015-0000-0010-1003-000000101003"
            ,"00020015-0000-0010-1004-000000101004"
            ,"00020015-0000-0010-1005-000000101005"
            ,"00020015-0000-0010-1006-000000101006"
            ,"00020015-0000-0010-1007-000000101007" };

    public Signal_Processor(Context context){
        deviceParameter = new DeviceParameter(context);
        signalAnalyzer = new Signal_analyzer(context);
    }

    public void setPath(List<Node> nodeList){
        signalAnalyzer.setPath(nodeList);
    }

    public void setAllWaypointData(HashMap<String, Node> allWaypointData){
        signalAnalyzer.setAllWaypointData(allWaypointData);
        deviceParameter.setAllWaypointData(allWaypointData);
    }

    public boolean isOurBeacon(String uuid){
        boolean isOurBeacon = false;
        for(int i = 0; i < listOfUUID.length; i++){
            if(listOfUUID[i].equals(uuid))
                isOurBeacon = true;
        }
        return isOurBeacon;
    }

    public List<String> getLocation(Beacon beacon, float remindRange, double offset, String lastNode, Context context){

        Log.i("beaconList" ,"beacon = " + beacon);
        //Bluetooth signal data
        String[]  beaconData = new String[]{
                beacon.getId1().toString(),
                beacon.getId2().toString(),
                beacon.getId3().toString(),
                String.valueOf(beacon.getRssi()),
                String.valueOf(beacon.getDistance()),
                String.valueOf(beacon.getBeaconTypeCode()),
                String.valueOf(beacon.getIdentifiers())
        };
        //Get the uuid
        String hexString = beaconData[0].concat(beaconData[1]);
        hexString = hexString.substring(2, 26).concat(hexString.substring(28, 36));
        String uuid = hexString.toUpperCase();
        uuid = uuid.substring(0, 8) + "-"
                + uuid.substring(8, 12) + "-"
                + uuid.substring(12, 16) + "-"
                + uuid.substring(16, 20) + "-"
                + uuid.substring(20, 32);
        Log.i("UUID","uuid = " + uuid);
        reserchData.clear();
        reserchData.add(uuid);
        //Combine uuid and rssi
        List<String> signalData = Arrays.asList(uuid, beaconData[3]);

        if(!lastNodeID.equals(lastNode)){
            counter = 0;
            lastNodeID = lastNode;
        }
        Log.i("isOurBeacon","" + isOurBeacon(uuid));
        if(isOurBeacon(uuid)){

            beaconDataQueue.add(signalData);
            long endTime = System.currentTimeMillis();
            if(endTime - startTime > 1000){
                counter ++;
                startTime = System.currentTimeMillis();
                reserchData.addAll(signalAnalyzer.analyzingSignal(beaconDataQueue,remindRange, offset));
                beaconDataQueue.clear();
                Log.i("isOurBeacon","" + reserchData);
                return reserchData;
            }
        }
        else
            Log.i("reserchData","is not our beacon");
        if(reserchData.size() > 1)
            Log.i("reserchData","" + reserchData);
        Log.i("reserchData","" + reserchData);
        return reserchData;
    }
}
