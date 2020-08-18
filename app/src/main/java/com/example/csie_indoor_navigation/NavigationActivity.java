package com.example.csie_indoor_navigation;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Entity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.util.LocaleData;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.csie_indoor_navigation.Signal_Processing.DeviceParameter;
import com.example.csie_indoor_navigation.Signal_Processing.Signal_Processor;
import com.viro.core.internal.annotation.BridgeOnly;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;

import java.net.Inet4Address;
import java.net.StandardSocketOptions;
import java.sql.SQLTransactionRollbackException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.ToDoubleBiFunction;

import static com.example.csie_indoor_navigation.GeoCalulation.getDirectionFromBearing;
import static com.example.csie_indoor_navigation.Setting.getPreferenceValue;

public class NavigationActivity extends AppCompatActivity implements BeaconConsumer {
    private static final int NORMAL_WAYPOINT = 0;
    private static final int ELEVATOR_WAYPIOINT = 1;
    private static final int STAIRWELL_WAYPOINT = 2;
    private static final int CONNECTPOINT = 3;
    private static final int ARRIVED_NOTIFIER = 0;
    private static final int WRONGWAY_NOTIFIER = 1;
    private static final int MAKETURN_NOTIFIER = 2;

    private static final int VIRTUAL_UP = 1;
    private static final int VIRTUAL_DOWN = 2;

    private static final String FRONT = "front";
    private static final String FRONT_RIGHTSIDE = "frontRightSide";
    private static final String FRONT_LEFTSIDE = "frontLeftSide";
    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final String FRONT_LEFT = "frontLeft";
    private static final String FRONT_RIGHT = "forntRight";
    private static final String REAR_LEFT = "rearLeft";
    private static final String REAR_RIGHT = "rearRight";
    private static final String ELEVATOR = "elevator";
    private static final String STAIR = "stair";
    private static final String ARRIVED = "arrived";
    private static final String WRONG = "wrong";

    private String TITLE;
    private String NOW_GO_STRAIGHT_RIGHTSIDE;
    private String NOW_GO_STRAIGHT_LEFTSIDE;
    private String NOW_GO_STRAIGHT;
    private String NOW_TURN_LEFT;
    private String NOW_TURN_RIGHT;
    private String NOW_TURN_FRONT_LEFT;
    private String NOW_TURN_FRONT_RIGHT;
    private String NOW_TURN_REAR_LEFT;
    private String NOW_TURN_REAR_RIGHT;
    private String NOW_TAKE_ELEVATOR;
    private String NOW_WALK_UP_STAIR;
    private String NOW_WALK_DOWN_STAIR;

    private String GO_STRAIGHT_ABOUT;
    private String THEN_GO_STRAIGHT;
    private String THEN_GO_STRAIGHT_RIGHTSIDE;
    private String THEN_GO_STRAIGHT_LEFTSIDE;
    private String THEN_TURN_LEFT;
    private String THEN_TURN_RIGHT;
    private String THEN_TURN_FRONT_LEFT;
    private String THEN_TURN_FRONT_RIGHT;
    private String THEN_TURN_REAR_LEFT;
    private String THEN_TURN__REAR_RIGHT;
    private String THEN_TAKE_ELEVATOR;
    private String THEN_WALK_UP_STAIR;
    private String THEN_WALK_DOWN_STAIR;
    private String WAIT_FOR_ELEVATOR;
    private String WALKING_UP_STAIR;
    private String WALKING_DOWN_STAIR;

    private String YOU_HAVE_ARRIVE;
    private String GET_LOST;
    private String METERS;
    private String PLEASE_GO_STRAIGHT;
    private String PLEASE_GO_STRAIGHT_RIGHTSIDE;
    private String PLEASE_GO_STRAIGHT_LEFTSIDE;
    private String PLEASE_TURN_LEFT;
    private String PLEASE_TURN_RIGHT;
    private String PLEASE_TURN_FRONT_LEFT;
    private String PLEASE_TURN_FRONT_RIGHT;
    private String PLEASE_TURN_REAR_LEFT;
    private String PLEASE_TURN_REAR_RIGHT;
    private String PLEASE_TAKE_ELEVATOR;
    private String PLEASE_WALK_UP_STAIR;


    private String TO;
    private String TO_FIRST_FLOOR;
    private String TO_SECOND_FLOOR;
    private String FLOOR;

    private String REROUTING;
    private String TURN_AROUND;
    private String WAIT_FOR_SIGNAL;
    private String THEN_ARRIVE;

    private String PRESENT_POSITION;
    private String DESTINATION;


    Node tmpNode;
    Node recordBeacon;
    int error_count = 0;
    int turnback_count = 0;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private long startT = System.currentTimeMillis();
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    String sourceID, destinationID, sourceRegion, destinationRegion,
           destinationName,tmpdestinationID, tmpdestinationRegion;
    String currentLocationName;
    boolean isFirstBeacon = true;
    boolean firstTurn = true;
    boolean isInVirtualNode = false;
    boolean lastisSlash = false;
    boolean directionCompute = false;
    boolean jumpNode = false;
    boolean arriveInWrong = false;
    boolean isLongerPath = false;
    boolean callDirectionInWrong = false;
    boolean calibration =false;

    Node startNode;
    Node endNode;
    Node lastNode;
    Node wrongWaypoint;
    Node choseStartNode;

    int walkedWaypoint = 0;
    int pathLength = 0;
    int regionIndex = 0;
    int passedGroupID = -1;
    String passedRegionID;
    List<String> tmpDestinationID = new ArrayList<>();

    //GyroSensor
    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private SensorEventListener gyroscopeEventListener;

    //A list of NavigationSubgraph object representing a Navigation Graph
    List<NavigationSubgraph> navigationGraph = new ArrayList<>();
    List<NavigationSubgraph> navigationGrpahForAllWaypoint = new ArrayList<>();
    
    //A list of Region object storing thge information of regions that will be traveled through
    List<Region>regionPath = new ArrayList<>();

    //Hashmap for storing region data
    RegionGraph regionGraph = new RegionGraph();

    //A list of Node object representing a navigation path
    List<Node> navigationPath = new ArrayList<>();
    List<Node> virtualNodeUP = new ArrayList<>();
    List<Node> virtualNodeDown = new ArrayList<>();


    HashMap<String, String> navigationPath_ID_to_Name_Mapping = new HashMap<>();
    HashMap<String, String> mappingOfRegionNameAndID = new HashMap<>();
    HashMap<String, Node> allWaypointData = new HashMap<>();

    //Reminder for destination and current location
    TextView destinationReminder, currentLocationReminder;
    //Textual navigation instruction
    TextView firstMovement, howFarToMove, nextTurnMovement, nowDoInstruction;
    //Graphical navigation indicator
    ImageView imageTurnIndicator;

    //Indicator for popupwindow notifiying user to make a turn at each waypoint
    String turnNotificationForPopup = null;

    //Voice engine for vocal navigation instruction
    private TextToSpeech textToSpeech;

    //Popup window
    private PopupWindow popupWindow;
    private LinearLayout postionOfPopup;

    //Beacon Manager
    private BeaconManager beaconManager;
    private org.altbeacon.beacon.Region region;

    //Thread for handling LBeacon ID while in a navigation rour
    Thread threadForHandleLbeaconID;
    //Handler for "threadForHandlerLbeaconID", receive message from the thread
    static Handler instructionHandler, currentPositionHandler, walkedPointHandler, progressHandler;

    // synchronization between Lbeacon receiver and handler thread
    final Object sync = new Object();

    //String for storing currently received LBeaconID
    String currentLBeaconID = "EmptyString";

    //Draw panel to display navigation progress bar
    Paint paint = new Paint();
    Bitmap myBitmap;
    Bitmap workingBitmap;
    Bitmap mutableBitmap;
    Canvas canvas;
    //ProgressBar
    ProgressBar progressBar;
    TextView progressNumber;
    int progressStatus = 0;
    int whichWaypointOnProgressBar = 0;

    //Signal Processor
    private Signal_Processor signalProcessor;
    private DateFormat dateFormat = new SimpleDateFormat("yy_MM_DD_hh_mm");
    private DeviceParameter deviceParameter;
    String receiveBeacon;
    double offset;
    String lastProcessedBeacon = "";

