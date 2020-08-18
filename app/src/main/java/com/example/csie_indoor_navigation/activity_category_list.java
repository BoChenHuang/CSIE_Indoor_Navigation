package com.example.csie_indoor_navigation;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class activity_category_list extends AppCompatActivity {

    private String TITLE;

    //存放所有類別NodeName的StringList
    List<String> categoryList = new ArrayList<>();

    //存放該類型Node頂點的RegionData
    List<Node> listForStoringAllNodes = new ArrayList<>();
    List<Node> tempList1 = new ArrayList<>();
    List<Node> tempList2 = new ArrayList<>();
    List<Node> sortList = new ArrayList<>();
    List<List<Node>> segment = new ArrayList<>();

    //對應到頂點資料的HashMap
    HashMap<String,List<Node>> categoryDataList = new HashMap<>();

    private RecyclerView recyclerView;
    private RecyclerViewAdaper adapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.homepage_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.back_item)
        {
            Intent intent = new Intent(activity_category_list.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            Intent intent = new Intent(activity_category_list.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void checkLanguage()
    {
        //語言設定
        SharedPreferences languagePref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String language_option = languagePref.getString("language","繁體中文");
        if(language_option.equals("繁體中文")) {
            TITLE = getString(R.string.CHT_TITLE);
        }else  if(language_option.equals("English")) {
            TITLE = getString(R.string.ENG_TITLE);
        }
        setTitle(TITLE);
    }

    public void loadLocationDataFromRegionGraph(){
        //存放Region Data的HashMap
        RegionGraph regionGraph = DataParser.getRegionDataFromRegionGraph(this);

        categoryList = DataParser.getCategoryList();
        int index = 0;

        //排序選單List，region數取決於場域，因此index也要做隨region數量做更動
        for(Region r : regionGraph.regionData.values()){
            listForStoringAllNodes.addAll(r._locationsOfRegion);
            if(index == 0)
                tempList1.addAll(listForStoringAllNodes);
            else if(index == 1)
                tempList2.addAll(listForStoringAllNodes);
            index ++;
        }

        for(int i = 0; i < tempList1.size(); i++){
            tempList2.removeAll(tempList1);
        }

        sortList.addAll(tempList2);
        sortList.addAll(tempList1);

        for(int i = 0; i < categoryList.size(); i++){
            List<Node> tempDataList = new ArrayList<>();

            for(Node v : listForStoringAllNodes){
                if(v._category.equals(categoryList.get(i)))
                    tempDataList.add(v);
            }
            categoryDataList.put(categoryList.get(i),tempDataList);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        checkLanguage();
        
        Bundle bundle = getIntent().getExtras();
        String category = bundle.getString("Category");
        recyclerView = findViewById(R.id.displayList);
        loadLocationDataFromRegionGraph();

        String[] stringArray = new String[categoryDataList.size()];
        adapter = new RecyclerViewAdaper(this,categoryDataList.get(category));

        //Seprate every selectable item with divider line
        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getBaseContext(),R.drawable.divider));

        //RecycleView set with an adapter with selected data list then feed the data list into UI display
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(divider);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DataParser.clearCategoryList();

    }
}
