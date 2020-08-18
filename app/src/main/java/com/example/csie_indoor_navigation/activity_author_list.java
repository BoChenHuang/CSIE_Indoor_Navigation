package com.example.csie_indoor_navigation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class activity_author_list extends AppCompatActivity {

    String[] listViewTitle = new String[]{"Freepik"};
    String[] listviewShortDescription = new String[]{"Icons made by Freepik https://www.flaticon.com/authors/freepik"};
    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String,List<String>> listHashMap;

    private String EOS_TEAM;
    private String VERSION;
    private String INFORMATION;
    private String TEAM;
    private String AUTHOR;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.homepage_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.back_item)
        {
            Intent intent = new Intent(activity_author_list.this,MainActivity.class);
            startActivity(intent);
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkLanguage()
    {
        Context appContext = getApplicationContext();
        SharedPreferences languagePref = PreferenceManager.getDefaultSharedPreferences(appContext);
        String language_option = languagePref.getString("language","繁體中文");

        if(language_option.equals("繁體中文"))
        {
            EOS_TEAM = "雲科大嵌入式作業系統實驗室";
            INFORMATION = "相關資訊";
            VERSION = "版本";
            TEAM = "參與團隊";
            AUTHOR = "圖片作者";
        }
        else if(language_option.equals("English"))
        {
            EOS_TEAM = "Yuntech EOS LAB";
            INFORMATION = "Information";
            VERSION = "Version";
            TEAM = "Development Team";
            AUTHOR = "Picture Author";
        }
    }

    private void initData()
    {
        setTitle(INFORMATION);
        listDataHeader = new ArrayList<>();
        listDataHeader.add(VERSION);
        listDataHeader.add(TEAM);
        listDataHeader.add(AUTHOR);

        listHashMap = new HashMap<>();

        List<String> version = new ArrayList<>();
        version.add(getString(R.string.version_code));

        List<String> team = new ArrayList<>();
        team.add(EOS_TEAM);

        List<String> author = new ArrayList<>();
        for(int i=0; i<listViewTitle.length;i++){
            author.add(listViewTitle[i] +"\n\n" +listviewShortDescription[i]);
        }

        listHashMap.put(listDataHeader.get(0),version);
        listHashMap.put(listDataHeader.get(1),team);
        listHashMap.put(listDataHeader.get(2),author);
    }

    public boolean onKeyDown(int ketCode, KeyEvent event) {
        if(ketCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(activity_author_list.this,MainActivity.class);
            startActivity(intent);
            this.finish();
        }
        return super.onKeyDown(ketCode,event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_list);
        checkLanguage();
        listView = (ExpandableListView)findViewById(R.id.author_list);
        initData();
        listAdapter = new ExpandableListAdapter(this,listDataHeader,listHashMap);
        listView.setAdapter(listAdapter);
        }

    }
