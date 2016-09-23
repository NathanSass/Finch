package com.codepath.apps.finch.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.codepath.apps.finch.TwitterApplication;
import com.codepath.apps.finch.TwitterClient;
import com.codepath.apps.finch.models.User;
import com.codepath.apps.finch.util.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by nathansass on 8/13/16.
 */
public class FollowingFragment extends UserListFragment{
    private TwitterClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        populateTimelineFromAPI();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        populateTimelineFromAPI();
    }

    public static FollowingFragment newInstance(String screen_name) {
        FollowingFragment followingFragment = new FollowingFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screen_name);
        followingFragment.setArguments(args);
        return followingFragment;
    }


    public void populateTimelineFromAPI() {
        String screenName = getArguments().getString("screen_name");
        client.getFollowing(screenName, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                ArrayList<User> newUsers = User.fromJsonArray(json);
                addAll(newUsers);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray json = response.getJSONArray("users");
                    ArrayList<User> newUsers = User.fromJsonArray(json);
                    addAll(newUsers);
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
