package com.example.nikoleta.newsapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nikoleta.newsapp.DBManager;
import com.example.nikoleta.newsapp.MainActivity;
import com.example.nikoleta.newsapp.NewsContentFragment;
import com.example.nikoleta.newsapp.R;
import com.example.nikoleta.newsapp.model.News;
import com.example.nikoleta.newsapp.model.NewsManager;
import com.example.nikoleta.newsapp.tasks.ExtractOrigLinkAndShareTask;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewAdapter.NewsViewHolder>{

    public static final int RECYCLER_VIEW_WITH_NO_DELETE_OPTION = 0;
    public static final int RECYCLER_VIEW_WITH_DELETE_OPTION = 1;
    private int code;
    private int counter;
    private Context context;
    private List<News> news = new ArrayList<>();
    private List<Integer> expandedPositions = new ArrayList<>();

    public NewsRecyclerViewAdapter(Context context, List news, int code){
        counter=0;
        this.context = context;
        this.news = news;
        this.code = code;
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
        private TextView dateTv;
        private ImageButton delete;


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
            this.dateTv= (TextView) row.findViewById(R.id.date_tv);
            this.delete = (ImageButton) row.findViewById(R.id.remove_news_button);
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
        final News current = this.news.get(position);
        if(context instanceof MainActivity) {
            if (position >= 0 && position % 5 == 0) {
                if (counter <= position) {
                    MainActivity ma = (MainActivity) context;
                    MainActivity.getClm().setScrollEnabled(false);
                    MainActivity.DownloadSmallAmountOfImages downloadTask = ma.new DownloadSmallAmountOfImages(context, this.news);
                    for(int i=0;i<NewsManager.getInstance(context).getTasks().size();i++){
                        if(NewsManager.getInstance(context).getTasks().get(i).getStatus().equals(AsyncTask.Status.FINISHED)){
                            NewsManager.getInstance(context).getTasks().remove(i);
                            continue;
                        }
                        if(NewsManager.getInstance(context).getTasks().get(i).getStatus().equals(AsyncTask.Status.RUNNING)
                                ||NewsManager.getInstance(context).getTasks().get(i).getStatus().equals(AsyncTask.Status.PENDING)){
                            NewsManager.getInstance(context).getTasks().get(i).cancel(true);
                            NewsManager.getInstance(context).getTasks().remove(i);
                        }
                    }
                    NewsManager.getInstance(context).getTasks().add(downloadTask);
                    downloadTask.execute(counter);
                    counter += 5;
                }
            }
        }

        holder.title.setText(current.getTitle());
        readMore(holder.title, position);
        holder.author.setText(current.getAuthor());
        holder.text.setText( (current.getText().length()>200? current.getText().substring(0,200): current.getText())+"..." );
        holder.dateTv.setText(current.getDate().substring(0,10));

        boolean isExpanded=false;
        if(expandedPositions.contains(new Integer(position))){
            isExpanded=true;
        }
        if(isExpanded){
            holder.text.setVisibility(View.VISIBLE);
        }else{
            holder.text.setVisibility(View.GONE);
        }

        if(current.getBitmapIMG()==null){
            if(current.getAuthor().equals("cnn.com")) {
                holder.image.setImageResource(R.drawable.cnn);
            }
            if(current.getAuthor().equals("bbc.co.uk")){
                holder.image.setImageResource(R.drawable.bbc);
            }
        }else {
            holder.image.setImageBitmap(current.getBitmapIMG());
        }
        readMore(holder.image, position);


        setLikeButtonColor(holder, position);
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DBManager.getInstance(context).getLikedNews().containsKey(news.get(position).getTitle())){
                    if(code == 0){
                        holder.like.setImageResource(R.mipmap.ic_like_purple);
                        NewsManager.getInstance(context).removeNews(news.get(position), NewsManager.LIKED_NEWS);
                    }
                    else {
                        holder.like.setImageResource(R.mipmap.ic_like_purple);
                        deleteItem(position);
                    }
                }
                else{
                    holder.like.setImageResource(R.mipmap.ic_like_red);
                    addItem(position);
                }
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

        if(code == 1) {
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItem(position);
                }
            });
        }else {
            holder.delete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return news.size();
    }
    private void readMore(View view, final int position){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewsContentFragment fragment = new NewsContentFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putInt("code", 1);
                bundle.putString("author", news.get(position).getAuthor());
                fragment.setArguments(bundle);
                View view = ((AppCompatActivity) context).getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)((AppCompatActivity) context).getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
//                MaterialSearchView sv= (MaterialSearchView) ((AppCompatActivity) context).findViewById(R.id.search_view_main);
//                Log.d("OPEN",sv.isSearchOpen()+"");
                ((AppCompatActivity) context).findViewById(R.id.invisible_button).setVisibility(View.GONE);
                FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
                manager.beginTransaction()
                        .add(R.id.layout_main_activity, fragment, "ContentFragment")
                        .commit();

            }
        });
    }
    private void setLikeButtonColor(NewsViewHolder holder, int position){
        if(!DBManager.getInstance(context).getLikedNews().containsKey(news.get(position).getTitle())){
            holder.like.setImageResource(R.mipmap.ic_like_purple);
        }
        else{
            holder.like.setImageResource(R.mipmap.ic_like_red);
        }
    }
    private void deleteItem(int position){
        NewsManager.getInstance(context).removeNews(news.get(position), NewsManager.LIKED_NEWS);
        NewsManager.getInstance(context).removeNews(news.get(position), NewsManager.SELECTED_NEWS);
        news.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, news.size());
        notifyDataSetChanged();
    }
    private void addItem(int position){
        String title = NewsManager.getInstance(context).getSelected().get(position).getTitle();
        String author = NewsManager.getInstance(context).getSelected().get(position).getAuthor();
        String text = NewsManager.getInstance(context).getSelected().get(position).getText();
        String data = NewsManager.getInstance(context).getSelected().get(position).getDate();
        String link = NewsManager.getInstance(context).getSelected().get(position).getOriginalArticleURL();
        Bitmap image = NewsManager.getInstance(context).getSelected().get(position).getBitmapIMG();
        News current = new News(title, author, text,image,data,link);
        NewsManager.getInstance(context).addNews(current, 2);
    }
}
