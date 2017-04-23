package com.example.nikoleta.newsapp.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.nikoleta.newsapp.MainActivity;
import com.example.nikoleta.newsapp.adapters.NewsRecyclerViewAdapter;
import com.example.nikoleta.newsapp.model.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadAndParseTask extends AsyncTask<String,Void,Void> {

    private StringBuilder jsonText;
    private MainActivity activity;
    private ProgressBar progBar;
    private RecyclerView recyclerView;
    private static MainActivity.CustomLayoutManager clm;

    public DownloadAndParseTask(MainActivity activity){
        this.activity=activity;
        progBar=activity.getProgBar();
        recyclerView=activity.getRecyclerView();
        jsonText= new StringBuilder("");
    }

    @Override
    protected void onPreExecute() {
        jsonText=new StringBuilder("");
        progBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(String... params) {

        try {
            URL url= new URL(params[0]);

            Log.d("unik",url.toString());
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                jsonText.append(inputLine);
            }
            in.close();

//                Scanner sc = new Scanner(connection.getInputStream());
//                while(sc.hasNext()){
//                    jsonText.append(" "+sc.next());
//                }
            //sc.close();
            connection.disconnect();

            JSONObject jsonObject=new JSONObject(jsonText.toString());

            JSONArray posts=jsonObject.getJSONArray("posts");

            for(int i=0;i<posts.length();i++){
                JSONObject post=  posts.getJSONObject(i);

                String title=post.getString("title");
                String desc=post.getString("text");
                String urlImage=post.getJSONObject("thread").getString("main_image");
                String author=post.getJSONObject("thread").getString("site");

                if(desc.length()>200){
                    desc=desc.substring(0,200);
                }
                Log.d("opa",i+"");
                Log.d("opa", "TITLE: "+title);
                Log.d("opa", author);
                Log.d("opa", "DESC: "+desc);
                Log.d("opa", "imgURL: "+urlImage);

                News news=new News(title,author,desc,urlImage);
                MainActivity.newsList.add(news);
            }

            Log.d("opa", posts.length()+"");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        DownloadImageTask downloadImageTask=new DownloadImageTask();
        downloadImageTask.execute();
    }

    private class DownloadImageTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... urls) {
            String url = "";
            InputStream in=null;

            for(int i=0;i<5;i++){
                Bitmap bitmap = null;

                if(i>=MainActivity.newsList.size()){
                    return null;
                }
                url=MainActivity.newsList.get(i).getImageURL();
                try {
                    in = new java.net.URL(url).openStream();
                    bitmap = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", ""+i);
                    e.printStackTrace();
                }
                if(bitmap!=null) {
                    MainActivity.newsList.get(i).setBitmapIMG(Bitmap.createScaledBitmap(bitmap, 180, 180, true));
                }
            }
            try {
                if(in!=null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void avoid) {

            progBar.setVisibility(View.GONE);
            NewsRecyclerViewAdapter adapter = new NewsRecyclerViewAdapter(activity, MainActivity.newsList);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(MainActivity.getClm());

        }
    }

}
