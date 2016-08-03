package com.codepath.apps.finch.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.codepath.apps.finch.R;
import com.codepath.apps.finch.TwitterApplication;
import com.codepath.apps.finch.TwitterClient;
import com.codepath.apps.finch.adapters.TweetsAdapter;
import com.codepath.apps.finch.models.Tweet;
import com.codepath.apps.finch.util.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
//    private RecyclerView rvTweets;
    private TweetsAdapter adapter;
    private ArrayList<Tweet> tweets;
    private Context context;

    @BindView(R.id.rvTweets) RecyclerView rvTweets;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        context = this;

        client = TwitterApplication.getRestClient();

        setUpRecyclerView();
        populateTimeline();

    }

    public void setUpRecyclerView() {
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);

        rvTweets.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        rvTweets.setLayoutManager(linearLayoutManager);

        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                long maxId = tweets.get(tweets.size() - 1).getUid();

                client.getHomeTimeline(maxId, new JsonHttpResponseHandler() {
                    public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                      handleTweetJsonSuccess(json);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        handleTweetJsonFailure(errorResponse);
                    }
                });
            }
        });
    }

    public void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                handleTweetJsonSuccess(json);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
               handleTweetJsonFailure(errorResponse);
            }
        });
    }

    public void handleTweetJsonSuccess(JSONArray json) {
        int curSize = adapter.getItemCount();

        ArrayList<Tweet> newTweets = Tweet.fromJsonArray(json);

        tweets.addAll(newTweets);
        adapter.notifyItemRangeInserted(curSize, newTweets.size());

    }

    public void handleTweetJsonFailure(JSONObject e) {
        Log.d("DEBUG", e.toString());
    }

}
