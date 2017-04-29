package com.example.nikoleta.newsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nikoleta.newsapp.model.News;
import com.example.nikoleta.newsapp.model.NewsManager;
import com.example.nikoleta.newsapp.tasks.ExtractOrigLinkAndShareTask;

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
        if(getActivity()==null){
            Log.d("OMG","VLIZA");
        }
        if(getActivity().getSupportFragmentManager().findFragmentByTag("NewsFragment")!=null){
            list= NewsManager.getInstance(getContext()).getSelected();

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
        backToMainPageButton = (ImageButton) view.findViewById(R.id.back_option);

        backToMainPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommunicatorNewsContentFragment communicator=
                        (CommunicatorNewsContentFragment) getActivity();

                communicator.closeNewsContentFragment();
            }
        });

        like = (ImageButton) view.findViewById(R.id.like_button_news_content_fragment);
        final News n = list.get(getArguments().getInt("position"));
        if(NewsManager.getInstance(getContext()).existsInLiked(n)){
            like.setImageResource(R.mipmap.ic_like_red);
        }
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DBManager.getInstance(getContext()).getLikedNews().containsKey(n.getTitle())){
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

//        final ArrayList<News> related = new ArrayList<>();
//        recyclerView.setAdapter(new NewsRecyclerViewAdapter(getContext(), MainActivity.newsList));
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        return view;
    }


}
