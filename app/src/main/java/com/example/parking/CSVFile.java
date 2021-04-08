package com.example.parking;

import android.app.Application;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CSVFile {
    private InputStream inputStream = null;
    private Application application;

    public CSVFile(Application application){
        this.application = application;
    }

    public float[] getInput(String csvName){
        File file =new File(application.getExternalFilesDir(null)+"/data/"+csvName);
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        float[] csvList = read();

        return csvList;
    }

    private float[]read(){
        float[][] csvList = new float[64][48];
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            int i = 0;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
                for(int j=0;j<row.length;j++){
                    csvList[i][j]= Float.parseFloat(row[j]);
                }
                i++;
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }
        float[]resultList = extract(csvList);

        return resultList;
    }

    private float[] extract(float[][] csvList){
        float[]resultList = new float[26*39];
        for(int i=0;i<26;i++){
            for(int j=0;j<39;j++){
                if(j<33)
                    resultList[i*39+j]=csvList[i+33][j+2];
                else
                    resultList[i*39+j]=csvList[i+33][j+5];
            }
        }
        return resultList;
    }

}
