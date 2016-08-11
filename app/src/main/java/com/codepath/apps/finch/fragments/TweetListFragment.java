package com.codepath.apps.finch.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.finch.R;
import com.codepath.apps.finch.adapters.TweetsAdapter;
import com.codepath.apps.finch.models.Tweet;
import com.codepath.apps.finch.util.EndlessRecyclerViewScrollListener;
import com.codepath.apps.finch.util.ItemClickSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by nathansass on 8/8/16.
 */
public abstract class TweetListFragment extends Fragment {


    private TweetsAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    @BindView(R.id.rvTweets)
    RecyclerView rvTweets;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<Tweet> tweets;

    private Unbinder unbinder;

    private OnItemSelectedListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false );
        unbinder = ButterKnife.bind(this, v);

        setUpRecyclerView();

        initEndlessScrolling();

        setTweetItemClickListener();

        setupSwipeRefreshListener();

        populateTimelineFromDB();

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement TweetListFragment.onItemSelectedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setUpRecyclerView() {
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(getActivity(), tweets);

        rvTweets.setAdapter(adapter);
        linearLayoutManager = new LinearLayoutManager(getActivity());

        rvTweets.setLayoutManager(linearLayoutManager);
    }

    public void addAll(List<Tweet> tweets) {
        adapter.clear();
        adapter.addAll(tweets);
        adapter.notifyDataSetChanged();
        stopRefreshing();
    }

    /* This must be overridden */
    public abstract void populateTimelineOnSwipeRefresh();
    public void populateTimelineFromDB(){}

    public void stopRefreshing() {
        swipeRefreshLayout.setRefreshing(false);
    }
    public void setupSwipeRefreshListener() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();

                populateTimelineOnSwipeRefresh();
            }
         });

        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    public void appendTo(List<Tweet> tweets) {
        int curSize = adapter.getItemCount();
        adapter.addAll(tweets);
        adapter.notifyItemRangeInserted(curSize, adapter.getItemCount());
    }

    public void addToStart(Tweet tweet) {
        tweets.add(0,tweet);
        adapter.notifyItemInserted(0);

        rvTweets.smoothScrollToPosition(0);
    }

    public void initEndlessScrolling() {
        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                long maxId = tweets.get(tweets.size() - 1).getUid();

                endlessScrollingAction(maxId);
            }
        });
    }

    /* This is overridden to implement endless scrolling */
    public void endlessScrollingAction(long maxId) {}

    public void setTweetItemClickListener() {
        ItemClickSupport.addTo(rvTweets).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                        listener.onTweetClick(tweets.get(position));

                    }
                }
        );
    }

    public interface OnItemSelectedListener {
       void onTweetClick(Tweet tweet);
    }
}
