package com.example.nikoleta.newsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nikoleta.newsapp.model.News;

public class NewsContentFragment extends Fragment {

    public interface CommunicatorNewsContentFragment {
        void closeNewsContentFragment();
    }

    ImageView image;
    TextView title;
    TextView text;
    TextView author;
    TextView date;
    TextView original;
    RecyclerView recyclerView;
    ImageButton backToMainPageButton;

    public NewsContentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        ((AppCompatActivity) getActivity()).findViewById(R.id.tabs).setVisibility(View.GONE);

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
                CommunicatorNewsContentFragment comunicator=
                        (CommunicatorNewsContentFragment) getActivity();

                comunicator.closeNewsContentFragment();
            }
        });

        view.findViewById(R.id.like_button_news_content_fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                News n = MainActivity.newsList.get(getArguments().getInt("position"));
                DBManager.getInstance(getContext()).addNews(n);
            }
        });

        view.findViewById(R.id.share_button_news_content_fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                getContext().startActivity(Intent.createChooser(intent, "Sharing Option"));
            }
        });

        final News n = MainActivity.newsList.get(getArguments().getInt("position"));
        //Toast.makeText(getContext(), n.getTitle() + "" , Toast.LENGTH_SHORT).show();
        image.setImageBitmap(n.getBitmapIMG());
        title.setText(n.getTitle());
        text.setText(n.getText());
        //author.setText(n.getAuthor());

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
