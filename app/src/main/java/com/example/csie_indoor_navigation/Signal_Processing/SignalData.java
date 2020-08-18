package com.example.csie_indoor_navigation.Signal_Processing;
import android.annotation.*;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class SignalData extends AppCompatActivity implements Comparable<SignalData> {
    private int sortWays = 0;
    private List<Integer> rssiList = new ArrayList<>();
    private List<Integer> rssiExcludedExtrmeValue = new ArrayList<>();
    private String uuid = null;
    private int parameter;

    public SignalData(String recievedUuid, int recievedRssi){
        uuid = recievedUuid;
        rssiList.add(recievedRssi);
    }

    public SignalData(String recievedUuid, int receivedRssi, int parameter){
        uuid = recievedUuid;
        rssiList.add(receivedRssi);
        parameter = parameter;
    }

    public void setUuid(String setedUuid){
        uuid = setedUuid;
    }

    public void setValue(int value){
        List<Integer> tmpList = new ArrayList<>();
        tmpList.add(value);
        tmpList.addAll(rssiList);
        rssiList.clear();
        rssiList.addAll(tmpList);
        tmpList.clear();

    }

    public void excludedExtrmeValue(){
        List<Integer> tmpList = new ArrayList<>();
        tmpList.addAll(rssiList);
        Collections.sort(tmpList);
        tmpList.remove(0);
        tmpList.remove(tmpList.size() - 1);
        rssiExcludedExtrmeValue.clear();
        rssiExcludedExtrmeValue.addAll(tmpList);
        tmpList.clear();
    }

    public String getUuid(){
        return uuid;
    }

    public String getWaypintName(String uuid){
        SharedPreferences languagePref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String language_option = languagePref.getString("language","繁體中文");
        String name = uuid;
        switch (uuid){
            case "00010015-0000-0010-1001-000000101001":
                if(language_option.equals("繁體中文"))
                    name = "營建系門口";
                else if(language_option.equals("English"))
                    name = "Exit of Civil and Construction Engineering";
                break;
            case "00010015-0000-0010-1002-000000101002":
                if(language_option.equals("繁體中文"))
                    name = "系辦";
                else if(language_option.equals("English"))
                    name = "Office of CSIE";
                break;
            case "00010015-0000-0010-1003-000000101003":
                if(language_option.equals("繁體中文"))
                    name = "EB109&EB110";
                else if(language_option.equals("English"))
                    name = "EB109&EB110";
                break;
            case "00010015-0000-0010-1004-000000101004":
                if(language_option.equals("繁體中文"))
                    name = "資工系門出口";
                else if(language_option.equals("English"))
                    name = "Exit of CSIE";
                break;
            case "00020015-0000-0010-1001-000000101001":
                if(language_option.equals("繁體中文"))
                    name = "EB208&EB209";
                else if(language_option.equals("English"))
                    name = "EB208&EB209";
                break;
            case "00020015-0000-0010-1002-000000101002":
                if(language_option.equals("繁體中文"))
                    name = "EB210";
                else if(language_option.equals("English"))
                    name = "EB210";
                break;
            case "00020015-0000-0010-1003-000000101003":
                if(language_option.equals("繁體中文"))
                    name = "EB211";
                else if(language_option.equals("English"))
                    name = "EB211";
                break;
            case "00020015-0000-0010-1004-000000101004":
                if(language_option.equals("繁體中文"))
                    name = "樓梯";
                else if(language_option.equals("English"))
                    name = "Stair";
                break;
            case "00020015-0000-0010-1005-000000101005":
                if(language_option.equals("繁體中文"))
                    name = "EB207&EB206";
                else if(language_option.equals("English"))
                    name = "EB207&EB206";
                break;
            case "00020015-0000-0010-1006-000000101006":
                if(language_option.equals("繁體中文"))
                    name = "EB202";
                else if(language_option.equals("English"))
                    name = "EB202";
                break;
            case "00020015-0000-0010-1007-000000101007":
                if(language_option.equals("繁體中文"))
                    name = "EB201";
                else if(language_option.equals("English"))
                    name = "EB201";
                break;
            default:
                name = uuid;
                break;
        }
        return name;
    }

    public int getRssi(int index){return rssiList.get(index);}

    public List getRssiList(){return rssiList;}

    public List getRssiListExcludeExtremeValue(){return rssiExcludedExtrmeValue;}

    public int getRssoListSize(){return rssiList.size();}

    public int getLatestRssi(){return rssiList.get(rssiList.size() - 1);}

    public int getCount(){
        int count = 0;
        for(int rssi : rssiList){
            count++;
        }
        return count;
    }

    public float getAvg(){
        int count = 0, sum = 0;
        for(int rssi : rssiList){
            sum += rssi;
            count++;
        }
        return sum / count;
    }

    public int getMedian(){
        int median = 0;
        List<Integer> tmpList = new ArrayList<>();
        tmpList.clear();
        tmpList.addAll(rssiList);
        Collections.sort(tmpList);
        median = tmpList.get(tmpList.size() / 2);
        return median;
    }

    public float getStandardDeviation(){
        float standardDeviation = 0, sum = 0;
        float avg = this.getAvg();
        for(int rssi : rssiList){
            standardDeviation = standardDeviation + (float)Math.pow(rssi - avg, 2);
        }
        standardDeviation = standardDeviation / this.getCount();
        standardDeviation = (float)Math.sqrt(standardDeviation);
        return standardDeviation;
    }

    public void setSortWays(int wayType){this.sortWays = wayType;}

    @Override
    public int compareTo(SignalData o) {
        switch (sortWays){
            case 1:
                if(this.getAvg() < o.getAvg())
                    return 1;
                else if(this.getAvg() > o.getAvg())
                    return -1;
                else
                    return 0;
            default:
                if(this.getCount() < o.getCount())
                    return 1;
                else if(this.getCount() > o.getCount())
                    return -1;
                else
                    return 0;
        }
    }
}
