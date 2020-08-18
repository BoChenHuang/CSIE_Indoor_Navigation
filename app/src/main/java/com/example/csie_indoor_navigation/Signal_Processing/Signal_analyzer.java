package com.example.csie_indoor_navigation.Signal_Processing;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.LongSparseArray;
import android.widget.Filter;

import com.example.csie_indoor_navigation.GeoCalulation;
import com.example.csie_indoor_navigation.Node;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.xml.transform.dom.DOMLocator;

import static java.lang.Math.pow;
import static java.lang.Math.sin;

public class Signal_analyzer {
    private Queue<SignalData> weightedQueue = new LinkedList<>();
    private List<SignalData> signalDataList = new ArrayList<>();
    private int weightSize = 5;
    private boolean firstTime = true;
    private static float distance = 0;
    private static Node[] tmpPath;
    private static HashMap<String, Node> allWaypointData = new HashMap<>();
    private static DeviceParameter deviceParameter;
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

    public Signal_analyzer(Context context){
        deviceParameter = new DeviceParameter(context);
    }

    public void setPath(List<Node> path) {
        this.tmpPath = new Node[path.size()];
        for (int i = 0; i < path.size(); i++) {
            this.tmpPath[i] = path.get(i);
        }
    }

    public void setAllWaypointData(HashMap<String, Node> allWaypointData) {
        this.allWaypointData = allWaypointData;
    }

    public boolean isOurBeacon(String uuid)
    {
        boolean isOurBeacon = false;
        for(int i = 0; i < listOfUUID.length; i++){
            if(listOfUUID[i].equals(uuid))
                isOurBeacon = true;
        }
        return isOurBeacon;
    }

    public List<String> analyzingSignal(List<List<String>> listOfBeaconData, float remindRange, double offset) {

        List<String> puttedBeaconList = new ArrayList<>();
        signalDataList.clear();

        //Put the signal data into the list
        for (int i = 0; i < listOfBeaconData.size(); i++) {
            //New Beacon
            if (puttedBeaconList.indexOf(listOfBeaconData.get(i).get(0)) == -1) {
                puttedBeaconList.add(listOfBeaconData.get(i).get(0));
                signalDataList.add(new SignalData(listOfBeaconData.get(i).get(0),
                        Integer.parseInt(listOfBeaconData.get(i).get(1))));
            } else {
                //Update the current signal value
                signalDataList.get(puttedBeaconList.indexOf(listOfBeaconData.get(i).get(0)))
                        .setValue(Integer.parseInt(listOfBeaconData.get(i).get(1)));
            }
        }

        //Find the next Waypoint
        List<String> locationRange = new ArrayList<>();
        locationRange.add("");
        Log.i("signalDataList","orignal = " + signalDataList);
        Log.i("signalDataList","size = " + signalDataList.size());
        if (signalDataList.size() > 1) {
            for (int i = 0; i < signalDataList.size(); i++)
                signalDataList.get(i).setSortWays(1);//sort by avg
            Collections.sort(signalDataList);
            Log.i("signalDataList","sorted = " + signalDataList);

            List<Float> predictDifference = countDifference(signalDataList, remindRange, offset);
            Log.i("predictDifference","predicDifference = " + predictDifference);

            if(predictDifference != null && isOurBeacon(signalDataList.get(0).getUuid())){
                //Find the neighbor
                if(predictDifference.size() > 2){
                    //Count the current difference
                    float currentDifference = Math.abs(signalDataList.get(0).getAvg()
                                                        - signalDataList.get(Math.round(predictDifference.get(2))).getAvg());
                    //Average greater than the last signal
                    if(signalDataList.get(0).getAvg() > predictDifference.get(1)){
                        if(firstTime == true){
                            locationRange.add("close");
                            locationRange.add(signalDataList.get(0).getUuid());
                            firstTime = false;
                            Log.i("locationRange","return =  1");
                        }
                        else if(firstTime == false && currentDifference > predictDifference.get(0)){
                            locationRange.add("close");
                            locationRange.add(signalDataList.get(0).getUuid());
                            Log.i("locationRange","return =  2");
                        }
                    }
                    else {
                        locationRange.add("near");
                        locationRange.add(signalDataList.get(0).getUuid());
                        Log.i("locationRange","return =  3");
                    }
                }
                //Can't find the neighbor
                else {
                    int currentSignal = Math.round(signalDataList.get(0).getAvg());
                    int signalTreshold = (int)countQuadratic(signalDataList.get(0).getUuid(),3,offset);
                    if(currentSignal > signalTreshold){
                        locationRange.add("close");
                        locationRange.add(signalDataList.get(0).getUuid());
                        Log.i("locationRange","return =  4");
                    }
                    else {
                        locationRange.add("near");
                        locationRange.add(signalDataList.get(0).getUuid());
                        Log.i("locationRange","return =  5");
                    }
                }
            }
            else if(predictDifference == null && isOurBeacon(signalDataList.get(0).getUuid())){
                int currentSignal = Math.round(signalDataList.get(0).getAvg());
                int signalTreshold = (int)countQuadratic(signalDataList.get(0).getUuid(),3,offset);
                if(currentSignal > signalTreshold){
                    locationRange.add("close");
                    locationRange.add(signalDataList.get(0).getUuid());
                    Log.i("locationRange","return =  6");
                }
                else {
                    locationRange.add("near");
                    locationRange.add(signalDataList.get(0).getUuid());
                    Log.i("locationRange","return =  7" + " signalDataList.get(0) = " + signalDataList.get(0).getUuid() + " isOureBeacon = " + isOurBeacon(signalDataList.get(0).getUuid()));
                }
            }
        }
        else {
            if(isOurBeacon(signalDataList.get(0).getUuid()))
            {
                int currentSignal = Math.round(signalDataList.get(0).getAvg());
                Log.i("currentSignal","" + currentSignal);
                if(currentSignal > -60){
                    locationRange.add("close");
                    locationRange.add(signalDataList.get(0).getUuid());
                    Log.i("locationRange","return =  9");
                }
                else {
                    locationRange.add("near");
                    locationRange.add(signalDataList.get(0).getUuid());
                    Log.i("locationRange","return =  10" + "signalDataList.get(0) = " + signalDataList.get(0).getUuid() + " isOureBeacon = " + isOurBeacon(signalDataList.get(0).getUuid()));
                }
            }
        }
        Log.i("locationRange","" + locationRange);
        return locationRange;
    }


