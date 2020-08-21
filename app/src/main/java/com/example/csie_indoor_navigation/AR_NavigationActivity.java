package com.example.csie_indoor_navigation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.csie_indoor_navigation.Signal_Processing.DeviceParameter;
import com.example.csie_indoor_navigation.Signal_Processing.Signal_Processor;
import com.viro.core.ARAnchor;
import com.viro.core.ARNode;
import com.viro.core.ARScene;
import com.viro.core.AmbientLight;
import com.viro.core.AsyncObject3DListener;
import com.viro.core.CameraListener;
import com.viro.core.Object3D;
import com.viro.core.Vector;
import com.viro.core.ViroView;
import com.viro.core.ViroViewARCore;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;

import java.lang.ref.WeakReference;
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

import static com.example.csie_indoor_navigation.GeoCalulation.getBearingOfTwoPoints;
import static com.example.csie_indoor_navigation.GeoCalulation.getDirectionFromBearing;
import static com.example.csie_indoor_navigation.Setting.getPreferenceValue;


public class AR_NavigationActivity extends AppCompatActivity implements BeaconConsumer {
    //--------------------UI of Layout--------------------
    private TextView facingDirectionText;
    private TextView destinationText;
    private TextView currentLocationText;
    private ProgressBar progressBar;
    private TextView progressNumber;
    int progressStatus = 0;

    private String turnNotificationForPopup = null;

    //--------------------Viro Core uses Parameters--------------------
    private ViroView mViroView;
    //The ARScene we will be creating within this activity.
    private com.viro.core.ARScene mScene;
    //The list of placed node
    private List<ARNode> placedNode;
    private List<String> placedID;
    //Trace Camera Position, Rotation and Forward
    private Vector lastCameraPosition;
    private Vector lastCameraRotation;
    private Vector lastCameraForward;

    //Boolean to show whether  or not cross the floor
    private boolean isOverFloor;
    //---------------------Sensor---------------------
    private SensorManager sensorManager;
    //accelerometer
    private Sensor accelerometerSensor;
    float[] accelerometerValue = new float[3];
    //magnetic
    private Sensor magneticSensor;
    float[] magneticValue = new float[3];
    //toShowCurrentDirection
    private Integer currentDirection;
    private Integer predictDirection;

    //---------------------Connect Waypoint Category---------------------
    private static final int NORMAL_WAYPOINT = 0;
    private static final int ELEVATOR_WAYPOINT = 1;
    private static final int STAIRWELL_WAYPOINT = 2;
    private static final int CONNECTPOINT = 3;
    //---------------------Intruction Category to decise which UI to Show---------------------
    private static final int ARRIVED_NOTIFIER = 0;
    private static final int WRONGWAY_NOTIFIER = 1;
    private static final int MAKETURN_NOTIFIER = 2;
    //---------------------Virtual Node---------------------
    private static final int VIRTUAL_UP = 1;
    private static final int VIRTUAL_DOWN = 2;

    //---------------------String of UI---------------------
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

    private String PRESENT_POSITION;
    private String DESTINATION;

    private String REROUTING;
    private String TURN_AROUND;
    private String WAIT_FOR_SIGNAL;
    private String THEN_ARRIVE;

    private String INIT_DIRECTION_TITLE;

    //---------------------Direcion List---------------------
    private String NORTH;
    private String NORTHEAST;
    private String EAST;
    private String SOUTHEAST;
    private String SOUTH;
    private String SOUTHWEST;
    private String WEST;
    private String NORTHWEST;
    private List<String> directionList;
    private String PLEASE_FACE_TO;
    private String DIRECTION;
    private String RIGHT_DIRECTION;
    private String FACING_DIRECTION;

    //---------------------Intruction List---------------------
    private static final String FRONT = "front";
    private static final String FRONT_RIGHTSIDE = "frontRightSide";
    private static final String FRONT_LEFTSIDE = "frontLeftSide";
    private static final String LEFT = "left";
    private static final String FRONT_LEFT = "frontLeft";
    private static final String REAR_LEFT = "rearLeft";
    private static final String RIGHT = "right";
    private static final String FRONT_RIGHT = "frontRight";
    private static final String REAR_RIGHT = "rearRight";
    private static final String ELEVATOR = "elevator";
    private static final String STAIR = "stair";
    private static final String ARRIVED = "arrived";
    private static final String WRONG = "wrong";

    //---------------------Bluetooth---------------------
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    //---------------------Routing Information--------------------
    private String  sourceID,
                    destinationID,
                    sourceRegion,
                    destinationRegion,
                    destinationName,
                    tmpdestinationID,
                    tmpDestinationRegion;
    private String currentLocationName;
    private Node startNode, endNode, lastNode, wrongWaypoint, chosenStartNode;
    //Integer to record how many waypoints hava been traveled
    private int walkedWaypoint = 0;
    private int pathLength = 0;
    private int regionIndex = 0;
    private int passedGroupID = -1;
    private String passedRegionID;
    List<String> tmpDestinationID = new ArrayList<>();
    //Counter for count the receive the same LBeacon times
    private int errorCount = 0;
    private int turnBackCount = 0;
    private Node tmpNode;
    private Node recordBeacon;
    private int nextFloor;
    private String firstMovement;
    private String nextTurnMovement;
    private String howFarToMove;

    //Map Information
    //List of NavigationSubgraph object representng a Navigation Graph
    List<NavigationSubgraph> navigationGraph = new ArrayList<>();
    List<NavigationSubgraph> navigationGraphForAllWaypoint = new ArrayList<>();
    //List of Region object storing the information of regions that will be traveled through
    List<Region> regionPath = new ArrayList<>();
    //Hashmap for storing region data
    RegionGraph regionGraph = new RegionGraph();
    //List of Node object representing a navigation path
    List<Node> navigationPath = new ArrayList<>();
    List<Node> virtualNodeUP = new ArrayList<>();
    List<Node> virtualNodeDown = new ArrayList<>();
    HashMap<String, String> navigationPath_ID_to_Name_Mapping = new HashMap<>();
    HashMap<String, String> mappingOfRegionNameAndID = new HashMap<>();
    HashMap<String, Node> allWaypointData = new HashMap<>();

    //---------------------Virtual Node---------------------
    private TextToSpeech voiceEngine;

    //---------------------Beacon Manager---------------------
    private BeaconManager beaconManager;
    private org.altbeacon.beacon.Region regionForBeacon;
    Thread threadForHandleLBeaconID;
    //Handler for "threadForHandleLBeaconID", receive message from the thread
    static Handler instructionHandler, currentPositionHandler, walkedPointHandler, progressHandler;
    //Synchronization between LBeacon receiver and handler thread
    final Object sync = new Object();
    //String for storing currently receive LBeacon ID
    String currentLBeaconID = "";
    String lastRoundBeaconID = "";

    //Signal Processor
    private Signal_Processor signalProcessor;
    private DateFormat dateFormat = new SimpleDateFormat("yy_MM_DD_hh_mm");
    private DeviceParameter deviceParameter;
    String receiveBeacon;
    double offset;
    String lastProcessedBeacon = "";

    //---------------------Boolean for judge---------------------
    boolean isFirstBeacon = true;
    boolean firstTurn = true;
    boolean isInVirtualNode = false;
    boolean stairGoUP = false;
    boolean lastisSlash = false;
    boolean directionCompute = false;
    boolean jumpNode = false;
    boolean arriveInWrong = false;
    boolean isLongerPath = false;
    boolean callDirectionInWrong = false;
    boolean calibration = false;

    //---------------------Language option---------------------
    String languageOption;

    //Initial Time
    private long startTime = System.currentTimeMillis();


