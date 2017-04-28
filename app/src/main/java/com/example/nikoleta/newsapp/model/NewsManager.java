package com.example.nikoleta.newsapp.model;

import java.util.ArrayList;
import java.util.List;

public class NewsManager {

    private static NewsManager ourInstance;
    private static List<News> categoryNewsList=new ArrayList<News>();

    public static NewsManager getInstance() {
        if(ourInstance==null){
            ourInstance=new NewsManager();
        }
        return ourInstance;
    }

    public List<News> getCategoryNewsList(){
        return categoryNewsList;
    }

    public void addNewsinCategoryNewsList(News news){
        if(news!=null){
            categoryNewsList.add(news);
        }
    }

    private NewsManager() {
    }
}
