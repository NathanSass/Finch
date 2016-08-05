package com.codepath.apps.finch.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.codepath.apps.finch.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nathansass on 8/2/16.
 */


@Table(name = "Tweets")
@Parcel(analyze={Tweet.class})
public class Tweet extends Model{

    @Column(name = "Body")
    public String body;

    @Column(name = "Uid")
    public long uid;

    @Column(name = "User", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    public User user;

    @Column(name = "CreatedAt")
    String createdAt;

    public Tweet() { super(); }

    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();

        try {
            tweet.uid = jsonObject.getLong("id");
            tweet.body = jsonObject.getString("text");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON( jsonObject.getJSONObject("user") );

            tweet.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public static ArrayList<Tweet> fromJsonArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(tweetJson);
                if (tweet!= null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }

        }
        return tweets;
    }

    public static List<Tweet> getAllTweetsFromDB() {
        return new Select()
                .from(Tweet.class)
                .orderBy("CreatedAt ASC")
                .execute();
    }

    public String getTweetAge() {
        String tweetAge = Util.getRelativeTimeAgo(createdAt);
        return tweetAge;
    }

    public long getUid() {
        return uid;
    }

    public String getBody() {
        return body;
    }

    public User getUser() {
        return user;
    }
}
