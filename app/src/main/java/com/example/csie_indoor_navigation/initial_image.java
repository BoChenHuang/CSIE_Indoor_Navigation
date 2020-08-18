package com.example.csie_indoor_navigation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class initial_image extends AppCompatActivity {

    private TextView faceDirection;
    private ImageView imageView;
    int passedDegree;
    String TITLE;
    String DIRECTION;
    String nowWaypointID;
    String nextWaypointID;
    int predictDirection;

    public void languageSetup(){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String lanuguageSetup =  preferences.getString("language","繁體中文");
        if(lanuguageSetup.equals("繁體中文")){
            setTitle(getString(R.string.CHT_TITLE));
            faceDirection.setText(getString(R.string.CHT_PLEASE_FACE_TO_THE_DIRECION));
        }
        else if(lanuguageSetup.equals("English")){
            setTitle(getString(R.string.ENG_TITLE));
            faceDirection.setText(getString(R.string.ENG_PLEASE_FACE_TO_THE_DIRECION));
        }

    }

    public int judgeTheDirection(String nowWaypointID, String nextWaypointID){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int direction = Integer.MAX_VALUE;

        switch (nowWaypointID){
            //A1
            case "00010015-0000-0010-1001-000000101001":
                if(nextWaypointID.equals(getString(R.string.UUID_of_A2)))
                {
                    imageView.setImageResource(R.drawable.a1_2);
                    direction = 0;
                }
                break;

            //A2
            case "00010015-0000-0010-1002-000000101002":
                if(nextWaypointID.equals(getString(R.string.UUID_of_A1)))
                {
                    imageView.setImageResource(R.drawable.a2_1);
                    direction = 4;
                }
                else if (nextWaypointID.equals(getString(R.string.UUID_of_A3)))
                {
                    imageView.setImageResource(R.drawable.a2_3);
                    direction = 2;
                }
                else if(nextWaypointID.equals(getString(R.string.UUID_of_A4)))
                {
                    imageView.setImageResource(R.drawable.a2_2);
                    direction = 6;
                }
                break;

            //A3
            case "00010015-0000-0010-1003-000000101003":
                if(nextWaypointID.equals(getString(R.string.UUID_of_A2)))
                {
                    imageView.setImageResource(R.drawable.a3_2);
                    direction = 6;
                }
                else if(nextWaypointID.equals(getString(R.string.UUID_of_B1)))
                {
                    imageView.setImageResource(R.drawable.a3_1);
                    direction = 4;
                }
                break;

            //A4
            case "00010015-0000-0010-1004-000000101004":
                if(nextWaypointID.equals(getString(R.string.UUID_of_A2)))
                {
                    imageView.setImageResource(R.drawable.a4_3);
                    direction = 2;
                }
                else if(nextWaypointID.equals(getString(R.string.UUID_of_B4)))
                {
                    imageView.setImageResource(R.drawable.a4_2);
                    direction = 4;
                }
                break;

            //B1
            case "00020015-0000-0010-1001-000000101001":
                if(nextWaypointID.equals(getString(R.string.UUID_of_A3)))
                {
                    imageView.setImageResource(R.drawable.b1_2);
                    direction = 4;
                }
                else if(nextWaypointID.equals(getString(R.string.UUID_of_B2)))
                {
                    imageView.setImageResource(R.drawable.b1_1);
                    direction = 6;
                }
                break;

            //B2
            case "00020015-0000-0010-1002-000000101002":
                if(nextWaypointID.equals(getString(R.string.UUID_of_B1)))
                {
                    imageView.setImageResource(R.drawable.b2_1);
                    direction = 2;
                }
                else if(nextWaypointID.equals(getString(R.string.UUID_of_B3)))
                {
                    imageView.setImageResource(R.drawable.b2_2);
                    direction = 6;
                }
                else if(nextWaypointID.equals(getString(R.string.UUID_of_B5)))
                {
                    imageView.setImageResource(R.drawable.b2_3);
                    direction = 4;
                }
                break;
            //B3
            case "00020015-0000-0010-1003-000000101003":
                if(nextWaypointID.equals(getString(R.string.UUID_of_B2)))
                {
                    imageView.setImageResource(R.drawable.b3_2);
                    direction = 2;
                }
                else if(nextWaypointID.equals(getString(R.string.UUID_of_B4)))
                {
                    imageView.setImageResource(R.drawable.b3_1);
                    direction = 6;
                }
                break;

            //B4
            case "00020015-0000-0010-1004-000000101004":
                if(nextWaypointID.equals(getString(R.string.UUID_of_B3)))
                {
                    imageView.setImageResource(R.drawable.b4_1);
                    direction = 2;
                }
                else if(nextWaypointID.equals(getString(R.string.UUID_of_A4)))
                {
                    imageView.setImageResource(R.drawable.b4_2);
                    direction = 4;
                }
                break;

            //B5
            case "00020015-0000-0010-1005-000000101005":
                if(nextWaypointID.equals(getString(R.string.UUID_of_B2)))
                {
                    imageView.setImageResource(R.drawable.b5_1);
                    direction = 0;
                }
                else if(nextWaypointID.equals(getString(R.string.UUID_of_B6)))
                {
                    imageView.setImageResource(R.drawable.b5_2);
                    direction = 4;
                }
                break;

            //B6
            case "00020015-0000-0010-1006-000000101006":
                Log.i("B6_case","nextWaypointID = " + nextWaypointID);
                if(nextWaypointID.equals(getString(R.string.UUID_of_B5)))
                {
                    Log.i("B6_case","nextImage = B5");
                    imageView.setImageResource(R.drawable.b6_1);
                    direction = 0;
                }
                else if(nextWaypointID.equals(getString(R.string.UUID_of_B7)))
                {
                    Log.i("B6_case","nextImage = B7");
                    imageView.setImageResource(R.drawable.b6_2);
                    direction = 6;
                }
                break;
            //B7
            case "00020015-0000-0010-1007-000000101007":
                if(nextWaypointID.equals(getString(R.string.UUID_of_B6)))
                {
                    imageView.setImageResource(R.drawable.b7_1);
                    direction = 2;
                }
                break;
        }

        return direction;
    }

    public void goNavigation(View view)
    {
        imageView.setImageDrawable(null);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_image);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();

        //Initial UI
        imageView = findViewById(R.id.initImage);
        faceDirection = findViewById(R.id.faceDirectoin);

        //Get the information from Navigation Activity
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            nowWaypointID = bundle.getString("nowID");
            nextWaypointID = bundle.getString("nextID");
        }
        languageSetup();
        predictDirection = judgeTheDirection(nowWaypointID,nextWaypointID);
        editor.putInt("predictDirection",predictDirection);
        editor.commit();

    }
}
