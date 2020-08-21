package com.example.csie_indoor_navigation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CurriculumActivity extends AppCompatActivity {

    TextView dateText;
    TextView curriculumText;
    Button eb109TimeAbtn;
    Button eb110TimeAbtn;
    Button eb203TimeAbtn;
    Button eb208TimeAbtn;
    Button eb211TimeAbtn;

    Button eb109TimeBbtn;
    Button eb110TimeBbtn;
    Button eb203TimeBbtn;
    Button eb208TimeBbtn;
    Button eb211TimeBbtn;

    Button eb109TimeCbtn;
    Button eb110TimeCbtn;
    Button eb203TimeCbtn;
    Button eb208TimeCbtn;
    Button eb211TimeCbtn;

    Button eb109TimeDbtn;
    Button eb110TimeDbtn;
    Button eb203TimeDbtn;
    Button eb208TimeDbtn;
    Button eb211TimeDbtn;

    Button eb109TimeEbtn;
    Button eb110TimeEbtn;
    Button eb203TimeEbtn;
    Button eb208TimeEbtn;
    Button eb211TimeEbtn;

    Button eb109TimeFbtn;
    Button eb110TimeFbtn;
    Button eb203TimeFbtn;
    Button eb208TimeFbtn;
    Button eb211TimeFbtn;

    Button eb109TimeGbtn;
    Button eb110TimeGbtn;
    Button eb203TimeGbtn;
    Button eb208TimeGbtn;
    Button eb211TimeGbtn;

    Button eb109TimeHbtn;
    Button eb110TimeHbtn;
    Button eb203TimeHbtn;
    Button eb208TimeHbtn;
    Button eb211TimeHbtn;

    Button eb109TimeZbtn;
    Button eb110TimeZbtn;
    Button eb203TimeZbtn;
    Button eb208TimeZbtn;
    Button eb211TimeZbtn;

    String DATE;
    String CURRICULUM;
    //Course Name
    String EMBEDDED_OPERATING_SYSTEM;
    String COMPUTER_ORGANIZATION;
    String PROBABILITY_AND_STATISTICS;
    String SYSTEM_PROGRAMMING;
    String DIGITAL_LOGIC_DESIGN;
    String INTRODUCTION_TO_WIRELESS_NETWORK_AND_INTERNET_OF_VEHICLES_APPLICATION;
    String IOS_APP_PROGRAMMING;
    String DEEP_LEARNING_THEORY_AND_PRACTICE;
    String SEMINAR_I;
    String MACHINE_LEARNING;
    String CLOUD_COMPUTING_AND_MOBILE_EDGE_COMPUTING;
    String HYPERSPECTRAL_IMAGE_PROCESSING_AND_APPLICATIONS;
    String PHYSICS_I;
    String LECTURES_ON_ENGINEERING_PRACTICE_I;
    String MOBILE_APPLICATION_SECURITY;
    String DIGTAL_SIGNAL_PROCESSING;
    String ENGINEERING_MATHEMATICS;
    String INFORMATION_SECURITY;
    String COMPUTER_NETWORK;
    String DATA_STRUCTURES;
    String CALCULUS_I;

    String SUNDAY;
    String MONDAY;
    String TUESDAY;
    String WEDNESDAY;
    String THURSDAY;
    String FRIDAY;
    String SATURDAY;

    public void languageSetup()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String languageOption = sharedPreferences.getString("language","繁體中文");
        if(languageOption.equals("繁體中文"))
        {
            EMBEDDED_OPERATING_SYSTEM = "嵌入式作業系統";
            COMPUTER_ORGANIZATION = "計算機組織";
            PROBABILITY_AND_STATISTICS = "機率與統計";
            SYSTEM_PROGRAMMING = "系統程式";
            DIGITAL_LOGIC_DESIGN = "數位邏輯設計";
            INTRODUCTION_TO_WIRELESS_NETWORK_AND_INTERNET_OF_VEHICLES_APPLICATION = "無線網路\n與車聯網應用概論";
            IOS_APP_PROGRAMMING = "iOS\n智慧裝置軟體設計";
            DEEP_LEARNING_THEORY_AND_PRACTICE = "深度學習\n理論與實務";
            SEMINAR_I = "書報討論(一)";
            MACHINE_LEARNING = "機器學習";
            CLOUD_COMPUTING_AND_MOBILE_EDGE_COMPUTING = "雲端計算\n與行動邊緣計算";
            HYPERSPECTRAL_IMAGE_PROCESSING_AND_APPLICATIONS = "高光譜影像處理\n技術與應用";
            PHYSICS_I = "物理(一)";
            LECTURES_ON_ENGINEERING_PRACTICE_I = "科技新知講座 (一)";
            MOBILE_APPLICATION_SECURITY = "行動應用軟體安全";
            DIGTAL_SIGNAL_PROCESSING = "數位訊號處理";
            ENGINEERING_MATHEMATICS = "工程數學";
            INFORMATION_SECURITY = "資訊安全";
            COMPUTER_NETWORK = "計算機網路";
            DATA_STRUCTURES = "資料結構";
            CALCULUS_I = "微積分(一)";

            MONDAY = "星期一";
            TUESDAY = "星期二";
            WEDNESDAY = "星期三";
            THURSDAY = "星期四";
            FRIDAY = "星期五";
            SATURDAY = "星期六";
            SUNDAY = "星期日";
        }
        else if(languageOption.equals("English"))
        {
            EMBEDDED_OPERATING_SYSTEM = "Embedded Operating System";
            COMPUTER_ORGANIZATION = "Computer Organization";
            PROBABILITY_AND_STATISTICS = "Probability and Statistics";
            SYSTEM_PROGRAMMING = "System Programming";
            DIGITAL_LOGIC_DESIGN = "Digital Logic Design";
            INTRODUCTION_TO_WIRELESS_NETWORK_AND_INTERNET_OF_VEHICLES_APPLICATION = "Introduction to Wireless Network\n and Internet of Vehicles Application";
            IOS_APP_PROGRAMMING = "iOS App Programming";
            DEEP_LEARNING_THEORY_AND_PRACTICE = "Deep Learning \nTheory and Practice ";
            SEMINAR_I = "Seminar (I) ";
            MACHINE_LEARNING = "Machine Learning ";
            CLOUD_COMPUTING_AND_MOBILE_EDGE_COMPUTING = "Cloud Computing \nand Mobile Edge Computing ";
            HYPERSPECTRAL_IMAGE_PROCESSING_AND_APPLICATIONS = "Hyperspectral Image \nProcessing and Applications";
            PHYSICS_I = "Physics (I)";
            LECTURES_ON_ENGINEERING_PRACTICE_I = "Lectures on Engineering Practice (I)";
            MOBILE_APPLICATION_SECURITY = "Mobile Application Security";
            DIGTAL_SIGNAL_PROCESSING = "Digtal Signal Processing";
            ENGINEERING_MATHEMATICS = "Engineering Mathematics";
            INFORMATION_SECURITY = "Information Security";
            COMPUTER_NETWORK = "Computer Network ";
            DATA_STRUCTURES = "Data Structures";
            CALCULUS_I = "Calculus (I)";

            MONDAY = "Monday";
            TUESDAY = "Tuesday";
            WEDNESDAY = "Wednesday";
            THURSDAY = "Thursday";
            FRIDAY = "Friday";
            SATURDAY = "Saturday";
            SUNDAY = "Sunday";
        }
    }
    public void initialButtom(int week)
    {
        if(week == 2 || week == 6)
            return;
        //Monday
        else if(week == 2)
        {
            eb109TimeBbtn.setVisibility(View.VISIBLE);
            eb109TimeCbtn.setVisibility(View.VISIBLE);
            eb109TimeDbtn.setVisibility(View.VISIBLE);


            eb109TimeGbtn.setVisibility(View.VISIBLE);
            eb109TimeHbtn.setVisibility(View.VISIBLE);
            eb109TimeZbtn.setVisibility(View.VISIBLE);

            eb110TimeEbtn.setVisibility(View.VISIBLE);
            eb110TimeFbtn.setVisibility(View.VISIBLE);
            eb110TimeGbtn.setVisibility(View.VISIBLE);

            eb208TimeEbtn.setVisibility(View.VISIBLE);
            eb208TimeFbtn.setVisibility(View.VISIBLE);
            eb208TimeGbtn.setVisibility(View.VISIBLE);

            eb208TimeBbtn.setVisibility(View.VISIBLE);
            eb208TimeCbtn.setVisibility(View.VISIBLE);
            eb208TimeDbtn.setVisibility(View.VISIBLE);

            eb211TimeBbtn.setVisibility(View.VISIBLE);
            eb211TimeCbtn.setVisibility(View.VISIBLE);
            eb211TimeDbtn.setVisibility(View.VISIBLE);

        }
        //Tuesday
        else if(week == 3)
        {

        }
        //Wednesday
        else if(week == 4)
        {

        }
        //Thrusday
        else if(week == 5)
        {
            eb109TimeBbtn.setVisibility(View.VISIBLE);
            eb109TimeCbtn.setVisibility(View.VISIBLE);
            eb109TimeDbtn.setVisibility(View.VISIBLE);
            eb109TimeBbtn.setClickable(true);
            eb109TimeCbtn.setClickable(true);
            eb109TimeDbtn.setClickable(true);
            eb109TimeBbtn.setText(DATA_STRUCTURES);
            eb109TimeCbtn.setText(DATA_STRUCTURES);
            eb109TimeDbtn.setText(DATA_STRUCTURES);

            eb109TimeEbtn.setVisibility(View.VISIBLE);
            eb109TimeFbtn.setVisibility(View.VISIBLE);
            eb109TimeGbtn.setVisibility(View.VISIBLE);
            eb109TimeEbtn.setClickable(true);
            eb109TimeFbtn.setClickable(true);
            eb109TimeGbtn.setClickable(true);
            eb109TimeEbtn.setText(CALCULUS_I);
            eb109TimeFbtn.setText(CALCULUS_I);
            eb109TimeGbtn.setText(CALCULUS_I);

            eb110TimeEbtn.setVisibility(View.VISIBLE);
            eb110TimeFbtn.setVisibility(View.VISIBLE);
            eb110TimeGbtn.setVisibility(View.VISIBLE);
            eb110TimeEbtn.setClickable(true);
            eb110TimeFbtn.setClickable(true);
            eb110TimeGbtn.setClickable(true);
            eb110TimeEbtn.setText(INFORMATION_SECURITY);
            eb110TimeFbtn.setText(INFORMATION_SECURITY);
            eb110TimeGbtn.setText(INFORMATION_SECURITY);

            eb208TimeBbtn.setVisibility(View.VISIBLE);
            eb208TimeCbtn.setVisibility(View.VISIBLE);
            eb208TimeDbtn.setVisibility(View.VISIBLE);
            eb208TimeBbtn.setClickable(true);
            eb208TimeCbtn.setClickable(true);
            eb208TimeDbtn.setClickable(true);
            eb208TimeBbtn.setText(CLOUD_COMPUTING_AND_MOBILE_EDGE_COMPUTING);
            eb208TimeCbtn.setText(CLOUD_COMPUTING_AND_MOBILE_EDGE_COMPUTING);
            eb208TimeDbtn.setText(CLOUD_COMPUTING_AND_MOBILE_EDGE_COMPUTING);

            eb211TimeBbtn.setVisibility(View.VISIBLE);
            eb211TimeCbtn.setVisibility(View.VISIBLE);
            eb211TimeDbtn.setVisibility(View.VISIBLE);
            eb211TimeBbtn.setClickable(true);
            eb211TimeCbtn.setClickable(true);
            eb211TimeDbtn.setClickable(true);
            eb211TimeBbtn.setText(DEEP_LEARNING_THEORY_AND_PRACTICE);
            eb211TimeCbtn.setText(DEEP_LEARNING_THEORY_AND_PRACTICE);
            eb211TimeDbtn.setText(DEEP_LEARNING_THEORY_AND_PRACTICE);
        }
        //Friday
        else if(week == 6)
        {

        }
    }
    public void showDateToUI()
    {
        String time = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int week = calendar.get(Calendar.DAY_OF_WEEK);

        dateText.setText(calendar.get(Calendar.YEAR) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.DAY_OF_MONTH));
        switch (week)
        {
            //Sunday
            case 1:
                curriculumText.setText(SUNDAY + " " + calendar.get(Calendar.HOUR_OF_DAY) + " : "+ calendar.get(Calendar.MINUTE));
                break;
            //Monday
            case 2:
                curriculumText.setText(MONDAY + " " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE));
                break;

            //Tuesday
            case 3:
                curriculumText.setText(TUESDAY + " " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE));
                break;

            //Wednesday
            case 4:
                curriculumText.setText(WEDNESDAY + " " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE));
                break;

            //Thursday
            case 5:
                curriculumText.setText(THURSDAY + " " + calendar.get(Calendar.HOUR_OF_DAY) + " : " +calendar.get(Calendar.MINUTE));
                break;
            //Friday
            case 6:
                curriculumText.setText(FRIDAY + " " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE));
                break;

            //Saturday
            case 7:
                curriculumText.setText(SATURDAY + " " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE));
                break;
        }
        initialButtom(week);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curriculum);
        dateText = findViewById(R.id.dateText);
        curriculumText = findViewById(R.id.curriculumText);
        eb109TimeAbtn = findViewById(R.id.eb109TimeAbtn);
        eb110TimeAbtn = findViewById(R.id.eb110TimeAbtn);
        eb203TimeAbtn = findViewById(R.id.eb203TimeAbtn);
        eb208TimeAbtn = findViewById(R.id.eb208TimeAbtn);
        eb211TimeAbtn = findViewById(R.id.eb211TimeAbtn);

        eb109TimeBbtn = findViewById(R.id.eb109TimeBbtn);
        eb110TimeBbtn = findViewById(R.id.eb110TimeBbtn);
        eb203TimeBbtn = findViewById(R.id.eb203TimeBbtn);
        eb208TimeBbtn = findViewById(R.id.eb208TimeBbtn);
        eb211TimeBbtn = findViewById(R.id.eb211TimeBbtn);

        eb109TimeCbtn = findViewById(R.id.eb109TimeCbtn);
        eb110TimeCbtn = findViewById(R.id.eb110TimeCbtn);
        eb203TimeCbtn = findViewById(R.id.eb203TimeCbtn);
        eb208TimeCbtn = findViewById(R.id.eb208TimeCbtn);
        eb211TimeCbtn = findViewById(R.id.eb211TimeCbtn);

        eb109TimeDbtn = findViewById(R.id.eb109TimeDbtn);
        eb110TimeDbtn = findViewById(R.id.eb110TimeDbtn);
        eb203TimeDbtn = findViewById(R.id.eb203TimeDbtn);
        eb208TimeDbtn = findViewById(R.id.eb208TimeDbtn);
        eb211TimeDbtn = findViewById(R.id.eb211TimeDbtn);

        eb109TimeEbtn = findViewById(R.id.eb109TimeEbtn);
        eb110TimeEbtn = findViewById(R.id.eb110TimeEbtn);
        eb203TimeEbtn = findViewById(R.id.eb203TimeEbtn);
        eb208TimeEbtn = findViewById(R.id.eb208TimeEbtn);
        eb211TimeEbtn = findViewById(R.id.eb211TimeEbtn);

        eb109TimeFbtn = findViewById(R.id.eb109TimeFbtn);
        eb110TimeFbtn = findViewById(R.id.eb110TimeFbtn);
        eb203TimeFbtn = findViewById(R.id.eb203TimeFbtn);
        eb208TimeFbtn = findViewById(R.id.eb208TimeFbtn);
        eb211TimeFbtn = findViewById(R.id.eb211TimeFbtn);

        eb109TimeGbtn = findViewById(R.id.eb109TimeGbtn);
        eb110TimeGbtn = findViewById(R.id.eb110TimeGbtn);
        eb203TimeGbtn = findViewById(R.id.eb203TimeGbtn);
        eb208TimeGbtn = findViewById(R.id.eb208TimeGbtn);
        eb211TimeGbtn = findViewById(R.id.eb211TimeGbtn);

        eb109TimeHbtn = findViewById(R.id.eb109TimeHbtn);
        eb110TimeHbtn = findViewById(R.id.eb110TimeHbtn);
        eb203TimeHbtn = findViewById(R.id.eb203TimeHbtn);
        eb208TimeHbtn = findViewById(R.id.eb208TimeHbtn);
        eb211TimeHbtn = findViewById(R.id.eb211TimeHbtn);

        eb109TimeZbtn = findViewById(R.id.eb109TimeZbtn);
        eb110TimeZbtn = findViewById(R.id.eb110TimeZbtn);
        eb203TimeZbtn = findViewById(R.id.eb203TimeZbtn);
        eb208TimeZbtn = findViewById(R.id.eb208TimeZbtn);
        eb211TimeZbtn = findViewById(R.id.eb211TimeZbtn);

        languageSetup();
        showDateToUI();

    }
}
