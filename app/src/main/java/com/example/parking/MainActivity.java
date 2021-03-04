package com.example.parking;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView lvShow=null;
    private TextView idx_cpu,w_cpu,runTime_cpu,idx_gpu,w_gpu,runTime_gpu,file;
    private List<String> csvFile = new ArrayList<String>();
    private String csvName="";
    private Button CPU,GPU;
    private RunDlc runDlc;
    private Application myApplication;
    //
    private ToWeb toWeb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idx_cpu = findViewById(R.id.textView2);
        w_cpu = findViewById(R.id.textView3);
        runTime_cpu = findViewById(R.id.textView5);
        idx_gpu = findViewById(R.id.textView10);
        w_gpu = findViewById(R.id.textView8);
        runTime_gpu = findViewById(R.id.textView9);
        CPU = findViewById(R.id.button);
        GPU = findViewById(R.id.button2);
        file = findViewById(R.id.textView12);
        CPU.setEnabled(false);
        GPU.setEnabled(false);

        lvShow=(ListView)findViewById(R.id.listV);
        setFileName();
        setAdapter();

        myApplication = this.getApplication();
        lvShow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                csvName=csvFile.get(position);
                Log.d("tzu msg",csvName);
                file.setText(csvName);
                CPU.setEnabled(true);
                GPU.setEnabled(true);
                //view.setBackgroundColor(Color.rgb(255, 255, 148));
            }
        });
        CPU.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                runDlc = new RunDlc(myApplication,0);
                runModel(csvName,runDlc);
                idx_cpu.setText(runDlc.getAns()+"");
                w_cpu.setText(runDlc.getW()+"");
                runTime_cpu.setText(runDlc.getTime()+" s");
                //
                toWeb = new ToWeb(runDlc.getAns());
                toWeb.run();
            }
        });
        GPU.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                runDlc = new RunDlc(myApplication,1);
                runModel(csvName,runDlc);
                idx_gpu.setText(runDlc.getAns()+"");
                w_gpu.setText(runDlc.getW()+"");
                runTime_gpu.setText(runDlc.getTime()+" s");
                //
                toWeb = new ToWeb(runDlc.getAns());
                toWeb.run();
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
    private void runModel(String csvName,RunDlc runDlc){
        File file =new File(this.getExternalFilesDir(null)+"/data/"+csvName);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        CSVFile csvFile = new CSVFile(inputStream);
        float[] csvList = csvFile.read();
        /////////////////////////////////////////////////////////

        runDlc.classify(csvList);
        //
        //Log.d("tzu runTime", runDlc.getSupportedRuntimes().toString());
    }

}