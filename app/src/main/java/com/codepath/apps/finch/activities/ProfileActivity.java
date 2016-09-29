package com.codepath.apps.finch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
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
import com.codepath.apps.finch.fragments.FollowingFragment;
import com.codepath.apps.finch.fragments.TweetListFragment;
import com.codepath.apps.finch.fragments.UserTimelineFragment;
import com.codepath.apps.finch.models.Tweet;
import com.codepath.apps.finch.models.User;
import com.codepath.apps.finch.util.CircleTransform;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        client = TwitterApplication.getRestClient();

        ButterKnife.bind(this);


        if (savedInstanceState == null) {
//            constructUserTimeLineFragment();
        }
        getUserInfo();
    }

    public void setUpViewPager(User user) {
        vpPager.setAdapter(new UserProfilePageAdapter(getSupportFragmentManager(), user));
        tabStrip.setViewPager(vpPager);
    }

    public class UserProfilePageAdapter extends FragmentPagerAdapter {

        public User user;
        public String tabTitles[];

        public UserProfilePageAdapter(FragmentManager fm, User user) {
            super(fm);
            this.user = user;

//            tabTitles = new String[]{"Timeline",  "Followers", "Following"};
            tabTitles = new String[]{"Timeline", user.getFollowersCount() + " Followers", user.getFollowingCount() + " Following"};
        }

        @Override
        public Fragment getItem(int position) {

            if (position == 0) {

                return UserTimelineFragment.newInstance(this.user.getScreenName());
            } else if (position == 1) {
                return FollowersFragment.newInstance(this.user.getScreenName());
            } else if (position == 2) {
                return FollowingFragment.newInstance(this.user.getScreenName());
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
        Picasso.with(this).load(user.getProfileImageUrl()).transform(new CircleTransform()).into(ivProfileImage);
    }

    public void getUserInfo() {
//        String screenName = getIntent().getStringExtra("screen_name"); //BUGBUG: currently not doing anything
        client.getUserInfo( new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                userSuccess(response);
            }

            public void userSuccess(JSONObject response) {
                user = User.fromJSON(response);

                setUpViewPager(user);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_profile, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // This is the up button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                // overridePendingTransition(R.animator.anim_left, R.animator.anim_right);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* Deprecated */
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
}
