package com.codepath.apps.finch.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.finch.R;
import com.codepath.apps.finch.TwitterApplication;
import com.codepath.apps.finch.TwitterClient;
import com.codepath.apps.finch.models.Tweet;
import com.codepath.apps.finch.util.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    private TwitterClient client;
    private Context context;

    private final int TWEET_LENGTH = 140;

    @BindView(R.id.etTweetBody)
    EditText etTweetBody;
    @BindView(R.id.pbLoading)
    ProgressBar progressBar;

    @BindView(R.id.tvCharCount)
    TextView tvCharCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        context = this;
        client = TwitterApplication.getRestClient();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @OnTextChanged(R.id.etTweetBody)
    public void textChanged (CharSequence text) {
        int remaining = TWEET_LENGTH - text.length();

        tvCharCount.setText("" + remaining);
    }

    @OnClick(R.id.submit)
    public void submitTweet() {

        String tweetBody = etTweetBody.getText().toString();

        progressBar.setVisibility( ProgressBar.VISIBLE );

        client.postNewTweet(tweetBody, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressBar.setVisibility( ProgressBar.INVISIBLE );

                Tweet tweet = Tweet.fromJSON(response);

                Intent data = new Intent();
                data.putExtra("tweet", Parcels.wrap(tweet));
                data.putExtra("code", 200);
                setResult(RESULT_OK, data);

                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(context, "Tweet Failed", Toast.LENGTH_SHORT).show();
                Util.handleJsonFailure(errorResponse);
            }
        });
    }

}
