package com.paavansoni.newsgateway;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NewsSourceAsync extends AsyncTask<String, Integer, String> {
    private static final String TAG = "SourceAsync";

    private static final String KEY = "b484f8c03c634d56b2422d190a795585";
    private static final String dataURLstart = "https://newsapi.org/v2/sources?language=en&country=us&category=";
    private static final String dataURLend = "&apiKey=";

    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;

    NewsSourceAsync(MainActivity ma){
        this.mainActivity = ma;
    }

    @Override
    protected void onPostExecute(String s) {
        if(s == null){

        }
        else{
            ArrayList<Source> sources = parseJSON(s);
            mainActivity.setSources(sources);
        }
    }

    private ArrayList<Source> parseJSON(String s) {
        ArrayList<Source> sources = new ArrayList<>();
        try{
            JSONObject jObjMain = new JSONObject(s);

            JSONArray sourceArray = jObjMain.getJSONArray("sources");
            for(int i=0;i<sourceArray.length();i++){
                JSONObject source = sourceArray.getJSONObject(i);

                String id = source.getString("id");
                String name = source.getString("name");
                String category = source.getString("category");

                sources.add(new Source(id, name, category));
            }
        }
        catch(Exception e){
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return sources;
    }

    @Override
    protected String doInBackground(String... strings) {
        String madeUrl = dataURLstart + dataURLend + KEY;

        Uri dataUri = Uri.parse(madeUrl);
        String urlToUse = dataUri.toString();

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return sb.toString();
    }
}