package com.codepath.apps.finch.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.finch.R;
import com.codepath.apps.finch.TwitterApplication;
import com.codepath.apps.finch.TwitterClient;
import com.codepath.apps.finch.fragments.ComposeTweetFragment;
import com.codepath.apps.finch.models.Tweet;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TweetActivity extends AppCompatActivity implements ComposeTweetFragment.TweetDetailCommunicator {

    private final int REQUEST_CODE = 12;

    Tweet tweet;
    Context context;
    private TwitterClient client;

    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.tvUserName) TextView tvUserName;
    @BindView(R.id.tvBody) TextView tvBody;
    @BindView(R.id.ivMedia) ImageView ivMedia;
    @BindView(R.id.tvScreenName) TextView screenName;

    @BindView(R.id.tvTweetAge) TextView tvTweetAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        context = this;
        client = TwitterApplication.getRestClient();

        ButterKnife.bind(this);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        setUpUi();
    }

    @OnClick({R.id.ivReplyIcon, R.id.tvReplyText})
    public void replyClick() {

        showComposeDialog();
    }

    @OnClick({ R.id.ivProfileImage, R.id.tvUserName, R.id.tvScreenName })
    public void showUserProfile() {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("screen_name", tweet.getUser().getScreenName());
        startActivity(i);
    }

    private void showComposeDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetFragment composeTweetFragment = ComposeTweetFragment.newInstance("Compose New Tweet", tweet);
        composeTweetFragment.show(fm, "activity_compose");
    }

    public void setUpUi() {
        tvUserName.setText(tweet.getUser().getName());
        tvBody.setText(tweet.getBody());
        tvTweetAge.setText(tweet.getTweetAge());
        screenName.setText("@" + tweet.getUser().getScreenName());
        Picasso.with(this).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);
        if (tweet.getMediaUrl() != null) {
            Picasso.with(this).load(tweet.getMediaUrl()).into(ivMedia);
        }

        String mediaUrl = tweet.getMediaUrl();

        if (mediaUrl != null) {
            Glide.with(context)
                    .load(mediaUrl)
                    .into(ivMedia);
        }
    }


    @Override
    public void onTweetPost() {
        Intent i = new Intent(TweetActivity.this, TimelineActivity.class);
        startActivity(i);
    }
}
