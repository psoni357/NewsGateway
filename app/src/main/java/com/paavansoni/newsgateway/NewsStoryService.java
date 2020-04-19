package com.paavansoni.newsgateway;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class NewsStoryService extends Service {

    private static final String TAG = "NewsService";
    private boolean running = true;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Creating new thread for my service
        //ALWAYS write your long running tasks in a separate thread, to avoid ANR

        new Thread(new Runnable() {
            @Override
            public void run() {

                int counter = 1;
                while (running) {

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sendBroadcast("Count Service Broadcast Message " + counter);
                    counter++;
                }

                sendBroadcast("Service Thread Stopped");


                Log.d(TAG, "run: Ending loop");
            }
        }).start();


        return Service.START_NOT_STICKY;
    }

    private void sendBroadcast(String msg) {
        Intent intent = new Intent();
        sendBroadcast(intent);
    }


    @Override
    public void onDestroy() {

        running = false;
        super.onDestroy();
    }
}