    private void unbindBeaconManager(){
        beaconManager.removeAllMonitorNotifiers();
        beaconManager.removeAllRangeNotifiers();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, org.altbeacon.beacon.Region region) {
                if(firstTurn == true && calibration == false){
                    long endTime = System.currentTimeMillis();
                    if(endTime - startT > 8000){
                        offset = offset - 5;
                        calibration = true;
                    }
                }

                if(beacons.size() > 0){
                    Iterator<Beacon> beaconIterator = beacons.iterator();
                    while(beaconIterator.hasNext()){
                        Beacon beacon = beaconIterator.next();
                        logBeaconData(signalProcessor.getLocation(beacon,3,offset,lastProcessedBeacon,NavigationActivity.this));
                    }
                }
            }
        });
        try {
            beaconManager.startRangingBeaconsInRegion(new org.altbeacon.beacon.Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void loadNavigationGraph(){
        regionGraph = DataParser.getRegionDataFromRegionGraph(this);
        mappingOfRegionNameAndID = DataParser.waypointNameAndIDMappings(this,regionGraph.getAllRegionNames());
        regionPath = regionGraph.getRegionPath(sourceRegion,destinationRegion);

        List<String> regionPathID = new ArrayList<>();

        for(int i = 0; i < regionPath.size(); i++)
            regionPathID.add(regionPath.get(i)._regionName);

        navigationGraph = DataParser.getWaypointDataFromNavigationGraph(this,regionPathID);

        startNode = navigationGraph.get(0).nodesInSubgraph.get(sourceID);
        endNode = navigationGraph.get(navigationGraph.size() - 1).nodesInSubgraph.get(destinationID);
    }

    public List<Node> getShortestPathToDestination(Node destination){
        List<Node> path = new ArrayList<>();
        for(Node node = destination; node != null; node = node.previous){
            path.add(node);
        }

        Collections.reverse(path);
        return path;
    }

    public List<Node> computeDijkstraShortestPath(Node source, Node destination){
        source.minDistance = 0;
        PriorityQueue<Node> nodesQueue = new PriorityQueue<Node>();
        nodesQueue.add(source);
        int destinationGroup = destination._mainID;

        while(!nodesQueue.isEmpty()){
            Node node = nodesQueue.poll();

            if(destinationGroup != 0 && directionCompute == false){
                for(int i = 0; i < node._attachIDs.size(); i++){
                    if(node._attachIDs.get(i) == destinationGroup){
                        destination = navigationGraph.get(navigationGraph.size() - 1).nodesInSubgraph.get(node._waypointID);
                        break;
                    }
                }
            }

            if(node._waypointID.equals(destination._waypointID))
                break;

            for(Edge e : node._edges){
               Node target = e.target;
               double weight = e.weight;
               double distanceThroughU = node.minDistance + weight;
               if(distanceThroughU < target.minDistance){
                   nodesQueue.remove(target);
                   target.minDistance = distanceThroughU;
                   target.previous = node;
                   nodesQueue.add(target);
               }
            }
        }

        return getShortestPathToDestination(destination);
    }

    public Node computePathToTraversePoint(Node source, Boolean sameElevation, int indexOfNextRegion){
        Node backupTransferNode = null;
        boolean entered = false;
        source.minDistance = 0;
        PriorityQueue<Node> nodeQueue = new PriorityQueue<Node>();
        nodeQueue.add(source);

        while(!nodeQueue.isEmpty()){
            Node u = nodeQueue.poll();

            for(Edge e : u._edges){
                Node v = e.target;
                double weight = e.weight;
                double distanceTroughU = u.minDistance + weight;
                if(distanceTroughU < v.minDistance){
                    nodeQueue.remove(v);
                    v.minDistance = distanceTroughU;
                    v.previous = u;
                    nodeQueue.add(v);
                }
                if(sameElevation == true && v._nodeType == CONNECTPOINT){
                    if(navigationGraph.get(indexOfNextRegion).nodesInSubgraph.get(v._waypointID) != null)
                        return v;
                }
                else if(sameElevation == false && v._nodeType == getPreferenceValue()){
                    tmpDestinationID.add(v._waypointID);
                    return v;
                }
                else if(sameElevation == false && v._nodeType != getPreferenceValue() && v._nodeType != NORMAL_WAYPOINT && entered == false){
                    backupTransferNode = v;
                    entered = true;
                }
            }
        }

        if(backupTransferNode != null)
            return backupTransferNode;
        else
            return source;
    }

    public String find_Source_IN_Next_Region(int currentConnectID, int nextRegionIndex){
        for(Map.Entry<String, Node> entry : navigationGraph.get(nextRegionIndex).nodesInSubgraph.entrySet()){
            Node v = entry.getValue();

            if(v._connectPointID == currentConnectID){
                String id = v.getID();
                return id;
            }
        }
        return null;
    }

    public List<Node> startNavigation(){
        List<Node> path = new ArrayList<>();
        int startNodeType = startNode._nodeType;

        int connectPointId;

        if(navigationGraph.size() == 1){
            path = computeDijkstraShortestPath(startNode, endNode);
        }
        else{
            for(int i = 0; i < navigationGraph.size() - 1; i++){
                Node destinationOfRegion = null;
                tmpDestinationID.clear();
                navigationGraph.get(i).nodesInSubgraph.get(sourceID)._nodeType = NORMAL_WAYPOINT;

                if(regionPath.get(i)._elevation == regionPath.get(i + 1)._elevation){
                    destinationOfRegion = computePathToTraversePoint(navigationGraph.get(i).nodesInSubgraph.get(sourceID),true,i+1);
                    sourceID = destinationOfRegion.getID();
                }
                else if(regionPath.get(i)._elevation != regionPath.get(i + 1)._elevation){
                    if(startNodeType == Setting.getPreferenceValue() && find_Source_IN_Next_Region(startNode._connectPointID, i + 1) != null){
                        destinationOfRegion = startNode;
                        connectPointId = destinationOfRegion._connectPointID;
                        sourceID = find_Source_IN_Next_Region(connectPointId, i +1);
                    }
                    else {
                        String tmpSourceId = null;
                        while(tmpSourceId == null){
                            if(tmpDestinationID.size() >= 1){
                                loadNavigationGraph();
                                for(int count = 0;count < tmpDestinationID.size(); count++)
                                    navigationGraph.get(i).nodesInSubgraph.get(tmpDestinationID.get(count))._nodeType = NORMAL_WAYPOINT;
                            }
                            destinationOfRegion = computePathToTraversePoint(navigationGraph.get(i).nodesInSubgraph.get(sourceID), false, i + 1);
                            connectPointId = destinationOfRegion._connectPointID;
                            tmpSourceId = find_Source_IN_Next_Region(connectPointId,i + 1);
                        }
                        sourceID = tmpSourceId;
                    }
                }
                path.addAll(getShortestPathToDestination(destinationOfRegion));
            }
            List<Node> pathInLastRegion = computeDijkstraShortestPath(navigationGraph.get(navigationGraph.size() - 1).nodesInSubgraph.get(sourceID), endNode);
            path.addAll(pathInLastRegion);

            for(int i = 1; i < path.size(); i++){
                if(path.get(i)._waypointID.equals(path.get(i - 1)._waypointID))
                    path.remove(i);
            }
        }

        for(int i = 0; i < path.size(); i++)
            navigationPath_ID_to_Name_Mapping.put(path.get(i)._waypointID, path.get(i)._waypointName);

        signalProcessor.setPath(path);
        pathLength = GeoCalulation.getPathLength(path);

        for(int i = 0; i < path.size(); i++)
            Log.i("path",path.get(i)._waypointName);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        },1000);
        return path;
    }

    public void showHintAtWaypoint(final int instruction){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toast_layout));
        ImageView image = layout.findViewById(R.id.toast_image);

        final Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 25);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);

        int DistanceForShowHint = 0;
        if(navigationPath.size() >= 2)
            DistanceForShowHint = GeoCalulation.getDistance(navigationPath.get(0), navigationPath.get(1));

        String turnDirection = null;
        Vibrator myVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);

        if(instruction == ARRIVED_NOTIFIER){
            turnDirection = YOU_HAVE_ARRIVE;
            image.setImageResource(R.drawable.arrived_1);
            textToSpeech.speak(turnDirection, TextToSpeech.QUEUE_ADD, null);
            toast.show();
            myVibrator.vibrate(800);
            beaconManager.removeAllRangeNotifiers();
            beaconManager.removeAllMonitorNotifiers();
            beaconManager.unbind(NavigationActivity.this);

            Intent i = new Intent(NavigationActivity.this, MainActivity.class);
            i.putExtra("arrivedFlag",1);
            startActivity(i);
            finish();
        }
        else if(instruction == WRONGWAY_NOTIFIER){
            turnDirection = REROUTING;
            image.setImageResource(R.drawable.refresh);
            toast.show();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    toast.cancel();
                }
            },1000);
            myVibrator.vibrate(1000);
        }
        else if(instruction == MAKETURN_NOTIFIER){
            switch (turnNotificationForPopup){
                case RIGHT:
                    turnDirection = PLEASE_TURN_RIGHT;
                    nowDoInstruction.setText(NOW_TURN_RIGHT);
                    imageTurnIndicator.setImageResource(R.drawable.right_arrow);
                    image.setImageResource(R.drawable.right_arrow);
                    lastisSlash = false;
                    break;

                case LEFT:
                    turnDirection = PLEASE_TURN_LEFT;
                    nowDoInstruction.setText(NOW_TURN_LEFT);
                    imageTurnIndicator.setImageResource(R.drawable.left_arrow);
                    image.setImageResource(R.drawable.left_arrow);
                    lastisSlash = false;
                    break;

                case FRONT_RIGHT:
                    turnDirection = PLEASE_TURN_FRONT_RIGHT;
                    if(lastisSlash == false){
                        nowDoInstruction.setText(NOW_TURN_FRONT_RIGHT);
                        image.setImageResource(R.drawable.front_right);
                        imageTurnIndicator.setImageResource(R.drawable.front_right);
                        lastisSlash = true;
                    }
                    else {
                        nowDoInstruction.setText(NOW_GO_STRAIGHT);
                        imageTurnIndicator.setImageResource(R.drawable.front_arrow);
                        image.setImageResource(R.drawable.front_arrow);
                        lastisSlash = false;
                    }
                    break;

                case FRONT_LEFT:
                    turnDirection = PLEASE_TURN_FRONT_LEFT;
                    if(lastisSlash == false){
                        nowDoInstruction.setText(NOW_TURN_FRONT_LEFT);
                        imageTurnIndicator.setImageResource(R.drawable.front_left);
                        image.setImageResource(R.drawable.front_left);
                        lastisSlash = true;
                    }
                    else {
                        nowDoInstruction.setText(NOW_GO_STRAIGHT);
                        imageTurnIndicator.setImageResource(R.drawable.front_arrow);
                        image.setImageResource(R.drawable.front_arrow);
                        lastisSlash = false;
                    }
                    break;

                case REAR_RIGHT:
                    turnDirection = PLEASE_TURN_REAR_RIGHT;
                    nowDoInstruction.setText(NOW_TURN_REAR_RIGHT);
                    imageTurnIndicator.setImageResource(R.drawable.rear_right);
                    image.setImageResource(R.drawable.rear_right);
                    lastisSlash = false;
                    break;

                case REAR_LEFT:
                    turnDirection = PLEASE_TURN_REAR_LEFT;
                    nowDoInstruction.setText(NOW_TURN_REAR_LEFT);
                    imageTurnIndicator.setImageResource(R.drawable.rear_left);
                    image.setImageResource(R.drawable.rear_left);
                    lastisSlash = false;
                    break;

                case FRONT:
                    turnDirection = PLEASE_GO_STRAIGHT;
                    nowDoInstruction.setText(NOW_GO_STRAIGHT);
                    imageTurnIndicator.setImageResource(R.drawable.front_arrow);
                    image.setImageResource(R.drawable.front_arrow);
                    lastisSlash = false;
                    break;

                case FRONT_RIGHTSIDE:
                    turnDirection = PLEASE_GO_STRAIGHT_RIGHTSIDE;
                    if(lastisSlash == false){
                        nowDoInstruction.setText(NOW_GO_STRAIGHT_RIGHTSIDE);
                        imageTurnIndicator.setImageResource(R.drawable.rightside);
                        image.setImageResource(R.drawable.rightside);
                        lastisSlash = true;
                    }
                    else{
                        nowDoInstruction.setText(NOW_GO_STRAIGHT);
                        imageTurnIndicator.setImageResource(R.drawable.front_arrow);
                        image.setImageResource(R.drawable.front_arrow);
                        lastisSlash = false;
                    }
                    break;

                case FRONT_LEFTSIDE:
                    turnDirection = PLEASE_GO_STRAIGHT_LEFTSIDE;
                    if(lastisSlash == false){
                        nowDoInstruction.setText(NOW_GO_STRAIGHT_LEFTSIDE);
                        imageTurnIndicator.setImageResource(R.drawable.leftside);
                        image.setImageResource(R.drawable.leftside);
                        lastisSlash = true;
                    }
                    else {
                        nowDoInstruction.setText(NOW_GO_STRAIGHT);
                        imageTurnIndicator.setImageResource(R.drawable.front_arrow);
                        image.setImageResource(R.drawable.front_arrow);
                        lastisSlash = false;
                    }
                    break;

                case ELEVATOR:
                    turnDirection = PLEASE_TAKE_ELEVATOR;
                    image.setImageResource(R.drawable.elevator);
                    lastisSlash = false;
                    break;

                case STAIR:
                    turnDirection = PLEASE_WALK_UP_STAIR;
                    if(navigationPath.get(1)._elevation > navigationPath.get(0)._elevation)
                        image.setImageResource(R.drawable.stairs_up);
                    else
                        image.setImageResource(R.drawable.stairs_down);
                    lastisSlash = false;
                    break;

                case "goback":
                    turnDirection = "";
                    imageTurnIndicator.setImageResource(R.drawable.turn_back);
                    image.setImageResource(R.drawable.turn_back);
                    break;
            }

            if(gyroscopeSensor != null && turnNotificationForPopup != STAIR)
                sensorManager.registerListener(gyroscopeEventListener,gyroscopeSensor,SensorManager.SENSOR_DELAY_FASTEST);
        }
        textToSpeech.speak(turnDirection, TextToSpeech.QUEUE_ADD, null);

        if(turnNotificationForPopup != null)
            myVibrator.vibrate(new long[]{50, 100, 50}, -1);
    }

    private void showBackgroud()
    {
        nowDoInstruction.setVisibility(View.VISIBLE);
        firstMovement.setVisibility(View.VISIBLE);
        howFarToMove.setVisibility(View.VISIBLE);
        nextTurnMovement.setVisibility(View.VISIBLE);
        imageTurnIndicator.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        progressNumber.setVisibility(View.VISIBLE);
    }

    private void logBeaconData(List<String> beacons){
        if(beacons.size() > 2){
            Node receiveNode;
            Boolean pass = false;
            receiveBeacon = null;

            if(lastProcessedBeacon.equals(beacons.get(3)) && beacons.get(2).equals("close"))
                turnback_count++;
            else if(beacons.get(2).equals("close"))
                turnback_count = 0;

            if(turnback_count == 0 || turnback_count > 5){
                if(beacons.get(2).equals("close") && navigationPath.size() > 0){
                    tmpNode = allWaypointData.get(beacons.get(3));
                    if(navigationPath.get(0)._waypointID.equals(beacons.get(3))){
                        receiveBeacon = beacons.get(3);
                        error_count = 0;
                    }
                    else if(navigationPath.get(0)._groupID != 0 && navigationPath.get(0)._groupID == tmpNode._groupID){
                        receiveBeacon = beacons.get(3);
                        error_count = 0;
                    }
                    else if(!navigationPath.get(0)._waypointID.equals(beacons.get(3)) && error_count == 0){
                        recordBeacon = allWaypointData.get(beacons.get(3));
                        Log.i("recordBeacon","beacons = " + beacons);
                        Log.i("recordBeacon","beacon.get(3) = " + beacons.get(3));
                        Log.i("recordBeacon","first = " + recordBeacon);
                        error_count = 1;
                    }
                    else if(!navigationPath.get(0)._waypointID.equals(beacons.get(3)) && error_count == 1){
                        Log.i("recordBeacon","second = " + recordBeacon);
                        if(recordBeacon._waypointID.equals(beacons.get(3))){
                            receiveBeacon = beacons.get(3);
                            error_count = 0;
                        }
                        else if(!recordBeacon._waypointID.equals(beacons.get(3)) && recordBeacon._groupID != 0){
                            if(tmpNode._groupID == recordBeacon._groupID){
                                receiveBeacon = beacons.get(3);
                                error_count = 0;
                            } else
                                error_count = 0;
                        } else
                            error_count = 0;
                    } 
                    else {
                        receiveBeacon = beacons.get(3);
                        error_count = 0;
                    }
                }
            }

            receiveNode = allWaypointData.get(receiveBeacon);
            if(receiveNode != null)
                currentLocationReminder.setText(PRESENT_POSITION + receiveNode._waypointName);

            if(isFirstBeacon && receiveNode != null){
                choseStartNode = receiveNode;
                sourceID = receiveNode._waypointID;
                sourceRegion = receiveNode._regionID;
                passedRegionID = sourceRegion;
                loadNavigationGraph();
                navigationPath = startNavigation();
                progressBar.setMax(navigationPath.size());

                for(int i = 0; i < choseStartNode._attachIDs.size(); i++){
                    if((endNode._mainID != 0 && endNode._mainID == choseStartNode._attachIDs.get(i))){
                        showHintAtWaypoint(ARRIVED_NOTIFIER);
                        isFirstBeacon = false;
                    }
                }

                if(choseStartNode._waypointID.equals(endNode._waypointID)){
                    showHintAtWaypoint(ARRIVED_NOTIFIER);
                    isFirstBeacon = false;
                }

                //顯示初始方向圖片
                if(navigationPath.size() >= 1 && isFirstBeacon == true){
                    beaconManager.removeAllMonitorNotifiers();
                    beaconManager.removeAllRangeNotifiers();
                    beaconManager.unbind(this);
                    Intent intent = new Intent(NavigationActivity.this, initial_image.class);
                    intent.putExtra("nowID", navigationPath.get(0)._waypointID);
                    intent.putExtra("nextID", navigationPath.get(1)._waypointID);
                    startActivity(intent);
                    imageTurnIndicator.setImageResource(R.drawable.front_arrow);
                    showBackgroud();
                }

                isFirstBeacon = false;
            }

            if(navigationPath.size() > 0)
            {
                if(receiveBeacon != null && !currentLBeaconID.equals(receiveBeacon) && receiveNode != null)
                {
                    if(receiveNode._groupID == navigationPath.get(0)._groupID && receiveNode._groupID != 0)
                    {
                        currentLBeaconID = navigationPath.get(0)._waypointID;
                        pass = true;
                    }
                    else if(receiveNode._groupID == passedGroupID && receiveNode._groupID != 0)
                        pass = false;
                    else
                    {
                        currentLBeaconID = receiveBeacon;
                        pass = true;
                    }
                }
                else
                    pass = false;
            }

            if(pass)
            {
                whichWaypointOnProgressBar += 1;
                synchronized (sync)
                {
                    sync.notify();
                }
            }
        }
    }

    private void languageSetup(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String languageSetup = sharedPreferences.getString("language","繁體中文");
        if(languageSetup.equals("繁體中文")){
            TITLE = getString(R.string.CHT_TITLE);
            NOW_GO_STRAIGHT_RIGHTSIDE = getString(R.string.CHT_RIGHSIDE);
            NOW_GO_STRAIGHT_LEFTSIDE = getString(R.string.CHT_LEFTSIDE);
            NOW_GO_STRAIGHT = getString(R.string.GHT_GO_STRAIGHT);
            NOW_TURN_LEFT = getString(R.string.CHT_TURN_LEFT);
            NOW_TURN_RIGHT = getString(R.string.CHT_TURN_RIGHT);
            NOW_TURN_FRONT_LEFT = getString(R.string.CHT_TURN_FRONT_LEFT);
            NOW_TURN_FRONT_RIGHT = getString(R.string.CHT_TURN_FRONT_RIGHT);
            NOW_TURN_REAR_LEFT = getString(R.string.CHT_TURN_REAR_LEFT);
            NOW_TURN_REAR_RIGHT = getString(R.string.CHT_TURN_REAR_RIGHT);
            NOW_TAKE_ELEVATOR = getString(R.string.CHT_TAKE_ELEVATOR);
            NOW_WALK_UP_STAIR = getString(R.string.CHT_WALK_UP_STAIR);
            NOW_WALK_DOWN_STAIR = getString(R.string.CHT_WALK_DOWN_STAIR);

            GO_STRAIGHT_ABOUT = getString(R.string.CHT_GO_STRAIGHT_ABOUT);
            THEN_GO_STRAIGHT = getString(R.string.CHT_THEN_GO_STRAIGHT);
            THEN_GO_STRAIGHT_RIGHTSIDE = getString(R.string.CHT_THEN_GO_STRAIGHT_RIGHTSIDE);
            THEN_GO_STRAIGHT_LEFTSIDE = getString(R.string.CHT_THEN_GO_STRAIGHT_LEFTSIDE);
            THEN_TURN_LEFT = getString(R.string.CHT_THEN_TURN_LEFT);

            THEN_TURN_RIGHT = getString(R.string.CHT_THEN_TURN_RIGHT);
            THEN_TURN_FRONT_LEFT = getString(R.string.CHT_THEN_TURN_LEFT);
            THEN_TURN_FRONT_RIGHT = getString(R.string.CHT_THEN_TURN_FORNT_RIGHT);
            THEN_TURN_REAR_LEFT = getString(R.string.CHT_THEN_TURN_REAR_LEFT);
            THEN_TURN__REAR_RIGHT = getString(R.string.CHT_THEN_TURN_REAR_RIGHT);
            THEN_TAKE_ELEVATOR = getString(R.string.CHT_THEN_TAKE_ELEVATOR);
            THEN_WALK_UP_STAIR = getString(R.string.CHT_THEN_WALK_UP_STAIR);
            THEN_WALK_DOWN_STAIR = getString(R.string.CHT_THEN_WALK_DOWN_STAIR);
            WAIT_FOR_ELEVATOR = getString(R.string.CHT_WAIT_FOR_ELEVATOR);
            WALKING_UP_STAIR = getString(R.string.CHT_WALKING_UP_STAIR);
            WALKING_DOWN_STAIR = getString(R.string.CHT_WALKING_DOWN_STAIR);

            YOU_HAVE_ARRIVE = getString(R.string.CHT_ARRIVE);
            GET_LOST = getString(R.string.CHT_GET_LOST);
            METERS = getString(R.string.CHT_METER);
            PLEASE_GO_STRAIGHT = getString(R.string.CHT_PLEASE_GO_STRAIGHT);
            PLEASE_GO_STRAIGHT_RIGHTSIDE = getString(R.string.CHT_PLEASE_GO_STRAIGHT_RIGHTSIDE);
            PLEASE_GO_STRAIGHT_LEFTSIDE = getString(R.string.CHT_PLEASE_GO_STRAIGHT_LEFTSIDE);
            PLEASE_TURN_LEFT = getString(R.string.CHT_PLEASE_TURN_LEFT);
            PLEASE_TURN_RIGHT = getString(R.string.CHT_PLEASE_TURN_RIGHT);
            PLEASE_TURN_FRONT_LEFT = getString(R.string.CHT_PLEASE_TURN_FRONT_LEFT);
            PLEASE_TURN_FRONT_RIGHT = getString(R.string.CHT_PLEASE_TURN_FRONT_RIGHT);
            PLEASE_TURN_REAR_LEFT = getString(R.string.CHT_PLEASE_TURN_REAR_LEFT);
            PLEASE_TURN_REAR_RIGHT = getString(R.string.CHT_PLEASE_TURN_REAR_RIGHT);
            PLEASE_TAKE_ELEVATOR = getString(R.string.CHT_PLEASE_TAKE_ELEVATOR);
            PLEASE_WALK_UP_STAIR = getString(R.string.CHT_PLEASE_WALK_UP_STAIR);

            TO = getString(R.string.CHT_TO);
            TO_FIRST_FLOOR = getString(R.string.CHT_TO_FIRST_FLOOR);
            TO_SECOND_FLOOR = getString(R.string.CHT_TO_SECOND_FLOOR);

            PRESENT_POSITION = getString(R.string.CHT_PRESENT_POSITION);
            DESTINATION = getString(R.string.CHT_DESTINATION);

            REROUTING = getString(R.string.CHT_REROUTING);
            TURN_AROUND = getString(R.string.CHT_TURN_BACK);
            WAIT_FOR_SIGNAL = getString(R.string.CHT_WAIT_FOR_INSTRUCTION);
            THEN_ARRIVE = getString(R.string.CHT_THEN_ARRIVE);

            destinationReminder.setText(DESTINATION);
            currentLocationReminder.setText(PRESENT_POSITION);
            howFarToMove.setText(getString(R.string.CHT_RECEIVING_SIGNAL));
        }
        else if(languageSetup.equals("English")){
            TITLE = getString(R.string.ENG_TITLE);
            NOW_GO_STRAIGHT_RIGHTSIDE = getString(R.string.ENG_RIGHSIDE);
            NOW_GO_STRAIGHT_LEFTSIDE = getString(R.string.ENG_LEFTSIDE);
            NOW_GO_STRAIGHT = getString(R.string.ENG_GO_STRAIGHT);
            NOW_TURN_LEFT = getString(R.string.ENG_TURN_LEFT);
            NOW_TURN_RIGHT = getString(R.string.ENG_TURN_RIGHT);
            NOW_TURN_FRONT_LEFT = getString(R.string.ENG_TURN_FRONT_LEFT);
            NOW_TURN_FRONT_RIGHT = getString(R.string.ENG_TURN_FRONT_RIGHT);
            NOW_TURN_REAR_LEFT = getString(R.string.ENG_TURN_REAR_LEFT);
            NOW_TURN_REAR_RIGHT = getString(R.string.ENG_TURN_REAR_RIGHT);
            NOW_TAKE_ELEVATOR = getString(R.string.ENG_TAKE_ELEVATOR);
            NOW_WALK_UP_STAIR = getString(R.string.ENG_WALK_UP_STAIR);
            NOW_WALK_DOWN_STAIR = getString(R.string.ENG_WALK_DOWN_STAIR);

            GO_STRAIGHT_ABOUT = getString(R.string.ENG_GO_STRAIGHT_ABOUT);
            THEN_GO_STRAIGHT = getString(R.string.ENG_THEN_GO_STRAIGHT);
            THEN_GO_STRAIGHT_RIGHTSIDE = getString(R.string.ENG_THEN_GO_STRAIGHT_RIGHTSIDE);
            THEN_GO_STRAIGHT_LEFTSIDE = getString(R.string.ENG_THEN_GO_STRAIGHT_LEFTSIDE);
            THEN_TURN_LEFT = getString(R.string.ENG_THEN_TURN_LEFT);

            THEN_TURN_RIGHT = getString(R.string.ENG_THEN_TURN_RIGHT);
            THEN_TURN_FRONT_LEFT = getString(R.string.ENG_THEN_TURN_LEFT);
            THEN_TURN_FRONT_RIGHT = getString(R.string.ENG_THEN_TURN_FORNT_RIGHT);
            THEN_TURN_REAR_LEFT = getString(R.string.ENG_THEN_TURN_REAR_LEFT);
            THEN_TURN__REAR_RIGHT = getString(R.string.ENG_THEN_TURN_REAR_RIGHT);
            THEN_TAKE_ELEVATOR = getString(R.string.ENG_THEN_TAKE_ELEVATOR);
            THEN_WALK_UP_STAIR = getString(R.string.ENG_THEN_WALK_UP_STAIR);
            THEN_WALK_DOWN_STAIR = getString(R.string.ENG_THEN_WALK_DOWN_STAIR);
            WAIT_FOR_ELEVATOR = getString(R.string.ENG_WAIT_FOR_ELEVATOR);
            WALKING_UP_STAIR = getString(R.string.ENG_WALKING_UP_STAIR);
            WALKING_DOWN_STAIR = getString(R.string.ENG_WALKING_DOWN_STAIR);

            YOU_HAVE_ARRIVE = getString(R.string.ENG_ARRIVE);
            GET_LOST = getString(R.string.ENG_GET_LOST);
            METERS = getString(R.string.ENG_METER);
            PLEASE_GO_STRAIGHT = getString(R.string.ENG_PLEASE_GO_STRAIGHT);
            PLEASE_GO_STRAIGHT_RIGHTSIDE = getString(R.string.ENG_PLEASE_GO_STRAIGHT_RIGHTSIDE);
            PLEASE_GO_STRAIGHT_LEFTSIDE = getString(R.string.ENG_PLEASE_GO_STRAIGHT_LEFTSIDE);
            PLEASE_TURN_LEFT = getString(R.string.ENG_PLEASE_TURN_LEFT);
            PLEASE_TURN_RIGHT = getString(R.string.ENG_PLEASE_TURN_RIGHT);
            PLEASE_TURN_FRONT_LEFT = getString(R.string.ENG_PLEASE_TURN_FRONT_LEFT);
            PLEASE_TURN_FRONT_RIGHT = getString(R.string.ENG_PLEASE_TURN_FRONT_RIGHT);
            PLEASE_TURN_REAR_LEFT = getString(R.string.ENG_PLEASE_TURN_REAR_LEFT);
            PLEASE_TURN_REAR_RIGHT = getString(R.string.ENG_PLEASE_TURN_REAR_RIGHT);
            PLEASE_TAKE_ELEVATOR = getString(R.string.ENG_PLEASE_TAKE_ELEVATOR);
            PLEASE_WALK_UP_STAIR = getString(R.string.ENG_PLEASE_WALK_UP_STAIR);

            TO = getString(R.string.ENG_TO);
            TO_FIRST_FLOOR = getString(R.string.ENG_TO_FIRST_FLOOR);
            TO_SECOND_FLOOR = getString(R.string.ENG_TO_SECOND_FLOOR);

            REROUTING = getString(R.string.ENG_REROUTING);
            TURN_AROUND = getString(R.string.ENG_TURN_BACK);
            WAIT_FOR_SIGNAL = getString(R.string.ENG_WAIT_FOR_INSTRUCTION);
            THEN_ARRIVE = getString(R.string.ENG_THEN_ARRIVE);

            PRESENT_POSITION = getString(R.string.ENG_PRESENT_POSITION);
            DESTINATION = getString(R.string.ENG_DESTINATION);

            destinationReminder.setText(DESTINATION);
            currentLocationReminder.setText(PRESENT_POSITION);
            howFarToMove.setText(getString(R.string.ENG_RECEIVING_SIGNAL));
        }
    }

    public void loadAllWaypointData(){

        Log.i("regions", "region = " + regionGraph.getAllRegionNames());
        //Load the navigation graph
        navigationGrpahForAllWaypoint = DataParser.getWaypointDataFromNavigationGraph(this, regionGraph.getAllRegionNames());
        //Get the all Node from each graph
        for(int i = 0; i < navigationGrpahForAllWaypoint.size(); i++){
            allWaypointData.putAll(navigationGrpahForAllWaypoint.get(i).nodesInSubgraph);
        }
        Log.i("allWaypointData", "" + allWaypointData + "\nsize = " + allWaypointData.size());

        signalProcessor.setAllWaypointData(allWaypointData);
    }

    public void beaconManagerSetup(){
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.unbind(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-15,i:16-19,i:20-23,p:24-24"));

        // Detect the Eddystone main identifier (UID) frame
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));

        // Detect the Eddystone telemetry (TLM) frame
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15"));

        // Detect the Eddystone URL frame
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20"));

        beaconManager.setForegroundScanPeriod(50);
        beaconManager.setForegroundBetweenScanPeriod(0);

        beaconManager.removeAllMonitorNotifiers();
        beaconManager.removeAllRangeNotifiers();

        //Get the details for all the beacons we encounter
        region = new org.altbeacon.beacon.Region("justGiveMeEverything", null, null, null);
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Scanning for Beacons");
        Intent intent = new Intent(this, NavigationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My Notification Channel ID",
                                                                    "My Notification Name",
                                                                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("My Notification Channel Description");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channel.getId());
        }
        beaconManager.enableForegroundServiceScanning(builder.build(),456);
        beaconManager.setEnableScheduledScanJobs(false);
        beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.setBackgroundScanPeriod(1100);
        beaconManager.bind(NavigationActivity.this);
    }

    class NavigationThread implements Runnable{
        @Override
        public void run() {
            while(!navigationPath.isEmpty()){
                synchronized (sync){
                    try {
                        sync.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if(navigationPath.get(0)._waypointID.equals(currentLBeaconID)){
                    Message messageFromInstructionHandler = instructionHandler.obtainMessage();
                    Message messageFromCurrentPositionHandler = currentPositionHandler.obtainMessage();
                    Message messageFromWalkedPointHandler = walkedPointHandler.obtainMessage();
                    Message messageFromProgressHandler = progressHandler.obtainMessage();

                    messageFromCurrentPositionHandler.obj = navigationPath.get(0)._waypointName;

                    if(navigationPath.size() >= 3){
                        //Normal condition next waypoint are same region
                        if(navigationPath.get(0)._regionID.equals(navigationPath.get(1)._regionID) &&
                                navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID)){
                            messageFromInstructionHandler.obj = getDirectionFromBearing(navigationPath.get(0), navigationPath.get(1),navigationPath.get(2));
                        }
                        //If the next two waypoint are not in same region, means that the nexy waypoint is the last waypoint of the regiion
                        else if(!(navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))){
                            messageFromInstructionHandler.obj = FRONT;
                        }
                        //If the current waypoint and next waypoint are not in the same region transfer through elevator or stair
                        else if(!(navigationPath.get(0)._regionID.equals(navigationPath.get(1)._regionID))){
                            if(navigationPath.get(0)._nodeType == ELEVATOR_WAYPIOINT)
                                messageFromInstructionHandler.obj = ELEVATOR;
                            else if(navigationPath.get(0)._nodeType == STAIRWELL_WAYPOINT)
                                messageFromInstructionHandler.obj = STAIR;
                            else if(navigationPath.get(0)._nodeType == CONNECTPOINT)
                                messageFromInstructionHandler.obj = getDirectionFromBearing(navigationPath.get(0), navigationPath.get(1), navigationPath.get(2));
                            else if(navigationPath.get(0)._nodeType == NORMAL_WAYPOINT){
                                if(Setting.getPreferenceValue() == ELEVATOR_WAYPIOINT)
                                    messageFromInstructionHandler.obj = ELEVATOR;
                                else if(Setting.getPreferenceValue() == STAIRWELL_WAYPOINT)
                                    messageFromInstructionHandler.obj = STAIR;
                            }
                        }
                    }
                    else if(navigationPath.size() == 2){
                        if(!(navigationPath.get(0)._regionID.equals(navigationPath.get(1)._regionID))){
                            if(navigationPath.get(0)._nodeType == ELEVATOR_WAYPIOINT)
                                messageFromInstructionHandler.obj = ELEVATOR;
                            else if(navigationPath.get(0)._nodeType == STAIRWELL_WAYPOINT)
                                messageFromInstructionHandler.obj = STAIR;
                        }
                        else messageFromInstructionHandler.obj = FRONT;
                    }
                    else if(navigationPath.size() == 1)
                        messageFromInstructionHandler.obj = ARRIVED;

                    walkedWaypoint++;

                    messageFromWalkedPointHandler.obj = walkedWaypoint;

                    messageFromProgressHandler.obj = true;

                    walkedPointHandler.sendMessage(messageFromWalkedPointHandler);
                    instructionHandler.sendMessage(messageFromInstructionHandler);
                    currentPositionHandler.sendMessage(messageFromCurrentPositionHandler);
                    progressHandler.sendMessage(messageFromProgressHandler);
                }
                else if(!(navigationPath.get(0)._waypointID.equals(currentLBeaconID))){
                    Message messageFromInstrucitonHandler = instructionHandler.obtainMessage();
                    messageFromInstrucitonHandler.obj = WRONG;
                    instructionHandler.sendMessage(messageFromInstrucitonHandler);
                }
            }
        }
    }

    void closeImage()
    {
        nowDoInstruction.setVisibility(View.INVISIBLE);
        firstMovement.setVisibility(View.INVISIBLE);
        howFarToMove.setVisibility(View.INVISIBLE);
        nextTurnMovement.setVisibility(View.INVISIBLE);
        imageTurnIndicator.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        progressNumber.setVisibility(View.INVISIBLE);
    }

    public void ShowDirectionFromConnectPoint()
    {
        if(navigationPath.get(0)._connectPointID == 0)
            isInVirtualNode = false;

        if(choseStartNode._waypointID != navigationPath.get(0)._waypointID){

            //進入樓梯
            if(navigationPath.get(0)._connectPointID != 0 && navigationPath.get(1)._connectPointID == navigationPath.get(0)._connectPointID && isInVirtualNode == false){
                //Up stair
                if(navigationPath.get(1)._elevation > navigationPath.get(0)._elevation)
                {
                    for(int i = 0; i < virtualNodeUP.size(); i++)
                    {
                        if(virtualNodeUP.get(i)._connectPointID == navigationPath.get(0)._connectPointID)
                            turnNotificationForPopup = getDirectionFromBearing(lastNode, navigationPath.get(0), virtualNodeUP.get(i));
                    }
                }
                //Down stair
                else if(navigationPath.get(1)._elevation < navigationPath.get(0)._elevation)
                {
                    for(int i = 0; i < virtualNodeDown.size(); i++)
                    {
                        if(virtualNodeDown.get(i)._connectPointID == navigationPath.get(0)._connectPointID)
                            turnNotificationForPopup = getDirectionFromBearing(lastNode, navigationPath.get(0),virtualNodeDown.get(i));
                    }
                }
                if(firstTurn == false && callDirectionInWrong == false)
                    showHintAtWaypoint(MAKETURN_NOTIFIER);
                isInVirtualNode = true;
            }
            //離開樓梯
            else if(navigationPath.get(0)._connectPointID != 0 && lastNode._connectPointID == navigationPath.get(0)._connectPointID && isInVirtualNode == true){
                //Up stair
                if(lastNode._elevation < navigationPath.get(0)._elevation)
                {
                    for(int i = 0; i < virtualNodeUP.size(); i++)
                    {
                        if(navigationPath.get(0)._connectPointID == virtualNodeUP.get(i)._connectPointID)
                            turnNotificationForPopup = getDirectionFromBearing(virtualNodeUP.get(i), navigationPath.get(0), navigationPath.get(1));
                    }
                }
                //Down stair
                else if(lastNode._elevation > navigationPath.get(0)._elevation)
                {
                    for(int i = 0; i < virtualNodeDown.size(); i++)
                    {
                        if(navigationPath.get(0)._connectPointID == virtualNodeDown.get(i)._connectPointID)
                            turnNotificationForPopup = getDirectionFromBearing(virtualNodeDown.get(i), navigationPath.get(0),navigationPath.get(1));
                    }
                }
            }
        }
        else if(choseStartNode._waypointID == navigationPath.get(0)._waypointID)
        {
            if(navigationPath.get(0)._connectPointID != 0 && navigationPath.get(0)._connectPointID == navigationPath.get(1)._connectPointID)
            {
                //Up stair
                if(navigationPath.get(1)._elevation > navigationPath.get(0)._elevation)
                {
                    for(int i = 0; i < virtualNodeUP.size(); i++)
                    {
                        if(navigationPath.get(0)._connectPointID == virtualNodeUP.get(i)._connectPointID)
                            turnNotificationForPopup = getDirectionFromBearing(lastNode,navigationPath.get(0), virtualNodeUP.get(1));
                    }
                }
                //Down stair
                else if(navigationPath.get(1)._elevation < navigationPath.get(0)._elevation)
                {
                    for(int i = 0; i < virtualNodeDown.size(); i++)
                    {
                        if(navigationPath.get(0)._connectPointID == virtualNodeDown.get(i)._connectPointID)
                            turnNotificationForPopup = getDirectionFromBearing(virtualNodeDown.get(i), navigationPath.get(0), navigationPath.get(1));
                    }
                }
                isInVirtualNode = true;
            }
        }
        callDirectionInWrong = false;
    }

    public void elevationDisplay(int transferPointType, int elevation){
        if(transferPointType == ELEVATOR_WAYPIOINT){
            switch (elevation){
                case 1:
                    nextTurnMovement.setText(THEN_TAKE_ELEVATOR + TO_FIRST_FLOOR);
                    break;
                case 2:
                    nextTurnMovement.setText(THEN_TAKE_ELEVATOR + TO_SECOND_FLOOR);
            }
        }
        else if(transferPointType == STAIRWELL_WAYPOINT){
            //Up stair
           if(navigationPath.get(0)._elevation < navigationPath.get(2)._elevation)
           {
               switch(elevation){
                   case 1:
                       nextTurnMovement.setText(THEN_WALK_UP_STAIR + TO_FIRST_FLOOR);
                       break;
                   case 2:
                       nextTurnMovement.setText(THEN_WALK_UP_STAIR + TO_SECOND_FLOOR);
                       break;
               }
           }
           //Down stair
           else if(navigationPath.get(0)._elevation > navigationPath.get(2)._elevation)
           {
               switch (elevation){
                   case 1:
                       nextTurnMovement.setText(THEN_WALK_DOWN_STAIR + TO_FIRST_FLOOR);
                       break;
                   case 2:
                       nextTurnMovement.setText(THEN_WALK_DOWN_STAIR + TO_SECOND_FLOOR);
                       break;
               }
           }
        }
    }

    void navigationInstructionDisplay(String turnDirection, int distance){
        closeImage();

        if(firstTurn == true) {
            lastNode = navigationPath.get(0);
            if (navigationPath.size() == 2 && navigationPath.get(0)._elevation != navigationPath.get(1)._elevation) {
                if (navigationPath.get(0)._nodeType == 1)
                    turnDirection = ELEVATOR;
                else if (navigationPath.get(0)._nodeType == 2)
                    turnDirection = STAIR;
            }
        }

            if(navigationPath.size() >= 2 && !turnDirection.equals(WRONG))
                ShowDirectionFromConnectPoint();

            nextTurnMovement.setText("");

            switch (turnDirection){
                case LEFT:
                    firstMovement.setText(GO_STRAIGHT_ABOUT);
                    if(navigationPath.size() > 1)
                        howFarToMove.setText("" + distance +" "+ METERS + TO + navigationPath.get(1)._waypointName);

                    switch (navigationPath.get(1)._nodeType){
                        case ELEVATOR_WAYPIOINT:
                            if(!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                                elevationDisplay(ELEVATOR_WAYPIOINT, navigationPath.get(2)._elevation);
                            break;
                        case STAIRWELL_WAYPOINT:
                            if(!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                                elevationDisplay(STAIRWELL_WAYPOINT,navigationPath.get(2)._elevation);
                            break;
                        case NORMAL_WAYPOINT:
                            break;
                    }
                    if(turnNotificationForPopup != null)
                        showHintAtWaypoint(MAKETURN_NOTIFIER);

                    turnNotificationForPopup = LEFT;
                    break;

                case FRONT_LEFT:
                    firstMovement.setText(GO_STRAIGHT_ABOUT);
                    if(navigationPath.size() > 1)
                        howFarToMove.setText("" + distance + " " + METERS + TO + navigationPath.get(1)._waypointName);

                    switch (navigationPath.get(1)._nodeType){
                        case ELEVATOR_WAYPIOINT:
                            if(!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                                elevationDisplay(ELEVATOR_WAYPIOINT, navigationPath.get(2)._elevation);
                        case STAIRWELL_WAYPOINT:
                            if(!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                                elevationDisplay(STAIRWELL_WAYPOINT, navigationPath.get(2)._elevation);
                        case NORMAL_WAYPOINT:
                            break;
                    }

                    if(turnNotificationForPopup != null)
                        showHintAtWaypoint(MAKETURN_NOTIFIER);

                    turnNotificationForPopup = FRONT_LEFT;
                    break;

                case REAR_LEFT:
                    firstMovement.setText(GO_STRAIGHT_ABOUT);
                    if(navigationPath.size() > 1)
                        howFarToMove.setText("" + distance + " " +METERS + TO + navigationPath.get(1)._waypointName);

                    switch (navigationPath.get(1)._nodeType){
                        case ELEVATOR_WAYPIOINT:
                            if(!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                                elevationDisplay(ELEVATOR_WAYPIOINT, navigationPath.get(2)._elevation);
                            break;
                            
                        case STAIRWELL_WAYPOINT:
                            if(!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                                elevationDisplay(STAIRWELL_WAYPOINT, navigationPath.get(2)._elevation);
                            break;
                            
                        case NORMAL_WAYPOINT:
                            break;
                    }
                    
                    if(turnNotificationForPopup != null)
                        showHintAtWaypoint(MAKETURN_NOTIFIER);
                    
                    turnNotificationForPopup = REAR_LEFT;
                    break;
                    
                case RIGHT:
                    firstMovement.setText(GO_STRAIGHT_ABOUT);
                    if(navigationPath.size() > 1)
                        howFarToMove.setText("" + distance + " " + METERS + TO + navigationPath.get(1)._waypointName);
                    
                    switch (navigationPath.get(1)._nodeType){
                        case ELEVATOR_WAYPIOINT:
                            if(!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                                elevationDisplay(ELEVATOR_WAYPIOINT, navigationPath.get(2)._elevation);
                            break;
                        case STAIRWELL_WAYPOINT:
                            if(!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                                elevationDisplay(STAIRWELL_WAYPOINT, navigationPath.get(2)._elevation);
                            break;
                        case NORMAL_WAYPOINT:
                            break;
                    }
                    
                    if(turnNotificationForPopup != null)
                        showHintAtWaypoint(MAKETURN_NOTIFIER);
                    
                    turnNotificationForPopup = RIGHT;
                    break;
                    
                case FRONT_RIGHT:
                    firstMovement.setText(GO_STRAIGHT_ABOUT);
                    if(navigationPath.size() > 1)
                        howFarToMove.setText("" + distance + " " + METERS + TO + navigationPath.get(1)._waypointName);
                    
                    switch (navigationPath.get(1)._nodeType){
                        case ELEVATOR_WAYPIOINT:
                            if(!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                                elevationDisplay(ELEVATOR_WAYPIOINT, navigationPath.get(2)._elevation);
                            break;
                        case STAIRWELL_WAYPOINT:
                            if(!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                                elevationDisplay(STAIRWELL_WAYPOINT,navigationPath.get(2)._elevation);
                            break;
                        case NORMAL_WAYPOINT:
                            break;
                    }
                    
                    if(turnNotificationForPopup != null)
                        showHintAtWaypoint(MAKETURN_NOTIFIER);
                    
                    turnNotificationForPopup = FRONT_RIGHT;
                    break;
                    
                case REAR_RIGHT:
                    firstMovement.setText(GO_STRAIGHT_ABOUT);
                    if(navigationPath.size() > 1)
                        howFarToMove.setText("" + distance + " " + METERS + TO + navigationPath.get(1)._waypointName);
                    
                    switch (navigationPath.get(1)._nodeType){
                        case ELEVATOR_WAYPIOINT:
                            if(!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                                elevationDisplay(ELEVATOR_WAYPIOINT, navigationPath.get(2)._elevation);
                            break;

                        case STAIRWELL_WAYPOINT:
                            if(!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                                elevationDisplay(STAIRWELL_WAYPOINT, navigationPath.get(2)._elevation);
                            break;

                        case NORMAL_WAYPOINT:
                            break;
                    }

                    if(turnNotificationForPopup != null)
                        showHintAtWaypoint(MAKETURN_NOTIFIER);

                    turnNotificationForPopup = REAR_RIGHT;
                    break;

                case FRONT:
                    firstMovement.setText(GO_STRAIGHT_ABOUT);
                    if(navigationPath.size() > 1)
                        howFarToMove.setText("" + distance + " " + METERS + TO + navigationPath.get(1)._waypointName);

                    switch (navigationPath.get(1)._nodeType){
                        case ELEVATOR_WAYPIOINT:
                            if(navigationPath.size() == 2){
                                howFarToMove.setText("" + distance + " " + METERS);
                                nextTurnMovement.setText(YOU_HAVE_ARRIVE);
                            }
                            else {
                                if(!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                                    elevationDisplay(ELEVATOR_WAYPIOINT, navigationPath.get(2)._elevation);
                            }
                            break;

                        case STAIRWELL_WAYPOINT:
                            if(navigationPath.size() == 2){
                                howFarToMove.setText("" + distance + " " + METERS);
                                nextTurnMovement.setText(YOU_HAVE_ARRIVE);
                            }
                            else {
                                if(!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                                    elevationDisplay(STAIRWELL_WAYPOINT, navigationPath.get(2)._elevation);
                            }
                            break;

                        case NORMAL_WAYPOINT:
                            if(navigationPath.size() == 2){
                                howFarToMove.setText("" + distance + " " + METERS);
                                nextTurnMovement.setText(YOU_HAVE_ARRIVE);
                            }
                            break;
                    }

                    if(turnNotificationForPopup != null)
                        showHintAtWaypoint(MAKETURN_NOTIFIER);

                    turnNotificationForPopup = FRONT;
                    break;

                case FRONT_RIGHTSIDE:
                    firstMovement.setText(GO_STRAIGHT_ABOUT);
                    if(navigationPath.size() > 1)
                        howFarToMove.setText("" + distance + " " + METERS + TO + navigationPath.get(1)._waypointName);

                    switch (navigationPath.get(1)._nodeType){
                        case ELEVATOR_WAYPIOINT:
                            if(navigationPath.size() == 2){
                                howFarToMove.setText("" + distance + " " + METERS);
                                nextTurnMovement.setText(YOU_HAVE_ARRIVE);
                            }
                            else{
                                if(!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                                    elevationDisplay(ELEVATOR_WAYPIOINT, navigationPath.get(2)._elevation);
                            }
                            break;

                        case STAIRWELL_WAYPOINT:
                            if(navigationPath.size() == 2){
                                howFarToMove.setText("" + distance + " " + METERS);
                                nextTurnMovement.setText(YOU_HAVE_ARRIVE);
                            }
                            else {
                                if(!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                                    elevationDisplay(STAIRWELL_WAYPOINT, navigationPath.get(2)._elevation);
                            }
                            break;

                        case NORMAL_WAYPOINT:
                            if(navigationPath.size() == 2){
                                howFarToMove.setText("" + distance + " " + METERS);
                                nextTurnMovement.setText(YOU_HAVE_ARRIVE);
                            }
                            break;
                    }
                    if(turnNotificationForPopup != null)
                        showHintAtWaypoint(MAKETURN_NOTIFIER);

                    turnNotificationForPopup = FRONT_RIGHTSIDE;
                    break;

                case FRONT_LEFTSIDE:
                    firstMovement.setText("" + distance + " " + METERS + TO + navigationPath.get(1)._waypointName);

                    switch(navigationPath.get(1)._nodeType){
                        case ELEVATOR_WAYPIOINT:
                            if(navigationPath.size() == 2){
                                howFarToMove.setText("" + distance + " " + METERS);
                                nextTurnMovement.setText(YOU_HAVE_ARRIVE);
                            }
                            else {
                                if(!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                                    elevationDisplay(ELEVATOR_WAYPIOINT, navigationPath.get(2)._elevation);
                            }
                            break;

                        case STAIRWELL_WAYPOINT:
                            if(navigationPath.size() == 2){
                                howFarToMove.setText("" + distance + " " + METERS);
                                nextTurnMovement.setText(YOU_HAVE_ARRIVE);
                            }
                            else {
                                if(!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                                    elevationDisplay(STAIRWELL_WAYPOINT, navigationPath.get(2)._elevation);
                            }
                            break;

                        case NORMAL_WAYPOINT:
                            if(navigationPath.size() == 2){
                                howFarToMove.setText("" + distance + " " + METERS);
                                nextTurnMovement.setText(YOU_HAVE_ARRIVE);
                            }
                            break;
                    }

                    if(turnNotificationForPopup != null)
                        showHintAtWaypoint(MAKETURN_NOTIFIER);

                    turnNotificationForPopup = FRONT_LEFTSIDE;
                    break;

                case STAIR:
                    turnNotificationForPopup = STAIR;
                    if(navigationPath.size() > 2){
                        //Up Stair
                        if(navigationPath.get(2)._elevation > navigationPath.get(0)._elevation){
                            switch(navigationPath.get(2)._elevation){
                                case 1:
                                    firstMovement.setText(WALKING_UP_STAIR + TO_FIRST_FLOOR);
                                    break;
                                case 2:
                                    firstMovement.setText(WALKING_UP_STAIR + TO_SECOND_FLOOR);
                                    break;
                            }
                        }//Down Stair
                        else if(navigationPath.get(2)._elevation < navigationPath.get(0)._elevation){
                            switch (navigationPath.get(2)._elevation){
                                case 1:
                                    firstMovement.setText(WALKING_DOWN_STAIR + TO_FIRST_FLOOR);
                                    break;
                                case 2:
                                    firstMovement.setText(WALKING_DOWN_STAIR + TO_SECOND_FLOOR);
                                    break;
                            }
                        }
                    }
                    else if(navigationPath.size() == 2){
                        //Up Stair
                        if(navigationPath.get(1)._elevation > navigationPath.get(0)._elevation){
                            switch (navigationPath.get(1)._elevation){
                                case 1:
                                    firstMovement.setText(WALKING_UP_STAIR + TO_FIRST_FLOOR);
                                    break;
                                case 2:
                                    firstMovement.setText(WALKING_UP_STAIR + TO_SECOND_FLOOR);
                                    break;
                            }
                        }
                        else if(navigationPath.get(1)._elevation < navigationPath.get(0)._elevation){
                            switch (navigationPath.get(1)._elevation){
                                case 1:
                                    firstMovement.setText(WALKING_DOWN_STAIR + TO_FIRST_FLOOR);
                                    break;
                                case 2:
                                    firstMovement.setText(WALKING_DOWN_STAIR + TO_SECOND_FLOOR);
                                    break;
                            }
                        }
                    }
                    howFarToMove.setText("");
                    nextTurnMovement.setText("");

                    if(turnNotificationForPopup != null && firstTurn == false)
                        showHintAtWaypoint(MAKETURN_NOTIFIER);
                    walkedWaypoint = 0;
                    sourceID = navigationPath.get(1)._waypointID;

                    if(navigationPath.get(1)._elevation > navigationPath.get(0)._elevation)
                        imageTurnIndicator.setImageResource(R.drawable.stairs_up);
                    else if(navigationPath.get(1)._elevation < navigationPath.get(0)._elevation)
                        imageTurnIndicator.setImageResource(R.drawable.stairs_down);
                    break;

                case ELEVATOR:
                    turnNotificationForPopup = ELEVATOR;
                    firstMovement.setText(WAIT_FOR_ELEVATOR + TO +navigationPath.get(1)._elevation + FLOOR);
                    howFarToMove.setText("");
                    nextTurnMovement.setText("");
                    imageTurnIndicator.setImageResource(R.drawable.elevator);
                    if(turnNotificationForPopup != null)
                        showHintAtWaypoint(MAKETURN_NOTIFIER);

                    walkedWaypoint = 0;
                    sourceID = navigationPath.get(1)._waypointID;
                    break;

                case ARRIVED:
                    if(turnNotificationForPopup != null)
                        showHintAtWaypoint(ARRIVED_NOTIFIER);

                    firstMovement.setText("");
                    howFarToMove.setText("");
                    nextTurnMovement.setText("");

                    walkedWaypoint = 0;
                    break;

                case WRONG:
                    List<Node> newPath = new ArrayList<>();
                    List<Node> wrongPath = new ArrayList<>();
                    wrongWaypoint = allWaypointData.get(currentLBeaconID);
                    currentLocationReminder.setText(PRESENT_POSITION + currentLocationName);

                    //錯誤點為終點 -> 直接抵達
                    for(int i = 0; i < wrongWaypoint._attachIDs.size(); i++){
                        if(endNode._mainID != 0 && (endNode._mainID == wrongWaypoint._attachIDs.get(i))){
                            showHintAtWaypoint(ARRIVED_NOTIFIER);
                            arriveInWrong = true;
                            break;
                        }
                    }
                    if(wrongWaypoint._waypointID.equals(endNode._waypointID)){
                        showHintAtWaypoint(ARRIVED_NOTIFIER);
                        arriveInWrong = true;
                    }

                    if(arriveInWrong == false){
                        isLongerPath = false;
                        jumpNode = true;

                        //搜尋上個點的鄰居是否包含目前錯誤點，來判斷是否跳點
                        for(int i = 0; i < lastNode._adjacentWaypoints.size(); i++){
                            if(lastNode._adjacentWaypoints.get(i).equals(wrongWaypoint._waypointID))
                                jumpNode = false;
                        }

                        //推出走錯前的上一個點
                        if(jumpNode == true){
                            directionCompute = true;
                            tmpdestinationID = destinationID;
                            tmpdestinationRegion = destinationRegion;

                            sourceID = startNode._waypointID;
                            sourceRegion = startNode._regionID;
                            destinationID = wrongWaypoint._waypointID;
                            destinationRegion = wrongWaypoint._regionID;
                            loadNavigationGraph();
                            wrongPath = startNavigation();

                            if(wrongPath.size() > 2)
                                lastNode = wrongPath.get(wrongPath.size() - 2);

                            directionCompute = false;
                            destinationID = tmpdestinationID;
                            destinationRegion = tmpdestinationRegion;
                        }

                        //Reroute
                        sourceID = wrongWaypoint._waypointID;
                        sourceRegion = wrongWaypoint._regionID;
                        loadNavigationGraph();
                        newPath = startNavigation();

                        for(int i = 0; i < newPath.size(); i++){
                            if(newPath.get(i)._waypointName.equals(lastNode._waypointName)){
                                //表示新的導航路線須迴轉
                                isLongerPath = true;
                                break;
                            }
                            isLongerPath = false;
                        }

                        if(isLongerPath){
                            currentLBeaconID = "EmptyString";
                            navigationPath = newPath;
                            progressBar.setMax(navigationPath.size());
                            progressStatus = 0;
                            nowDoInstruction.setText(TURN_AROUND);
                            firstMovement.setText(WAIT_FOR_SIGNAL);
                            howFarToMove.setText("");
                            nextTurnMovement.setText("");
                            turnNotificationForPopup = "goback";
                            imageTurnIndicator.setImageResource(R.drawable.turn_back);

                            showHintAtWaypoint(MAKETURN_NOTIFIER);
                            turnNotificationForPopup = null;
                        }
                        else {
                            navigationPath = newPath;
                            progressBar.setMax(navigationPath.size());
                            progressStatus = 1;
                            progressNumber.setText(progressStatus + "/" + progressBar.getMax());
                            turnNotificationForPopup = null;
                            firstMovement.setText(GO_STRAIGHT_ABOUT);

                            //目前指令
                            if(navigationPath.size() >= 2){
                                howFarToMove.setText("" + GeoCalulation.getDistance(navigationPath.get(0), navigationPath.get(1)) + " " + METERS
                                                            + TO + navigationPath.get(1)._waypointName);

                                turnNotificationForPopup = getDirectionFromBearing(lastNode, navigationPath.get(0), navigationPath.get(1));
                            }
                            currentLocationReminder.setText(PRESENT_POSITION  + currentLocationName);

                            showHintAtWaypoint(MAKETURN_NOTIFIER);

                            //算下一步指令
                            if(navigationPath.size() >= 3){
                                //路線在同一層
                                if(navigationPath.get(2)._elevation == navigationPath.get(1)._elevation){
                                    turnNotificationForPopup = getDirectionFromBearing(navigationPath.get(0), navigationPath.get(1), navigationPath.get(2));
                                }
                                //路線跨樓層
                                //上樓
                                else if(navigationPath.get(2)._elevation > navigationPath.get(0)._elevation){
                                    ShowDirectionFromConnectPoint();
                                    switch(navigationPath.get(2)._elevation){
                                        case 1:
                                            if(navigationPath.get(1)._nodeType == ELEVATOR_WAYPIOINT)
                                                nextTurnMovement.setText(THEN_TAKE_ELEVATOR + TO_FIRST_FLOOR);
                                            else if(navigationPath.get(1)._nodeType == STAIRWELL_WAYPOINT)
                                                nextTurnMovement.setText(THEN_WALK_UP_STAIR + TO_FIRST_FLOOR);
                                            break;
                                        case 2:
                                            if(navigationPath.get(1)._nodeType == ELEVATOR_WAYPIOINT)
                                                nextTurnMovement.setText(THEN_TAKE_ELEVATOR + TO_SECOND_FLOOR);
                                            else if(navigationPath.get(1)._nodeType == STAIRWELL_WAYPOINT)
                                                nextTurnMovement.setText(THEN_WALK_UP_STAIR + TO_SECOND_FLOOR);
                                            break;
                                    }
                                }
                                //下樓
                                else if(navigationPath.get(2)._elevation < navigationPath.get(0)._elevation){
                                    ShowDirectionFromConnectPoint();
                                    switch(navigationPath.get(2)._elevation){
                                        case 1:
                                            if(navigationPath.get(1)._nodeType == ELEVATOR_WAYPIOINT)
                                                nextTurnMovement.setText(THEN_TAKE_ELEVATOR + TO_FIRST_FLOOR);
                                            else if(navigationPath.get(1)._nodeType == STAIRWELL_WAYPOINT)
                                                nextTurnMovement.setText(THEN_WALK_DOWN_STAIR + TO_FIRST_FLOOR);
                                            break;
                                        case 2:
                                            if(navigationPath.get(1)._nodeType == ELEVATOR_WAYPIOINT)
                                                nextTurnMovement.setText(THEN_TAKE_ELEVATOR + TO_SECOND_FLOOR);
                                            else if(navigationPath.get(1)._nodeType == STAIRWELL_WAYPOINT)
                                                nextTurnMovement.setText(THEN_WALK_DOWN_STAIR + TO_SECOND_FLOOR);
                                            break;
                                    }
                                }

                                //新路徑需垮樓層且目前點為樓梯或電梯
                                if(navigationPath.get(0)._connectPointID != 0 && navigationPath.get(0)._connectPointID == navigationPath.get(1)._connectPointID){
                                    elevationDisplay(navigationPath.get(1)._nodeType, navigationPath.get(1)._elevation);
                                    if(navigationPath.get(1)._nodeType == ELEVATOR_WAYPIOINT)
                                        turnNotificationForPopup = ELEVATOR;
                                    else if(navigationPath.get(1)._nodeType == STAIRWELL_WAYPOINT)
                                        turnNotificationForPopup = STAIR;

                                    if(navigationPath.size() > 2){
                                        switch(navigationPath.get(2)._elevation){
                                            case 1:
                                                if(navigationPath.get(1)._nodeType == ELEVATOR_WAYPIOINT)
                                                    firstMovement.setText(PLEASE_TAKE_ELEVATOR + TO_FIRST_FLOOR);
                                                else if(navigationPath.get(1)._nodeType == STAIRWELL_WAYPOINT){
                                                    if(navigationPath.get(2)._elevation > navigationPath.get(0)._elevation)
                                                        firstMovement.setText(WALKING_UP_STAIR + TO_FIRST_FLOOR);
                                                    else if(navigationPath.get(2)._elevation < navigationPath.get(0)._elevation)
                                                        firstMovement.setText(WALKING_DOWN_STAIR + TO_FIRST_FLOOR);
                                                }
                                                break;
                                            case 2:
                                                if(navigationPath.get(1)._nodeType == ELEVATOR_WAYPIOINT)
                                                    firstMovement.setText(PLEASE_TAKE_ELEVATOR + TO_SECOND_FLOOR);
                                                else if(navigationPath.get(1)._nodeType == STAIRWELL_WAYPOINT){
                                                    if(navigationPath.get(2)._elevation > navigationPath.get(0)._elevation)
                                                        firstMovement.setText(WALKING_UP_STAIR + TO_SECOND_FLOOR);
                                                    else if(navigationPath.get(2)._elevation < navigationPath.get(0)._elevation)
                                                        firstMovement.setText(WALKING_DOWN_STAIR + TO_SECOND_FLOOR);
                                                }
                                                break;
                                        }
                                    }
                                    howFarToMove.setText("");
                                    nextTurnMovement.setText("");
                                    if(turnNotificationForPopup != null)
                                        showHintAtWaypoint(MAKETURN_NOTIFIER);
                                    walkedWaypoint = 0;
                                    sourceID = navigationPath.get(1)._waypointID;
                                    if(navigationPath.get(1)._nodeType == ELEVATOR_WAYPIOINT)
                                        imageTurnIndicator.setImageResource(R.drawable.elevator);
                                    else if(navigationPath.get(1)._nodeType == STAIRWELL_WAYPOINT){
                                        if(navigationPath.get(1)._elevation > navigationPath.get(0)._elevation)
                                            imageTurnIndicator.setImageResource(R.drawable.stairs_up);
                                        else if(navigationPath.get(1)._elevation < navigationPath.get(0)._elevation)
                                            imageTurnIndicator.setImageResource(R.drawable.stairs_down);
                                    }
                                }
                            }
                            else 
                                nextTurnMovement.setText(THEN_ARRIVE);

                            if(jumpNode == false){
                                callDirectionInWrong = true;
                                ShowDirectionFromConnectPoint();
                            }
                            else {
                               turnNotificationForPopup = getDirectionFromBearing(lastNode, navigationPath.get(0), navigationPath.get(1));
                               showHintAtWaypoint(MAKETURN_NOTIFIER);

                               if(navigationPath.size() > 2){
                                   turnNotificationForPopup = getDirectionFromBearing(navigationPath.get(0),navigationPath.get(1), navigationPath.get(2));
                                   if(navigationPath.get(0)._adjacentWaypoints.size() <= 2 && navigationPath.get(1)._adjacentWaypoints.size() <= 2 && navigationPath.get(0)._elevation == lastNode._elevation){
                                       turnNotificationForPopup = FRONT;
                                       lastisSlash = true;
                                   }
                               }
                            }
                            lastNode = navigationPath.get(0);
                            navigationPath.remove(0);
                            passedGroupID = navigationPath.get(0)._groupID;
                        }
                    }
                    break;
            }

            if(navigationPath.size() > 1){
                if(!passedRegionID.equals(navigationPath.get(0)._regionID))
                    regionIndex++;
                passedRegionID = navigationPath.get(0)._regionID;
                passedGroupID = navigationPath.get(0)._groupID;

                if(!turnDirection.equals(WRONG) && navigationPath.size() > 2 && lastNode._elevation == navigationPath.get(0)._elevation){
                    if(navigationPath.get(0)._adjacentWaypoints.size() <= 2 && navigationPath.get(1)._adjacentWaypoints.size() <= 2 && navigationPath.get(0)._elevation == navigationPath.get(1)._elevation){
                        turnNotificationForPopup = FRONT;
                        turnDirection = FRONT;
                        showHintAtWaypoint(MAKETURN_NOTIFIER);
                        lastisSlash = true;
                        if(navigationPath.get(1)._waypointID.equals(endNode._waypointID))
                            nextTurnMovement.setText(THEN_ARRIVE);
                    }
                }
            }

            if(arriveInWrong == false && firstTurn == false)
                readNavigationInstruction();

            if(!turnDirection.equals(WRONG)){
                lastNode = navigationPath.get(0);
                navigationPath.remove(0);
            }

            firstTurn = false;
            lastProcessedBeacon = lastNode._waypointID;

            if(firstTurn == false){
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showBackgroud();
                    }
                }, 1000);
            }
        }


    public void readNavigationInstruction(){
        textToSpeech.speak(firstMovement.getText().toString() + howFarToMove.getText().toString() +
                            nextTurnMovement.getText().toString(), TextToSpeech.QUEUE_ADD, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        offset = (double)sharedPreferences.getFloat("offset",0);
        
        //GyroSensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        gyroscopeEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(event.values[2] > 2f) //Left
                    imageTurnIndicator.setImageResource(R.drawable.front_arrow);

                else if(event.values[2] < -2f)
                    imageTurnIndicator.setImageResource(R.drawable.front_arrow);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        //Initial UI component
        nowDoInstruction = findViewById(R.id.nowDoInsturction);
        firstMovement = findViewById(R.id.firstDoInstruction);
        howFarToMove = findViewById(R.id.distanceText);
        nextTurnMovement = findViewById(R.id.nextDoInsturction);
        destinationReminder = findViewById(R.id.destinationText);
        currentLocationReminder = findViewById(R.id.currentLocation);
        imageTurnIndicator = findViewById(R.id.imageViewPg);
        postionOfPopup = findViewById(R.id.navigationLayout);
        progressBar = findViewById(R.id.progressBar);
        progressNumber = findViewById(R.id.progressNumber);
        signalProcessor = new Signal_Processor(NavigationActivity.this);

        //Voice engine setup
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR)
                    textToSpeech.setLanguage(Locale.CHINESE);
            }
        });

        languageSetup();

        //Receive the data passed from MainActivity
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            destinationID = bundle.getString("destinationID");
            destinationName = bundle.getString("destinationName");
            destinationRegion = bundle.getString("destinationRegion");
        }
        Log.i("destination","destinationID = " + bundle.getString("destinationID")
                                    + "destinationName = " + bundle.getString("destinationName")
                                    + "destinationRegion = " +bundle.getString("destinationRegion"));
        //Load region data from region graph
        regionGraph = DataParser.getRegionDataFromRegionGraph(this);
        mappingOfRegionNameAndID = DataParser.waypointNameAndIDMappings(this,regionGraph.getAllRegionNames());
        navigationPath.add(new Node("empty", "empty", "empty", "empty"));

        //Load all waypoint data for precise position
        loadAllWaypointData();

        //Load the virtual node
        virtualNodeUP = DataParser.getVirtualNode(this, VIRTUAL_UP);
        virtualNodeDown = DataParser.getVirtualNode(this, VIRTUAL_DOWN);
        destinationReminder.setText(DESTINATION + destinationName);

        //LBeacon Manager setup
        beaconManagerSetup();

        threadForHandleLbeaconID = new Thread(new NavigationThread());
        threadForHandleLbeaconID.start();

        //Handle for navigation instruction
        instructionHandler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                String turnDirection = (String) msg.obj;

                int distance = 0;

                if(navigationPath.size() >= 2)
                    distance = (int) GeoCalulation.getDistance(navigationPath.get(0), navigationPath.get(1));

                navigationInstructionDisplay(turnDirection, distance);
            }
        };

        //Handle for current position
        currentPositionHandler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                currentLocationName = (String) msg.obj;
                currentLocationReminder.setText(PRESENT_POSITION + currentLocationName);
            }
        };

        walkedPointHandler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                int numberOfWaypointTraveled = (int)msg.obj;
                if(numberOfWaypointTraveled == 1 && navigationPath.size() >= 2)
                    turnNotificationForPopup = null;
            }
        };

        progressBar.setMax(navigationPath.size());

        progressHandler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                Boolean isMakingProgress = (Boolean) msg.obj;
                if(isMakingProgress == true){
                    progressStatus += 1;
                    progressBar.setProgress(progressStatus);
                    progressNumber.setText(progressStatus  + "/" + progressBar.getMax());
                }
            }
        };
    }

    @Override
    protected void onResume(){
        super.onResume();
        beaconManagerSetup();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        beaconManager.removeAllMonitorNotifiers();
        beaconManager.removeAllRangeNotifiers();
        beaconManager.unbind(this);
        System.gc();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            beaconManager.removeAllMonitorNotifiers();
            beaconManager.removeAllRangeNotifiers();
            beaconManager.unbind(NavigationActivity.this);
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_navigation,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.menu_home){
            beaconManager.removeAllMonitorNotifiers();
            beaconManager.removeAllRangeNotifiers();
            beaconManager.unbind(NavigationActivity.this);
            Intent intent = new Intent();
            intent = new Intent(NavigationActivity.this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
