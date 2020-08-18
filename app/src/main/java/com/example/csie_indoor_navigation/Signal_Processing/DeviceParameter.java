/* Project Name :
        CSIE Indoor Navigation

    File Name :
        DeviceParameter.java

    File Desciption :
        This class is the file reader to read the json file ot the waypoint parameter.

    Author :
        BoChenHuang, rf9440817@gmail.com
 */
package com.example.csie_indoor_navigation.Signal_Processing;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import com.example.csie_indoor_navigation.Node;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeviceParameter {
    private Context context;
    private JSONArray jsonArray = new JSONArray();
    private static HashMap<String, Node> allWaypointData = new HashMap<>();
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


    public DeviceParameter(Context context){
        this.context = context;
        this.jsonArray = readDeviceParameters(context);
        Log.i("jsonArray", "" + jsonArray);
    }

    public void setAllWaypointData(HashMap<String, Node> waypointData){
        this.allWaypointData = waypointData;
    }

    public JSONArray readDeviceParameters(Context context){
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open("DeviceParamation.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String parameterText = new String(buffer, "UTF-8");
            try {
                jsonArray = new JSONArray(parameterText);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //If the file does not have beacon data, set the default value
        jsonArray = setDefaultValue(jsonArray);
        return jsonArray;
    }

    public JSONArray setDefaultValue(JSONArray jsonArray){

        List<String> allWaypointUUID  = new ArrayList<>();
        List<String> inFileWaypointUUID = new ArrayList<>();


        for(Node node : allWaypointData.values()){
            allWaypointUUID.add(node.getID());
        }

        for(int i = 0; i < jsonArray.length(); i++){
            try {
                JSONObject waypoiunt = jsonArray.getJSONObject(i);
                inFileWaypointUUID.add(waypoiunt.getString("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        allWaypointUUID.removeAll(inFileWaypointUUID);

        if(!allWaypointUUID.isEmpty()){
            for(Node node : allWaypointData.values()){
                for(String uuid : allWaypointUUID){
                    if(uuid.equals(node.getID())){
                        JSONObject addObject = new JSONObject();
                        try {
                            addObject.put("id",node.getID());
                            addObject.put("parameter", 0.0);
                            addObject.put("a", 0.1175);
                            addObject.put("b", -3.4473);
                            addObject.put("c",-49.029);
                            jsonArray.put(addObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return jsonArray;
    }

    public double getParameters(String uuid){
        /*This function is to get the notification distance of the waypoint.
                   The  notification distance is the parameter value + 3.
                   ex. -1 = 2m, 0 = 3m, 1 = 4m
                */
        JSONObject object = null;
        for(int i = 0; i < jsonArray.length(); i++){
            try {
                object = jsonArray.getJSONObject(i);
                if(object.getString("id").equals(uuid))
                    return object.getDouble("parameter");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public double getAValue(String uuid){
        /*This function is to get the a of the quadratic
                   Quadratic = ax^2 + bx + c
                 */
        JSONObject object = null;
        for(int i = 0; i <  jsonArray.length(); i ++){
            try {
                object = jsonArray.getJSONObject(i);
                if(object.getString("id").equals(uuid))
                    return object.getDouble("a");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public double getBValue(String uuid){
        /*This function is to get the b of the quadratic
                   Quadratic = ax^2 + bx + c
                 */
        JSONObject object = null;
        for(int i = 0; i <  jsonArray.length(); i ++){
            try {
                object = jsonArray.getJSONObject(i);
                if(object.getString("id").equals(uuid))
                    return object.getDouble("b");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public double getCValue(String uuid){
        /*This function is to get the c of the quadratic
                   Quadratic = ax^2 + bx + c
                 */
        JSONObject object = null;
        for(int i = 0; i <  jsonArray.length(); i ++){
            try {
                object = jsonArray.getJSONObject(i);
                if(object.getString("id").equals(uuid))
                    return object.getDouble("c");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public Boolean isOurBeacon(String uuid){
        Log.i("locationRange","uuid = " + uuid);
        /*for(int i = 0; i < jsonArray.length(); i++){
            try {
                Log.i("match","inFileUUID = " + jsonArray.getJSONObject(i).getString("id") +"\n" + "searchingUUID = " + uuid);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                if(jsonArray.getJSONObject(i).getString("id").equals(uuid))
                {
                    Log.i("isOurBeacon","true");
                    returnValue = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/
        boolean isOurBeacon = false;
        for(int i = 0; i < listOfUUID.length; i++){
            if(listOfUUID[i].equals(uuid))
                isOurBeacon = true;
        }
        return isOurBeacon;
    }
}
