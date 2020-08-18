package com.example.csie_indoor_navigation;

/*--

Module Name:

    GeoCalculation.java

Abstract:

    This module contains the methods to calculate
    the bearing and distance of two point with their
    latitude and longitude are given

Author:

    Phil Wu 01-Feb-2018

--*/


import android.util.Log;

import java.util.List;

public class GeoCalulation {

    private static final int RADIUS_OF_EARTH = 6371;
    private static final int FRONT_DIRECTION_SMALL_LOWER_BOUND = 0;
    private static final int FRONT_DIRECTION_SMALL_UPPER_BOUND = 10;
    private static final int FRONT_RIGHTSIDE_DIRECTION_BOUND = 40;
    private static final int RIGHT_DIRECTION_LOWER_BOUND = 70;
    private static final int RIGHT_DIRECTION_UPPER_BOUND = 110;
    private static final int BACK_DIRECTION_BOUND = 180;
    private static final int LEFT_DIRECTION_LOWER_BOUND = 250;
    private static final int LEFT_DIRECTION_UPPER_BOUND = 290;
    private static final int FRONT_LEFTSIDE_DIRECTION_BOUND = 320;
    private static final int FRONT_DIRECTION_BIG_LOWER_BOUND = 350;
    private static final int FRONT_DIRECTION_BIG_UPPER_BOUND = 360;



    //Get the bearing which is the direction of travel as displayed
    // on a compass when moving from point A to point B

    static int getBearingOfTwoPoints(Node A, Node B){

        double latA = Math.toRadians(A._lat); //latitude for Node A
        double longA = Math.toRadians(A._lon); //longitude for Node A
        double latB = Math.toRadians(B._lat); //latitude for Node B
        double longB = Math.toRadians(B._lon); //longitude for Node B

        double deltaLon = (longB - longA);

        double y = Math.sin(deltaLon) * Math.cos(latB);
        double x = Math.cos(latA) * Math.sin(latB) - Math.sin(latA)
                * Math.cos(latB) * Math.cos(deltaLon);

        double bearingFromAToB = Math.atan2(y, x);

        bearingFromAToB = Math.toDegrees(bearingFromAToB);
        bearingFromAToB = (bearingFromAToB + 360) % 360;

        //round the value of bearing to integer
        return (int) Math.rint(bearingFromAToB);
    }


    //Get the turn direction at Node B, when moving from Node A to Node B to Node C
    public static String getDirectionFromBearing(Node A, Node B, Node C){

        // direction result;
        String direction = null;

        // get bearing from A to B
        int bearingFromAToB = getBearingOfTwoPoints(A, B);

        // get bearing from B to C
        int bearingFromBToC = getBearingOfTwoPoints(B, C);

        // get the difference of two bearings
        int delta = bearingFromBToC - bearingFromAToB;

        Log.i("direction", "Delta A to B "+ bearingFromAToB);
        Log.i("direction", "Delta B to C"+ bearingFromBToC);
        Log.i("direction", "Delta "+ delta);
        // Make delta a positive number
        if(delta < 0)
            delta += 360;

        // Difference interval to determine the turn direction
        if(delta >= FRONT_DIRECTION_SMALL_LOWER_BOUND && delta <= FRONT_DIRECTION_SMALL_UPPER_BOUND)
            direction = "front";
        else if(delta >= FRONT_DIRECTION_SMALL_UPPER_BOUND && delta <= FRONT_RIGHTSIDE_DIRECTION_BOUND)
            direction = "frontRightSide";
        else if(delta >= FRONT_RIGHTSIDE_DIRECTION_BOUND && delta <= RIGHT_DIRECTION_LOWER_BOUND)
            direction = "frontRight";
        else if(delta >= RIGHT_DIRECTION_LOWER_BOUND && delta <= RIGHT_DIRECTION_UPPER_BOUND)
            direction = "right";
        else if(delta >= RIGHT_DIRECTION_UPPER_BOUND && delta <= BACK_DIRECTION_BOUND)
            direction = "rearRight";
        else if(delta >= BACK_DIRECTION_BOUND && delta <= LEFT_DIRECTION_LOWER_BOUND)
            direction = "rearLeft";
        else if(delta >= LEFT_DIRECTION_LOWER_BOUND && delta <= LEFT_DIRECTION_UPPER_BOUND)
            direction = "left";
        else if(delta >= LEFT_DIRECTION_UPPER_BOUND && delta <= FRONT_LEFTSIDE_DIRECTION_BOUND)
            direction = "frontLeft";
        else if(delta >= FRONT_LEFTSIDE_DIRECTION_BOUND && delta <= FRONT_DIRECTION_BIG_LOWER_BOUND)
            direction = "frontLeftSide";
        else if(delta >= FRONT_DIRECTION_BIG_LOWER_BOUND && delta <= FRONT_DIRECTION_BIG_UPPER_BOUND)
            direction = "front";

        return direction;
    }


    // Get the distance in meter between two vertices A and B with latitudes and longitudes given
    public static int getDistance(Node A, Node B) {

        Log.i("Node","Node A = " + A._waypointName);
        Log.i("Node","Node B = " + B._waypointName);
        double latA = A._lat; //latitude for Node A
        double lonA = A._lon; //longitude for Node A
        double latB = B._lat; //latitude for Node B
        double lonB = B._lon; //longitude for Node B

        double latDistance = Math.toRadians(latB - latA);
        double lonDistance = Math.toRadians(lonB - lonA);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(latA)) * Math.cos(Math.toRadians(latB))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = RADIUS_OF_EARTH * c * 1000; // convert to meters

        //round the value of distance to integer
        return (int) Math.rint(distance);
    }

    public static int getPathLength(List<Node> navigationPath){

        int length = 0;

        for(int i=0; i<navigationPath.size()-1; i++)
            length += getDistance(navigationPath.get(i), navigationPath.get(i+1));

        return length;
    }
}

