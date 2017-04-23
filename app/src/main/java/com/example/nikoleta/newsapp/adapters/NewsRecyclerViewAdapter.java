package com.example.nikoleta.newsapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nikoleta.newsapp.DBManager;
import com.example.nikoleta.newsapp.MainActivity;
import com.example.nikoleta.newsapp.NewsContentFragment;
import com.example.nikoleta.newsapp.R;
import com.example.nikoleta.newsapp.SearchActivity;
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
        Button readMoreButton;
        ImageButton like;
        ImageButton share;
        Button readMore;


        public NewsViewHolder(View row) {
            super(row);
            this.row = row;
            this.image = (ImageView) row.findViewById(R.id.news_image);
            this.title = (TextView) row.findViewById(R.id.news_title);
            this.author = (TextView) row.findViewById(R.id.news_author);
            this.text = (TextView) row.findViewById(R.id.news_text);
            this.readMoreButton= (Button) row.findViewById(R.id.read_more_button);
            this.like = (ImageButton) row.findViewById(R.id.like_button);
            this.share = (ImageButton) row.findViewById(R.id.share_button);
            this.readMore = (Button) row.findViewById(R.id.read_more_button);
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
    public void onBindViewHolder(final NewsRecyclerViewAdapter.NewsViewHolder holder, final int position) {
        News news = this.news.get(position);
        if(context instanceof MainActivity) {
            if (position > 0 && position % 5 == 0) {
                if (counter < position) {
                    counter = position;
                    MainActivity ma = (MainActivity) context;
                    MainActivity.DownloadSmallAmountOfImages downloadTask = ma.new DownloadSmallAmountOfImages(context);
                    downloadTask.execute(counter);
                }
            }
        }
        if(context instanceof SearchActivity) {
            if (position % 5 == 0) {
                if (counter < position) {
                    counter = position;
                    MainActivity ma=new MainActivity();
                    MainActivity.DownloadSmallAmountOfImages downloadTask = ma.new DownloadSmallAmountOfImages(context);
                    downloadTask.execute(counter);
                }
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

        // setting like, share and read more actions
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = holder.title.getText().toString();
                String author = holder.author.getText().toString();
                String text = holder.text.getText().toString();
                News current = new News(title, author, text);
                DBManager.getInstance(context).addNews(current);
            }
        });
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                context.startActivity(Intent.createChooser(intent, "Shearing Option"));
            }
        });

        holder.readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context instanceof MainActivity) {
                    ((MainActivity) context).getRecyclerView().setVisibility(View.GONE);
                }
                NewsContentFragment fragment = new NewsContentFragment();
                News chosen = MainActivity.newsList.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("content", chosen);
                fragment.setArguments(bundle);

                android.support.v4.app.FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
                manager.beginTransaction()
                        .add(R.id.layout_main_activity, fragment, "content fragment")
                        .commit();
            }
        });
    }


    @Override
    public int getItemCount() {
        return news.size();
    }

}
