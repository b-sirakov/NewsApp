package com.example.nikoleta.newsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
    public static ArrayList<News> newsList;
    public static List<News> foundNews=new ArrayList<>();
    private StringHolder stringHolder;
    private MaterialSearchView searchView;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        searchView= (MaterialSearchView) findViewById(R.id.search_view_main);
        recyclerView= (RecyclerView) findViewById(R.id.recycler_view_main_activity);
        progBar= (ProgressBar) findViewById(R.id.progress_bar);
        progBar2= (ProgressBar) findViewById(R.id.progress_bar2);
        clm=new CustomLayoutManager(this);
        stringHolder=new StringHolder();

        getSupportActionBar().setTitle("NewsApp");

        newsList = new ArrayList<>();
        jsonText=new StringBuilder("");

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

        FragmentManager fm=getSupportFragmentManager();
        if((fm.findFragmentByTag("ContentFragment"))!=null){
            closeNewsContentFragment();
            return;
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
                NewsRecyclerViewAdapter adapter = new NewsRecyclerViewAdapter(MainActivity.this, foundNews);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.cnn_news) {
            Toast.makeText(this, "CNN News", Toast.LENGTH_SHORT).show();

            //set the URLs for each categort for CNN
            stringHolder.setBusinessURL(
                    "http://webhose.io/search?token=685aabb3-30d0-4e41-a950-af95718a07cb&" +
                            "format=json&q=business%20site%3Acnn.com"
            );
            stringHolder.setHealthURL(
                    "http://webhose.io/search?token=685aabb3-30d0-4e41-a950-af95718a07cb&" +
                            "format=json&q=health%20site%3Acnn.com%20(site_type%3Anews)"
            );
            stringHolder.setPoliticsURL(
                    "http://webhose.io/search?token=685aabb3-30d0-4e41-a950-af95718a07cb&" +
                            "format=json&q=politics%20site%3Acnn.com%20(site_type%3Anews)"
            );
            stringHolder.setSportsURL(
                    "http://webhose.io/search?token=685aabb3-30d0-4e41-a950-af95718a07cb&" +
                            "format=json&q=sport%20site%3Acnn.com%20(site_type%3Anews)"
            );
            stringHolder.setTechnologiesURL(
                    "http://webhose.io/search?token=685aabb3-30d0-4e41-a950-af95718a07cb&" +
                            "format=json&q=technology%20site%3Acnn.com%20(site_type%3Anews)"
            );
            callAsyncTask("http://webhose.io/search?token=685aabb3-30d0-4e41-a950-af95718a07cb&" +
                            "format=json&q=language%3A(english)%20site%3Acnn.com%20performance_score%3A%3E2%20(site_type%3Anews)");
        } else if (id == R.id.bbc_news) {
            Toast.makeText(this, "BBC News", Toast.LENGTH_SHORT).show();

            //set the URLs for each categort for BBC
            stringHolder.setBusinessURL(
                    "http://webhose.io/search?token=685aabb3-30d0-4e41-a950-af95718a07cb&" +
                            "format=json&q=economy%20language%3A(english)%20site%3Abbc.co.uk%20(site_type%3Anews)"
            );
            stringHolder.setHealthURL(
                    "http://webhose.io/search?token=685aabb3-30d0-4e41-a950-af95718a07cb&" +
                            "format=json&q=health%20language%3A(english)%20site%3Abbc.co.uk%20(site_type%3Anews)"
            );
            stringHolder.setPoliticsURL(
                    "http://webhose.io/search?token=685aabb3-30d0-4e41-a950-af95718a07cb&" +
                            "format=json&q=politics%20language%3A(english)%20site%3Abbc.co.uk%20(site_type%3Anews)"
            );
            stringHolder.setSportsURL(
                    "http://webhose.io/search?token=685aabb3-30d0-4e41-a950-af95718a07cb&" +
                            "format=json&q=sport%20language%3A(english)%20site%3Abbc.co.uk%20(site_type%3Anews)"
            );
            stringHolder.setTechnologiesURL(
                    "http://webhose.io/search?token=685aabb3-30d0-4e41-a950-af95718a07cb&" +
                            "format=json&q=technology%20language%3A(english)%20site%3Abbc.co.uk%20(site_type%3Anews)"
            );

            callAsyncTask("http://webhose.io/search?token=685aabb3-30d0-4e41-a950-af95718a07cb&" +
                            "format=json&q=language%3A(english)%20site%3Abbc.co.uk%20performance_score%3A%3E2%20(site_type%3Anews)");

        }else if(id == R.id.fox_news){
            Toast.makeText(this, "FOX News", Toast.LENGTH_SHORT).show();

            //set the URLs for each categort for FOX News
            stringHolder.setBusinessURL(
                    "http://webhose.io/search?token=685aabb3-30d0-4e41-a950-af95718a07cb&" +
                            "format=json&q=business%20language%3A(english)%20site%3Afoxnews.com%20(site_type%3Anews)"
            );
            stringHolder.setHealthURL(
                    "http://webhose.io/search?token=685aabb3-30d0-4e41-a950-af95718a07cb&" +
                            "format=json&q=health%20language%3A(english)%20site%3Afoxnews.com%20(site_type%3Anews)"
            );
            stringHolder.setPoliticsURL(
                    "http://webhose.io/search?token=685aabb3-30d0-4e41-a950-af95718a07cb&" +
                            "format=json&q=politics%20language%3A(english)%20site%3Afoxnews.com%20(site_type%3Anews)"
            );
            stringHolder.setSportsURL(
                    "http://webhose.io/search?token=685aabb3-30d0-4e41-a950-af95718a07cb&" +
                            "format=json&q=sports%20language%3A(english)%20site%3Afoxnews.com%20(site_type%3Anews)"
            );
            stringHolder.setTechnologiesURL(
                    "http://webhose.io/search?token=685aabb3-30d0-4e41-a950-af95718a07cb&" +
                            "format=json&q=technology%20language%3A(english)%20site%3Afoxnews.com%20(site_type%3Anews)"
            );


            callAsyncTask("http://webhose.io/search?token=685aabb3-30d0-4e41-a950-af95718a07cb&" +
                    "format=json&q=site%3Afoxnews.com");

        } else if (id == R.id.business_category) {
            Toast.makeText(this, "Business category selected", Toast.LENGTH_SHORT).show();
            callAsyncTask(stringHolder.getBusinessURL());

        } else if (id == R.id.entertainment_category) {
            Toast.makeText(this, "Entertainment category selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.health_category) {
            Toast.makeText(this, "Health category selected", Toast.LENGTH_SHORT).show();
            callAsyncTask(stringHolder.getHealthURL());

        } else if (id == R.id.politics_category) {
            Toast.makeText(this, "Politics category selected", Toast.LENGTH_SHORT).show();
            callAsyncTask(stringHolder.getPoliticsURL());

        } else if (id == R.id.sports_category) {
            Toast.makeText(this, "Sports category selected", Toast.LENGTH_SHORT).show();
            callAsyncTask(stringHolder.getSportsURL());

        }else if (id == R.id.technology_category) {
            Toast.makeText(this, "Technology category selected", Toast.LENGTH_SHORT).show();
            callAsyncTask(stringHolder.getTechnologiesURL());

        }else if (id == R.id.liked_news){
            Toast.makeText(this, "Liked News", Toast.LENGTH_SHORT).show();
            if (!newsList.isEmpty()) {
                newsList.clear();
                recyclerView.getAdapter().notifyDataSetChanged();
            }
            List<News> liked = new ArrayList<News>(DBManager.getInstance(this).getLikedNews().values());
            newsList.addAll(liked);
            recyclerView.setAdapter(new NewsRecyclerViewAdapter(this, newsList));
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void callAsyncTask(String url){
        if (!newsList.isEmpty()) {
            newsList.clear();
            recyclerView.getAdapter().notifyDataSetChanged();
        }
        new DownloadAndParseTask(this).execute(url);
    }

    @Override
    public void closeNewsContentFragment() {

        android.support.v4.app.Fragment frag = getSupportFragmentManager().findFragmentByTag("ContentFragment");
        getSupportFragmentManager().beginTransaction().remove(frag).commit();

        this.getSupportActionBar().show();
        findViewById(R.id.tabs).setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
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

            for(int i=position;i<=position+5;i++){
                Bitmap bitmap = null;

                if(i>=this.listNews.size()){
                    return null;
                }
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

    // tabs functionality
    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tab, container, false);
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    TabFragment fragment = new TabFragment();
                    callAsyncTask("http://webhose.io/search?token=48ea2974-f86c-4b77-a968-1c9d64845502&" +
                            "format=json&q=language%3A(english)%20site%3Abbc.com&sort=social.gplus.shares");
                    notifyDataSetChanged();
                    return fragment;
                case 1:
                    TabFragment fragment2 = new TabFragment();
                    callAsyncTask("http://webhose.io/search?token=48ea2974-f86c-4b77-a968-1c9d64845502&" +
                            "format=json&q=language%3A(english)%20site%3Acnn.com&sort=social.gplus.shares");
                    notifyDataSetChanged();
                    return fragment2;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "BBC Top news";
                case 1:
                    return "CNN Top news";
            }
            return null;
        }
    }
}
