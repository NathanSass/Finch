package com.codepath.apps.finch.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

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

    @Column(name= "Screen_name")
    public String screenName;

    @Column(name = "Uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public Long uid;

    @Column(name = "Profile_image_url")
    String profileImageUrl;

    @Column(name = "Tagline")
    public String tagline;

    @Column(name = "FollowersCount")
    public int followersCount;

    @Column(name = "FollowingCount")
    public int followingCount;

    public User(){ super(); }

    public static User fromJSON(JSONObject json) {
        User u = new User();
        try {
            u.name = json.getString("name");
            u.uid = json.getLong("id");
            u.screenName = json.getString("screen_name");
            u.profileImageUrl = json.getString("profile_image_url");
            u.tagline = json.getString("description");
            u.followersCount = json.getInt("followers_count");
            u.followingCount = json.getInt("friends_count");

            u.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return u;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getTagline() {
        return tagline;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
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

    public static User fromUserId(Long userId) {
        return new Select()
                .from(User.class)
                .where("Uid = ?", userId)
                .limit(1).executeSingle();
    }
}
