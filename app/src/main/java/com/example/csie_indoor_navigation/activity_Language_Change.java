package com.example.csie_indoor_navigation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class activity_Language_Change extends AppCompatActivity {
    private ListView language_list;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.homepage_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.back_item)
        {
            Intent intent = new Intent(activity_Language_Change.this,MainActivity.class);
            startActivity(intent);
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__language__change);
        language_list = (ListView)findViewById(R.id.language_list);
        final String[] language = {"繁體中文","English"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_view_layout, language);
        language_list.setAdapter(adapter);
        language_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            //單擊事件
            //onItemClick(ListView, item )
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)view;
                String language_item = (String)textView.getText();
                Log.i("language", "language  : " + language_item);
                //write the language option in SharedPreferences
                SharedPreferences languagePref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = languagePref.edit();
                editor.putString("language",language_item);
                editor.commit();
                Intent intent = new Intent(activity_Language_Change.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
