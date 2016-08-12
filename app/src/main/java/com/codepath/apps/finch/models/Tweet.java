package com.codepath.apps.finch.models;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.codepath.apps.finch.TwitterApplication;
import com.codepath.apps.finch.TwitterClient;
import com.codepath.apps.finch.util.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by nathansass on 8/2/16.
 */


@Table(name = "Tweets")
@Parcel(analyze={Tweet.class})
public class Tweet extends Model{

    @Column(name = "Body")
    public String body;

    @Column(name = "Uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public Long uid;

    @Column(name = "user_id")
    public Long user_id;

    public User user;

    @Column(name = "CreatedAt")
    String createdAt;

    @Column(name = "Media_url")
    String mediaUrl;

    @Column(name = "Video_url") // not used currently
    String videoUrl;

    @Column(name= "Favorited")
    Boolean favorited;

    @Column(name= "Retweeted")
    Boolean retweeted;

    public Tweet() { super(); }


    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();

        try {
            tweet.uid = jsonObject.getLong("id");
            tweet.body = jsonObject.getString("text");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON( jsonObject.getJSONObject("user") );
            tweet.user_id = tweet.user.getUid();

            tweet.favorited = jsonObject.getBoolean("favorited");
            tweet.retweeted = jsonObject.getBoolean("retweeted");

            try {
                JSONArray mediaArr = jsonObject.getJSONObject("entities").getJSONArray("media");
                if ( mediaArr.length() > 0 ) {
                    tweet.mediaUrl = mediaArr.getJSONObject(0).getString("media_url");
                }
            } catch (JSONException e) {
//                e.printStackTrace();
            }

            try { // These are for the video URLS, but currently unable to get that working
                JSONArray urlArr = jsonObject.getJSONObject("entities").getJSONArray("urls");
                tweet.videoUrl = urlArr.getJSONObject(0).getString("display_url");
//                Log.v("DEBUG", "URLARR: " + tweet.videoUrl);
            } catch (JSONException e) {
//                e.printStackTrace();
            }



            tweet.save();
        } catch (JSONException e) {
//            e.printStackTrace();
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
                .orderBy("CreatedAt DESC")
                .limit("25")
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
        if (user != null) {
            return user;
        } else {
            return User.fromUserId(user_id);
        }
//        return user != null ? user : User.fromUserId(user_id);
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public Boolean isFavorited() {
        return favorited;
    }

    public Boolean isRetweeted() {
        return retweeted;
    }

    public void setRetweeted(Boolean isRetweet) {
        TwitterClient client = TwitterApplication.getRestClient();
        client.postRetweet(isRetweet, getUid(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("DEBUG", "Set Retweet Success");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.v("DEBUG", "Set Retweet Failure");
            }
        });
        this.retweeted = isRetweet;
    }

    public void setFavorited(Boolean isFavorite) {
        TwitterClient client = TwitterApplication.getRestClient();
        client.postFavorites(isFavorite, getUid(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("DEBUG", "Set Favorite Success");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.v("DEBUG", "Set Favorite Failure");
            }
        });

        this.favorited = isFavorite;
    }
}
