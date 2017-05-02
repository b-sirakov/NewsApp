package com.example.nikoleta.newsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.example.nikoleta.newsapp.model.News;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.HashMap;
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
        Cursor cursor = ourInstance.getWritableDatabase().rawQuery("SELECT id, title, author, text,date,link,image FROM liked", null);
        while (cursor.moveToNext()){
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String author = cursor.getString(cursor.getColumnIndex("author"));
            String text = cursor.getString(cursor.getColumnIndex("text"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String link = cursor.getString(cursor.getColumnIndex("link"));

            // get image
            byte[] blob = cursor.getBlob(cursor.getColumnIndex("image"));
            Bitmap image = null; // convert the byte array to Bitmap
            if(blob != null) {
                image = BitmapFactory.decodeByteArray(blob, 0, blob.length);
            }

            News current = new News(title, author, text, image, date, link);
            likedNews.put(title, current);
        }
    }
    public void addNews(News news){
        if(isAlreadyAdded(news.getTitle())){
            removeNews(news);
            return;
        }
        ContentValues content = new ContentValues();
        content.put("title", news.getTitle());
        content.put("author", news.getAuthor());
        content.put("text", news.getText());
        content.put("date",news.getDate());
        content.put("link",news.getOriginalArticleURL());

        // insert bitmap
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        news.getBitmapIMG().compress(Bitmap.CompressFormat.PNG, 100, out);
        byte[] buffer = out.toByteArray();
        content.put("image", buffer);

        long id = getWritableDatabase().insert("liked", null, content);
        news.setId((int) id);
        likedNews.put(news.getTitle(), news);
        Toast.makeText(context, "Like", Toast.LENGTH_SHORT).show();

    }
    public boolean isAlreadyAdded(String title){
        return getLikedNews().containsKey(title);
    }
    public  void removeNews(News news){
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