    public void languageSetup(){
        SharedPreferences languagePref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        languageOption = languagePref.getString("language","繁體中文");
        directionList = new ArrayList<String>();
        if(languageOption.equals("繁體中文")){
            TITLE = getString(R.string.CHT_TITLE);
            INIT_DIRECTION_TITLE = getString(R.string.CHT_PLEASE_FACE_TO_THE_DIRECION);

            //指令
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
            NOW_WALK_UP_STAIR = getString(R.string.CHT_WALKING_UP_STAIR);
            NOW_WALK_DOWN_STAIR = getString(R.string.CHT_WALKING_DOWN_STAIR);

            GO_STRAIGHT_ABOUT = getString(R.string.CHT_GO_STRAIGHT_ABOUT);
            THEN_GO_STRAIGHT = getString(R.string.CHT_THEN_GO_STRAIGHT);
            THEN_GO_STRAIGHT_RIGHTSIDE = getString(R.string.CHT_THEN_GO_STRAIGHT_RIGHTSIDE);
            THEN_GO_STRAIGHT_LEFTSIDE = getString(R.string.CHT_THEN_GO_STRAIGHT_LEFTSIDE);
            THEN_TURN_LEFT = getString(R.string.CHT_THEN_TURN_LEFT);
            THEN_TURN_RIGHT = getString(R.string.CHT_THEN_TURN_RIGHT);
            THEN_TURN_FRONT_LEFT = getString(R.string.CHT_THEN_TURN_FORNT_LEFT);
            THEN_TURN_FRONT_RIGHT = getString(R.string.CHT_THEN_TURN_FORNT_RIGHT);
            THEN_TURN__REAR_RIGHT = getString(R.string.CHT_THEN_TURN_REAR_RIGHT);
            THEN_TURN_REAR_LEFT = getString(R.string.CHT_THEN_TURN_REAR_LEFT);
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
            PLEASE_TURN_REAR_RIGHT = getString(R.string.CHT_PLEASE_TURN_REAR_RIGHT);
            PLEASE_TURN_REAR_LEFT = getString(R.string.CHT_PLEASE_TURN_REAR_LEFT);
            PLEASE_TAKE_ELEVATOR = getString(R.string.CHT_PLEASE_TAKE_ELEVATOR);
            PLEASE_WALK_UP_STAIR = getString(R.string.CHT_PLEASE_WALK_UP_STAIR);

            TO = getString(R.string.CHT_TO);
            TO_FIRST_FLOOR = getString(R.string.CHT_TO_FIRST_FLOOR);
            TO_SECOND_FLOOR = getString(R.string.CHT_TO_SECOND_FLOOR);
            FLOOR = getString(R.string.CHT_FLOOR);

            PRESENT_POSITION = getString(R.string.CHT_PRESENT_POSITION);
            DESTINATION = getString(R.string.CHT_DESTINATION);

            directionList.clear();
            NORTH = getString(R.string.CHT_NORTH);
            directionList.add(NORTH);
            NORTHEAST = getString(R.string.CHT_NORTHEAST);
            directionList.add(NORTHEAST);
            EAST = getString(R.string.CHT_EAST);
            directionList.add(EAST);
            SOUTHEAST = getString(R.string.CHT_SOUTHEAST);
            directionList.add(SOUTHEAST);
            SOUTH = getString(R.string.CHT_SOUTH);
            directionList.add(SOUTH);
            SOUTHWEST = getString(R.string.CHT_SOUTHWEST);
            directionList.add(SOUTHWEST);
            WEST = getString(R.string.CHT_WEST);
            directionList.add(WEST);
            NORTHWEST = getString(R.string.CHT_NORTHWEST);
            directionList.add(NORTHWEST);
            PLEASE_FACE_TO = getString(R.string.CHT_PLEASE_FACE_TO);
            DIRECTION = getString(R.string.CHT_DIRECION);
            RIGHT_DIRECTION = getString(R.string.CHT_RIGHTDIRECION);
            FACING_DIRECTION = getString(R.string.CHT_FACING_DIRECION);
            TURN_AROUND = getString(R.string.CHT_TURN_BACK);
            REROUTING = getString(R.string.CHT_REROUTING);
            WAIT_FOR_SIGNAL = getString(R.string.CHT_WAIT_FOR_INSTRUCTION);
            THEN_ARRIVE = getString(R.string.CHT_THEN_ARRIVE);
        }
        else if(languageOption.equals("English")){
            TITLE = getString(R.string.ENG_TITLE);
            INIT_DIRECTION_TITLE = getString(R.string.ENG_PLEASE_FACE_TO_THE_DIRECION);
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
            NOW_WALK_UP_STAIR = getString(R.string.ENG_WALKING_UP_STAIR);
            NOW_WALK_DOWN_STAIR = getString(R.string.ENG_WALKING_DOWN_STAIR);

            GO_STRAIGHT_ABOUT = getString(R.string.ENG_GO_STRAIGHT_ABOUT);
            THEN_GO_STRAIGHT = getString(R.string.ENG_THEN_GO_STRAIGHT);
            THEN_GO_STRAIGHT_RIGHTSIDE = getString(R.string.ENG_THEN_GO_STRAIGHT_RIGHTSIDE);
            THEN_GO_STRAIGHT_LEFTSIDE = getString(R.string.ENG_THEN_GO_STRAIGHT_LEFTSIDE);
            THEN_TURN_LEFT = getString(R.string.ENG_THEN_TURN_LEFT);
            THEN_TURN_RIGHT = getString(R.string.ENG_THEN_TURN_RIGHT);
            THEN_TURN_FRONT_LEFT = getString(R.string.ENG_THEN_TURN_FORNT_LEFT);
            THEN_TURN_FRONT_RIGHT = getString(R.string.ENG_THEN_TURN_FORNT_RIGHT);
            THEN_TURN__REAR_RIGHT = getString(R.string.ENG_THEN_TURN_REAR_RIGHT);
            THEN_TURN_REAR_LEFT = getString(R.string.ENG_THEN_TURN_REAR_LEFT);
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
            PLEASE_TURN_REAR_RIGHT = getString(R.string.ENG_PLEASE_TURN_REAR_RIGHT);
            PLEASE_TURN_REAR_LEFT = getString(R.string.ENG_PLEASE_TURN_REAR_LEFT);
            PLEASE_TAKE_ELEVATOR = getString(R.string.ENG_PLEASE_TAKE_ELEVATOR);
            PLEASE_WALK_UP_STAIR = getString(R.string.ENG_PLEASE_WALK_UP_STAIR);

            TO = getString(R.string.ENG_TO);
            TO_FIRST_FLOOR = getString(R.string.ENG_TO_FIRST_FLOOR);
            TO_SECOND_FLOOR = getString(R.string.ENG_TO_SECOND_FLOOR);
            FLOOR = getString(R.string.ENG_FLOOR);

            PRESENT_POSITION = getString(R.string.ENG_PRESENT_POSITION);
            DESTINATION = getString(R.string.ENG_DESTINATION);

            directionList.clear();
            NORTH = getString(R.string.ENG_NORTH);
            directionList.add(NORTH);
            NORTHEAST = getString(R.string.ENG_NORTHEAST);
            directionList.add(NORTHEAST);
            EAST = getString(R.string.ENG_EAST);
            directionList.add(EAST);
            SOUTHEAST = getString(R.string.ENG_SOUTHEAST);
            directionList.add(SOUTHEAST);
            SOUTH = getString(R.string.ENG_SOUTH);
            directionList.add(SOUTH);
            SOUTHWEST = getString(R.string.ENG_SOUTHWEST);
            directionList.add(SOUTHWEST);
            WEST = getString(R.string.ENG_WEST);
            directionList.add(WEST);
            NORTHWEST = getString(R.string.ENG_NORTHWEST);
            directionList.add(NORTHWEST);
            PLEASE_FACE_TO = getString(R.string.ENG_PLEASE_FACE_TO);
            DIRECTION = getString(R.string.ENG_DIRECION);
            RIGHT_DIRECTION = getString(R.string.ENG_RIGHTDIRECION);
            FACING_DIRECTION = getString(R.string.ENG_FACING_DIRECION);
            TURN_AROUND = getString(R.string.ENG_TURN_BACK);
            REROUTING = getString(R.string.ENG_REROUTING);
            WAIT_FOR_SIGNAL = getString(R.string.ENG_WAIT_FOR_INSTRUCTION);
            THEN_ARRIVE = getString(R.string.ENG_THEN_ARRIVE);
        }

    }

