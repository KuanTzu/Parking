package com.example.parking;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView lvShow=null;
    private TextView file;
    private List<String> csvFile = new ArrayList<String>();
    private String csvName="";
    private Application myApplication;
    private String[] btn = {"SNPE-CPU","SNPE-GPU","TF-CPU","TF-GPU"};
    //
    private ToWeb toWeb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        file = findViewById(R.id.textView12);

        lvShow=(ListView)findViewById(R.id.listV);
        setFileName();
        setAdapter();

        myApplication = this.getApplication();

        RecyclerView rclv=findViewById(R.id.recycle);
        RecyclerView.LayoutManager rclvLM=new LinearLayoutManager(this);
        rclv.setLayoutManager(rclvLM);


        lvShow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                csvName=csvFile.get(position);
                Log.d("tzu msg",csvName);
                file.setText(csvName);
                //view.setBackgroundColor(Color.rgb(255, 255, 148));

                MyAdapter a1=new MyAdapter(btn,myApplication,csvName);
                rclv.setAdapter(a1);
            }
        });
    }
    private void setAdapter() {
        ArrayAdapter<String> adapter=
                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,csvFile);
        lvShow.setAdapter(adapter);
    }
    private void setFileName(){
        File files = new File(this.getExternalFilesDir(null)+"/data");
        for(int i=0;i<files.listFiles().length;i++){
            csvFile.add(files.listFiles()[i].getName());
        }
    }
}