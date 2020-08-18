/* Project Name :
        CSIE Indoor Navigation

    File Name :
        WriteReadFile.java

    File Desciption :
        This class is the file reader to read the json file ot the waypoint parameter.

    Author :
        BoChenHuang, rf9440817@gmail.com
 */
package com.example.csie_indoor_navigation.Signal_Processing;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class WriteReadFile {
    private File file;
    private static String fileName = "Log";
    private static boolean lock = false;
    private AssetManager assetManager;
    public void WriteReadFile(Context context){
        assetManager = context.getAssets();
    }

    public JSONArray readDeviceParameters(Context context){
        JSONArray jsonArray = null;
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
        return jsonArray;
    }
}
