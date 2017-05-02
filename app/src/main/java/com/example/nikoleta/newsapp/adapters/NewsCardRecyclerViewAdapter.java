package com.example.nikoleta.newsapp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nikoleta.newsapp.MainActivity;
import com.example.nikoleta.newsapp.NewsContentFragment;
import com.example.nikoleta.newsapp.R;
import com.example.nikoleta.newsapp.model.News;

import java.util.ArrayList;
import java.util.List;

public class NewsCardRecyclerViewAdapter extends RecyclerView.Adapter<NewsCardRecyclerViewAdapter.CardViewHolder> {

    private int counter;
    private Context context;
    private List<News> list = new ArrayList<>();

    public NewsCardRecyclerViewAdapter(Context context, List news){
        counter = 0;
        this.context = context;
        this.list = news;
    }

    public class CardViewHolder extends RecyclerView.ViewHolder{
        private View row;
        private ImageView image;
        private TextView title;

        public CardViewHolder(View row) {
            super(row);
            this.row = row;
            this.image = (ImageView) row.findViewById(R.id.image_news_card);
            this.title = (TextView) row.findViewById(R.id.title_news_card);
        }
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.news_card, parent, false);
        NewsCardRecyclerViewAdapter.CardViewHolder holder = new NewsCardRecyclerViewAdapter.CardViewHolder(row);
        return holder;
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        final News current = this.list.get(position);
        if(context instanceof MainActivity) {
            if (position >= 0 && position % 5 == 0) {
                if (counter <= position) {
                    MainActivity ma = (MainActivity) context;
                    MainActivity.getClm().setScrollEnabled(false);
                    MainActivity.DownloadSmallAmountOfImages downloadTask = ma.new DownloadSmallAmountOfImages(context, list);
                    downloadTask.execute(counter);
                    counter += 5;
                }
            }
        }

        holder.title.setText(current.getTitle());
        if(current.getBitmapIMG() == null){
            if(current.getAuthor().equals("cnn.com")) {
                holder.image.setImageResource(R.drawable.cnn);
            }
            if(current.getAuthor().equals("bbc.co.uk")){
                holder.image.setImageResource(R.drawable.bbc);
            }
        }else {
            holder.image.setImageBitmap(current.getBitmapIMG());
        }
        readMore(holder.title, position);
        readMore(holder.image, position);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void readMore(View view, final int position){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewsContentFragment fragment = new NewsContentFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putInt("code", 3);
                bundle.putString("author", list.get(position).getAuthor());
                fragment.setArguments(bundle);

                FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.layout_main_activity, fragment, "ContentFragment")
                        .commit();
            }
        });
    }

}
