package com.codepath.apps.finch.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nathansass on 8/2/16.
 */
public class User {
    private String name;

    private long uid;

    private String screenName;
    private String profileImageUrl;
    public static User fromJSON(JSONObject json) {
        User u = new User();
        try {
            u.name = json.getString("name");
            u.uid = json.getLong("id");
            u.screenName = json.getString("screen_name");
            u.profileImageUrl = json.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return u;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }
}
