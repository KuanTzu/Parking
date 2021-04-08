package com.example.parking;

import android.app.Application;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.CompatibilityList;
import org.tensorflow.lite.gpu.GpuDelegate;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TFLiteGPU extends Model{
    private final Interpreter.Options tfliteOptions = new Interpreter.Options();
    private final CompatibilityList compatList = new CompatibilityList();
    private static final String MODEL_FILENAME = "file:///android_asset/test_2 - Copy.tflite";
    private Interpreter tfLite;
    private int dataC=0;
    private float ansC=0;
    private Application application;

    public TFLiteGPU(Application application){
        this.application = application;
    }
    /** Memory-map the model file in Assets. */
    private static MappedByteBuffer loadModelFile(AssetManager assets, String modelFilename)
            throws IOException {
        AssetFileDescriptor fileDescriptor = assets.openFd(modelFilename);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public void RunTFLiteGPU(float[] InputBuffer){
        if(compatList.isDelegateSupportedOnThisDevice()){
            // if the device has a supported GPU, add the GPU delegate
            GpuDelegate.Options delegateOptions = compatList.getBestOptionsForThisDevice();
            GpuDelegate gpuDelegate = new GpuDelegate(delegateOptions);
            tfliteOptions.addDelegate(gpuDelegate);
            Log.d("TZU", "used GPU");
        } else {
            // if the GPU is not supported, run on 4 threads
            tfliteOptions.setNumThreads(4);
        }

        String actualModelFilename = MODEL_FILENAME.split("file:///android_asset/", -1)[1];
        try {
            tfLite = new Interpreter(loadModelFile(application.getAssets(), actualModelFilename),tfliteOptions);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        float[][] outputScores = new float[1][64];
        float[][][][] InputBufferReshape = reshape(new int[]{1, 26, 39, 1},InputBuffer);
        Object[] inputArray = {InputBufferReshape};
        Map<Integer, Object> outputMap = new HashMap<>();
        outputMap.put(0, outputScores);
        // Run the model.
        tfLite.runForMultipleInputsOutputs(inputArray, outputMap);

        Log.d("output", Arrays.toString(outputScores[0]));
        Log.d("output", argmax(outputScores[0])+"");
    }

    private float[][][][] reshape(int[] size,float[] InputBuffer){
        float[][][][] arr= new float[size[0]][size[1]][size[2]][size[3]];

        for(int i = 0; i<size[1]; i++){
            for(int j = 0; j<size[2]; j++){
                arr[0][i][j][0] = InputBuffer[i*39+j];
            }
        }

        Log.d("InputBuffer", Arrays.toString(InputBuffer));
        Log.d("arr", Arrays.deepToString(arr));

        return arr;
    }

}
