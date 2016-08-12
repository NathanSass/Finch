package com.codepath.apps.finch.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.codepath.apps.finch.TwitterApplication;
import com.codepath.apps.finch.TwitterClient;
import com.codepath.apps.finch.models.Tweet;
import com.codepath.apps.finch.util.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class HomeTimelineFragment extends TweetListFragment {

    private TwitterClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();

        populateTimelineFromAPI();
    }


    @Override
    public void endlessScrollingAction(long maxId) {
        showProgressSpinner();
        client.getHomeTimeline(maxId, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                hideProgressSpinner();
                appendTo( Tweet.fromJsonArray(json) );
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                hideProgressSpinner();
                Util.handleJsonFailure(getContext(), errorResponse);
            }
        });
    }

    @Override
    public void populateTimelineFromDB() {
        ArrayList<Tweet> dbTweets = (ArrayList<Tweet>) Tweet.getAllTweetsFromDB();

        addAll(dbTweets);
    }

    @Override
    public void populateTimelineOnSwipeRefresh() {
        populateTimelineFromAPI();
    }

    public void populateTimelineFromAPI() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                ArrayList<Tweet> newTweets = Tweet.fromJsonArray(json);
                addAll(newTweets);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Util.handleJsonFailure(errorResponse);
            }
        });

    }


}
