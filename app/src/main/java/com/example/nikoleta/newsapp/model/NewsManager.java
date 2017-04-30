package com.example.nikoleta.newsapp.model;

import android.content.Context;

import com.example.nikoleta.newsapp.DBManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewsManager {

    private Context context;
    private static NewsManager ourInstance = null;
    private static List<News> selected;
    private static List<News> liked;
    private static List<News> related;

    private NewsManager(Context context) {
        this.context = context;
        selected = new ArrayList<>();
        liked = new ArrayList<>(DBManager.getInstance(context).getLikedNews().values());
        related = new ArrayList<>();
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

    public void addNews(News n, int code){
        if(!isValid(n)){
            return;
        }
        switch (code){
            case 1:
                selected.add(n);
                break;
            case 2:
                liked.add(n);
                DBManager.getInstance(context).addNews(n);
                break;
            case 3:
                related.add(n);
                break;
            default: break;
        }
    }
    public void addAllNews(List<News> list){
        // add all given news to 'selected' news list
        for(News n : list){
            addNews(n, 1);
        }
    }
    public void removeNews(News n, int code){
        if(!isValid(n)){
            return;
        }
        switch (code){
            case 1:
                if(existsInSelected(n)){
                    selected.remove(n);
                }
                break;
            case 2:
                if(existsInLiked(n)){
                    liked.remove(n);
                    DBManager.getInstance(context).removeNews(n);
                }
                break;
            case 3:
                if(existsInRelated(n)){
                    related.remove(n);
                }
                break;
            default: break;
        }
    }
    public void update(int code){
        switch (code){
            case 1: selected.clear(); break;
            case 2: liked.clear(); break;
            case 3: related.clear(); break;
            default: break;
        }
    }

    public boolean isValid(News n){
        return (n != null);
    }
    public boolean existsInSelected(News n){ return (selected.contains(n));}
    public boolean existsInLiked(News n) {return liked.contains(n);}
    public boolean existsInRelated(News n) {return related.contains(n);}

}
