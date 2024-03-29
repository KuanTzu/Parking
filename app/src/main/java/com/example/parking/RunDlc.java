package com.example.parking;

import android.app.Application;
import android.util.Log;

import com.qualcomm.qti.snpe.FloatTensor;
import com.qualcomm.qti.snpe.NeuralNetwork;
import com.qualcomm.qti.snpe.SNPE;
import com.qualcomm.qti.snpe.Tensor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class RunDlc extends Model{

    private NeuralNetwork mNeuralNetwork;
    final String mInputLayer;
    final String mOutputLayer;
    private final Application application;
    private int rT=0;

    public RunDlc(Application application,int rT){
        this.application = application;
        this.rT = rT;
        //buildNetwork
        SNPE.NeuralNetworkBuilder builder = null;
        if(rT==0) {
            Log.d("TZU","SNPE-CPU");
            try {
                builder = new SNPE.NeuralNetworkBuilder(application)
                        .setRuntimeOrder(NeuralNetwork.Runtime.CPU)
                        .setModel(new File(application.getExternalFilesDir(null) + "/models/test_2.dlc"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(rT==1) {
            Log.d("TZU","SNPE-GPU");
            try {
                builder = new SNPE.NeuralNetworkBuilder(application)
                        .setRuntimeOrder(NeuralNetwork.Runtime.GPU_FLOAT16)
                        .setModel(new File(application.getExternalFilesDir(null) + "/models/test_2.dlc"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        final NeuralNetwork network = builder.build();

        mNeuralNetwork = network;
        Set<String> inputNames = mNeuralNetwork.getInputTensorsNames();
        Set<String> outputNames = mNeuralNetwork.getOutputTensorsNames();
        mInputLayer = inputNames.iterator().next();
        mOutputLayer = outputNames.iterator().next();
    }

    public void runModel(float[] csvList){
        final List<String> result = new ArrayList<String>();

        final FloatTensor tensor = mNeuralNetwork.createFloatTensor(
                mNeuralNetwork.getInputTensorsShapes().get(mInputLayer));

        tensor.write(csvList, 0, csvList.length);

        final Map<String, FloatTensor> inputs = new HashMap<>();
        inputs.put(mInputLayer, tensor);


        long start = System.currentTimeMillis();
        final Map<String, FloatTensor> outputs = mNeuralNetwork.execute(inputs);
        for (Map.Entry<String, FloatTensor> output : outputs.entrySet()) {
            if (output.getKey().equals(mOutputLayer)) {
                FloatTensor outputTensor = output.getValue();//model output at outputTensor
                //Log.d("output", Arrays.toString(outputTensor.getShape()));
                final float[] array = new float[outputTensor.getSize()];
                outputTensor.read(array, 0, array.length);//Tensor to Array
                //Log.d("output",Arrays.toString(array));

                idx = argmax(array);
                w = array[idx];
                Log.d("result",idx+" , "+array[idx]);
            }
        }

        releaseTensors(inputs, outputs);
        long end = System.currentTimeMillis();
        time = (float) ((end-start)/1000.);
    }


    private final void releaseTensors(Map<String, ? extends Tensor>... tensorMaps) {
        for (Map<String, ? extends Tensor> tensorMap: tensorMaps) {
            for (Tensor tensor: tensorMap.values()) {
                tensor.release();
            }
        }
    }

    public List<NeuralNetwork.Runtime> getSupportedRuntimes() {
        final List<NeuralNetwork.Runtime> result = new LinkedList<>();
        final SNPE.NeuralNetworkBuilder builder = new SNPE.NeuralNetworkBuilder(application);
        for (NeuralNetwork.Runtime runtime : NeuralNetwork.Runtime.values()) {
            if (builder.isRuntimeSupported(runtime)) {
                result.add(runtime);
            }
        }
        return result;
    }

}
