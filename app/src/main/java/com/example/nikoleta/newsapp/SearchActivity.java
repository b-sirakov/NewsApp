package com.example.nikoleta.newsapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.example.nikoleta.newsapp.adapters.NewsRecyclerViewAdapter;
import com.example.nikoleta.newsapp.model.News;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressBar progBar;
    private  MaterialSearchView searchView;
    private RecyclerView recyclerView;
    public static List<News> foundNews;
    private MainActivity.CustomLayoutManager clm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        progBar= (ProgressBar) findViewById(R.id.prog_bar_search_activity);
        this.foundNews=new ArrayList<>();
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search");

       searchView= (MaterialSearchView) findViewById(R.id.search_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item,menu);
        MenuItem item = menu.findItem(R.id.action_search);

        recyclerView= (RecyclerView) findViewById(R.id.recycle_view_search_activity);
        NewsRecyclerViewAdapter adapter = new NewsRecyclerViewAdapter(this, this.foundNews);
        recyclerView.setAdapter(adapter);
        clm=new MainActivity.CustomLayoutManager(this);
        recyclerView.setLayoutManager(clm);

        searchView.setMenuItem(item);
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
            }
            @Override
            public void onSearchViewClosed() {
                SearchActivity.this.foundNews.clear();
                //If closed Search View , recycle view will be empty
                NewsRecyclerViewAdapter adapter = new NewsRecyclerViewAdapter(SearchActivity.this, foundNews);
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

                    SearchActivity.this.foundNews.clear();

                    for(News news:MainActivity.newsList){
                        if(news.getTitle().toLowerCase().contains(newText.toLowerCase())){
                            foundNews.add(news);
                        }
                    }
                    NewsRecyclerViewAdapter adapter = new NewsRecyclerViewAdapter(SearchActivity.this, SearchActivity.this.foundNews);
                    recyclerView.setAdapter(adapter);
                }
                else{
                    foundNews.clear();
                    NewsRecyclerViewAdapter adapter = new NewsRecyclerViewAdapter(SearchActivity.this, SearchActivity.this.foundNews);
                    recyclerView.setAdapter(adapter);
                }
                return true;
            }

        });
        return true;
    }

    public MainActivity.CustomLayoutManager getClm() {
        return clm;
    }

    public ProgressBar getProgBar() {
        return progBar;
    }
}
