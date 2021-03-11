package com.example.parking;

import android.app.Application;
import android.util.Log;

import com.example.parking.ml.Test2;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;

public class TFLite {

    private final Application application;
    private float w,time;
    private int idx;
    public TFLite(Application application){
        this.application = application;
    }

    public void runTFlite(float[] csvList){
        try {
            Test2 model = Test2.newInstance(application);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 26, 39, 1}, DataType.FLOAT32);
            inputFeature0.loadArray(csvList);

            long start = System.currentTimeMillis();
            // Runs model inference and gets result.
            Test2.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            long end = System.currentTimeMillis();
            time = (float) ((end-start)/1000.);

            float[] array = outputFeature0.getFloatArray();
            idx = argmax(array);
            w = array[idx];

            Log.d("20210305", idx+"_ "+w);
            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }
    public void runTFliteGPU(float[] csvList){

    }
    private int argmax(float[] array) {
        float max = array[0];
        int re = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
                re = i;
            }
        }
        return re;
    }
    public float getW(){
        return w;
    }
    public float getTime(){
        return time;
    }
    public int getIdx(){
        return idx;
    }
    public String getAns(){
        return String.format("%06d",Integer.valueOf(Integer.toString(idx, 2)));
    }

}
