package com.example.parking;

import android.app.Application;
import android.util.Log;

import com.example.parking.ml.Test2;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;

public class TFLite extends Model{

    private final Application application;
    public TFLite(Application application){
        this.application = application;
    }

    public void runModel(float[] csvList){
        Log.d("TZU","TF-CPU");
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

            Log.d("TFlite", idx+"_ "+w);
            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }
}
