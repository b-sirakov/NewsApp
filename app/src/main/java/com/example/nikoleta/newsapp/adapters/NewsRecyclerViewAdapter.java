package com.example.nikoleta.newsapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nikoleta.newsapp.R;
import com.example.nikoleta.newsapp.model.News;

import java.util.ArrayList;
import java.util.List;

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewAdapter.NewsViewHolder>{

    private Context context;
    private List<News> news = new ArrayList<News>();

    public NewsRecyclerViewAdapter(Context context, List news){
        this.context = context;
        this.news = news;
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder{
        View row;
        ImageView image;
        TextView title;
        TextView author;
        TextView text;


        public NewsViewHolder(View row) {
            super(row);
            this.row = row;
            this.image = (ImageView) row.findViewById(R.id.news_image);
            this.title = (TextView) row.findViewById(R.id.news_title);
            this.author = (TextView) row.findViewById(R.id.news_author);
            this.text = (TextView) row.findViewById(R.id.news_text);
        }
    }

    @Override
    public NewsRecyclerViewAdapter.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.news_row, parent, false);
        NewsViewHolder holder = new NewsViewHolder(row);
        return holder;
    }

    @Override
    public void onBindViewHolder(NewsRecyclerViewAdapter.NewsViewHolder holder, int position) {
        News news = this.news.get(position);
        // TODO add an image
        holder.title.setText(news.getTitle());
        holder.author.setText(news.getAuthor());
        holder.text.setText(news.getText());
        holder.image.setImageBitmap(news.getBitmapIMG());
    }


    @Override
    public int getItemCount() {
        return news.size();
    }

}