    public List<Float> countDifference(List<SignalData> signalDataList, float remindRange, double offset) {
        Node[] tmpNodeList = new Node[2];
        for(int i = 0; i < signalDataList.size(); i++)
        {
            if(isOurBeacon(signalDataList.get(i).getUuid()))
            {
                tmpNodeList[0] = allWaypointData.get(signalDataList.get(i).getUuid());
                break;
            }
        }


        tmpNodeList[0] = allWaypointData.get(signalDataList.get(0).getUuid());
        Log.i("tmpNode", "allWaypointData = " + allWaypointData);
        Log.i("tmpNode", "signalDataListaGet = " + signalDataList.get(0).getUuid());
        if(tmpNodeList[0] != null && isOurBeacon(tmpNodeList[0].getID()))
        {
            Log.i("tmpNode", "tmpNode[0] = " + tmpNodeList[0].getID());
            Log.i("tmpNode","neighbor = " + tmpNodeList[0].getNeighborIDs());
        }
        else if(tmpNodeList[0] == null)
        {
            Log.i("tmpNode","is not our beacon");
            return null;
        }
        List<String> neighborNodes = tmpNodeList[0].getNeighborIDs();
        boolean hasNeighbor = false;
        int index = Integer.MAX_VALUE;
        List<Float> returnValue = new ArrayList<>();
        
        //Find neighbors within the received signal
        for(int i = 1; i < signalDataList.size(); i++){
            for(String  neibor : neighborNodes){
                if(neibor.equals(signalDataList.get(i).getUuid())){
                    tmpNodeList[1] = allWaypointData.get(neibor);
                    Log.i("tmpNode","tmpNodeList[1] = " + tmpNodeList[1].getID());
                    index = i;
                    hasNeighbor = true;
                    break;
                }
                else 
                    tmpNodeList[1] = null;
            }
            if(hasNeighbor) 
                break;
        }
        
        //Count the difference between beacons
        if(signalDataList.size() > 1){
            if(tmpNodeList[0] != null && tmpNodeList[1] != null)
                distance = GeoCalulation.getDistance(tmpNodeList[0],tmpNodeList[1]);
            else 
                return null;
            Log.i("distanceBetweenBeacon","" + distance);
            returnValue.clear();
            double[] signalValueOfBeacons = new double[2];
            signalValueOfBeacons[0] = countQuadratic(tmpNodeList[0].getID(), remindRange, offset);
            signalValueOfBeacons[1] = countQuadratic(tmpNodeList[1].getID(), distance - remindRange, offset);

            //Difference of beacons
            returnValue.add((float)Math.abs(signalValueOfBeacons[0] - signalValueOfBeacons[1]));
            //The nearest beacon signal
            returnValue.add((float) signalValueOfBeacons[0]);
            //The index of nearest beacon
            returnValue.add((float) index);

            Log.i("returnValue","signalValueOfBeacons[0] = " + signalValueOfBeacons[0]);
            Log.i("returnValue","signalValueOfBeacons[1] = " + signalValueOfBeacons[1]);
            Log.i("returnValue","returnValue[0] = " + returnValue.get(0));
            Log.i("returnValue","returnValue[1] = " + returnValue.get(1));
            Log.i("returnValue","returnValue[2] = " + returnValue.get(2));
            return returnValue;
        }
        else
            return null;
    }

    private double countQuadratic(String uuid, float rangeOffset, double offset) {
       double aValue = deviceParameter.getAValue(uuid);
       double bValue = deviceParameter.getBValue(uuid);
       double cValue = deviceParameter.getCValue(uuid);
       double remindRange = deviceParameter.getParameters(uuid) + rangeOffset;
       Log.i("countQuadratic","a = " + aValue);
       Log.i("countQuadratic","b = " + bValue);
       Log.i("countQuadratic", "c = " + cValue);
       Log.i("countQuadratic", "remindRange = " + remindRange);

        // TODO: 2020/7/21 addd the difference type of the signal

       return aValue * pow(remindRange,2) + bValue * remindRange + cValue + offset;
    }
}
