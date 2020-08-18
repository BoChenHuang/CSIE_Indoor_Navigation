package com.example.csie_indoor_navigation;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private String SYSTEM;
    private String OK;
    private String CANCEL;
    private String AUTHORITY_MESSAGE;
    private String TITLE;
    private String CLASS_SERCHING;
    private String LAB;
    private String CLASSROOM;
    private String MEETINGROOM;
    private String OFFICE;
    private String OTHER;
    private String TURN_AUTHORITY_MESSAGE;
    private String MODE_SELECT_TITLE;
    private String AR_MOD;
    private String NORMAL_MOD;

    //UI
    private Intent intent;
    private Button courseBtn;
    private String courseBtnText = CLASS_SERCHING;
    private Button labBtn;
    private String labBtnText = LAB;
    private Button classRoomBtn;
    private String classRoomBtnText = CLASSROOM;
    private Button meetingRoomBtn;
    private String meetingRoomBtnText = MEETINGROOM;
    private Button officeRoomBtn;
    private String officeRoomBtnText = OFFICE;
    private Button otherBtn;
    private String otherBtnText = OTHER;

    //儲存所有類型的waypoint之名稱
    List<String> categoryList = new ArrayList<>();
    //儲存單類型的Node
    List<Node> CList = new ArrayList<>();
    //儲存模式選擇清單
    String[] listItem;
    //防止連續兩次點擊
    boolean ButtonClicked = false;
    //A HashMap which has String as key and list of vertice as value to be retrieved
    HashMap<String, List<Node>> categorizedDataList = new HashMap<>();
    //List of vertice for storing location data from regionData
    List<Node> listForStoringAllNodes = new ArrayList<>();
    static String destinationName,destinationID,destinationRegion;


    @Override
    //上方action bar之menu初始化
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //menu之按鈕功能
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.gear)
        {
            Intent intent = new Intent(MainActivity.this,activity_Language_Change.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.information)
        {
            Intent intent = new Intent(MainActivity.this,activity_author_list.class);
            startActivity(intent);
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    protected void checkAuthority() {
        // 定位權限要求
        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean providerEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!providerEnabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle(SYSTEM);
            dialog.setMessage(AUTHORITY_MESSAGE);
            dialog.setCancelable(false);
            dialog.setNegativeButton(CANCEL, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                }
            });
            dialog.setPositiveButton(OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    Intent locationintent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(locationintent);
                }
            });
            dialog.show();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Context appContext = getApplicationContext();
        SharedPreferences languagePref = PreferenceManager.getDefaultSharedPreferences(appContext);
        String language_option = languagePref.getString("language","繁體中文");

        if(language_option.equals("繁體中文"))
        {
            SYSTEM = getString(R.string.CHT_SYSTEM);
            OK = getString(R.string.CHT_OK);
            CANCEL = getString(R.string.CHT_CANCEL);
            AUTHORITY_MESSAGE = getString(R.string.CHT_AUTHORITY_MESSAGE);
            TURN_AUTHORITY_MESSAGE = getString(R.string.CHT_TURN_AUTHORITY_MESSAGE);
            TITLE = getString(R.string.CHT_TITLE);
            CLASS_SERCHING = getString(R.string.CHT_CLASS_SERCHING);
            LAB = getString(R.string.CHT_LAB);
            CLASSROOM = getString(R.string.CHT_CLASSROOM);
            MEETINGROOM = getString(R.string.CHT_MEETINGROOM);
            OFFICE = getString(R.string.CHT_OFFICE);
            OTHER = getString(R.string.CHT_OTHER);
            AR_MOD = getString(R.string.CHT_AR_MODE);
            NORMAL_MOD = getString(R.string.CHT_NORMAL_MODE);
            MODE_SELECT_TITLE = getString(R.string.CHT_MOD_SELECT);
            listItem = new String[]{AR_MOD,NORMAL_MOD};


            courseBtnText = CLASS_SERCHING;
            labBtnText = LAB;
            classRoomBtnText = CLASSROOM;
            meetingRoomBtnText = MEETINGROOM;
            officeRoomBtnText = OFFICE;
            otherBtnText = OTHER;

            courseBtn.setTextSize(18);
            labBtn.setTextSize(18);
            classRoomBtn.setTextSize(18);
            meetingRoomBtn.setTextSize(18);
            officeRoomBtn.setTextSize(18);
            otherBtn.setTextSize(18);
        }
        else if(language_option.equals("English"))
        {
            SYSTEM =getString(R.string.ENG_SYSTEM);
            OK = getString(R.string.ENG_OK);
            CANCEL = getString(R.string.ENG_CANCEL);
            AUTHORITY_MESSAGE = getString(R.string.ENG_AUTHORITY_MESSAGE);
            TURN_AUTHORITY_MESSAGE = getString(R.string.ENG_TURN_AUTHORITY_MESSAGE);
            TITLE = getString(R.string.ENG_TITLE);
            CLASS_SERCHING = getString(R.string.ENG_CLASS_SERCHING);
            LAB = getString(R.string.ENG_LAB);
            CLASSROOM = getString(R.string.ENG_CLASSROOM);
            MEETINGROOM = getString(R.string.ENG_MEETINGROOM);
            OFFICE = getString(R.string.ENG_OFFICE);
            OTHER = getString(R.string.ENG_OTHER);
            AR_MOD = getString(R.string.ENG_AR_MODE);
            NORMAL_MOD = getString(R.string.ENG_NORMAL_MODE);
            MODE_SELECT_TITLE = getString(R.string.ENG_MOD_SELECT);
            listItem = new String[]{AR_MOD,NORMAL_MOD};

            courseBtnText = CLASS_SERCHING;
            labBtnText = LAB;
            classRoomBtnText = CLASSROOM;
            meetingRoomBtnText = MEETINGROOM;
            officeRoomBtnText = OFFICE;
            otherBtnText = OTHER;


            courseBtn.setTextSize(15);
            labBtn.setTextSize(15);
            classRoomBtn.setTextSize(15);
            meetingRoomBtn.setTextSize(15);
            officeRoomBtn.setTextSize(15);
            otherBtn.setTextSize(15);
        }
        setTitle(TITLE);
        courseBtn.setText(courseBtnText);
        labBtn.setText(labBtnText);
        classRoomBtn.setText(classRoomBtnText);
        meetingRoomBtn.setText(meetingRoomBtnText);
        officeRoomBtn.setText(officeRoomBtnText);
        otherBtn.setText(otherBtnText);

    }

    @Override
    protected void onDestroy() {
        System.gc();
        super.onDestroy();
    }

    public void onClick(View view){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean providerEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!bluetoothAdapter.isEnabled() || !providerEnable)
        {
            Toast toast = Toast.makeText(MainActivity.this,
                    TURN_AUTHORITY_MESSAGE, Toast.LENGTH_LONG);
            toast.show();
        }
        else if(ButtonClicked == false)
        {
            ButtonClicked = true;
            switch (view.getId())
            {
                case R.id.bookButtom:
                    // TODO: 2020/6/4 課程查詢頁面轉換
                    break;
                case R.id.labButton:
                    //取出category = 實驗室的目的地
                    for(int i = 0; i < listForStoringAllNodes.size(); i++)
                    {
                        if(listForStoringAllNodes.get(i)._category.equals("實驗室"))
                            CList.add(listForStoringAllNodes.get(i));
                    }
                    Log.i("CList","size = " + CList.size());
                    //如果開類型只有一個Node直接進NaviagationActivity
                    if(CList.size() == 1)
                    {
                        destinationName = CList.get(0)._waypointName;
                        destinationID = CList.get(0)._waypointID;
                        destinationRegion = CList.get(0)._regionID;

                        new android.app.AlertDialog.Builder(MainActivity.this).setTitle(MODE_SELECT_TITLE)
                        .setItems(listItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0){
                                    // TODO: 2020/7/16 intent to AR activity
                                }
                                else if(which == 1){
                                    Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
                                    intent.putExtra("destinationName", destinationName);
                                    intent.putExtra("destinationID", destinationID);
                                    intent.putExtra("destinationRegion", destinationRegion);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }).show();
                        CList.clear();
                        ButtonClicked = false;
                    }
                    else if(CList.size() > 1)
                    {
                        intent = new Intent(MainActivity.this, activity_category_list.class);
                        intent.putExtra("Category","實驗室");
                        startActivity(intent);
                        ButtonClicked = false;
                        finish();
                    }
                    break;
                case R.id.classRoomButton:
                    for(int i = 0; i < listForStoringAllNodes.size(); i++){
                        if(listForStoringAllNodes.get(i)._category.equals("教室")){
                            CList.add(listForStoringAllNodes.get(i));
                        }
                    }

                    if(CList.size() == 1){
                        destinationName = CList.get(0)._waypointName;
                        destinationID = CList.get(0)._waypointID;
                        destinationRegion = CList.get(0)._regionID;

                        new android.app.AlertDialog.Builder(MainActivity.this).setTitle(MODE_SELECT_TITLE)
                                .setItems(listItem, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which == 0){
                                            // TODO: 2020/7/16  intent to AR activity
                                        }
                                        else if(which == 1){
                                            Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
                                            intent.putExtra("destinationName", destinationName);
                                            intent.putExtra("destinationID", destinationID);
                                            intent.putExtra("destinationRegion", destinationRegion);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }).show();
                        CList.clear();
                        ButtonClicked = false;
                    }
                    else if(CList.size() > 1){
                        intent = new Intent(MainActivity.this, activity_category_list.class);
                        intent.putExtra("Category", "教室");
                        startActivity(intent);
                        ButtonClicked = false;
                        finish();
                    }
                    break;
                case R.id.meetingRoomButton:
                    for (int i = 0; i < listForStoringAllNodes.size(); i++){
                        if(listForStoringAllNodes.get(i)._category.equals("會議室"))
                            CList.add(listForStoringAllNodes.get(i));
                    }
                    if(CList.size() == 1){
                        destinationID = CList.get(0)._waypointID;
                        destinationName = CList.get(0)._waypointName;
                        destinationRegion = CList.get(0)._regionID;

                        new android.app.AlertDialog.Builder(MainActivity.this).setTitle(MODE_SELECT_TITLE)
                                .setItems(listItem, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which == 0){
                                            // TODO: 2020/7/16  intent to AR activity
                                        }
                                        else if (which == 1){
                                            Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
                                            intent.putExtra("destinationName", destinationName);
                                            intent.putExtra("destinationID", destinationID);
                                            intent.putExtra("destinationRegion", destinationRegion);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }).show();
                        CList.clear();
                        ButtonClicked = false;
                    }
                    else if(CList.size() > 1){
                        intent = new Intent(MainActivity.this, activity_category_list.class);
                        intent.putExtra("Category", "會議室");
                        startActivity(intent);
                        ButtonClicked = false;
                        finish();
                    }
                    break;
                case R.id.officeButton:
                    for(int i = 0; i < listForStoringAllNodes.size(); i++){
                        if(listForStoringAllNodes.get(i)._category.equals("系辦"))
                            CList.add(listForStoringAllNodes.get(i));
                    }
                    if(CList.size() == 1){
                        destinationID = CList.get(0)._waypointID;
                        destinationName = CList.get(0)._waypointName;
                        destinationRegion = CList.get(0)._regionID;

                        new android.app.AlertDialog.Builder(MainActivity.this).setTitle(MODE_SELECT_TITLE)
                                .setItems(listItem, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which == 0){
                                            // TODO: 2020/7/16  intent to AR activity
                                        }
                                        else if(which == 1){
                                            Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
                                            intent.putExtra("destinationName", destinationName);
                                            intent.putExtra("destinationID", destinationID);
                                            intent.putExtra("destinationRegion", destinationRegion);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }).show();
                        CList.clear();
                        ButtonClicked = false;
                    }
                    else if(CList.size() > 1){
                        intent = new Intent(MainActivity.this, activity_category_list.class);
                        intent.putExtra("Category", "系辦");
                        startActivity(intent);
                        ButtonClicked = false;
                        finish();
                    }
                    break;
                case R.id.otherButton:
                    for (int i = 0; i < listForStoringAllNodes.size(); i++){
                        if(listForStoringAllNodes.get(i)._category.equals("其他"))
                            CList.add(listForStoringAllNodes.get(i));
                    }
                    if (CList.size() == 1){
                        destinationID = CList.get(0)._waypointID;
                        destinationName = CList.get(0)._waypointName;
                        destinationRegion = CList.get(0)._regionID;

                        new android.app.AlertDialog.Builder(MainActivity.this).setTitle(MODE_SELECT_TITLE)
                                .setItems(listItem, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which == 0){
                                            // TODO: 2020/7/16 intent to AR activity
                                        }
                                        else if(which == 1){
                                            Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
                                            intent.putExtra("destinationName", destinationName);
                                            intent.putExtra("destinationID", destinationID);
                                            intent.putExtra("destinationRegion", destinationRegion);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }).show();
                        CList.clear();
                        ButtonClicked = false;
                    }
                    else if(CList.size() > 1){
                        intent = new Intent(MainActivity.this, activity_category_list.class);
                        intent.putExtra("Category", "其他");
                        startActivity(intent);
                        ButtonClicked = false;
                        finish();
                    }
                    break;
            }
        }
    }

    //讀取首頁分類選單的waypoint
    public void loadLocationDatafromRegionGraph()
    {
        Log.i("xxx_List","loadLocationDatafromRegionGraph");
        //A HashMap to store region data, use region name as key to retrieve data
        DataParser dataParser = new DataParser(getApplicationContext());
        RegionGraph regionGraph = dataParser.getRegionDataFromRegionGraph(this);



        //Get all category names of POI(point of interest) of the test building
        categoryList = DataParser.getCategoryList();

        //Retrieve all location information from regionData and store it as a list of vertice
        for(Region r : regionGraph.regionData.values()){
            listForStoringAllNodes.addAll(r._locationsOfRegion);
        }

        //Categorize Vertices into data list,
        //the Vertices in the same data list have the same category
        for(int i = 0; i< categoryList.size(); i++){

            List<Node> tmpDataList = new ArrayList<>();

            for(Node v : listForStoringAllNodes){

                if(v._category.equals(categoryList.get(i)))
                    tmpDataList.add(v);
            }

            categorizedDataList.put(categoryList.get(i),tmpDataList);
        }

        for(int i = 0; i < listForStoringAllNodes.size(); i++){
            Log.i("All_Nodes_List","all node = " + listForStoringAllNodes.get(i)._waypointName);
        }

        // TODO: 2020/7/16  finish the version check
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(TITLE);
        checkAuthority();

        //initial UI
        courseBtn = findViewById(R.id.bookButtom);
        labBtn = findViewById(R.id.labButton);
        classRoomBtn = findViewById(R.id.classRoomButton);
        meetingRoomBtn = findViewById(R.id.meetingRoomButton);
        officeRoomBtn = findViewById(R.id.officeButton);
        otherBtn = findViewById(R.id.otherButton);

        //read the xml file
        loadLocationDatafromRegionGraph();
    }
}
