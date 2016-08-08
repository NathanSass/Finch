package com.codepath.apps.finch.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;

/**
 * Created by nathansass on 8/7/16.
 */
public class ComposeTweetFragment extends DialogFragment {

    private Unbinder unbinder;


    private TwitterClient client;
    private final int TWEET_LENGTH = 140;

    Tweet replyToTweet;

    @BindView(R.id.etTweetBody)
    EditText etTweetBody;
    @BindView(R.id.pbLoading)
    ProgressBar progressBar;

    @BindView(R.id.tvCharCount)
    TextView tvCharCount;

    Activity activity;

    public ComposeTweetFragment() {}

    public static ComposeTweetFragment newInstance(String title, Tweet incomingReplyTweet) {
        ComposeTweetFragment frag = new ComposeTweetFragment();
        Bundle args = new Bundle();

        args.putString("title", title);
        args.putParcelable("replyToTweet", Parcels.wrap(incomingReplyTweet));
//        Log.v("DEBUG", "replyTOTWeet in composetweetfrag: " + replyToTweet.toString());

        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_compose, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            replyToTweet = Parcels.unwrap(getArguments().getParcelable("replyToTweet"));
        } catch (NullPointerException e) {
            replyToTweet = null;
//            e.printStackTrace();
        }

        client = TwitterApplication.getRestClient();

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

        client.postNewTweet(replyToTweet, tweetBody, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressBar.setVisibility( ProgressBar.INVISIBLE );

                Tweet tweet = Tweet.fromJSON(response);

                if (replyToTweet != null) {
                    /* Routes Back to Timeline, then reloads it */
                    ((TweetDetailCommunicator) activity).onTweetPost();

                } else {
                    /* Goes to timeline then adds existing tweet to the adapter*/
                    ((TimelineCommunicator) activity).onTweetPost(tweet);
                }

                dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Util.handleJsonFailure(errorResponse);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public interface TimelineCommunicator {
        void onTweetPost(Tweet tweet);
    }

    public interface TweetDetailCommunicator {
        void onTweetPost();
    }
}