    public void loadAllWaypointData(){
        navigationGraphForAllWaypoint = DataParser.getWaypointDataFromNavigationGraph(this, regionGraph.getAllRegionNames());

        //將每個Region中的Node加入，建立所有的waypoint資料。
        Log.i("ARnavigationGraphForAllWaypoint", "size = " + navigationGraphForAllWaypoint.size());
        for(int i = 0; i < navigationGraphForAllWaypoint.size(); i++){
            Log.i("ARnavigationGraphForAllWaypoint", "get( "+ i +").nodeInSubgraph = " + navigationGraphForAllWaypoint.get(i).nodesInSubgraph.entrySet());
            allWaypointData.putAll(navigationGraphForAllWaypoint.get(i).nodesInSubgraph);
            Log.i("ARallWaypointData","" + allWaypointData.get(i));
        }

        signalProcessor.setAllWaypointData(allWaypointData);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, org.altbeacon.beacon.Region region) {
                if(firstTurn == true && calibration == false){
                    long endTime = System.currentTimeMillis();
                    if(endTime - startTime > 8000){
                        offset = offset - 5;
                        calibration = true;
                    }
                }

                if(beacons.size() > 0){
                    Iterator<Beacon> beaconIterator = beacons.iterator();
                    while(beaconIterator.hasNext()){
                        Beacon beacon = beaconIterator.next();
                        List<String> beaconList = signalProcessor.getLocation(beacon, 3, offset, lastProcessedBeacon, AR_NavigationActivity.this);
                        logBeaconData(beaconList);
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
        mappingOfRegionNameAndID = DataParser.waypointNameAndIDMappings(this, regionGraph.getAllRegionNames());

        //regionPath for storing Region objects represent the regions
        //that the user passes by from source to destination
        regionPath = regionGraph.getRegionPath(sourceRegion, destinationRegion);

        //A list of String of region name in regionPath
        List<String> regionPathID = new ArrayList<>();

        for(int i = 0; i < regionPath.size(); i++)
            regionPathID.add(regionPath.get(i)._regionName);

        //Load waypoint data from the navigation subgraphs according to the regionPathID
        navigationGraph = DataParser.getWaypointDataFromNavigationGraph(this,regionPathID);

        //Get the two Node objects that the represent starting point and destination
        startNode = navigationGraph.get(0).nodesInSubgraph.get(sourceID);
        endNode = navigationGraph.get(navigationGraph.size() - 1).nodesInSubgraph.get(destinationID);
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
    public List<Node> getShortestPathToDestination(Node destination){
        List<Node> path = new ArrayList<>();
        for(Node node = destination; node != null; node = node.previous){
            path.add(node);
        }

        Collections.reverse(path);
        return path;
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

                //Same floor
                if(regionPath.get(i)._elevation == regionPath.get(i + 1)._elevation){
                    destinationOfRegion = computePathToTraversePoint(navigationGraph.get(i).nodesInSubgraph.get(sourceID),true,i+1);
                    sourceID = destinationOfRegion.getID();
                }
                //Different floor
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
                                for(int count = 0; count < tmpDestinationID.size(); count++)
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

    public void cleanPlacedNode(List<ARNode> nodes)
    {
        for(int i = 0; i < nodes.size(); i++)
            nodes.get(i).detach();
        nodes.clear();
        isOverFloor = false;
    }

    public void cleanPlacedID(List<String> list)
    {
        list.clear();
    }

    private void add3DObject(String fileName, Vector position, String waypointID)
    {
        if(isOverFloor == true)
        {
            cleanPlacedNode(placedNode);
            cleanPlacedID(placedID);
        }

        ARNode node = mScene.createAnchoredNode(position.add(new Vector(0,-0.3, 0)));
        if(fileName == PLEASE_WALK_UP_STAIR)
        {
            isOverFloor = true;
            //Up Stair
            if(nextFloor > navigationPath.get(0)._elevation)
                fileName = "file:///android_asset/ARModel/upStair.obj";
            else if(nextFloor < navigationPath.get(0)._elevation)
                fileName = "file:///android_asset/ARModel/downStair.obj";
        }
        else if(fileName == PLEASE_TAKE_ELEVATOR)
        {
            isOverFloor = true;
            //Up
            if(nextFloor > navigationPath.get(0)._elevation)
                fileName= "file:///android_asset/ARModel/upElevator.obj";
            else if(nextFloor < navigationPath.get(0)._elevation)
                fileName = "file:///android_asset/ARModel/downElevator.obj";
        }

        final Object3D object3D = new Object3D();
        object3D.setScale(new Vector(1f,1f,1f));
        object3D.setRotation(mViroView.getLastCameraRotationEulerRealtime());
        object3D.loadModel(mViroView.getViroContext(), Uri.parse(fileName), Object3D.Type.OBJ, new AsyncObject3DListener() {
            @Override
            public void onObject3DLoaded(Object3D object3D, Object3D.Type type) {
                Log.i("load model", "Successfully loaded the model");
            }

            @Override
            public void onObject3DFailed(String s) {
                Log.i("load model", "Error loaded the model");
                Toast.makeText(AR_NavigationActivity.this,"An error occured when loading the 3D Object",Toast.LENGTH_LONG).show();
            }
        });

        if(object3D != null)
        {
            node.addChildNode(object3D);
            if(!placedNode.contains(node))
                placedNode.add(node);
            if(!placedID.contains(currentLBeaconID))
                placedID.add(currentLBeaconID);
        }
    }

    public void showHintAtWaypoint(final int instruction){
       LayoutInflater inflater = getLayoutInflater();
       View layout = inflater.inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toast_layout));
       ImageView image = (ImageView) layout.findViewById(R.id.toast_image);
       final Toast toast = new Toast(getApplicationContext());
       toast.setGravity(Gravity.CENTER_VERTICAL, 0,25);
       toast.setDuration(Toast.LENGTH_SHORT);
       toast.setView(layout);
       Vibrator myVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);

       int DistanceForShowHint = 0;
       if(navigationPath.size() >= 2)
           DistanceForShowHint = GeoCalulation.getDistance(navigationPath.get(0), navigationPath.get(1));

       String turnDirection = null;

       if(instruction == ARRIVED_NOTIFIER){
           turnDirection = YOU_HAVE_ARRIVE;
           image.setImageResource(R.drawable.arrived_1);
           voiceEngine.speak(turnDirection, TextToSpeech.QUEUE_ADD, null);
           toast.show();
           myVibrator.vibrate(800);
           beaconManager.removeAllMonitorNotifiers();
           beaconManager.removeAllRangeNotifiers();
           beaconManager.unbind(AR_NavigationActivity.this);

           Intent intent = new Intent(AR_NavigationActivity.this, MainActivity.class);
           intent.putExtra("Arrived_flag", 1);
           startActivity(intent);
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
           }, 1000);
           myVibrator.vibrate(1000);
       }
       else if(instruction == MAKETURN_NOTIFIER){
           switch (turnNotificationForPopup)
           {
               case RIGHT:
                   turnDirection = PLEASE_TURN_RIGHT;
                   add3DObject("file:///android_asset/ARModel/Right.obj",mViroView.getLastCameraPositionRealtime().add(mViroView.getLastCameraForwardRealtime()),navigationPath.get(0)._waypointID);
                   lastisSlash = false;
                   Log.i("Display Direction", "跳出指令方向 = " + turnNotificationForPopup);
                   break;

               case LEFT:
                   turnDirection = PLEASE_TURN_LEFT;
                   add3DObject("file:///android_asset/ARModel/Left.obj",mViroView.getLastCameraPositionRealtime().add(mViroView.getLastCameraForwardRealtime()),navigationPath.get(0)._waypointID);
                   lastisSlash = false;
                   Log.i("Display Direction", "跳出指令方向 = " + turnNotificationForPopup);
                   break;

               case FRONT_RIGHT:
                   turnDirection = PLEASE_TURN_FRONT_RIGHT;
                   if (lastisSlash == false) {
                       add3DObject("file:///android_asset/ARModel/RightFront.obj",mViroView.getLastCameraPositionRealtime().add(mViroView.getLastCameraForwardRealtime()),navigationPath.get(0)._waypointID);
                       lastisSlash = true;
                   } else {
                       add3DObject("file:///android_asset/ARModel/Front.obj",mViroView.getLastCameraPositionRealtime().add(mViroView.getLastCameraForwardRealtime()),navigationPath.get(0)._waypointID);
                       lastisSlash = false;
                   }
                   Log.i("Display Direction", "跳出指令方向 = " + turnNotificationForPopup);
                   break;
                   
               case FRONT_LEFT:
                   turnDirection = PLEASE_TURN_FRONT_LEFT;
                   if (lastisSlash == false) {
                       add3DObject("file:///android_asset/ARModel/LeftFront.obj",mViroView.getLastCameraPositionRealtime().add(mViroView.getLastCameraForwardRealtime()),navigationPath.get(0)._waypointID);
                       lastisSlash = true;
                   } else {
                       add3DObject("file:///android_asset/ARModel/Front.obj",mViroView.getLastCameraPositionRealtime().add(mViroView.getLastCameraForwardRealtime()),navigationPath.get(0)._waypointID);
                       lastisSlash = false;
                   }
                   Log.i("Display Direction", "跳出指令方向 = " + turnNotificationForPopup);
                   break;
                   
               case REAR_RIGHT:
                   turnDirection = PLEASE_TURN_REAR_RIGHT;
                   add3DObject("file:///android_asset/ARModel/R-back.obj",mViroView.getLastCameraPositionRealtime().add(mViroView.getLastCameraForwardRealtime()),navigationPath.get(0)._waypointID);
                   lastisSlash = false;
                   Log.i("Display Direction", "跳出指令方向 = " + turnNotificationForPopup);
                   break;
                   
               case REAR_LEFT:
                   turnDirection = PLEASE_TURN_REAR_LEFT;
                   add3DObject("file:///android_asset/ARModel/L-back.obj",mViroView.getLastCameraPositionRealtime().add(mViroView.getLastCameraForwardRealtime()),navigationPath.get(0)._waypointID);
                   lastisSlash = false;
                   Log.i("Display Direction", "跳出指令方向 = " + turnNotificationForPopup);
                   break;
                   
               case FRONT:
                   turnDirection = PLEASE_GO_STRAIGHT;
                   add3DObject("file:///android_asset/ARModel/Front.obj",mViroView.getLastCameraPositionRealtime().add(mViroView.getLastCameraForwardRealtime()),navigationPath.get(0)._waypointID);
                   lastisSlash = false;
                   Log.i("Display Direction", "跳出指令方向 = " + turnNotificationForPopup);
                   break;
                   
               case FRONT_RIGHTSIDE:
                   turnDirection = PLEASE_GO_STRAIGHT_RIGHTSIDE;
                   if (lastisSlash == false) {
                       add3DObject("file:///android_asset/ARModel/RightFront.obj",mViroView.getLastCameraPositionRealtime().add(mViroView.getLastCameraForwardRealtime()),navigationPath.get(0)._waypointID);
                       lastisSlash = true;
                   } else {
                       add3DObject("file:///android_asset/ARModel/Front.obj",mViroView.getLastCameraPositionRealtime().add(mViroView.getLastCameraForwardRealtime()),navigationPath.get(0)._waypointID);
                       lastisSlash = false;
                   }
                   Log.i("Display Direction", "跳出指令方向 = " + turnNotificationForPopup);
                   break;
                   
               case FRONT_LEFTSIDE:
                   turnDirection = PLEASE_GO_STRAIGHT_LEFTSIDE;
                   if (lastisSlash == false) {
                       add3DObject("file:///android_asset/ARModel/LeftFront.obj",mViroView.getLastCameraPositionRealtime().add(mViroView.getLastCameraForwardRealtime()),navigationPath.get(0)._waypointID);
                       lastisSlash = true;
                   } else {
                       add3DObject("file:///android_asset/ARModel/Front.obj",mViroView.getLastCameraPositionRealtime().add(mViroView.getLastCameraForwardRealtime()),navigationPath.get(0)._waypointID);
                       lastisSlash = false;
                   }
                   Log.i("Display Direction", "跳出指令方向 = " + turnNotificationForPopup);
                   break;
                   
               case STAIR:
                   turnDirection = PLEASE_WALK_UP_STAIR;
                   placedID.add(navigationPath.get(0)._waypointID);
                   lastisSlash = false;
                   Log.i("Display Direction", "跳出指令方向 = " + turnNotificationForPopup);
                   break;
               case ELEVATOR:
                   turnDirection = PLEASE_TAKE_ELEVATOR;
                   placedID.add(navigationPath.get(0)._waypointID);
                   lastisSlash = false;
                   Log.i("xxx_Direction", "跳出指令方向 = " + turnNotificationForPopup);
                   break;
                   
               case "goback":
                   turnDirection = " ";
                   image.setImageResource(R.drawable.turn_back);
                   toast.show();
                   myVibrator.vibrate(800);
                   Log.i("xxx_Direction", "跳出指令方向 = " + turnNotificationForPopup);
                   break;
           }
       }
    }

    private void logBeaconData(List<String> beacon){
        if(beacon.size() > 2){
            Node receiveNode;
            Boolean pass = false;

            receiveBeacon  = null;

            if(beacon.get(2).equals("close"))
                Log.i("closeBeacon","" + beacon.get(3));
            if(lastProcessedBeacon.equals(beacon.get(3)) && beacon.get(2).equals("close"))
                turnBackCount ++;
            else if(beacon.get(2).equals("close"))
                turnBackCount = 0;

            if(turnBackCount == 0 || turnBackCount > 5){
                if (beacon.get(2).equals("close") && navigationPath.size() > 0){
                    tmpNode = allWaypointData.get(beacon.get(3));
                    if(navigationPath.get(0)._waypointID.equals(beacon.get(3))){
                        receiveBeacon = beacon.get(3);
                        errorCount = 0;
                    }
                    else if(navigationPath.get(0)._groupID != 0 && navigationPath.get(0)._groupID == tmpNode._groupID){
                        receiveBeacon = beacon.get(3);
                        errorCount = 0;
                    }
                    else if(!navigationPath.get(0)._waypointID.equals(beacon.get(3)) && errorCount == 0){
                        recordBeacon = allWaypointData.get(beacon.get(3));
                        errorCount ++;
                    }
                    else if(!navigationPath.get(0)._waypointID.equals(beacon.get(3)) && errorCount == 1){
                        if(recordBeacon._waypointID.equals(beacon.get(3))){
                            receiveBeacon = beacon.get(3);
                            errorCount = 0;
                        }
                        else if(!recordBeacon._waypointID.equals(beacon.get(3)) && recordBeacon._groupID != 0){
                            if(tmpNode._groupID == recordBeacon._groupID){
                                receiveBeacon = beacon.get(3);
                                errorCount = 0;
                            }
                            else
                                errorCount = 0;
                        }
                        else
                            errorCount = 0;
                    }
                    else{
                        receiveBeacon = beacon.get(3);
                        errorCount = 0;
                    }
                }
            }

            receiveNode = allWaypointData.get(receiveBeacon);
            if(receiveNode != null)
                currentLocationText.setText(PRESENT_POSITION + receiveNode._waypointName);

            if(isFirstBeacon && receiveNode != null){
                chosenStartNode = receiveNode;
                sourceID = receiveNode._waypointID;
                sourceRegion = receiveNode._regionID;
                passedRegionID = sourceRegion;
                loadNavigationGraph();
                navigationPath = startNavigation();
                progressBar.setMax(navigationPath.size());

                for(int i = 0; i < chosenStartNode._attachIDs.size(); i++){
                    if((endNode._mainID != 0 && endNode._mainID == chosenStartNode._attachIDs.get(i))){
                        showHintAtWaypoint(ARRIVED_NOTIFIER);
                        isFirstBeacon = false;
                    }
                }

                if(chosenStartNode._waypointID.equals(endNode._waypointID)) {
                    showHintAtWaypoint(ARRIVED_NOTIFIER);
                    isFirstBeacon = false;
                }

                //初始面對方向
                if(navigationPath.size() >= 1 && isFirstBeacon == true){
                    beaconManager.removeAllMonitorNotifiers();
                    beaconManager.removeAllRangeNotifiers();
                    beaconManager.unbind(this);

                    ARInitialImage arInitialImage = new ARInitialImage(this);
                    int imageNumber = arInitialImage.deciseImageToShow(navigationPath.get(0)._waypointID, navigationPath.get(1)._waypointID);
                    predictDirection = arInitialImage.getPredict(navigationPath.get(0)._waypointID, navigationPath.get(1)._waypointID);

                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.ar_initial_image_layout, null);
                    ImageView image = layout.findViewById(R.id.initialImage);
                    image.setImageResource(imageNumber);
                    TextView title = layout.findViewById(R.id.faceto);
                    String initialTitle = INIT_DIRECTION_TITLE;
                    if(imageNumber == R.drawable.elevator)
                    {
                        if(languageOption == "繁體中文")
                            initialTitle = getString(R.string.CHT_PLEASE_TAKE_ELEVATOR);
                        else if(languageOption == "English")
                            initialTitle = getString(R.string.ENG_PLEASE_TAKE_ELEVATOR);
                    }
                    title.setText(initialTitle);

                    AlertDialog.Builder builder = new AlertDialog.Builder(AR_NavigationActivity.this);
                    builder.setView(layout);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            beaconManagerSetup();
                        }
                    });
                    builder.show();
                }
                isFirstBeacon = false;
            }

            if(navigationPath.size() > 0){
                if(receiveBeacon != null && !currentLBeaconID.equals(receiveBeacon) && receiveNode != null){
                    if(receiveNode._groupID == navigationPath.get(0)._groupID && receiveNode._groupID != 0)
                    {
                        currentLBeaconID = navigationPath.get(0)._waypointID;
                        if(!placedID.contains(currentLBeaconID))
                            pass = true;
                    }
                    else if(receiveNode._groupID == passedGroupID && receiveNode._groupID != 0)
                        pass = false;
                    else
                    {
                        currentLBeaconID = receiveBeacon;
                        if(!placedID.contains(currentLBeaconID))
                            pass = true;
                    }
                }
                else
                    pass = false;
            }

            if(allWaypointData.containsKey(currentLBeaconID))
            {
                if(!placedID.contains(currentLBeaconID))
                    pass = true;
            }

            if(pass)
            {
                pass = false;
                synchronized (sync)
                {
                    sync.notify();
                }
            }
        }
    }

    private class myCameraListener implements CameraListener{
        @Override
        public void onTransformUpdate(Vector vector, Vector vector1, Vector vector2) {
            lastCameraPosition = vector;
            lastCameraRotation = vector1;
            lastCameraForward = vector2;
        }
    }

    private class ARSceneListener implements ARScene.Listener{
        private WeakReference<Activity> mCurrentActivityWeak;
        private boolean mInitialized;

        public ARSceneListener(Activity activity, View rootView){
            mCurrentActivityWeak = new WeakReference<Activity>(activity);
            mInitialized = false;
        }

        @Override
        public void onTrackingInitialized() {

        }

        @Override
        public void onTrackingUpdated(ARScene.TrackingState trackingState, ARScene.TrackingStateReason trackingStateReason) {
            if(!mInitialized && trackingState == ARScene.TrackingState.NORMAL){
                Activity activity = mCurrentActivityWeak.get();
                if(activity == null)
                    return;
                Log.i("AR","AR is initialized");
                mInitialized = true;
            }
        }

        @Override
        public void onAmbientLightUpdate(float v, Vector vector) {

        }

        @Override
        public void onAnchorFound(ARAnchor arAnchor, ARNode arNode) {

        }

        @Override
        public void onAnchorUpdated(ARAnchor arAnchor, ARNode arNode) {

        }

        @Override
        public void onAnchorRemoved(ARAnchor arAnchor, ARNode arNode) {

        }
    }

    final SensorEventListener SensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                accelerometerValue = event.values;
            if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                magneticValue = event.values;
            calculateOrientation();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private void calculateOrientation(){
        float[] tempValues = new float[3];
        float[] tempRotation = new float[9];
        SensorManager.getRotationMatrix(tempRotation, null, accelerometerValue,magneticValue);
        SensorManager.getOrientation(tempRotation, tempValues);

        //toDegrees
        tempValues[0] = (float) Math.toDegrees(tempValues[0]);
        tempValues[1] = (float) Math.toDegrees(tempValues[1]);
        tempValues[2] = (float) Math.toDegrees(tempValues[2]);

        //pitch < -80 代表手機於直立狀態, 改以z軸的旋轉作為航向角,但與原本相差180度
        if(tempValues[1] < -80)
            tempValues[0] = (float) Math.toDegrees(Math.atan2(tempRotation[2], tempRotation[5])) - 180;
        Log.i("Azimuth","" + tempValues[0]);

        if(tempValues[0] >= -22 && tempValues[0] < 22){
            currentDirection = 0;
            facingDirectionText.setText(FACING_DIRECTION + NORTH);
            Log.i("facingDirection","北");
        }
        else if(tempValues[0] >= 22 && tempValues[0] < 67){
            currentDirection = 1;
            facingDirectionText.setText(FACING_DIRECTION + NORTHEAST);
            Log.i("facingDirection", "東北");
        }
        else if(tempValues[0] >= 67 && tempValues[0] < 112){
            currentDirection = 2;
            facingDirectionText.setText(FACING_DIRECTION + EAST);
            Log.i("facingDirection","東");
        }
        else if(tempValues[0] >= 112 && tempValues[0] < 157){
            currentDirection = 3;
            facingDirectionText.setText(FACING_DIRECTION + SOUTHEAST);
            Log.i("facingDirection","東南");
        }
        else if((tempValues[0] >= 157 && tempValues[0] <= 180) || (tempValues[0] >= -180 && tempValues[0] < -157)){
            currentDirection = 4;
            facingDirectionText.setText(FACING_DIRECTION + SOUTH);
            Log.i("facingDirection","南");
        }
        else if(tempValues[0] >= -157 && tempValues[0] < -122){
            currentDirection = 5;
            facingDirectionText.setText(FACING_DIRECTION + SOUTHWEST);
            Log.i("facingDirection","西南");
        }
        else if(tempValues[0] >= -122 && tempValues[0] < -67){
            currentDirection = 6;
            facingDirectionText.setText(FACING_DIRECTION + WEST);
            Log.i("facingDirection","西");
        }
        else if(tempValues[0] >= -67 && tempValues[0] < -22){
            currentDirection = 7;
            facingDirectionText.setText(FACING_DIRECTION + NORTHWEST);
            Log.i("facingDirection","西北");
        }
        Log.i("currentDirecion","" + currentDirection);

    }

    public void beaconManagerSetup(){
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.unbind(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-15,i:16-19,i:20-23,p:24-24"));
        // Detect the Eddystone main identifier (UID) frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));

        // Detect the Eddystone telemetry (TLM) frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15"));

        // Detect the Eddystone URL frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20"));

        beaconManager.setForegroundScanPeriod(50);
        beaconManager.setForegroundBetweenScanPeriod(0);

        beaconManager.removeAllRangeNotifiers();
        beaconManager.removeAllMonitorNotifiers();

        regionForBeacon = new org.altbeacon.beacon.Region("justGiveMeEverything", null, null,null);
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Scanning for Beacons");
        Intent intent = new Intent(this, AR_NavigationActivity.class);
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
        beaconManager.enableForegroundServiceScanning(builder.build(), 456);
        beaconManager.setEnableScheduledScanJobs(false);
        beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.setBackgroundScanPeriod(1100);
        beaconManager.bind(AR_NavigationActivity.this);
    }


    class NavigationThread implements Runnable{

        @Override
        public void run() {
            while (!navigationPath.isEmpty()){
                synchronized (sync){
                    try {
                        sync.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                //If the received ID matches the ID of the next waypoint in the navigation path
                if(navigationPath.get(0)._waypointID.equals(currentLBeaconID)){
                    Message messageFormInstructionHandler = instructionHandler.obtainMessage();
                    Message messageFromCurrentPositionHandler = currentPositionHandler.obtainMessage();
                    Message messageFromWalkedPointHandler = walkedPointHandler.obtainMessage();
                    Message messageFromProgressHandler = progressHandler.obtainMessage();

                    //CurrentPositionHandler get the message of currently matched waypoint name
                    messageFromCurrentPositionHandler.obj = navigationPath.get(0)._waypointName;

                    if(navigationPath.size() >= 3){
                        //If the next two waypoints are in the same region as the current waypoint
                        //get the turn direction at the next waypoint
                        if(navigationPath.get(0)._regionID.equals(navigationPath.get(1)._regionID) &&
                            navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                            messageFormInstructionHandler.obj = getDirectionFromBearing(navigationPath.get(0), navigationPath.get(1), navigationPath.get(2));
                        else if(!(navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID)))
                            messageFormInstructionHandler.obj = FRONT;
                        //If the current waypoint and the next waypoint are not in the same region
                        //transfer through elevator or stairwell
                        else if(!(navigationPath.get(0)._regionID.equals(navigationPath.get(1)._regionID))){
                            if(navigationPath.get(0)._nodeType == ELEVATOR_WAYPOINT)
                                messageFormInstructionHandler.obj = ELEVATOR;
                            else if(navigationPath.get(0)._nodeType == STAIRWELL_WAYPOINT)
                                messageFormInstructionHandler.obj = STAIR;
                            else if(navigationPath.get(0)._nodeType == CONNECTPOINT)
                                messageFormInstructionHandler.obj = getDirectionFromBearing(navigationPath.get(0), navigationPath.get(1), navigationPath.get(2));
                            else if(navigationPath.get(0)._nodeType == NORMAL_WAYPOINT){
                                if(Setting.getPreferenceValue() == ELEVATOR_WAYPOINT)
                                    messageFormInstructionHandler.obj = ELEVATOR;
                                else if(Setting.getPreferenceValue() == STAIRWELL_WAYPOINT)
                                    messageFormInstructionHandler.obj = STAIR;
                            }
                        }
                    }
                    else if(navigationPath.size() == 2){
                        //If the current waypoint and the next waypoint are not in the same region
                        if(!(navigationPath.get(0)._regionID.equals(navigationPath.get(1)._regionID))){
                            if(navigationPath.get(0)._nodeType == ELEVATOR_WAYPOINT)
                                messageFormInstructionHandler.obj = ELEVATOR;
                            else if(navigationPath.get(0)._nodeType == STAIRWELL_WAYPOINT)
                                messageFormInstructionHandler.obj = STAIR;
                        }
                        else
                            messageFormInstructionHandler.obj = FRONT;
                    }
                    else if(navigationPath.size() == 1)
                        messageFormInstructionHandler.obj = ARRIVED;

                    if(currentLBeaconID != lastRoundBeaconID)
                    {
                        walkedWaypoint++;
                        lastRoundBeaconID = currentLBeaconID;
                    }

                    messageFromWalkedPointHandler.obj = walkedWaypoint;
                    messageFromProgressHandler.obj = true;

                    walkedPointHandler.sendMessage(messageFromWalkedPointHandler);
                    instructionHandler.sendMessage(messageFormInstructionHandler);
                    currentPositionHandler.sendMessage(messageFromCurrentPositionHandler);
                    progressHandler.sendMessage(messageFromProgressHandler);
                }
                else if(!(navigationPath.get(0)._waypointID.equals(currentLBeaconID))){
                    Message messageFromInstructionHandler = instructionHandler.obtainMessage();
                    messageFromInstructionHandler.obj = WRONG;
                    instructionHandler.sendMessage(messageFromInstructionHandler);
                }
            }
        }
    }

    public void displayScene(){
        Log.i("AR","displayScene()");
        mScene = new ARScene();
        //Add a litener to the scene so we can update the "AR initialized" text.
        mScene.setListener(new ARSceneListener(this, mViroView));
        mScene.getRootNode().addLight(new AmbientLight(Color.WHITE, 1000f));
        mViroView.setScene(mScene);

        //The List contains the Nodes which have placed the model
        placedNode = new ArrayList<>();
        placedID = new ArrayList<>();
        isOverFloor = false;

        //UI Setup
        View.inflate(this, R.layout.activity_ar__navigation, ((ViewGroup) mViroView));
        setTitle(TITLE);
        facingDirectionText = findViewById(R.id.facingDirection);
        destinationText = findViewById(R.id.destination);
        destinationText.setText(DESTINATION + destinationName);
        currentLocationText = findViewById(R.id.currentLocation);
        currentLocationText.setText(PRESENT_POSITION);
        progressBar = findViewById(R.id.progressBar);
        progressNumber = findViewById(R.id.progressNumber);

        //Sensor Setup
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(SensorListener, accelerometerSensor, Sensor.TYPE_ACCELEROMETER, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(SensorListener, magneticSensor, Sensor.TYPE_MAGNETIC_FIELD, SensorManager.SENSOR_DELAY_GAME);

        //BeaconManager Setup
        beaconManagerSetup();

        //Create a thread to handle the LBeacon signal
        threadForHandleLBeaconID = new Thread(new AR_NavigationActivity.NavigationThread());
        threadForHandleLBeaconID.start();

        //Handler for instruction display
        instructionHandler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                String turnDirecion = (String) msg.obj;
                int distance = 0;

                if(navigationPath.size() >= 2)
                    distance = GeoCalulation.getDistance(navigationPath.get(0), navigationPath.get(1));

                navigationInstructionDisplay(turnDirecion, distance);
            }
        };

        currentPositionHandler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                currentLocationName = (String) msg.obj;
                currentLocationText.setText(PRESENT_POSITION + currentLocationName);
            }
        };

        walkedPointHandler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                int numberOfWaypointTraveled = (int) msg.obj;

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
                    progressStatus = walkedWaypoint;
                    progressBar.setProgress(progressStatus);
                    progressNumber.setText(progressStatus + "/" + progressBar.getMax());
                }
            }
        };

    }

    public String toDirection(int num){
        String returnString = "";
        switch (num)
        {
            case 0:
                returnString = NORTH;
                break;
            case 1:
                returnString = NORTHEAST;
                break;
            case 2:
                returnString = EAST;
                break;
            case 3:
                returnString = SOUTHEAST;
                break;
            case 4:
                returnString = SOUTH;
                break;
            case 5:
                returnString = SOUTHWEST;
                break;
            case 6:
                returnString = WEST;
                break;
            case 7:
                returnString = NORTHWEST;
                break;
        }

        return returnString + DIRECTION;
    }

    public void ShowDirectionFromConnectPoint()
    {
        Log.i("Step","connect");
        Log.i("ConnectionStep","path[0].connecPointID:" + navigationPath.get(0)._connectPointID);
        if(navigationPath.get(0)._connectPointID == 0)
            isInVirtualNode = false;
        Log.i("ConnectionStep","isInVirtualNode:" + isInVirtualNode);
        //選擇的起始點不是目前位置
        if(chosenStartNode._waypointID != navigationPath.get(0)._waypointID) {
            //收到的ConnectID != 0 目前與下個點的conectID相同，進入樓梯階段
            if (navigationPath.get(0)._connectPointID != 0 && navigationPath.get(1)._connectPointID == navigationPath.get(0)._connectPointID && isInVirtualNode == false) {
                Log.i("ConnectionStep","enter stair");
                Log.i("ConnectionStep","isInVirtualNodeInIF :" + isInVirtualNode);
                //判斷上下樓
                if(navigationPath.get(1)._elevation > navigationPath.get(0)._elevation) {
                    stairGoUP = true;
                }else{
                    stairGoUP = false;
                }//找到對應VirtualNode
                for (int i = 0; i < virtualNodeUP.size(); i++) {
                    if((virtualNodeDown.get(i)._connectPointID == navigationPath.get(0)._connectPointID)){
                        if(stairGoUP == true){
                            turnNotificationForPopup = getDirectionFromBearing(lastNode, navigationPath.get(0), virtualNodeDown.get(i));
                        }else {
                            turnNotificationForPopup = getDirectionFromBearing(lastNode, navigationPath.get(0), virtualNodeUP.get(i));
                        }
                        if(!placedID.contains(currentLBeaconID))
                        {
                            if(firstTurn == false && callDirectionInWrong == false)
                                showHintAtWaypoint(MAKETURN_NOTIFIER);
                            isInVirtualNode = true;
                            Log.i("ConnectionStep","show on enter stair");
                        }

                    }
                }
            }//離開樓梯階段
            else if (navigationPath.get(0)._connectPointID != 0 && lastNode._connectPointID == navigationPath.get(0)._connectPointID && isInVirtualNode == true) {
                Log.i("ConnectionStep","out stair");
                Log.i("lastNodeOnVirtual","lastNode = " + lastNode._waypointID);
                //判斷上下樓
                if(navigationPath.get(0)._elevation > lastNode._elevation) {
                    stairGoUP = true;
                }else{
                    stairGoUP = false;
                }
                for (int i = 0; i < virtualNodeUP.size(); i++) {
                    if((virtualNodeDown.get(i)._connectPointID == navigationPath.get(0)._connectPointID)) {
                        cleanPlacedID(placedID);
                        cleanPlacedNode(placedNode);
                        if(stairGoUP == true){
                            turnNotificationForPopup = getDirectionFromBearing(virtualNodeUP.get(i), navigationPath.get(0), navigationPath.get(1));
                        }else {
                            turnNotificationForPopup = getDirectionFromBearing(virtualNodeDown.get(i), navigationPath.get(0), navigationPath.get(1));
                        }
                        predictDirection = new ARInitialImage(AR_NavigationActivity.this).getPredict(virtualNodeDown.get(i)._waypointID,navigationPath.get(0)._waypointID);
                    }
                }
            }
        }//選擇起始點是目前位置，且進入樓梯
        else if(chosenStartNode._connectPointID != 0 && chosenStartNode._waypointID == navigationPath.get(0)._waypointID && navigationPath.get(0)._connectPointID == navigationPath.get(1)._connectPointID) {
            Log.i("ConnectionStep","enter stair II");
            //判斷是往上或往下走
            if(navigationPath.get(1)._elevation > navigationPath.get(0)._elevation) {
                stairGoUP = true;
            }else{
                stairGoUP = false;
            }
            for (int i = 0; i < virtualNodeUP.size(); i++) {
                //找到與目前位置相同ConnectID
                if (virtualNodeDown.get(i)._connectPointID == navigationPath.get(0)._connectPointID && isInVirtualNode == false) {
                    if(stairGoUP == true){
                        turnNotificationForPopup = getDirectionFromBearing(lastNode, navigationPath.get(0), virtualNodeDown.get(i));
                    }else{
                        turnNotificationForPopup = getDirectionFromBearing(lastNode, navigationPath.get(0), virtualNodeUP.get(i));
                    }
                    if(!placedID.contains(currentLBeaconID))
                    {
                        if(firstTurn == false && callDirectionInWrong == false)
                        {
                            showHintAtWaypoint(MAKETURN_NOTIFIER);
                            Log.i("ConnectionStep","show on enter stair II");
                        }
                        isInVirtualNode = true;
                    }
                }
            }
        }
        callDirectionInWrong = false;
    }
    
    public void makeNextPredictDirection(String turnNotificationForPopup)
    {
        switch (turnNotificationForPopup)
        {
            case RIGHT:
                predictDirection = (predictDirection + 2) % 8;
                break;
                
            case LEFT:
                predictDirection = (predictDirection + 6) % 8;
                break;
                
            case FRONT_RIGHT:
                predictDirection = (predictDirection + 1) % 8;
                break;
                
            case FRONT_LEFT:
                predictDirection = (predictDirection + 7) % 8;
                break;
                
            case REAR_RIGHT:
                predictDirection = (predictDirection + 3) % 8;
                break;
                
            case REAR_LEFT:
                predictDirection = (predictDirection + 5) % 8;
                break;
                
            case FRONT:
                predictDirection = (predictDirection + 0) % 8;
                break;
                
            case FRONT_RIGHTSIDE:
                predictDirection = (predictDirection + 1) % 8;
                break;
                
            case FRONT_LEFTSIDE:
                predictDirection = (predictDirection + 7) % 8;
                break;
                
            case "goback":
                predictDirection = (predictDirection + 4) % 8;
                break;
        }
    }

    synchronized public void navigationInstructionDisplay(String turnDirection, int distance){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toast_layout));
        ImageView image = layout.findViewById(R.id.toast_image);
        Vibrator myVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);

        final Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 25);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);

        if(firstTurn == true){
            lastNode = navigationPath.get(0);
            if(navigationPath.size() == 2 && navigationPath.get(0)._elevation != navigationPath.get(1)._elevation){
                if(currentDirection != predictDirection){
                    Toast.makeText(AR_NavigationActivity.this, PLEASE_FACE_TO + toDirection(predictDirection), Toast.LENGTH_LONG).show();
                    return;
                }
                else if(currentDirection == predictDirection){
                    Toast.makeText(AR_NavigationActivity.this, RIGHT_DIRECTION, Toast.LENGTH_LONG).show();
                    if(navigationPath.get(0)._nodeType == ELEVATOR_WAYPOINT)
                        turnDirection = ELEVATOR;
                    else if(navigationPath.get(1)._nodeType == STAIRWELL_WAYPOINT)
                        turnDirection = STAIR;
                }
            }
            else if(turnDirection != STAIR && turnDirection != ELEVATOR && turnDirection != ARRIVED)
            {
                if(currentDirection != predictDirection)
                {
                    Toast.makeText(AR_NavigationActivity.this, PLEASE_FACE_TO + toDirection(predictDirection), Toast.LENGTH_LONG).show();
                    return;
                }
                else if(currentDirection == predictDirection)
                {
                    Toast.makeText(AR_NavigationActivity.this, RIGHT_DIRECTION, Toast.LENGTH_LONG).show();
                    add3DObject("file:///android_asset/ARModel/Front.obj",mViroView.getLastCameraPositionRealtime().add(mViroView.getLastCameraForwardRealtime()),navigationPath.get(0)._waypointID);
                    firstTurn = false;
                }
            }
        }

        if(navigationPath.size() >= 2 && !turnDirection.equals(WRONG))
            ShowDirectionFromConnectPoint();
        Log.i("Instruction","turnNotification = " + turnNotificationForPopup
                                    + "\nturnDirection = " + turnDirection
                                    + "\npredictDirection = " + predictDirection
                                    + "\ncurrentWaypoint = " + currentLBeaconID);
        switch (turnDirection)
        {
            case LEFT:
                firstMovement = GO_STRAIGHT_ABOUT;
                if(navigationPath.size() > 1)
                    howFarToMove = distance + " " + METERS + TO +navigationPath.get(1)._waypointName;
                switch (navigationPath.get(1)._nodeType)
                {
                    case ELEVATOR_WAYPOINT:
                    case STAIRWELL_WAYPOINT:
                        if(!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                            nextFloor = navigationPath.get(2)._elevation;
                        break;
                    case NORMAL_WAYPOINT:
                        break;
                }
                if(!placedID.contains(currentLBeaconID))
                {
                    if(currentDirection == predictDirection)
                    {
                        if(turnNotificationForPopup != null)
                        {
                            Toast.makeText(AR_NavigationActivity.this, RIGHT_DIRECTION, Toast.LENGTH_LONG).show();
                            showHintAtWaypoint(MAKETURN_NOTIFIER);
                            makeNextPredictDirection(turnNotificationForPopup);
                        }
                    }
                    else
                    {
                        Toast.makeText(AR_NavigationActivity.this, PLEASE_FACE_TO + toDirection(predictDirection), Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                turnNotificationForPopup = LEFT;
                break;
                
            case FRONT_LEFT:
                firstMovement = GO_STRAIGHT_ABOUT;
                if(navigationPath.size() > 1)
                    howFarToMove = distance + " " + METERS + TO + navigationPath.get(1)._waypointName;
                switch (navigationPath.get(1)._nodeType)
                {
                    case ELEVATOR_WAYPOINT:
                    case STAIRWELL_WAYPOINT:
                        if(!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                            nextFloor = navigationPath.get(2)._elevation;
                        break;
                    case NORMAL_WAYPOINT:
                        break;
                }
                if(!placedID.contains(currentLBeaconID))
                {
                    if(currentDirection == predictDirection)
                    {
                        if(turnNotificationForPopup != null)
                        {
                            Toast.makeText(AR_NavigationActivity.this, RIGHT_DIRECTION, Toast.LENGTH_LONG).show();
                            showHintAtWaypoint(MAKETURN_NOTIFIER);
                            makeNextPredictDirection(turnNotificationForPopup);
                        }
                    }
                    else 
                    {
                        Toast.makeText(AR_NavigationActivity.this, PLEASE_FACE_TO + toDirection(predictDirection), Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                turnNotificationForPopup = FRONT_LEFT;
                break;
                
            case REAR_LEFT:
                firstMovement = GO_STRAIGHT_ABOUT;
                if(navigationPath.size() > 1)
                    howFarToMove = distance + " " + METERS + TO + navigationPath.get(1)._waypointName;
                switch (navigationPath.get(1)._nodeType)
                {
                    case ELEVATOR_WAYPOINT:
                    case STAIRWELL_WAYPOINT:
                        if(!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                            nextFloor = navigationPath.get(2)._elevation;
                        break;
                    case NORMAL_WAYPOINT:
                        break;
                }
                if(!placedID.contains(currentLBeaconID))
                {
                    if(currentDirection == predictDirection)
                    {
                        if(turnNotificationForPopup != null)
                        {
                            Toast.makeText(AR_NavigationActivity.this, RIGHT_DIRECTION, Toast.LENGTH_LONG).show();
                            showHintAtWaypoint(MAKETURN_NOTIFIER);
                            makeNextPredictDirection(turnNotificationForPopup);
                        }
                    }
                    else
                    {
                        Toast.makeText(AR_NavigationActivity.this, PLEASE_FACE_TO + toDirection(predictDirection), Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                turnNotificationForPopup = REAR_LEFT;
                break;
                
            case RIGHT:
                firstMovement = GO_STRAIGHT_ABOUT;
                if(navigationPath.size() > 1)
                    howFarToMove = distance + " " + METERS + TO + navigationPath.get(1)._waypointName;
                switch(navigationPath.get(1)._nodeType)
                {
                    case ELEVATOR_WAYPOINT:
                    case STAIRWELL_WAYPOINT:
                        if(!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                            nextFloor = navigationPath.get(2)._elevation;
                        break;
                    case NORMAL_WAYPOINT:
                        break;
                }
                if(!placedID.contains(currentLBeaconID))
                {
                    if(currentDirection == predictDirection)
                    {
                        if(turnNotificationForPopup != null)
                        {
                            Toast.makeText(AR_NavigationActivity.this, RIGHT_DIRECTION, Toast.LENGTH_LONG).show();
                            showHintAtWaypoint(MAKETURN_NOTIFIER);
                            makeNextPredictDirection(turnNotificationForPopup);
                        }
                    }
                    else
                    {
                        Toast.makeText(AR_NavigationActivity.this, PLEASE_FACE_TO + toDirection(predictDirection), Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                turnNotificationForPopup = RIGHT;
                break;

            case FRONT_RIGHT:
                firstMovement = GO_STRAIGHT_ABOUT;
                if(navigationPath.size() > 1)
                    howFarToMove = distance + " " + METERS + TO + navigationPath.get(1)._waypointName;
                switch (navigationPath.get(1)._nodeType)
                {
                    case ELEVATOR_WAYPOINT:
                    case STAIRWELL_WAYPOINT:
                        if(!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                            nextFloor = navigationPath.get(2)._elevation;
                        break;
                    case NORMAL_WAYPOINT:
                        break;
                }
                if(!placedID.contains(currentLBeaconID))
                {
                    if(currentDirection == predictDirection)
                    {
                        if(turnNotificationForPopup != null)
                        {
                            Toast.makeText(AR_NavigationActivity.this, RIGHT_DIRECTION, Toast.LENGTH_LONG).show();
                            showHintAtWaypoint(MAKETURN_NOTIFIER);
                            makeNextPredictDirection(turnNotificationForPopup);
                        }
                    }
                    else
                    {
                        Toast.makeText(AR_NavigationActivity.this, PLEASE_FACE_TO + toDirection(predictDirection), Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                turnNotificationForPopup = FRONT_RIGHT;
                break;

            case REAR_RIGHT:
                firstMovement = GO_STRAIGHT_ABOUT;
                if(navigationPath.size() > 1)
                    howFarToMove = distance + " " + METERS + TO + navigationPath.get(1)._waypointName;
                switch (navigationPath.get(1)._nodeType)
                {
                    case ELEVATOR_WAYPOINT:
                    case STAIRWELL_WAYPOINT:
                        if(!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                            nextFloor = navigationPath.get(2)._elevation;
                        break;
                    case NORMAL_WAYPOINT:
                        break;
                }
                if(!placedID.contains(currentLBeaconID))
                {
                    if(currentDirection == predictDirection)
                    {
                        if(turnNotificationForPopup != null)
                        {
                            Toast.makeText(AR_NavigationActivity.this, RIGHT_DIRECTION, Toast.LENGTH_LONG).show();
                            showHintAtWaypoint(MAKETURN_NOTIFIER);
                            makeNextPredictDirection(turnNotificationForPopup);
                        }
                    }
                    else
                    {
                        Toast.makeText(AR_NavigationActivity.this, PLEASE_FACE_TO + toDirection(predictDirection), Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                turnNotificationForPopup = REAR_RIGHT;
                break;

            case FRONT:
                firstMovement = GO_STRAIGHT_ABOUT;
                if(navigationPath.size() > 1)
                    howFarToMove = distance + " " + METERS + TO + navigationPath.get(1)._waypointName;
                switch (navigationPath.get(1)._nodeType)
                {
                    case ELEVATOR_WAYPOINT:
                    case STAIRWELL_WAYPOINT:
                        if(!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                            nextFloor = navigationPath.get(2)._elevation;
                        break;
                    case NORMAL_WAYPOINT:
                        break;
                }
                if(!placedID.contains(currentLBeaconID))
                {
                    if(currentDirection == predictDirection)
                    {
                        if(turnNotificationForPopup != null)
                        {
                            Toast.makeText(AR_NavigationActivity.this, RIGHT_DIRECTION, Toast.LENGTH_LONG);
                            showHintAtWaypoint(MAKETURN_NOTIFIER);
                            makeNextPredictDirection(turnNotificationForPopup);
                        }
                    }
                    else
                    {
                        Toast.makeText(AR_NavigationActivity.this, PLEASE_FACE_TO + toDirection(predictDirection), Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                turnNotificationForPopup = FRONT;
                break;

            case FRONT_RIGHTSIDE:
                firstMovement = GO_STRAIGHT_ABOUT;
                if(navigationPath.size() > 1)
                    howFarToMove = distance + " " + METERS + TO + navigationPath.get(1)._waypointName;
                switch (navigationPath.get(1)._nodeType)
                {
                    case ELEVATOR_WAYPOINT:
                    case STAIRWELL_WAYPOINT:
                        if(!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                            nextFloor = navigationPath.get(2)._elevation;
                        break;
                    case NORMAL_WAYPOINT:
                        break;
                }
                if(!placedID.contains(currentLBeaconID))
                {
                    if(currentDirection == predictDirection)
                    {
                        if(turnNotificationForPopup != null)
                        {
                            Toast.makeText(AR_NavigationActivity.this, RIGHT_DIRECTION, Toast.LENGTH_LONG).show();
                            showHintAtWaypoint(MAKETURN_NOTIFIER);
                            makeNextPredictDirection(turnNotificationForPopup);
                        }
                    }
                    else
                    {
                        Toast.makeText(AR_NavigationActivity.this, PLEASE_FACE_TO + toDirection(predictDirection), Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                turnNotificationForPopup = FRONT_RIGHTSIDE;
                break;

            case FRONT_LEFTSIDE:
                firstMovement = GO_STRAIGHT_ABOUT;
                if(navigationPath.size() > 1)
                    howFarToMove = distance + " " + METERS + TO + navigationPath.get(1)._waypointName;
                switch(navigationPath.get(1)._nodeType)
                {
                    case ELEVATOR_WAYPOINT:
                    case STAIRWELL_WAYPOINT:
                        if(!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                            nextFloor = navigationPath.get(2)._elevation;
                        break;
                    case NORMAL_WAYPOINT:
                        break;
                }
                if(!placedID.contains(currentLBeaconID))
                {
                    if(currentDirection == predictDirection)
                    {
                        Toast.makeText(AR_NavigationActivity.this, RIGHT_DIRECTION, Toast.LENGTH_LONG).show();
                        showHintAtWaypoint(MAKETURN_NOTIFIER);
                        makeNextPredictDirection(turnNotificationForPopup);
                    }
                    else
                    {
                        Toast.makeText(AR_NavigationActivity.this, PLEASE_FACE_TO + toDirection(predictDirection), Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                turnNotificationForPopup = FRONT_LEFTSIDE;
                break;

            case STAIR:
                turnNotificationForPopup = STAIR;
                if(navigationPath.size() > 2)
                {
                    nextFloor = navigationPath.get(2)._elevation;
                    //Up Stair
                    if(navigationPath.get(2)._elevation > navigationPath.get(0)._elevation)
                    {
                        if(!firstTurn)
                        {
                            image.setImageResource(R.drawable.stairs_up);
                            toast.show();
                            myVibrator.vibrate(800);
                        }
                        switch (navigationPath.get(2)._elevation)
                        {
                            case 1:
                                firstMovement = WALKING_UP_STAIR + TO_FIRST_FLOOR;
                                break;

                            case 2:
                                firstMovement = WALKING_UP_STAIR + TO_SECOND_FLOOR;
                                break;
                        }
                    }
                    //Down Stair
                    else if(navigationPath.get(2)._elevation < navigationPath.get(0)._elevation)
                    {
                        if(!firstTurn)
                        {
                            image.setImageResource(R.drawable.stairs_down);
                            toast.show();
                            myVibrator.vibrate(800);
                        }
                        switch (navigationPath.get(2)._elevation)
                        {
                            case 1:
                                firstMovement = WALKING_DOWN_STAIR + TO_FIRST_FLOOR;
                                break;

                            case 2:
                                firstMovement = WALKING_DOWN_STAIR + TO_SECOND_FLOOR;
                                break;
                        }
                    }
                }
                else if(navigationPath.size() == 2)
                {
                    nextFloor = navigationPath.get(1)._elevation;
                    //Up Stair
                    if(navigationPath.get(1)._elevation > navigationPath.get(0)._elevation)
                    {
                        if(!firstTurn)
                        {
                            image.setImageResource(R.drawable.stairs_up);
                            toast.show();
                            myVibrator.vibrate(800);
                        }
                        switch (navigationPath.get(1)._elevation)
                        {
                            case 1:
                                firstMovement = WALKING_UP_STAIR + TO_FIRST_FLOOR;
                                break;
                                
                            case 2:
                                firstMovement = WALKING_UP_STAIR + TO_SECOND_FLOOR;
                                break;
                        }
                    }
                    //Down Stair
                    else if(navigationPath.get(1)._elevation < navigationPath.get(0)._elevation)
                    {
                        if(!firstTurn)
                        {
                            image.setImageResource(R.drawable.stairs_down);
                            toast.show();
                            myVibrator.vibrate(800);
                        }
                        switch (navigationPath.get(1)._elevation)
                        {
                            case 1:
                                firstMovement = WALKING_DOWN_STAIR + TO_FIRST_FLOOR;
                                break;
                                
                            case 2:
                                firstMovement = WALKING_DOWN_STAIR + TO_SECOND_FLOOR;
                                break;
                        }
                        
                    }
                }
                howFarToMove = "";
                
                //起點為樓梯
                if(firstTurn == true)
                {
                    predictDirection = new ARInitialImage(AR_NavigationActivity.this)
                                            .getPredict(navigationPath.get(0)._waypointID, navigationPath.get(1)._waypointID);
                    if(currentDirection == predictDirection)
                    {
                        add3DObject("file:///android_asset/ARModel/Front.obj",
                                        mViroView.getLastCameraPositionRealtime().add(mViroView.getLastCameraForwardRealtime()),
                                        navigationPath.get(0)._waypointID);
                        firstTurn = false;
                    }
                    else 
                    {
                        Toast.makeText(AR_NavigationActivity.this,
                                        PLEASE_WALK_UP_STAIR + "(" + toDirection(predictDirection) + ")",
                                        Toast.LENGTH_LONG).show();
                        return; 
                    }
                }
                if(!placedID.contains(currentLBeaconID))
                {
                    if(turnNotificationForPopup != null && firstTurn == false)
                    {
                        showHintAtWaypoint(MAKETURN_NOTIFIER);
                        walkedWaypoint = 0;
                        sourceID = navigationPath.get(1)._waypointID;
                    }
                }
                break;

            case ELEVATOR:
                turnNotificationForPopup = ELEVATOR;
                firstMovement = WAIT_FOR_ELEVATOR + TO + navigationPath.get(1)._elevation + FLOOR;
                howFarToMove = "";
                if(!firstTurn)
                {
                    image.setImageResource(R.drawable.elevator);
                    toast.show();
                    myVibrator.vibrate(800);
                }
                if(navigationPath.size() > 2)
                    nextFloor = navigationPath.get(2)._elevation;
                else
                    nextFloor = navigationPath.get(1)._elevation;

                if(firstTurn == true)
                {
                    predictDirection = new ARInitialImage(AR_NavigationActivity.this).getPredict(navigationPath.get(0)._waypointID, navigationPath.get(1)._waypointID);
                    if(currentDirection == predictDirection)
                    {
                        add3DObject("file:///android_asset/ARModel/Front.obj",mViroView.getLastCameraPositionRealtime().add(mViroView.getLastCameraForwardRealtime()),navigationPath.get(0)._waypointID);
                        firstTurn = false;
                    }
                    else
                    {
                        Toast.makeText(AR_NavigationActivity.this, PLEASE_TAKE_ELEVATOR + "(" + toDirection(predictDirection) + ")", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                if(!placedID.contains(currentLBeaconID))
                {
                    if(turnNotificationForPopup != null && firstTurn == false)
                    {
                        showHintAtWaypoint(MAKETURN_NOTIFIER);
                        walkedWaypoint = 0;
                        predictDirection = new ARInitialImage(AR_NavigationActivity.this).getPredict(navigationPath.get(1)._waypointID, navigationPath.get(2)._waypointID);
                    }
                }
                break;

            case ARRIVED:
                firstMovement = "";
                howFarToMove = "";
                if(!placedID.contains(currentLBeaconID))
                {
                    if(turnNotificationForPopup != null)
                        showHintAtWaypoint(ARRIVED_NOTIFIER);
                    walkedWaypoint = 0;
                }
                cleanPlacedID(placedID);
                cleanPlacedNode(placedNode);
                break;

            case WRONG:
                List<Node> newPath = new ArrayList<>();
                List<Node> wrongPath = new ArrayList<>();
                cleanPlacedNode(placedNode);
                cleanPlacedID(placedID);
                wrongWaypoint = allWaypointData.get(currentLBeaconID);
                currentLocationText.setText(PRESENT_POSITION + currentLocationName);

                //WrongID = destination 直接改成ARRIVE
                for (int i = 0; i < wrongWaypoint._attachIDs.size(); i++) {
                    if ((endNode._mainID != 0 && endNode._mainID == wrongWaypoint._attachIDs.get(i))) {
                        showHintAtWaypoint(ARRIVED_NOTIFIER);
                        arriveInWrong = true;
                        break;
                    }
                }

                if (wrongWaypoint._waypointID.equals(endNode._waypointID)) {
                    showHintAtWaypoint(ARRIVED_NOTIFIER);
                    arriveInWrong = true;
                }

                if(arriveInWrong == false)
                {
                    isLongerPath = false;
                    //If the last waypoint's neighbor contain current waypoint then JumpNode = false
                    jumpNode = true;
                    for (int i = 0; i < lastNode._adjacentWaypoints.size(); i++) {
                        if (lastNode._adjacentWaypoints.get(i).equals(wrongWaypoint._waypointID)) {
                            jumpNode = false;
                        }
                    }

                    //Find the lastNode in the wrong way
                    if(jumpNode == true)
                    {
                        directionCompute = true;
                        tmpdestinationID = destinationID;
                        tmpDestinationRegion = destinationRegion;

                        sourceID = startNode._waypointID;
                        sourceRegion = startNode._regionID;
                        destinationID = wrongWaypoint._waypointID;
                        destinationRegion = wrongWaypoint._regionID;
                        loadNavigationGraph();
                        wrongPath = startNavigation();

                        if (wrongPath.size() > 2)
                            lastNode = wrongPath.get(wrongPath.size() - 2);

                        directionCompute = false;
                        destinationID = tmpdestinationID;
                        destinationRegion = tmpDestinationRegion;
                    }

                    //Reroute
                    sourceID = wrongWaypoint._waypointID;
                    sourceRegion = wrongWaypoint._regionID;
                    loadNavigationGraph();
                    newPath = startNavigation();

                    //Check the newPath is longer or not
                    for (int i = 0; i < newPath.size(); i++) {
                        if (newPath.get(i)._waypointName.equals(lastNode._waypointName)) {
                            isLongerPath = true;
                            break;
                        }
                        isLongerPath = false;
                    }

                    if (isLongerPath) {
                        currentLBeaconID = "EmptyString";
                        navigationPath = newPath;
                        progressBar.setMax(navigationPath.size());
                        walkedWaypoint = 0;
                        progressStatus = 0;
                        firstMovement = TURN_AROUND + WAIT_FOR_SIGNAL;
                        howFarToMove = "";
                        turnNotificationForPopup = "goback";
                        showHintAtWaypoint(MAKETURN_NOTIFIER);
                        predictDirection = new ARInitialImage(AR_NavigationActivity.this).getPredict(navigationPath.get(0)._waypointID, navigationPath.get(1)._waypointID);
                        firstTurn = true;
                    }
                    else
                    {
                        navigationPath = newPath;
                        progressBar.setMax(navigationPath.size());
                        walkedWaypoint = 1;
                        progressStatus = 1;
                        progressNumber.setText(progressStatus + "/" + progressBar.getMax());
                        firstMovement = GO_STRAIGHT_ABOUT;
                        turnNotificationForPopup = null;
                        predictDirection = new ARInitialImage(AR_NavigationActivity.this).getPredict(lastNode._waypointID, navigationPath.get(0)._waypointID);
                        if (navigationPath.size() >= 2) {
                            howFarToMove = GeoCalulation.getDistance(navigationPath.get(0), navigationPath.get(1)) + " " + METERS + TO + navigationPath.get(1)._waypointName;
                            turnNotificationForPopup = getDirectionFromBearing(lastNode, navigationPath.get(0), navigationPath.get(1));
                        }
                        currentLocationText.setText(PRESENT_POSITION + currentLocationName);
                        showHintAtWaypoint(MAKETURN_NOTIFIER);

                        if(navigationPath.size() >= 3)
                        {
                            if (navigationPath.get(2)._elevation == navigationPath.get(1)._elevation)  //新路線在同一層樓
                            {
                                turnNotificationForPopup = getDirectionFromBearing(navigationPath.get(0), navigationPath.get(1), navigationPath.get(2));//預先算下一點
                                predictDirection = new ARInitialImage(AR_NavigationActivity.this).getPredict(navigationPath.get(1)._waypointID, navigationPath.get(2)._waypointID);//預先算下一點預測方向
                            }
                            else
                            {
                                nextFloor = navigationPath.get(2)._elevation;
                                ShowDirectionFromConnectPoint();
                            }

                            //The current waypoint and next one is the same stair or elevator
                            if(navigationPath.get(0)._connectPointID != 0 && navigationPath.get(0)._connectPointID == navigationPath.get(1)._connectPointID)
                            {
                                nextFloor = navigationPath.get(1)._elevation;
                                switch (navigationPath.get(1)._nodeType)
                                {
                                    case ELEVATOR_WAYPOINT:
                                        turnNotificationForPopup = ELEVATOR;
                                        firstMovement = WAIT_FOR_ELEVATOR + TO + navigationPath.get(1)._elevation + FLOOR;
                                        break;

                                    case STAIRWELL_WAYPOINT:
                                        turnNotificationForPopup = STAIR;
                                        if(navigationPath.size() > 2)
                                        {
                                            switch(navigationPath.get(2)._elevation)
                                            {
                                                case 1:
                                                    //Up Stair
                                                    if(navigationPath.get(2)._elevation > navigationPath.get(0)._elevation)
                                                        firstMovement = WALKING_UP_STAIR + TO_FIRST_FLOOR;
                                                    //Dwon Stair
                                                    else if(navigationPath.get(2)._elevation < navigationPath.get(0)._elevation)
                                                        firstMovement = WALKING_DOWN_STAIR + TO_FIRST_FLOOR;
                                                    break;

                                                case 2:
                                                    //Up Stair
                                                    if(navigationPath.get(2)._elevation > navigationPath.get(0)._elevation)
                                                        firstMovement = WALKING_UP_STAIR + TO_SECOND_FLOOR;
                                                    //Dwon Stair
                                                    else if(navigationPath.get(2)._elevation < navigationPath.get(0)._elevation)
                                                        firstMovement = WALKING_DOWN_STAIR + TO_SECOND_FLOOR;
                                                    break;
                                            }
                                        }
                                        else if(navigationPath.size() == 2)
                                        {
                                            switch (navigationPath.get(1)._elevation)
                                            {
                                                case 1:
                                                    //Up Stair
                                                    if(navigationPath.get(1)._elevation > navigationPath.get(0)._elevation)
                                                        firstMovement = WALKING_UP_STAIR + TO_FIRST_FLOOR;
                                                        //Dwon Stair
                                                    else if(navigationPath.get(1)._elevation < navigationPath.get(0)._elevation)
                                                        firstMovement = WALKING_DOWN_STAIR + TO_FIRST_FLOOR;
                                                    break;

                                                case 2:
                                                    //Up Stair
                                                    if(navigationPath.get(1)._elevation > navigationPath.get(0)._elevation)
                                                        firstMovement = WALKING_UP_STAIR + TO_SECOND_FLOOR;
                                                        //Dwon Stair
                                                    else if(navigationPath.get(1)._elevation < navigationPath.get(0)._elevation)
                                                        firstMovement = WALKING_DOWN_STAIR + TO_SECOND_FLOOR;
                                                    break;
                                            }
                                        }
                                        break;

                                    case NORMAL_WAYPOINT:
                                        break;
                                }
                                howFarToMove = "";
                                if(turnNotificationForPopup != null)
                                    showHintAtWaypoint(MAKETURN_NOTIFIER);
                                walkedWaypoint = 0;
                                sourceID = navigationPath.get(1)._waypointID;
                            }
                        }

                        if(jumpNode == false)
                        {
                            callDirectionInWrong = true;
                            ShowDirectionFromConnectPoint();
                        }
                        else
                        {
                            turnNotificationForPopup = getDirectionFromBearing(lastNode, navigationPath.get(0), navigationPath.get(1));
                            showHintAtWaypoint(MAKETURN_NOTIFIER);
                            if(navigationPath.size() > 2)
                            {
                                turnNotificationForPopup = getDirectionFromBearing(navigationPath.get(0), navigationPath.get(1), navigationPath.get(2));
                                if(navigationPath.get(0)._adjacentWaypoints.size() <= 2 && navigationPath.get(1)._adjacentWaypoints.size() <= 2 && navigationPath.get(0)._elevation == lastNode._elevation)
                                {
                                    turnNotificationForPopup = FRONT;
                                    lastisSlash = true;
                                }
                            }
                        }
                        if(placedID.contains(navigationPath.get(0)._waypointID))
                        {
                            lastNode = navigationPath.get(0);
                            navigationPath.remove(0);
                            passedGroupID = navigationPath.get(0)._groupID;
                        }
                    }
                }
                break;
        }

        if(navigationPath.size() > 1)
        {
            if(!passedRegionID.equals(navigationPath.get(0)._regionID))
                regionIndex++;
            passedRegionID = navigationPath.get(0)._regionID;
            passedGroupID = navigationPath.get(0)._groupID;

            if(!turnDirection.equals(WRONG) && navigationPath.size() >  2 && lastNode._elevation == navigationPath.get(0)._elevation)
            {
                if(navigationPath.get(0)._adjacentWaypoints.size() <= 2 && navigationPath.get(1)._adjacentWaypoints.size() <= 2 && navigationPath.get(0)._elevation == navigationPath.get(1)._elevation)
                {
                    turnNotificationForPopup = FRONT;
                    turnDirection = FRONT;
                    lastisSlash = true;
                }
            }
        }
        if(arriveInWrong == false && firstTurn == false)
            readNavigationInstruction();

        if(!turnDirection.equals(WRONG))
        {
            if(placedID.contains(navigationPath.get(0)._waypointID))
            {
                lastNode = navigationPath.get(0);
                navigationPath.remove(0);
            }
        }

        lastProcessedBeacon = lastNode._waypointID;
    }

    public void readNavigationInstruction(){
        voiceEngine.speak(firstMovement + howFarToMove, TextToSpeech.QUEUE_ADD, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        signalProcessor = new Signal_Processor(AR_NavigationActivity.this);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        offset = (double)sharedPreferences.getFloat("offset",0);

        //Receive value passed from MainActivity
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            destinationName = bundle.getString("destinationName");
            destinationID = bundle.getString("destinationID");
            destinationRegion = bundle.getString("destinationRegion");
            Log.i("destination", "Name = " + destinationName + " ID = " + destinationID + " Region = " + destinationRegion);
        }

        deviceParameter = new DeviceParameter(AR_NavigationActivity.this);
        voiceEngine = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR)
                    voiceEngine.setLanguage(Locale.CHINESE);
            }
        });

        languageSetup();

        //Load region data from region graph
        regionGraph = DataParser.getRegionDataFromRegionGraph(this);
        mappingOfRegionNameAndID = DataParser.waypointNameAndIDMappings(this, regionGraph.getAllRegionNames());
        navigationPath.add(new Node("empty", "empty", "empty", "empty"));
        loadAllWaypointData();
        virtualNodeUP = DataParser.getVirtualNode(this,VIRTUAL_UP);
        virtualNodeDown = DataParser.getVirtualNode(this, VIRTUAL_DOWN);

        mViroView = new ViroViewARCore(this, new ViroViewARCore.StartupListener() {
            @Override
            public void onSuccess() {
                displayScene();
            }

            @Override
            public void onFailure(ViroViewARCore.StartupError startupError, String s) {
                Log.e("AR", "Error initializing AR [" + s + "]");
            }
        });
        mViroView.setCameraListener(new myCameraListener());
        setContentView(mViroView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_navigation,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_home){
            beaconManager.removeAllMonitorNotifiers();
            beaconManager.removeAllRangeNotifiers();
            beaconManager.unbind(AR_NavigationActivity.this);
            Intent intent = new Intent();
            intent = new Intent(AR_NavigationActivity.this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected  void onStart()
    {
        super.onStart();
        mViroView.onActivityStarted(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mViroView.onActivityResumed(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mViroView.onActivityPaused(this);
    }

    @Override
    protected  void onStop()
    {
        super.onStop();
        mViroView.onActivityStopped(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        beaconManager.removeAllMonitorNotifiers();
        beaconManager.removeAllRangeNotifiers();
        beaconManager.unbind(this);
        mViroView.onActivityDestroyed(this);
        System.gc();
    }

    //Back Key
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            beaconManager.removeAllMonitorNotifiers();
            beaconManager.removeAllRangeNotifiers();
            beaconManager.unbind(AR_NavigationActivity.this);
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
        return true;
    }
}
