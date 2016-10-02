package com.codepath.apps.finch.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
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

import java.util.ArrayList;
import java.util.Arrays;

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

        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Makes the dialog take up the whole screen
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
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

        if (remaining <= 0) {
            tvCharCount.setText("Tweetstorm");
        } else {
            tvCharCount.setText("" + remaining);
        }
    }

    @OnClick(R.id.ivBackArrow)
    public void upBottonClick() {
        dismiss();
    }

    @OnClick(R.id.submit)
    public void submitTweet() {

        String tweetBody = etTweetBody.getText().toString();

        progressBar.setVisibility( ProgressBar.VISIBLE );

        // Manipulate tweetbody here
        if (tweetBody.length() <= 140) {
            postTweet(tweetBody);
        } else { // Case: tweetstorm
            ArrayList<String> tweetStorms = separateIntoTweets(tweetBody);
            postTweetInOrder(tweetStorms);
        }

    }

    public ArrayList<String> separateIntoTweets(String tweetBody) {
        String[] tweetAsArr = tweetBody.split(" ");
        ArrayList<String> newTweets = new ArrayList<>();
        int currentLength = 1;
        int floor = 0;
        int ceiling = 0;

        for (int i = 0; i < tweetAsArr.length; i++) {

            if (currentLength + tweetAsArr[i].length() <= TWEET_LENGTH - 15 ) {
                ceiling = i;
                currentLength += tweetAsArr[i].length() + 1; // The 1 is for the space
            } else {
                String[] subTweetArr =  Arrays.copyOfRange(tweetAsArr, floor, i);
                String subTweet = newTweets.size() + 1  + "/ " + TextUtils.join(" ", subTweetArr);
                newTweets.add(subTweet);
                floor = i;
                currentLength = 1;
            }
        }

        if (floor < tweetAsArr.length) {
            String[] subTweetArr =  Arrays.copyOfRange(tweetAsArr, floor, tweetAsArr.length);
            String subTweet = newTweets.size() + 1 +  "/ " + TextUtils.join(" ", subTweetArr);
            newTweets.add(subTweet);
        }

        return newTweets;
    }

    public void postTweetInOrder(final ArrayList<String> tweets) {
        String tweetBody = tweets.remove(0);

        client.postNewTweet(replyToTweet, tweetBody, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Tweet tweet = Tweet.fromJSON(response); //Not currently being used

                if (tweets.size() > 0) {
                    postTweetInOrder(tweets); // Recursive call
                } else {
                    progressBar.setVisibility( ProgressBar.INVISIBLE );
                    dismiss();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Util.handleJsonFailure(errorResponse);
                return;
            }
        });
    }

    public void postTweet(String tweetBody) {

        client.postNewTweet(replyToTweet, tweetBody, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressBar.setVisibility( ProgressBar.INVISIBLE );

                Tweet tweet = Tweet.fromJSON(response);

                if (replyToTweet != null) { //Not currently doing anything
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
