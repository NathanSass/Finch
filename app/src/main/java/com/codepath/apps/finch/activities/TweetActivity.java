package com.codepath.apps.finch.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

    @BindView(R.id.tvTweetAge) TextView tvTweetAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        client = TwitterApplication.getRestClient();

        ButterKnife.bind(this);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        setUpUi();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @OnClick(R.id.btnReply)
    public void replyClick() {

        showComposeDialog();
    }

    private void showComposeDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetFragment composeTweetFragment = ComposeTweetFragment.newInstance("Compose New Tweet", tweet);
        composeTweetFragment.show(fm, "activity_compose");
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
//            int code = data.getExtras().getInt("code", 0);
//
//            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
//
//        }
//    }

    public void setUpUi() {
        tvUserName.setText(tweet.getUser().getName());
        tvBody.setText(tweet.getBody());
        tvTweetAge.setText(tweet.getTweetAge());
        Picasso.with(this).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);
    }


    @Override
    public void onTweetPost(Tweet tweet) {
        Intent i = new Intent(TweetActivity.this, TimelineActivity.class);
        startActivity(i);
    }
}
