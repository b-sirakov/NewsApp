package com.example.nikoleta.newsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.nikoleta.newsapp.adapters.NewsRecyclerViewAdapter;
import com.example.nikoleta.newsapp.model.News;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBManager extends SQLiteOpenHelper{

    private static Context context;
    private static HashMap<String, News> likedNews;
    private static final String SQL_CREATE_TABLE_LIKED = "CREATE TABLE liked(\n" +
            "\n" +
            " id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            " title text NOT NULL,\n" +
            " author text NOT NULL,\n" +
            " text text NOT NULL,\n" +
            " date text NOT NULL,\n" +
            " link text NOT NULL,\n" +
            " image BLOB\n" +
            ");";

    private static DBManager ourInstance;
    public static DBManager getInstance(Context context) {
        if(ourInstance == null){
            ourInstance = new DBManager(context);
            DBManager.context = context;
            ourInstance.create();
            likedNews = new HashMap<>();
            loadNews();
        }
        return ourInstance;
    }

    private DBManager(Context context) {
        super(context, "liked", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_LIKED);
        Toast.makeText(context, "Database created!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion == 1 && newVersion == 2){
            db.execSQL("DROP TABLE liked");
            onCreate(db);
        }
    }

    protected SQLiteDatabase create(){
        return getWritableDatabase();
    }
    private static void loadNews() {
        Cursor cursor = ourInstance.getWritableDatabase().rawQuery("SELECT title, author, text,date,link,image FROM liked", null);
        while (cursor.moveToNext()){
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String author = cursor.getString(cursor.getColumnIndex("author"));
            String text = cursor.getString(cursor.getColumnIndex("text"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String link = cursor.getString(cursor.getColumnIndex("link"));

            News current = new News(title, author, text,null,date,link);
            likedNews.put(title, current);
        }
    }
    public void addNews(News news){
        if(isAlreadyAdded(news.getTitle())){
            removeNews(news);
            Toast.makeText(context, "Unliked", Toast.LENGTH_SHORT).show();
            return;
        }
        ContentValues content = new ContentValues();
        content.put("title", news.getTitle());
        content.put("author", news.getAuthor());
        content.put("text", news.getText());
        content.put("date",news.getDate());
        content.put("link",news.getOriginalArticleURL());
        long id = getWritableDatabase().insert("liked", null, content);
        news.setId((int) id);
        likedNews.put(news.getTitle(), news);
        Toast.makeText(context, "Like", Toast.LENGTH_SHORT).show();

    }
    public boolean isAlreadyAdded(String title){
        return getLikedNews().containsKey(title);
    }
    public  void removeNews(News news){
        // TODO no button added in news_row.xml
        if(!likedNews.containsKey(news.getTitle())){
            return;
        }
        getWritableDatabase().delete("liked", "title = ?", new String[]{news.getTitle()});
        likedNews.remove(news.getTitle());

    }
    public Map<String, News> getLikedNews(){
        return Collections.unmodifiableMap(likedNews);
    }
}
