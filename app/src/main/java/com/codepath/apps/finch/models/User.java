package com.codepath.apps.finch.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by nathansass on 8/2/16.
 */


@Table(name = "Users")
@Parcel(analyze={User.class})
public class User extends Model{

    @Column(name = "Name")
    public String name;

    @Column(name = "Uid")
    public long uid;

    String screenName;
    String profileImageUrl;

    public User(){ super(); }

    public static User fromJSON(JSONObject json) {
        User u = new User();
        try {
            u.name = json.getString("name");
            u.uid = json.getLong("id");
            u.screenName = json.getString("screen_name");
            u.profileImageUrl = json.getString("profile_image_url");

            u.save();
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
