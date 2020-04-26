package com.paavansoni.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

public class NewsStoryService extends Service {

    private static final String TAG = "NewsService";
    private boolean running = true;

    private ServiceSampleReceiver serviceSampleReceiver;

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
                serviceSampleReceiver = new ServiceSampleReceiver();

                IntentFilter filter = new IntentFilter(MainActivity.ACTION_MSG_TO_SERVICE);
                registerReceiver(serviceSampleReceiver, filter);
                while(running){

                }

            }
        }).start();


        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(serviceSampleReceiver);
        running = false;
        super.onDestroy();
    }

    private void getArticles(String id) {
        new NewsStoryAsync(this).execute(id);
    }

    public void setArticles(ArrayList<Article> articles) {
        Intent intent = new Intent();
        intent.setAction(MainActivity.ACTION_NEWS_STORY);
        Bundle args = new Bundle();
        args.putSerializable("ARTICLES", articles);
        intent.putExtra("BUNDLE", args);
        sendBroadcast(intent);
    }

    class ServiceSampleReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action == null)
                return;
            switch (action) {
                case MainActivity.ACTION_MSG_TO_SERVICE:
                    Source s = (Source)intent.getSerializableExtra("SOURCE");
                    getArticles(s.getId());
                    break;
                default:
                    Log.d(TAG, "onReceive: Unknown broadcast received");
            }

        }
    }
}
