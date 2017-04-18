package com.example.nikoleta.newsapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.nikoleta.newsapp.adapters.NewsRecyclerViewAdapter;
import com.example.nikoleta.newsapp.model.News;
import com.example.nikoleta.newsapp.tasks.DownloadAndParseTask;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private static CustomLayoutManager clm;
    private ProgressBar progBar;
    private ProgressBar progBar2;
    private static StringBuilder jsonText;
    public static ArrayList<News> newsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView= (RecyclerView) findViewById(R.id.recycler_view_main_activity);
        progBar= (ProgressBar) findViewById(R.id.progress_bar);
        progBar2= (ProgressBar) findViewById(R.id.progress_bar2);
        clm=new CustomLayoutManager(this);

        newsList = new ArrayList<>();
        jsonText=new StringBuilder("");

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setBackgroundResource(R.color.dark_red);
                Intent intent =new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
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
            new DownloadAndParseTask(this)
                    .execute("http://webhose.io/search?token=685aabb3-30d0-4e41-a950-af95718a07cb&" +
                            "format=json&q=language%3A(english)%20site%3Acnn.com%20performance_score%3A%3E2%20(site_type%3Anews)");
        } else if (id == R.id.bbc_news) {
            if (!newsList.isEmpty()) {
                newsList.clear();
                recyclerView.getAdapter().notifyDataSetChanged();
            }
            Toast.makeText(this, "BBC News", Toast.LENGTH_SHORT).show();
            new DownloadAndParseTask(this)
                    .execute("http://webhose.io/search?token=685aabb3-30d0-4e41-a950-af95718a07cb&" +
                            "format=json&q=language%3A(english)%20site%3Abbc.co.uk%20performance_score%3A%3E2%20(site_type%3Anews)");
        }else if(id == R.id.fox_news){
            if (!newsList.isEmpty()) {
                newsList.clear();
                recyclerView.getAdapter().notifyDataSetChanged();
            }
            Toast.makeText(this, "FOX News", Toast.LENGTH_SHORT).show();
            new DownloadAndParseTask(this)
                    .execute("http://webhose.io/search?token=48ea2974-f86c-4b77-a968-1c9d64845502&" +
                            "format=json&q=language%3A(english)%20site%3Afoxnews.com");
        } else if (id == R.id.business_category) {
            // TODO
        } else if (id == R.id.entertainment_category) {

        } else if (id == R.id.health_category) {

        } else if (id == R.id.politics_category) {

        } else if (id == R.id.sports_category) {

        }else if (id == R.id.technology_category) {

        }else if (id == R.id.liked_news){
            Toast.makeText(this, "Liked News", Toast.LENGTH_SHORT).show();
            if (!newsList.isEmpty()) {
                newsList.clear();
                recyclerView.getAdapter().notifyDataSetChanged();
            }
            List<News> liked = new ArrayList<News>(DBManager.getInstance(this).likedNews.values());
            recyclerView.setAdapter(new NewsRecyclerViewAdapter(this, liked));
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class DownloadSmallAmountOfImages extends AsyncTask<Integer,Void,Void>{

        private Context context;
        private List<News> listNews;
        private ProgressBar progBar;
        private MainActivity.CustomLayoutManager clm;

        public DownloadSmallAmountOfImages(Context context) {
            this.context = context;
            if(context instanceof MainActivity){
                progBar=MainActivity.this.getProgBar2();
                listNews=MainActivity.newsList;
                clm=MainActivity.getClm();
            }
            if(context instanceof SearchActivity){
                listNews=SearchActivity.foundNews;
                progBar=((SearchActivity)context).getProgBar();
                clm=((SearchActivity)context).getClm();
            }
        }

        @Override
        protected void onPreExecute() {

                clm.setScrollEnabled(false);
                progBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(Integer... params) {
            int position=params[0];
            String url = "";
            InputStream in=null;

            for(int i=position+1;i<=position+5;i++){
                Bitmap bitmap = null;

                if(i>=this.listNews.size()){
                    return null;
                }
                url=this.listNews.get(i).getImage();
                try {
                    in = new java.net.URL(url).openStream();
                    bitmap = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", ""+i);
                    e.printStackTrace();
                }
                if(bitmap!=null) {
                    this.listNews.get(i).setBitmapIMG(Bitmap.createScaledBitmap(bitmap, 180, 180, true));
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
                progBar.setVisibility(View.GONE);
                clm.setScrollEnabled(true);
        }
    }

    public static class CustomLayoutManager extends LinearLayoutManager {
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

        @Override
        public boolean canScrollHorizontally() {
            return super.canScrollHorizontally();
        }
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public static CustomLayoutManager getClm() {
        return clm;
    }

    public ProgressBar getProgBar() {
        return progBar;
    }

    public static StringBuilder getJsonText() {
        return jsonText;
    }

    public ProgressBar getProgBar2() {
        return progBar2;
    }

}
