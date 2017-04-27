package com.example.nikoleta.newsapp.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.nikoleta.newsapp.MainActivity;
import com.example.nikoleta.newsapp.NewsListFragment;
import com.example.nikoleta.newsapp.R;
import com.example.nikoleta.newsapp.adapters.NewsRecyclerViewAdapter;
import com.example.nikoleta.newsapp.model.News;
import com.example.nikoleta.newsapp.model.NewsManager;

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
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class DownloadAndParseTask extends AsyncTask<String,Void,Void> {

    private StringBuilder jsonText;
    private MainActivity activity;
    private ProgressBar progBar;
    private RecyclerView recyclerView;
    private static MainActivity.CustomLayoutManager clm;
    private List<News> taskNewsList;
    private Fragment frag;



    public DownloadAndParseTask(MainActivity activity)  {
        this.activity=activity;
        jsonText= new StringBuilder("");
//        try {
//            Thread.currentThread().sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        if(activity.getSupportFragmentManager().findFragmentByTag("NewsFragment")!=null){
            frag=(NewsListFragment) activity.getSupportFragmentManager().findFragmentByTag("NewsFragment");
            this.taskNewsList= NewsManager.getInstance().getCategoryNewsList();
            progBar= (ProgressBar) frag.getView().findViewById(R.id.progress_bar_frag_newslist);
            recyclerView= (RecyclerView) frag.getView().findViewById(R.id.recycler_view_frag_newslist);
            clm=((NewsListFragment)frag).getCustomLayoutManagerFrag();
            return;
        }
            this.taskNewsList=MainActivity.newsList;
            progBar=activity.getProgBar();
            recyclerView=activity.getRecyclerView();
        clm=MainActivity.getClm();

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

            connection.disconnect();

            JSONObject jsonObject=new JSONObject(jsonText.toString());

            JSONArray posts=jsonObject.getJSONArray("posts");

            ArrayList<String> titles=new ArrayList<String>();

            Log.d("dabeda", posts.length()+"");

            for(int i=0;i<posts.length();i++){
                JSONObject post=  posts.getJSONObject(i);

                boolean isThisTitleRepeated=false;

                for(int a=0;a<titles.size();a++) {
                    if (titles.get(a).contains(post.getString("title")) && !titles.get(a).isEmpty()) {
                        isThisTitleRepeated=true;
                    }
                }
                if(isThisTitleRepeated){
                    continue;
                }

                String title=post.getString("title");
                String desc=post.getString("text");
                String urlImage=post.getJSONObject("thread").getString("main_image");
                String author=post.getJSONObject("thread").getString("site");
                String date = post.getJSONObject("thread").getString("published");
                String original = post.getJSONObject("thread").getString("url");
                titles.add(title);

                Log.d("opa",i+"");
                Log.d("opa", "TITLE: "+title);
                Log.d("opa", author);
                //Log.d("opa", "DESC: "+desc.substring(0,50));
                Log.d("opa", "imgURL: "+urlImage);
                Log.d("opa", "URL: "+original);

                News news=new News(title,author,desc,urlImage,date,original);
                taskNewsList.add(news);
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

                if(i>=taskNewsList.size()){
                    return null;
                }
                url=taskNewsList.get(i).getImageURL();
                try {
                    in = new java.net.URL(url).openStream();
                    bitmap = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", ""+i);
                    e.printStackTrace();
                }
                if(bitmap!=null) {
                    taskNewsList.get(i).setBitmapIMG(Bitmap.createScaledBitmap(bitmap, 360, 360, true));
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
            if(activity.getSupportFragmentManager().findFragmentByTag("NewsFragment")!=null) {
                Log.d("VLIZAME", "VLIZAAAAA IFa na taska za fragmenta");
            }
            NewsRecyclerViewAdapter adapter = new NewsRecyclerViewAdapter(activity, taskNewsList);
            progBar.setVisibility(View.GONE);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(clm);

        }
    }

}
