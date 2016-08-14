package com.codepath.apps.finch.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.codepath.apps.finch.TwitterApplication;
import com.codepath.apps.finch.TwitterClient;
import com.codepath.apps.finch.models.Tweet;
import com.codepath.apps.finch.util.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by nathansass on 8/14/16.
 */
public class SearchFragment extends TweetListFragment {

    private TwitterClient client;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        populateTimelineFromAPI();
    }

    public static SearchFragment newInstance(String query) {
        SearchFragment searchFragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString("query", query);
        searchFragment.setArguments(args);
        return searchFragment;
    }

    @Override
    public void populateTimelineOnSwipeRefresh() {}

    public void populateTimelineFromAPI() {
        String query = getArguments().getString("query");

        client.getTweetSearch(query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                ArrayList<Tweet> newTweets = Tweet.fromJsonArray(json);
                addAll(newTweets);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray arrResp = response.getJSONArray("statuses");
                    ArrayList<Tweet> newTweets = Tweet.fromJsonArray(arrResp);
                    addAll(newTweets);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Util.handleJsonFailure(errorResponse);
            }
        });

    }

}
