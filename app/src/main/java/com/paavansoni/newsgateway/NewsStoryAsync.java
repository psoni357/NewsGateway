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

public class NewsStoryAsync extends AsyncTask<String, Integer, String> {
    private static final String TAG = "ArticleAsync";

    private static final String KEY = "b484f8c03c634d56b2422d190a795585";
    private static final String dataURLstart = "https://newsapi.org/v2/everything?sources=";
    private static final String dataURLend = "&language=en&pageSize=100&apiKey=";

    @SuppressLint("StaticFieldLeak")
    private NewsStoryService service;

    NewsStoryAsync(NewsStoryService serv){
        this.service = serv;
    }

    @Override
    protected void onPostExecute(String s) {
        if(s == null){

        }
        else{
            ArrayList<Article> sources = parseJSON(s);
            service.setArticles(sources);
        }
    }

    private ArrayList<Article> parseJSON(String s) {
        ArrayList<Article> articles = new ArrayList<>();
        try{
            JSONObject jObjMain = new JSONObject(s);

            JSONArray articleArray = jObjMain.getJSONArray("articles");
            for(int i=0; i<articleArray.length(); i++){
                JSONObject article = articleArray.getJSONObject(i);

                String author;
                String title;
                String description;
                String url;
                String imageUrl;
                String publishedAt;

                if(article.has("author")){
                    author = article.getString("author");
                }
                else{
                    author = "null";
                }
                if(article.has("title")){
                    title = article.getString("title");
                }
                else{
                    title = "null";
                }
                if(article.has("description")){
                    description = article.getString("description");
                }
                else{
                    description = "null";
                }
                if(article.has("url")){
                    url = article.getString("url");
                }
                else{
                    url = "null";
                }
                if(article.has("urlToImage")){
                    imageUrl = article.getString("urlImage");
                }
                else{
                    imageUrl = "null";
                }
                if(article.has("publishedAt")){
                    publishedAt = article.getString("publishedAt");
                }
                else{
                    publishedAt = "null";
                }

                Article a = new Article (author, title, description, url, imageUrl, publishedAt);
                articles.add(a);
            }

        }
        catch(Exception e){
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return articles;
    }

    @Override
    protected String doInBackground(String... strings) {
        String source = strings[0];
        String madeUrl = dataURLstart + source + dataURLend + KEY;

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
