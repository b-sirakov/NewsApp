package com.example.nikoleta.newsapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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
import com.example.nikoleta.newsapp.model.News;
import com.example.nikoleta.newsapp.tasks.ExtractOrigLinkAndShareTask;

import java.util.ArrayList;
import java.util.List;

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewAdapter.NewsViewHolder>{

    private int counter;
    private Context context;
    private List<News> news = new ArrayList<News>();
    private List<Integer> expandedPositions =new ArrayList<Integer>();

    public NewsRecyclerViewAdapter(Context context, List news){
        counter=0;
        this.context = context;
        this.news = news;
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder{
        private View row;
        private ImageView image;
        private TextView title;
        private TextView author;
        private TextView text;
        private ImageButton like;
        private ImageButton share;
        private Button expandButton;


        public NewsViewHolder(View row) {
            super(row);
            this.row = row;
            this.image = (ImageView) row.findViewById(R.id.news_image);
            this.title = (TextView) row.findViewById(R.id.news_title);
            this.author = (TextView) row.findViewById(R.id.news_author);
            this.text = (TextView) row.findViewById(R.id.news_text);
            this.like = (ImageButton) row.findViewById(R.id.like_button);
            this.share = (ImageButton) row.findViewById(R.id.share_button);
            this.expandButton = (Button) row.findViewById(R.id.expand_button);
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
        holder.title.setText(news.getTitle());
        readMore(holder.title, position);
        holder.author.setText(news.getAuthor());
        holder.text.setText( (news.getText().length()>200? news.getText().substring(0,200): news.getText())+"..." );

        boolean isExpanded=false;
        if(expandedPositions.contains(new Integer(position))){
            isExpanded=true;
        }
        if(isExpanded){
            holder.text.setVisibility(View.VISIBLE);
        }else{
            holder.text.setVisibility(View.GONE);
        }

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
        readMore(holder.image, position);

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = MainActivity.newsList.get(position).getTitle();
                String author =MainActivity.newsList.get(position).getAuthor();
                String text = MainActivity.newsList.get(position).getText();
                String data=MainActivity.newsList.get(position).getDate();
                String link=MainActivity.newsList.get(position).getOriginalArticleURL();
                Bitmap image = MainActivity.newsList.get(position).getBitmapIMG();
                News current = new News(title, author, text,image,data,link);
                DBManager.getInstance(context).addNews(current);
            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ExtractOrigLinkAndShareTask(context).execute(new Integer(position),null,null);
            }
        });

        holder.expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( holder.text.getVisibility()==View.GONE){
                    holder.text.setVisibility(View.VISIBLE);
                    holder.expandButton.animate().rotation(180);
                    expandedPositions.add(position);
                }else{
                    holder.expandButton.animate().rotation(360);
                    holder.text.setVisibility(View.GONE);
                    expandedPositions.remove(new Integer(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return news.size();
    }
    private void readMore(View view, final int position){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context instanceof MainActivity) {
                    ((MainActivity) context).getRecyclerView().setVisibility(View.GONE);
                }
                NewsContentFragment fragment = new NewsContentFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                fragment.setArguments(bundle);

                FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
                manager.beginTransaction()
                        .add(R.id.layout_main_activity, fragment, "ContentFragment")
                        .commit();
            }
        });
    }
}
