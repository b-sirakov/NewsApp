package com.example.nikoleta.newsapp.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.nikoleta.newsapp.model.NewsManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;



public class ExtractOrigLinkAndShareTask extends AsyncTask<Integer,Void,Void> {

    private Context context;
    private int index;
    public ExtractOrigLinkAndShareTask(Context context) {
        this.context=context;
    }

    @Override
    protected Void doInBackground(Integer... position) {
        try {

            index=position[0];
                URLConnection connection =
                        (new URL(NewsManager.getInstance(context).getSelected().get(index).getOriginalArticleURL()).openConnection());
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
                Log.d("DABE",position+"->>"+originalURL);

                NewsManager.getInstance(context).getSelected().get(index).setOriginalArticleURL(originalURL);


        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("ttt","da1" );
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("ttt","da2");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d("DABE", NewsManager.getInstance(context).getSelected().get(index).getOriginalArticleURL());
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT,  NewsManager.getInstance(context).getSelected().get(index).getOriginalArticleURL());
        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }
}
