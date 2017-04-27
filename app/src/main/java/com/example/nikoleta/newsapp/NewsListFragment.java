package com.example.nikoleta.newsapp;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progBar;
    private ProgressBar progBar2;
    private Toolbar toolbar;
    private MainActivity.CustomLayoutManager customLayoutManagerFrag;

    public NewsListFragment() {
        // Required empty public constructor
    }

    public MainActivity.CustomLayoutManager getCustomLayoutManagerFrag() {
        return customLayoutManagerFrag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_news_list, container, false);

        recyclerView= (RecyclerView) view.findViewById(R.id.recycler_view_frag_newslist);
        progBar= (ProgressBar) view.findViewById(R.id.progress_bar_frag_newslist);
        progBar2= (ProgressBar) view.findViewById(R.id.progress_bar2_frag_newslist);
        toolbar= (Toolbar) view.findViewById(R.id.toolbar_frag_newslist);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_navigation_arrow_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(NewsListFragment.this).commit();
                ((MainActivity)getActivity()).getRecyclerView().setVisibility(View.VISIBLE);
                ((MainActivity)getActivity()).getSupportActionBar().show();
            }
        });

        customLayoutManagerFrag= new MainActivity.CustomLayoutManager(getActivity());

        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);


        return view;
    }
    public  class CustomLayoutManager extends LinearLayoutManager {
        private boolean isScrollEnabled = true;

        public CustomLayoutManager(Context context) {
            super(context);
        }

        public void setScrollEnabled(boolean flag) {
            this.isScrollEnabled = flag;
        }

        @Override
        public boolean canScrollVertically() {
            //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
            return isScrollEnabled && super.canScrollVertically();
        }

        @Override
        public boolean canScrollHorizontally() {
            return super.canScrollHorizontally();
        }
    }

}
