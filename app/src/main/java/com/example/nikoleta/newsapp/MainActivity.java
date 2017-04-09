package com.example.nikoleta.newsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.nikoleta.newsapp.adapters.NewsRecyclerViewAdapter;
import com.example.nikoleta.newsapp.model.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private static CustomLayoutManager clm;
    private ProgressBar progBar;
    private StringBuilder jsonText;
    private ArrayList<News> newsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progBar= (ProgressBar) findViewById(R.id.progress_bar);
        newsList = new ArrayList<>();
        jsonText=new StringBuilder("");



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_feedback){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.cnn_news) {
            Toast.makeText(this, "CNN News", Toast.LENGTH_SHORT).show();

            if(!newsList.isEmpty()) {
                newsList.clear();
                recyclerView.getAdapter().notifyDataSetChanged();
            }
            AsynqBBC asynq= (AsynqBBC) new AsynqBBC()
                    .execute("http://webhose.io/search?token=685aabb3-30d0-4e41-a950-af95718a07cb&" +
                            "format=json&q=language%3A(english)%20site%3Acnn.com%20performance_score%3A%3E2%20(site_type%3Anews)");
        } else if (id == R.id.bbc_news) {
            if(!newsList.isEmpty()) {
                newsList.clear();
                recyclerView.getAdapter().notifyDataSetChanged();
            }
            Toast.makeText(this, "BBC News", Toast.LENGTH_SHORT).show();
            AsynqBBC asynq= (AsynqBBC) new AsynqBBC()
                    .execute("http://webhose.io/search?token=685aabb3-30d0-4e41-a950-af95718a07cb&" +
                            "format=json&q=language%3A(english)%20site%3Abbc.co.uk%20performance_score%3A%3E2%20(site_type%3Anews)");

        } else if (id == R.id.business_category) {

        } else if (id == R.id.entertainment_category) {

        } else if (id == R.id.health_category) {

        } else if (id == R.id.politics_category) {

        } else if (id == R.id.sports_category) {

        }else if (id == R.id.technology_category) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class AsynqBBC extends AsyncTask<String,Void,Void> {

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
                    newsList.add(news);
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
    }
    
    private class DownloadImageTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... urls) {
            String url = "";
            InputStream in=null;

            for(int i=0;i<5;i++){
                Bitmap bitmap = null;

                url=newsList.get(i).getImage();
                try {
                    in = new java.net.URL(url).openStream();
                    bitmap = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", ""+i);
                    e.printStackTrace();
                }
                if(bitmap!=null) {
                    newsList.get(i).setBitmapIMG(Bitmap.createScaledBitmap(bitmap, 180, 180, true));
                }
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void avoid) {

            progBar.setVisibility(View.GONE);
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view_main_activity);
            NewsRecyclerViewAdapter adapter = new NewsRecyclerViewAdapter(MainActivity.this, newsList);
            recyclerView.setAdapter(adapter);
            clm= new CustomLayoutManager(MainActivity.this);
            recyclerView.setLayoutManager(clm);

        }
    }

    public class DownloadSmallAmountOfImages extends AsyncTask<Integer,Void,Void>{

        @Override
        protected void onPreExecute() {
           clm.setScrollEnabled(false);
           // progBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Integer... params) {
            int position=params[0];
            String url = "";
            InputStream in=null;

            if(position>=newsList.size()){
                return null;
            }
            for(int i=position+1;i<=position+5;i++){
                Bitmap bitmap = null;

                url=newsList.get(i).getImage();
                try {
                    in = new java.net.URL(url).openStream();
                    bitmap = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", ""+i);
                    e.printStackTrace();
                }
                if(bitmap!=null) {
                    newsList.get(i).setBitmapIMG(Bitmap.createScaledBitmap(bitmap, 180, 180, true));
                }
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //progBar.setVisibility(View.GONE);
            //recyclerView.getAdapter().notifyDataSetChanged();
            clm.setScrollEnabled(true);
        }
    }

    public class CustomLayoutManager extends LinearLayoutManager {
        private boolean isScrollEnabled = true;

        public CustomLayoutManager(Context context) {
            super(context);
        }

        public void setScrollEnabled(boolean flag) {
            this.isScrollEnabled = flag;
        }

        @Override
        public boolean canScrollVertically() {
            //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
            return isScrollEnabled && super.canScrollVertically();
        }
    }

    public static CustomLayoutManager getClm() {
        return clm;
    }
}
