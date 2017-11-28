package com.example.andriod.practical3;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class MyService extends Service {

    private Thread myThread;
    private MyServiceTask myTask;

    private final IBinder myBinder = new LocalBinder();


    class LocalBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }


    public MyService() {

    }

    @Override
    public void onCreate() {
        myTask = new MyServiceTask(this);
        myThread = new Thread(myTask);
        myThread.start();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!myThread.isAlive()) {
            myThread.start();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        myTask.stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }


    public boolean didItMove(){
        return myTask.didItMove();
    }



}

