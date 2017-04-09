package com.example.nikoleta.newsapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nikoleta.newsapp.MainActivity;
import com.example.nikoleta.newsapp.R;
import com.example.nikoleta.newsapp.model.News;

import java.util.ArrayList;
import java.util.List;

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewAdapter.NewsViewHolder>{

    private int counter;
    private Context context;
    private List<News> news = new ArrayList<News>();

    public NewsRecyclerViewAdapter(Context context, List news){
        counter=0;
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
        if(position>0&&position%5==0){
            if(counter<position) {
                counter=position;
                MainActivity ma = (MainActivity) context;
                MainActivity.DownloadSmallAmountOfImages downloadTask = ma.new DownloadSmallAmountOfImages();
                downloadTask.execute(counter);
            }

        }
        holder.title.setText(news.getTitle());
        holder.author.setText(news.getAuthor());
        holder.text.setText(news.getText());
        if(news.getBitmapIMG()==null){
            if(news.getAuthor().equals("cnn.com")) {
                holder.image.setImageResource(R.drawable.cnn);
            }
            if(news.getAuthor().equals("bbc.co.uk")){
                holder.image.setImageResource(R.drawable.bbc);
            }
        }else {
            holder.image.setImageBitmap(news.getBitmapIMG());
        }
    }


    @Override
    public int getItemCount() {
        return news.size();
    }

}
