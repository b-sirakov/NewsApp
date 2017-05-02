package com.example.nikoleta.newsapp.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.nikoleta.newsapp.model.News;
import com.example.nikoleta.newsapp.model.NewsManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class ExtractOrigLinkAndShareTask extends AsyncTask<Integer,Void,Void> {

    private Context context;
    private int index;
    private List<News> list=new ArrayList<>();
    public ExtractOrigLinkAndShareTask(Context context) {
        this.context=context;
        if(!NewsManager.getInstance(context).getFound().isEmpty()){
            list=NewsManager.getInstance(context).getFound();
        }else{
            list=NewsManager.getInstance(context).getSelected();
        }
    }

    @Override
    protected Void doInBackground(Integer... position) {
        try {

            index=position[0];
            Log.d("DABE",+index+"->>"+list.get(index).getOriginalArticleURL());
            if( !(list.get(index).getOriginalArticleURL().contains("omgili.com")) ){
                return null;
            }
            URLConnection connection =
                    (new URL(list.get(index).getOriginalArticleURL()).openConnection());
//                connection.setConnectTimeout(5000);
//                connection.setReadTimeout(5000);
            connection.connect();

            // Read and store the result line by line then return the entire string.
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder html = new StringBuilder();
            for (String line; (line = reader.readLine()) != null; ) {
                html.append(line);
            }
            if(in!=null) {
                Log.d("DABE","in");
                in.close();
            }

            String originalURL ="";
            String[] strs=html.toString().split(" ");
            for(int a=0;a<strs.length;a++){
                if(strs[a].startsWith("url=http://")){
                    originalURL=strs[a].substring(4,strs[a].length()-1);
                    break;
                }
            }


            //Log.d("DABE",+index+"->>"+originalURL);

            list.get(index).setOriginalArticleURL(originalURL);


        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("ttt","da1" );

        } catch (IOException e) {
            Log.d("ttt","da2");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d("DABE", list.get(index).getOriginalArticleURL());
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT,  list.get(index).getOriginalArticleURL());
        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }
}

