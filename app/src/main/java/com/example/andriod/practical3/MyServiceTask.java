package com.example.andriod.practical3;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import static android.content.Context.SENSOR_SERVICE;

public class MyServiceTask implements Runnable, SensorEventListener {


    private SensorManager sensorMan;
    private Sensor accelerometer;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;


    private long first_accel_time = -1;
    private final Object lock = new Object();


    private MyService service;
    private boolean running;
    private boolean moved;
    private long startTime;



    MyServiceTask(MyService service) {
        this.service = service;
    }



    @Override
    public void run() {

        running = true;
        moved = false;
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        startTime = System.currentTimeMillis();
        sensorMan = (SensorManager) service.getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMan.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);


        mAccel = 0.0f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;



    }


    void stop(){
        running = false;
        sensorMan.unregisterListener(this);
    }

    boolean didItMove() {
        synchronized (lock) {
            if (first_accel_time != -1 && (first_accel_time - startTime) > 30 && !moved) {
                return true;
            }
        }
       return false;
    }




    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float x = event.values.clone()[0];
            float y = event.values.clone()[1];


            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.pow((x*x + y*y),(0.5));
            float difference = mAccelCurrent - mAccelLast;

            if(difference > 0.1){
                first_accel_time = System.currentTimeMillis();
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }



    public interface ResultCallback {
        void onResultReady(ServiceResult result);
    }


}
