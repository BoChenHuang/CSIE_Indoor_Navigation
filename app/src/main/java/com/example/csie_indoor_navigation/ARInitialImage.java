package com.example.csie_indoor_navigation;

import android.content.Context;
import android.util.Log;

public class ARInitialImage {
    int predictDirection;
    Context context;
    public ARInitialImage(Context context)
    {
        predictDirection = -1;
        this.context = context;
    }

    public int getPredict(String nowWaypointID, String nextWaypointID)
    {
        int predict = -1;
        switch (nowWaypointID){
            //A1
            case "00010015-0000-0010-1001-000000101001":
                if(nextWaypointID.equals(context.getString(R.string.UUID_of_A2)))
                    predict = 0;
                break;

            //A2
            case "00010015-0000-0010-1002-000000101002":
                if(nextWaypointID.equals(context.getString(R.string.UUID_of_A1)))
                    predict = 4;
                else if (nextWaypointID.equals(context.getString(R.string.UUID_of_A3)))
                    predict = 2;
                else if(nextWaypointID.equals(context.getString(R.string.UUID_of_A4)))
                    predict = 6;
                break;

            //A3
            case "00010015-0000-0010-1003-000000101003":
                if(nextWaypointID.equals(context.getString(R.string.UUID_of_A2)))
                    predict = 6;
                else if(nextWaypointID.equals(context.getString(R.string.UUID_of_B1)))
                    predict = 4;
                break;

            //A4
            case "00010015-0000-0010-1004-000000101004":
                if(nextWaypointID.equals(context.getString(R.string.UUID_of_A2)))
                    predict = 2;
                else if(nextWaypointID.equals(context.getString(R.string.UUID_of_B4)))
                    predict = 4;
                break;

            //B1
            case "00020015-0000-0010-1001-000000101001":
                if(nextWaypointID.equals(context.getString(R.string.UUID_of_A3)))
                    predict = 4;
                else if(nextWaypointID.equals(context.getString(R.string.UUID_of_B2)))
                    predict = 6;
                break;

            //B2
            case "00020015-0000-0010-1002-000000101002":
                if(nextWaypointID.equals(context.getString(R.string.UUID_of_B1)))
                    predict = 2;
                else if(nextWaypointID.equals(context.getString(R.string.UUID_of_B3)))
                    predict = 6;
                else if(nextWaypointID.equals(context.getString(R.string.UUID_of_B5)))
                    predict = 4;
                break;
            //B3
            case "00020015-0000-0010-1003-000000101003":
                if(nextWaypointID.equals(context.getString(R.string.UUID_of_B2)))
                    predict = 2;
                else if(nextWaypointID.equals(context.getString(R.string.UUID_of_B4)))
                    predict = 6;
                break;

            //B4
            case "00020015-0000-0010-1004-000000101004":
                if(nextWaypointID.equals(context.getString(R.string.UUID_of_B3)))
                    predict = 2;
                else if(nextWaypointID.equals(context.getString(R.string.UUID_of_A4)))
                    predict = 4;
                break;

            //B5
            case "00020015-0000-0010-1005-000000101005":
                if(nextWaypointID.equals(context.getString(R.string.UUID_of_B2)))
                    predict = 0;
                else if(nextWaypointID.equals(context.getString(R.string.UUID_of_B6)))
                    predict = 4;
                break;

            //B6
            case "00020015-0000-0010-1006-000000101006":
                Log.i("B6_case","nextWaypointID = " + nextWaypointID);
                if(nextWaypointID.equals(context.getString(R.string.UUID_of_B5)))
                    predict = 0;
                else if(nextWaypointID.equals(context.getString(R.string.UUID_of_B7)))
                    predict = 6;
                break;
            //B7
            case "00020015-0000-0010-1007-000000101007":
                if(nextWaypointID.equals(context.getString(R.string.UUID_of_B6)))
                    predict = 2;
                break;
        }
        return predict;
    }

