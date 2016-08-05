package com.codepath.apps.finch.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.finch.R;
import com.codepath.apps.finch.TwitterApplication;
import com.codepath.apps.finch.TwitterClient;
import com.codepath.apps.finch.adapters.TweetsAdapter;
import com.codepath.apps.finch.models.Tweet;
import com.codepath.apps.finch.util.EndlessRecyclerViewScrollListener;
import com.codepath.apps.finch.util.ItemClickSupport;
import com.codepath.apps.finch.util.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private final int REQUEST_CODE = 99;

    private TwitterClient client;
    private TweetsAdapter adapter;
    private ArrayList<Tweet> tweets;
    private Context context;

    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeRefreshLayout;

    LinearLayoutManager linearLayoutManager;

    @BindView(R.id.rvTweets) RecyclerView rvTweets;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ButterKnife.bind(this);

        context = this;

        client = TwitterApplication.getRestClient();

        setUpRecyclerView();

        initEndlessScrolling();

        populateTimelineFromDB();

        setupSwipeRefreshListener();

        populateTimelineFromAPI();

        setTweetItemClickListener();
    }

    public void setTweetItemClickListener() {
        ItemClickSupport.addTo(rvTweets).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                        Toast.makeText(context, "item click " + position, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(TimelineActivity.this, TweetActivity.class);

                        intent.putExtra("tweet", Parcels.wrap(tweets.get(position)));

                        startActivity(intent);
                    }
                }
        );
    }

    public void setupSwipeRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();

                populateTimeline();
            }
        });

        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    public void setUpRecyclerView() {
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);

        rvTweets.setAdapter(adapter);
        linearLayoutManager = new LinearLayoutManager(this);

        rvTweets.setLayoutManager(linearLayoutManager);
    }

    public void initEndlessScrolling() {
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
                        Util.handleJsonFailure(errorResponse);
                    }
                });
            }
        });
    }

    public void populateTimelineFromAPI() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                adapter.clear(); // Clear DB Stored tweets

                ArrayList<Tweet> newTweets = Tweet.fromJsonArray(json);
                adapter.addAll(newTweets);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Util.handleJsonFailure(errorResponse);
            }
        });

    }

    public void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                handleTweetJsonSuccess(json);

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Util.handleJsonFailure(errorResponse);
            }
        });
    }

    public void populateTimelineFromDB() {
        ArrayList<Tweet> dbTweets = (ArrayList<Tweet>) Tweet.getAllTweetsFromDB();

        tweets.addAll(dbTweets);
        adapter.notifyDataSetChanged();
    }

    public void handleTweetJsonSuccess(JSONArray json) {
        int curSize = adapter.getItemCount();

        ArrayList<Tweet> newTweets = Tweet.fromJsonArray(json);

        tweets.addAll(newTweets);
        adapter.notifyItemRangeInserted(curSize, newTweets.size());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_compose) {
            Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
            startActivityForResult(i, REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            int code = data.getExtras().getInt("code", 0);

            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            addTweetToTimelineStart(tweet);

            rvTweets.smoothScrollToPosition(0);

            Toast.makeText(this, "Tweet: " + tweet.getBody(), Toast.LENGTH_SHORT).show();
        }
    }

    public void addTweetToTimelineStart(Tweet tweet) {
        tweets.add(0, tweet);
        adapter.notifyItemInserted(0);
    }
}
