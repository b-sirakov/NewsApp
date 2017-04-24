package com.example.nikoleta.newsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.nikoleta.newsapp.model.News;

import java.util.HashMap;

public class DBManager extends SQLiteOpenHelper{

    private static Context context;
    public static HashMap<String, News> likedNews;
    private static final String SQL_CREATE_TABLE_LIKED = "CREATE TABLE liked(\n" +
            "\n" +
            " title text NOT NULL,\n" +
            " author text NOT NULL,\n" +
            " text text NOT NULL,\n" +
            " image BLOB\n" +
            ");";

    private static DBManager ourInstance;
    public static DBManager getInstance(Context context) {
        if(ourInstance == null){
            ourInstance = new DBManager(context);
            DBManager.context = context;
            ourInstance.create();
            likedNews = new HashMap<String, News>();
            loadNews();
        }
        return ourInstance;
    }

    private DBManager(Context context) {
        super(context, "liked", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_LIKED);
        Toast.makeText(context, "Database created!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO
    }

    protected SQLiteDatabase create(){
        return getWritableDatabase();
    }
    private static void loadNews() {
        Cursor cursor = ourInstance.getWritableDatabase().rawQuery("SELECT title, author, text, image FROM liked", null);
        while (cursor.moveToNext()){
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String author = cursor.getString(cursor.getColumnIndex("author"));
            String text = cursor.getString(cursor.getColumnIndex("text"));

            News current = new News(title, author, text);
            likedNews.put(title, current);
        }
    }
    public void addNews(News news){
        if(isAlreadyAdded(news.getTitle())){
            Toast.makeText(context, "Already added", Toast.LENGTH_SHORT).show();
            return;
        }
        ContentValues content = new ContentValues();
        content.put("title", news.getTitle());
        content.put("author", news.getAuthor());
        content.put("text", news.getText());
        getWritableDatabase().insert("liked", null, content);
        likedNews.put(news.getTitle(), news);
        Toast.makeText(context, "Liked", Toast.LENGTH_SHORT).show();
    }
    protected boolean isAlreadyAdded(String title){
        return likedNews.containsKey(title);
    }
    public  void removeNews(News news){
        // TODO no button added in news_row.xml
        if(!likedNews.containsKey(news.getTitle())){
            return;
        }
        getWritableDatabase().delete("liked", "title = ?", new String[]{news.getTitle()});
        likedNews.remove(news.getTitle());
        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
    }
}