    int deciseImageToShow(String nowWaypointID, String nextWaypointID){
        int image = 0;
        switch (nowWaypointID){
            //A1
            case "00010015-0000-0010-1001-000000101001":
                if(nextWaypointID.equals(context.getString(R.string.UUID_of_A2)))
                {
                    predictDirection = 0;
                    image = R.drawable.a1_2;
                }
                break;

            //A2
            case "00010015-0000-0010-1002-000000101002":
                if(nextWaypointID.equals(context.getString(R.string.UUID_of_A1)))
                {
                    predictDirection = 4;
                    image = R.drawable.a2_1;
                }
                else if (nextWaypointID.equals(context.getString(R.string.UUID_of_A3)))
                {
                    predictDirection = 2;
                    image = R.drawable.a2_3;
                }
                else if(nextWaypointID.equals(context.getString(R.string.UUID_of_A4)))
                {
                    predictDirection = 6;
                    image = R.drawable.a2_2;
                }
                break;

            //A3
            case "00010015-0000-0010-1003-000000101003":
                if(nextWaypointID.equals(context.getString(R.string.UUID_of_A2)))
                {
                    predictDirection = 6;
                    image = R.drawable.a3_2;
                }
                else if(nextWaypointID.equals(context.getString(R.string.UUID_of_B1)))
                {
                    predictDirection = 4;
                    image = R.drawable.a3_1;
                }
                break;

            //A4
            case "00010015-0000-0010-1004-000000101004":
                if(nextWaypointID.equals(context.getString(R.string.UUID_of_A2)))
                {
                    predictDirection = 2;
                    image = R.drawable.a4_3;
                }
                else if(nextWaypointID.equals(context.getString(R.string.UUID_of_B4)))
                {
                    predictDirection = 4;
                    image = R.drawable.a4_2;
                }
                break;

            //B1
            case "00020015-0000-0010-1001-000000101001":
                if(nextWaypointID.equals(context.getString(R.string.UUID_of_A3)))
                {
                    predictDirection = 4;
                    image = R.drawable.b1_2;
                }
                else if(nextWaypointID.equals(context.getString(R.string.UUID_of_B2)))
                {
                    predictDirection = 6;
                    image = R.drawable.b1_1;
                }
                break;

            //B2
            case "00020015-0000-0010-1002-000000101002":
                if(nextWaypointID.equals(context.getString(R.string.UUID_of_B1)))
                {
                    predictDirection = 2;
                    image = R.drawable.b2_1;
                }
                else if(nextWaypointID.equals(context.getString(R.string.UUID_of_B3)))
                {
                    predictDirection = 6;
                    image = R.drawable.b2_2;
                }
                else if(nextWaypointID.equals(context.getString(R.string.UUID_of_B5)))
                {
                    predictDirection = 4;
                    image = R.drawable.b2_3;
                }
                break;
            //B3
            case "00020015-0000-0010-1003-000000101003":
                if(nextWaypointID.equals(context.getString(R.string.UUID_of_B2)))
                {
                    predictDirection = 2;
                    image = R.drawable.b3_2;
                }
                else if(nextWaypointID.equals(context.getString(R.string.UUID_of_B4)))
                {
                    predictDirection = 6;
                    image = R.drawable.b3_1;
                }
                break;

            //B4
            case "00020015-0000-0010-1004-000000101004":
                if(nextWaypointID.equals(context.getString(R.string.UUID_of_B3)))
                {
                    predictDirection = 2;
                    image = R.drawable.b4_1;
                }
                else if(nextWaypointID.equals(context.getString(R.string.UUID_of_A4)))
                {
                    predictDirection = 4;
                    image = R.drawable.b4_2;
                }
                break;

            //B5
            case "00020015-0000-0010-1005-000000101005":
                if(nextWaypointID.equals(context.getString(R.string.UUID_of_B2)))
                {
                    predictDirection = 0;
                    image = R.drawable.b5_1;
                }
                else if(nextWaypointID.equals(context.getString(R.string.UUID_of_B6)))
                {
                    predictDirection = 4;
                    image = R.drawable.b5_2;
                }
                break;

            //B6
            case "00020015-0000-0010-1006-000000101006":
                Log.i("B6_case","nextWaypointID = " + nextWaypointID);
                if(nextWaypointID.equals(context.getString(R.string.UUID_of_B5)))
                {
                    predictDirection = 0;
                    image = R.drawable.b6_1;
                }
                else if(nextWaypointID.equals(context.getString(R.string.UUID_of_B7)))
                {
                    predictDirection = 6;
                    image = R.drawable.b6_2;
                }
                break;
            //B7
            case "00020015-0000-0010-1007-000000101007":
                if(nextWaypointID.equals(context.getString(R.string.UUID_of_B6)))
                {
                    predictDirection = 2;
                    image = R.drawable.b7_1;
                }
                break;
        }
        return image;
    }
}
