package com.codepath.apps.finch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.finch.R;
import com.codepath.apps.finch.TwitterApplication;
import com.codepath.apps.finch.TwitterClient;
import com.codepath.apps.finch.fragments.FollowersFragment;
import com.codepath.apps.finch.fragments.TweetListFragment;
import com.codepath.apps.finch.fragments.UserTimelineFragment;
import com.codepath.apps.finch.models.Tweet;
import com.codepath.apps.finch.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity implements TweetListFragment.OnItemSelectedListener{

    TwitterClient client;

    User user;

    MenuItem miActionProgressItem;

    @BindView(R.id.ivProfileImage)
    ImageView ivProfileImage;

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.tvTagline)
    TextView tvTagline;

    @BindView(R.id.tvFollowers)
    TextView tvFollowers;

    @BindView(R.id.tvFollowing)
    TextView tvFollowing;

    @BindView(R.id.viewpager)
    ViewPager vpPager;

    @BindView(R.id.tabs)
    PagerSlidingTabStrip tabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        client = TwitterApplication.getRestClient();

        ButterKnife.bind(this);


        if (savedInstanceState == null) {
//            constructUserTimeLineFragment();
        }
        setUpViewPager();

        getUserInfo();
    }

    public void setUpViewPager() {
        vpPager.setAdapter(new UserProfilePageAdapter(getSupportFragmentManager()));
        tabStrip.setViewPager(vpPager);
    }

    public class UserProfilePageAdapter extends FragmentPagerAdapter {

//        public String tabTitles[] = { "Timeline", "Followers", "Following" };
        public String tabTitles[] = { "Timeline", "Followers" };

        public UserProfilePageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (position == 0) {
                return UserTimelineFragment.newInstance("nsass711");
            } else if (position == 1) {
//                return null;
                return FollowersFragment.newInstance("nsass711");
//                return new MentionsTimelineFragment();
            } else {
                return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }


    public void populateProfileHeader(User user) {

        tvTagline.setText(user.getTagline());
        tvName.setText(user.getName());
        tvFollowers.setText(user.getFollowersCount() + " Followers");
        tvFollowing.setText(user.getFollowingCount() + " Following");

        Picasso.with(this).load(user.getProfileImageUrl()).into(ivProfileImage);
    }

    public void getUserInfo() {
        String screenName = getIntent().getStringExtra("screen_name");
        client.getUserInfo(screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                userSuccess(response);
            }

            public void userSuccess(JSONObject response) {
                user = User.fromJSON(response);

                getSupportActionBar().setTitle("@" + user.getScreenName());
                populateProfileHeader(user);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    userSuccess(response.getJSONObject(0));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

//    public void constructFragment() {
//        String screenName = getIntent().getStringExtra("screen_name");
//        UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(screenName);
//
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.flContainer, userTimelineFragment);
//
//
//        ft.commit();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_profile, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        // Extract the action-view from the menu item
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onTweetClick(Tweet tweet) {
        Intent intent = new Intent(this, TweetActivity.class);

        intent.putExtra("tweet", Parcels.wrap(tweet));

        startActivity(intent);
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }

    @Override
    public void showProgressSpinner() {
        showProgressBar();
    }

    @Override
    public void hideProgressSpinner() {
        hideProgressBar();
    }
}
