package com.example.nikoleta.newsapp;


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


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsContentFragment extends Fragment {

    public interface ComunicatorNewsContentFragment{
        void closeNewsContentFragment();
    }

    ImageView image;
    TextView title;
    TextView text;
    TextView author;
    RecyclerView recyclerView;
    ImageButton backToMainPageButton;

    public NewsContentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        View view = inflater.inflate(R.layout.fragment_news_content, container, false);
        image = (ImageView) view.findViewById(R.id.image_news_content_fragment);
        title = (TextView) view.findViewById(R.id.title_news_content_fragment);
        text = (TextView) view.findViewById(R.id.text_news_content_fragment);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_suggested_news);
        backToMainPageButton = (ImageButton) view.findViewById(R.id.back_option);

        backToMainPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComunicatorNewsContentFragment comunicator=
                        (ComunicatorNewsContentFragment) getActivity();

                comunicator.closeNewsContentFragment();
            }
        });


        News n = (News) getArguments().getSerializable("content");
        //Toast.makeText(getContext(), n.getTitle() + "" , Toast.LENGTH_SHORT).show();
        image.setImageBitmap(n.getBitmapIMG());
        title.setText(n.getTitle());
        text.setText(n.getText());
        // author.setText(n.getAuthor());

//        final ArrayList<News> related = new ArrayList<>();
//
//        recyclerView.setAdapter(new NewsRecyclerViewAdapter(getContext(), MainActivity.newsList));
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        return view;
    }

}
