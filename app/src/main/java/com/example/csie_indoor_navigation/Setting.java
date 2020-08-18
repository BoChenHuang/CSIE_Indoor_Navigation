package com.example.csie_indoor_navigation;

import android.app.Application;

public class Setting extends Application {

    static int preferenceValue = 2;
    static int modeValue = 3;
    static boolean turnOnOK = false;
    static String fileName = "buildingA.xml";

    public static int getPreferenceValue(){
        return preferenceValue;
    }

    public static int getModeValue(){
        return modeValue;
    }

    public static void setPreferenceValue(int value){
        preferenceValue = value;
    }

    public static void setModeValue(int value){
        modeValue = value;
    }

    public static String getFileName(){
        return fileName;
    }

    public static void setFileName(String name){
        fileName = name;
    }
}
