package com.example.nikoleta.newsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.nikoleta.newsapp.adapters.NewsRecyclerViewAdapter;
import com.example.nikoleta.newsapp.model.News;
import com.example.nikoleta.newsapp.string_holders.StringHolder;
import com.example.nikoleta.newsapp.tasks.DownloadAndParseTask;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NewsContentFragment.CommunicatorNewsContentFragment {

    private RecyclerView recyclerView;
    private static CustomLayoutManager clm;
    private ProgressBar progBar;
    private ProgressBar progBar2;
    private static StringBuilder jsonText;
    public static  ArrayList<News> newsList;
    public static List<News> foundNews=new ArrayList<>();
    private StringHolder stringHolder;
    private MaterialSearchView searchView;
    private TextView titleSection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchView= (MaterialSearchView) findViewById(R.id.search_view_main);
        recyclerView= (RecyclerView) findViewById(R.id.recycler_view_main_activity);
        progBar= (ProgressBar) findViewById(R.id.progress_bar);
        progBar2= (ProgressBar) findViewById(R.id.progress_bar2);
        clm=new CustomLayoutManager(this);
        stringHolder=new StringHolder();
        newsList = new ArrayList<>();
        NewsRecyclerViewAdapter adapter = new NewsRecyclerViewAdapter(this, newsList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(clm);
        getSupportActionBar().setTitle("NewsApp");
        jsonText=new StringBuilder("");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setCategoriesURLforBBC();
        setCategoriesURLforCNN();
        stringHolder.setSection("BBC");
        ((TextView)findViewById(R.id.section_title_tv)).setText(stringHolder.getSection());
        callAsyncTask("http://webhose.io/search?token=e615070d-99c6-4d8f-a83b-b26dc590cd8b&" +
                "format=json&q=language%3A(english)%20site%3Abbc.co.uk%20performance_score%3A%3E2%20(site_type%3Anews)");
    }

    @Override
    public void onBackPressed() {

        FragmentManager fm=getSupportFragmentManager();
        if((fm.findFragmentByTag("ContentFragment"))!=null){
            closeNewsContentFragment();
            return;
        }
        if(fm.findFragmentByTag("NewsFragment")!=null){
            fm.findFragmentByTag("NewsFragment").getView().setVisibility(View.VISIBLE);
        }

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
        getMenuInflater().inflate(R.menu.menu_item,menu);
        MenuItem item = menu.findItem(R.id.action_search);

        searchView.setMenuItem(item);

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
            }
            @Override
            public void onSearchViewClosed() {
                foundNews.clear();
                //If closed Search View , recycle view will be empty
                NewsRecyclerViewAdapter adapter = new NewsRecyclerViewAdapter(MainActivity.this, newsList);
                recyclerView.setAdapter(adapter);
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText != null && !newText.isEmpty()){

                    foundNews.clear();

                    for(News news:MainActivity.newsList){
                        if(news.getTitle().toLowerCase().contains(newText.toLowerCase())){
                            foundNews.add(news);
                        }
                    }
                    NewsRecyclerViewAdapter adapter = new NewsRecyclerViewAdapter(MainActivity.this, foundNews);
                    recyclerView.setAdapter(adapter);
                }
                else{
                    foundNews.clear();
                    NewsRecyclerViewAdapter adapter = new NewsRecyclerViewAdapter(MainActivity.this, foundNews);
                    recyclerView.setAdapter(adapter);
                }
                return true;
            }

        });

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

    public void setCategoriesURLforBBC(){
        //set the URLs for each categort for BBC
        stringHolder.setBusinessURL(
                "http://webhose.io/search?token=e615070d-99c6-4d8f-a83b-b26dc590cd8b&" +
                        "format=json&q=economy%20language%3A(english)%20site%3Abbc.co.uk%20(site_type%3Anews)"
        );
        stringHolder.setHealthURL(
                "http://webhose.io/search?token=e615070d-99c6-4d8f-a83b-b26dc590cd8b&" +
                        "format=json&q=health%20language%3A(english)%20site%3Abbc.co.uk%20(site_type%3Anews)"
        );
        stringHolder.setPoliticsURL(
                "http://webhose.io/search?token=e615070d-99c6-4d8f-a83b-b26dc590cd8b&" +
                        "format=json&q=politics%20language%3A(english)%20site%3Abbc.co.uk%20(site_type%3Anews)"
        );
        stringHolder.setSportsURL(
                "http://webhose.io/search?token=e615070d-99c6-4d8f-a83b-b26dc590cd8b&" +
                        "format=json&q=sport%20language%3A(english)%20site%3Abbc.co.uk%20(site_type%3Anews)"
        );
        stringHolder.setTechnologiesURL(
                "http://webhose.io/search?token=e615070d-99c6-4d8f-a83b-b26dc590cd8b&" +
                        "format=json&q=technology%20language%3A(english)%20site%3Abbc.co.uk%20(site_type%3Anews)"
        );
    }
    public void setCategoriesURLforCNN(){
        //set the URLs for each categort for CNN
        stringHolder.setBusinessURL(
                        "http://webhose.io/search?token=e615070d-99c6-4d8f-a83b-b26dc590cd8b&" +
                        "format=json&q=business%20site%3Acnn.com"
        );
        stringHolder.setHealthURL(
                "http://webhose.io/search?token=e615070d-99c6-4d8f-a83b-b26dc590cd8b&" +
                        "format=json&q=health%20site%3Acnn.com%20(site_type%3Anews)"
        );
        stringHolder.setPoliticsURL(
                                           "http://webhose.io/search?token=e615070d-99c6-4d8f-a83b-b26dc590cd8b&" +
                        "format=json&q=politics%20site%3Acnn.com%20(site_type%3Anews)");
        stringHolder.setSportsURL(
                                            "http://webhose.io/search?token=e615070d-99c6-4d8f-a83b-b26dc590cd8b&" +
                        "format=json&q=sport%20site%3Acnn.com%20(site_type%3Anews)"
        );
        stringHolder.setTechnologiesURL(
                                           "http://webhose.io/search?token=e615070d-99c6-4d8f-a83b-b26dc590cd8b&" +
                        "format=json&q=technology%20site%3Acnn.com%20(site_type%3Anews)"
        );
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        titleSection= (TextView) findViewById(R.id.section_title_tv);

        switch (id){
            case R.id.cnn_news:
                setCategoriesURLforCNN();
                fillData("CNN News", "CNN", "http://webhose.io/search?token=e615070d-99c6-4d8f-a83b-b26dc590cd8b&" +
                        "format=json&q=language%3A(english)%20site%3Acnn.com%20performance_score%3A%3E2%20(site_type%3Anews)");
                break;
            case R.id.bbc_news:
                setCategoriesURLforBBC();
                fillData("BBC News", "BBC", "http://webhose.io/search?token=e615070d-99c6-4d8f-a83b-b26dc590cd8b&" +
                        "format=json&q=language%3A(english)%20site%3Abbc.co.uk%20performance_score%3A%3E2%20(site_type%3Anews)");
                break;
            case R.id.business_category:
                fillData("Business category selected", "Business", stringHolder.getBusinessURL());
                break;
            case R.id.health_category:
                fillData("Health category selected", "Health",stringHolder.getHealthURL() );
                break;
            case R.id.politics_category:
                fillData("Politics category selected", "Politics", stringHolder.getPoliticsURL());
                break;
            case R.id.sports_category:
                fillData("Sports category selected", "Sports", stringHolder.getSportsURL());
                break;
            case R.id.technology_category:
                fillData("Technology category selected", "Technology", stringHolder.getTechnologiesURL());
                break;
            case R.id.liked_news:
                fillLikedNewsData();
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void fillData(String toast, String category, String url){
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
        stringHolder.setSection(category);
        titleSection.setText(stringHolder.getSection());
        callAsyncTask(url);
    }
    public void checkRecyclerViewCondition(){
        // used in callAsyncTask method
        if (!newsList.isEmpty()) {
            newsList.clear();
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }
    public void callAsyncTask(String url){
        checkRecyclerViewCondition();
        new DownloadAndParseTask(this).execute(url);
    }
    public void fillLikedNewsData(){
        Toast.makeText(this, "Liked News", Toast.LENGTH_SHORT).show();
        stringHolder.setSection("Liked news");
        titleSection.setText(stringHolder.getSection());
        checkRecyclerViewCondition();
        List<News> liked = new ArrayList<News>(DBManager.getInstance(this).getLikedNews().values());
        newsList.addAll(liked);
        recyclerView.setAdapter(new NewsRecyclerViewAdapter(this, newsList));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void closeNewsContentFragment() {
        FragmentManager fm=getSupportFragmentManager();
        android.support.v4.app.Fragment frag = fm.findFragmentByTag("ContentFragment");
        getSupportFragmentManager().beginTransaction().remove(frag).commit();

        if(fm.findFragmentByTag("NewsFragment")!=null){
            fm.findFragmentByTag("NewsFragment").getView().setVisibility(View.VISIBLE);
        }else {
            this.getSupportActionBar().show();
            recyclerView.setVisibility(View.VISIBLE);
        }

    }

    public class DownloadSmallAmountOfImages extends AsyncTask<Integer,Void,Void>{

        private Context context;
        private List<News> listNews;
        private ProgressBar progBar;
        private MainActivity.CustomLayoutManager clm;

        public DownloadSmallAmountOfImages(Context context,List<News> listNews) {
            this.context = context;
            progBar=MainActivity.this.getProgBar2();
            this.listNews=listNews;
            clm=MainActivity.getClm();
        }

        @Override
        protected void onPreExecute() {
                clm.setScrollEnabled(false);
                progBar.setVisibility(View.VISIBLE);
            recyclerView.stopScroll();

        }

        @Override
        protected Void doInBackground(Integer... params) {
            int position=params[0];
            String url = "";
            InputStream in=null;

            for(int i=position;i<=position+5;i++){
                Bitmap bitmap = null;

                if(i>=this.listNews.size()){
                    return null;
                }
                Log.d("Count","Kartinki-"+i);
                url=this.listNews.get(i).getImageURL();
                try {
                    in = new java.net.URL(url).openStream();
                    bitmap = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", ""+i);
                    e.printStackTrace();
                }
                if(bitmap!=null) {
                    this.listNews.get(i).setBitmapIMG(Bitmap.createScaledBitmap(bitmap, 360, 360, true));
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
