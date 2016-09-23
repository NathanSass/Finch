package com.codepath.apps.finch.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.codepath.apps.finch.TwitterApplication;
import com.codepath.apps.finch.TwitterClient;
import com.codepath.apps.finch.models.Tweet;
import com.codepath.apps.finch.util.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by nathansass on 8/9/16.
 */
public class UserTimelineFragment extends TweetListFragment{
    private TwitterClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /* This needs to occur after view created because
            in my 3 part fragment in the user profile on 2 views are cached.
            And the creation of the recycler view is called mulp times

         */
        populateTimelineFromAPI();
    }

    public static UserTimelineFragment newInstance(String screen_name) {
        UserTimelineFragment userTimelineFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screen_name);
        userTimelineFragment.setArguments(args);
        return userTimelineFragment;
    }

    @Override
    public void populateTimelineOnSwipeRefresh() {
        populateTimelineFromAPI();
    }

    public void populateTimelineFromAPI() {
        String screenName = getArguments().getString("screen_name");
        client.getUserTimeline(screenName, new JsonHttpResponseHandler() {
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
