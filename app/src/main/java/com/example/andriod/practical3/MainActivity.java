package com.example.andriod.practical3;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager.WakeLock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private boolean serviceBound;
    private MyService myService;
    private WakeLock wakeLock;
    private TextView textView;

    private Handler handler = handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message inputMessage) {
            System.out.println(inputMessage);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serviceBound = false;
        textView = findViewById(R.id.moved);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }

    @Override
    protected void onStart() {
        super.onStart();



//        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
//
//        if (powerManager != null) {
//            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
//                    "MyWakelockTag");
//            wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
//        }

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        wakeLock.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
        bindMyService();
    }

    private void bindMyService() {
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder serviceBinder) {
            MyService.LocalBinder binder = (MyService.LocalBinder) serviceBinder;
            myService = binder.getService();
            serviceBound = true;
            boolean moved = myService.didItMove();
            textView.setText(Boolean.toString(moved));
//            System.out.println(myService.getMoved());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            serviceBound = false;
        }
    };


    @Override
    protected void onPause() {
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
            Intent intent = new Intent(this, MyService.class);
            stopService(intent);
        }
        super.onPause();
    }


}


//    public void handleState(MyServiceTask task, int state) {
//        Message completeMessage = mHandler.obtainMessage(state, task);
//        completeMessage.sendToTarget();
//
//    }
//
//    Handler mHandler = new Handler(Looper.getMainLooper()) {
//        @Override
//        public void handleMessage(Message inputMessage) {
//            // Gets the task from the incoming Message object.
//            MyService photoTask = (MyService) inputMessage.obj;
//            // Gets the ImageView for this task
//            boolean moved = photoTask.didItMove();
//
//        }
//
//    };


//    private class UiCallback implements Handler.Callback {
//        @Override
//        public boolean handleMessage(Message message) {
//            if (message.what == DISPLAY_NUMBER) {
//                // Gets the result.
//                ServiceResult result = (ServiceResult) message.obj;
//                // Displays it.
//                if (result != null) {
//                    TextView tv = findViewById(R.id.moved);
//                    tv.setText(result.toString());
//
//                    if (serviceBound && myService != null) {
//                        myService.releaseResult(result);
//                    }
//                } else {
//                    Log.e("", "Error: received empty message!");
//                }
//            }
//            return true;
//        }
//    }