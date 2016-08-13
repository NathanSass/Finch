package com.codepath.apps.finch.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.codepath.apps.finch.TwitterApplication;
import com.codepath.apps.finch.TwitterClient;
import com.codepath.apps.finch.models.User;
import com.codepath.apps.finch.util.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class FollowersFragment extends UserListFragment {
    private TwitterClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        populateTimelineFromAPI();
    }

    public static FollowersFragment newInstance(String screen_name) {
        FollowersFragment followersFragment = new FollowersFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screen_name);
        followersFragment.setArguments(args);
        return followersFragment;
    }


    public void populateTimelineFromAPI() {
        String screenName = getArguments().getString("screen_name");
        client.getFollowers(screenName, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                ArrayList<User> newUsers = User.fromJsonArray(json);
                addAll(newUsers);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Util.handleJsonFailure(errorResponse);
            }

        });


    }
}
