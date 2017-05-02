package com.example.nikoleta.newsapp.model;

import android.content.Context;
import android.os.AsyncTask;

import com.example.nikoleta.newsapp.DBManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewsManager {

    public static final int SELECTED_NEWS = 1;
    public static final int LIKED_NEWS = 2;
    public static final int RELATED_NEWS = 3;
    public static final int FOUND_NEWS = 4;
    private Context context;
    private static NewsManager ourInstance = null;
    private static List<News> selected;
    private static List<News> liked;
    private static List<News> related;
    private static List<News> found;
    private static List<AsyncTask> tasks;

    public List<AsyncTask> getTasks() {
        return tasks;
    }

    private NewsManager(Context context) {
        this.context = context;
        selected = new ArrayList<>();
        liked = new ArrayList<>(DBManager.getInstance(context).getLikedNews().values());
        related = new ArrayList<>();
        found = new ArrayList<>();
        tasks=new ArrayList<>();
    }

    public static NewsManager getInstance(Context context) {
        if(ourInstance == null){
            ourInstance = new NewsManager(context);
        }
        return ourInstance;
    }

    public List<News> getSelected(){
        return Collections.unmodifiableList(selected);
    }
    public List<News> getLiked() {
        return Collections.unmodifiableList(liked);
    }
    public List<News> getRelated() {
        return Collections.unmodifiableList(related);
    }
    public List<News> getFound() { return Collections.unmodifiableList(found); }

    public void addNews(News n, int code){
        if(!isValid(n)){
            return;
        }
        switch (code){
            case SELECTED_NEWS: selected.add(n); break;
            case LIKED_NEWS:
                liked.add(n);
                DBManager.getInstance(context).addNews(n);
                break;
            case RELATED_NEWS: related.add(n); break;
            case FOUND_NEWS: found.add(n); break;
            default: break;
        }
    }
    public void addAllNews(List<News> list){
        // add all given news to 'selected' news list
        for(News n : list){
            addNews(n, SELECTED_NEWS);
        }
    }
    public void removeNews(News n, int code){
        if(!isValid(n)){
            return;
        }
        switch (code){
            case SELECTED_NEWS:
                if(existsInSelected(n)){
                    selected.remove(n);
                }
                break;
            case LIKED_NEWS:
                if(existsInLiked(n)){
                    liked.remove(n);
                    DBManager.getInstance(context).removeNews(n);
                }
                break;
            case RELATED_NEWS:
                if(existsInRelated(n)){
                    related.remove(n);
                }
                break;
            default: break;
        }
    }
    public void update(int code){
        switch (code){
            case SELECTED_NEWS: selected.clear(); break;
            case LIKED_NEWS: liked.clear(); break;
            case RELATED_NEWS: related.clear(); break;
            case FOUND_NEWS: found.clear(); break;
            default: break;
        }
    }

    public boolean isValid(News n){
        return (n != null);
    }
    public boolean existsInSelected(News n){ return (selected.contains(n)); }
    public boolean existsInLiked(News n) { return liked.contains(n); }
    public boolean existsInRelated(News n) { return related.contains(n); }
    public boolean existsInFound(News n) { return found.contains(n);}
}
