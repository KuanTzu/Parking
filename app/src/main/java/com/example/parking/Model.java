package com.example.parking;

public class Model {

    public float w,time;
    public int idx;

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
    public int argmax(float[] array) {
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
}
