package com.example.csie_indoor_navigation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.csie_indoor_navigation.RecyclerViewAdaper.MyViewHolder;

public class RecyclerViewAdaper extends RecyclerView.Adapter<MyViewHolder> {
    private LayoutInflater inflater;
    List<Node> data = Collections.emptyList();
    Boolean clicked = false;
    Context context;
    String[] listItem;
    String AR_MOD;
    String NORMAL_MOD;
    String TITLE;

    public RecyclerViewAdaper(Context context, List<Node> data)
    {
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
        languageSetup(context);
    }

    public void languageSetup(Context context){

        Context appContext = GetApplicationContext.getAppContext();
        SharedPreferences languagePref = PreferenceManager.getDefaultSharedPreferences(context);
        String language_option = languagePref.getString("language","繁體中文");

        if(language_option.equals("繁體中文")){
            AR_MOD = "AR模式";
            NORMAL_MOD = "一般模式";
            TITLE = "模式選擇";
        }
        else if(language_option.equals("English")){
            AR_MOD = "AR Mode";
            NORMAL_MOD = "Normal Mode";
            TITLE = "Mode Select";
        }
        listItem = new String[]{AR_MOD,NORMAL_MOD};
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        private Button title;
        public MyViewHolder(View itemView)
        {
            super(itemView);
            title = (Button) itemView.findViewById(R.id.listText);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.rowitem,null);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    //绑定顯示在UI上的名稱及按鈕事件
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Node current = data.get(position);
        holder.title.setText(current.getName());
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        int width = windowManager.getDefaultDisplay().getWidth();
        holder.title.setWidth(width);
        
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clicked == false)
                {
                    clicked = true;
                    new AlertDialog.Builder(RecyclerViewAdaper.this.context).setTitle(TITLE)
                            .setItems(listItem, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which == 0){ //AR mode
                                        Intent intent = new Intent(context, AR_NavigationActivity.class);
                                        Log.i("current" ,"currentName = " + current.getName()
                                                + "\ncurrentID = " + current.getID()
                                                + "\ncurrentRegion = " + current.get_regionID());

                                        intent.putExtra("destinationName", current.getName());
                                        intent.putExtra("destinationID", current.getID());
                                        intent.putExtra("destinationRegion",current.get_regionID());
                                        context.startActivity(intent);
                                        ((Activity)context).finish();
                                    }
                                    else if(which == 1){//Normal mode
                                        Intent intent = new Intent(context, NavigationActivity.class);
                                        Log.i("current" ,"currentName = " + current.getName()
                                                                    + "\ncurrentID = " + current.getID()
                                                                    + "\ncurrentRegion = " + current.get_regionID());

                                        intent.putExtra("destinationName", current.getName());
                                        intent.putExtra("destinationID", current.getID());
                                        intent.putExtra("destinationRegion",current.get_regionID());
                                        context.startActivity(intent);
                                        ((Activity)context).finish();

                                    }
                                }
                            }).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
