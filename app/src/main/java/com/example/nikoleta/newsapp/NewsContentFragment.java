package com.example.nikoleta.newsapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nikoleta.newsapp.adapters.NewsCardRecyclerViewAdapter;
import com.example.nikoleta.newsapp.model.News;
import com.example.nikoleta.newsapp.model.NewsManager;
import com.example.nikoleta.newsapp.tasks.ExtractOrigLinkAndShareTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewsContentFragment extends Fragment {

    public  interface CommunicatorNewsContentFragment {
        void closeNewsContentFragment();
    }

    private ImageView image;
    private TextView title;
    private TextView text;
    private TextView date;
    private TextView original;
    private RecyclerView recyclerView;
    private ImageButton backToMainPageButton;
    private ImageButton like;
    private List<News> list=new ArrayList<>();

    public NewsContentFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        if(getActivity().getSupportFragmentManager().findFragmentByTag("NewsFragment")!= null){
            list = NewsManager.getInstance(getContext()).getSelected();

        }else {
            if(!MainActivity.foundNews.isEmpty()){
                list=MainActivity.foundNews;
            }else {
                list = NewsManager.getInstance(getContext()).getSelected();
            }
        }

        View view = inflater.inflate(R.layout.fragment_news_content, container, false);
        image = (ImageView) view.findViewById(R.id.image_news_content_fragment);
        title = (TextView) view.findViewById(R.id.title_news_content_fragment);
        text = (TextView) view.findViewById(R.id.text_news_content_fragment);
        date = (TextView) view.findViewById(R.id.date_news_content_fragment);
        original = (TextView) view.findViewById(R.id.link_news_content_fragment);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_suggested_news);
        recyclerView.setAdapter(new NewsCardRecyclerViewAdapter(getContext(), NewsManager.getInstance(getContext()).getRelated()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        backToMainPageButton = (ImageButton) view.findViewById(R.id.back_option);
        backToMainPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommunicatorNewsContentFragment communicator=
                        (CommunicatorNewsContentFragment) getActivity();

                communicator.closeNewsContentFragment();
            }
        });

        final News n = list.get(getArguments().getInt("position"));

        like = (ImageButton) view.findViewById(R.id.like_button_news_content_fragment);
        if(DBManager.getInstance(getContext()).isAlreadyAdded(n.getTitle())){
            like.setImageResource(R.mipmap.ic_like_red);
        }
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DBManager.getInstance(getContext()).isAlreadyAdded(n.getTitle())){
                    like.setImageResource(R.mipmap.ic_like_white);
                    NewsManager.getInstance(getContext()).removeNews(n, 2);
                }
                else{
                    like.setImageResource(R.mipmap.ic_like_red);
                    NewsManager.getInstance(getContext()).addNews(n, 2);
                }
            }
        });

        view.findViewById(R.id.share_button_news_content_fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ExtractOrigLinkAndShareTask(getActivity()).execute(new Integer(getArguments().getInt("position")),null,null);
//                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
//                intent.setType("text/plain");
//                getContext().startActivity(Intent.createChooser(intent, "Sharing Option"));
            }
        });

        image.setImageBitmap(n.getBitmapIMG());
        title.setText(n.getTitle());
        text.setText(n.getText());

        date.setText(n.getDate().substring(0,10) + " | ");
        original.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(n.getOriginalArticleURL()));
                startActivity(intent);
            }
        });

        callAsyncTask(n.getTitle());
        return view;
    }

    public class DownloadAndParseTask extends AsyncTask<String,Void,Void> {
        private Context context;
        private StringBuilder jsonText;
        private MainActivity.CustomLayoutManager clm;
        private List<News> taskNewsList;

        public DownloadAndParseTask(Context context)  {
            this.context = context;
            this.jsonText = new StringBuilder("");
            this.taskNewsList = NewsManager.getInstance(getContext()).getRelated();
            clm = MainActivity.getClm();
        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);

                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    jsonText.append(inputLine);
                }
                in.close();

                connection.disconnect();

                JSONObject jsonObject = new JSONObject(jsonText.toString());

                JSONArray posts = jsonObject.getJSONArray("posts");

                ArrayList<String> titles = new ArrayList<String>();

                for(int i = 0; i < posts.length(); i++){
                    JSONObject post = posts.getJSONObject(i);

                    boolean isThisTitleRepeated = false;

                    for(int a=0;a<titles.size();a++) {
                        String currentTitle = post.getString("title");
                        if(currentTitle.isEmpty()||currentTitle == null){
                            continue;
                        }
                        String[] titleByWords = titles.get(a).split(" ");
                        String[] currentTitleByWords = post.getString("title").split(" ");

                        if(!titles.get(a).isEmpty()&&titleByWords[0].equals(currentTitleByWords[0]) && titleByWords[1].equals(currentTitleByWords[1])
                                &&titleByWords[2].equals(currentTitleByWords[2])){
                            isThisTitleRepeated = true;
                        }

                        if (titles.get(a).contains(post.getString("title")) && !titles.get(a).isEmpty()) {
                            isThisTitleRepeated = true;
                        }
                    }
                    if(isThisTitleRepeated){
                        continue;
                    }

                    String title = post.getString("title");
                    String desc = post.getString("text");
                    String urlImage = post.getJSONObject("thread").getString("main_image");
                    String author = post.getJSONObject("thread").getString("site");
                    String date = post.getJSONObject("thread").getString("published");
                    String original = post.getJSONObject("thread").getString("url");
                    titles.add(title);

                    News news = new News(title,author,desc,urlImage,date,original);
                    NewsManager.getInstance(getContext()).addNews(news, 3);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            DownloadImageTask downloadImageTask = new DownloadImageTask();
            downloadImageTask.execute();

        }

        private class DownloadImageTask extends AsyncTask<Void, Void, Void> {

            protected Void doInBackground(Void... urls) {
                String url = "";
                InputStream in=null;

                for(int i = 0; i < 5; i++){
                    Bitmap bitmap = null;

                    if(i >= taskNewsList.size()){
                        return null;
                    }
                    url = taskNewsList.get(i).getImageURL();
                    try {
                        in = new java.net.URL(url).openStream();
                        bitmap = BitmapFactory.decodeStream(in);
                    } catch (Exception e) {
                        Log.e("Error", ""+i);
                        e.printStackTrace();
                    }
                    if(bitmap!=null) {
                        taskNewsList.get(i).setBitmapIMG(Bitmap.createScaledBitmap(bitmap, 360, 360, true));
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

            protected void onPostExecute(Void avoid) {
                NewsCardRecyclerViewAdapter adapter = new NewsCardRecyclerViewAdapter(getContext(), taskNewsList);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            }
        }

    }


    public void callAsyncTask(String title){
        if (!NewsManager.getInstance(getContext()).getSelected().isEmpty()) {
            NewsManager.getInstance(getContext()).update(3);
            recyclerView.getAdapter().notifyDataSetChanged();
        }
        String[] firstTwoWords = title.split("\\s+"); // get first two words from the title
        String first = firstTwoWords[0];
        String second = firstTwoWords[1];
        new DownloadAndParseTask(getContext()).execute("http://webhose.io/search?token=48ea2974-f86c-4b77-a968-1c9d64845502&" +
                "format=json&q=" + first + "%20" + second + "%20language%3A(english)%20site%3Acnn.com");
    }


}
