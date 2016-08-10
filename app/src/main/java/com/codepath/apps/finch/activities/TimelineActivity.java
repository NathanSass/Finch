package com.codepath.apps.finch.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.finch.R;
import com.codepath.apps.finch.TwitterClient;
import com.codepath.apps.finch.fragments.ComposeTweetFragment;
import com.codepath.apps.finch.fragments.HomeTimelineFragment;
import com.codepath.apps.finch.fragments.MentionsTimelineFragment;
import com.codepath.apps.finch.fragments.TweetListFragment;
import com.codepath.apps.finch.models.Tweet;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TimelineActivity extends AppCompatActivity implements TweetListFragment.OnItemSelectedListener{ //  implements ComposeTweetFragment.TimelineCommunicator

    private final int REQUEST_CODE = 99;

    private TwitterClient client;

    private Context context;

    @BindView(R.id.viewpager) ViewPager vpPager;
    @BindView(R.id.tabs) PagerSlidingTabStrip tabStrip;

//    @BindView(R.id.fab) FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ButterKnife.bind(this);

        context = this;

        setUpViewPager();

//        populateTimelineFromDB();

//        setupSwipeRefreshListener();



    }

    public void setUpViewPager() {
        vpPager.setAdapter(new TweetsPageAdapter(getSupportFragmentManager()));
        tabStrip.setViewPager(vpPager);
    }

    @OnClick(R.id.fab)
    public void onFabButtonClick() {

        showComposeDialog();

    }

    public void onProfileView(MenuItem mi) {
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    private void showComposeDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetFragment composeTweetFragment = ComposeTweetFragment.newInstance("Compose New Tweet", null);
        composeTweetFragment.show(fm, "activity_compose");
    }

    @Override
    public void onTweetClick(Tweet clickedTweet) {
        Intent intent = new Intent(TimelineActivity.this, TweetActivity.class);

        intent.putExtra("tweet", Parcels.wrap(clickedTweet));

        startActivity(intent);
    }

    // return the order of fragments in the view pager
    public class TweetsPageAdapter extends FragmentPagerAdapter {

        public String tabTitles[] = { "Home", "Mentions" };

        public TweetsPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (position == 0) {
                return new HomeTimelineFragment();
            } else if (position == 1) {
                return new MentionsTimelineFragment();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);

        return true;
    }

//    public void populateTimelineFromDB() {
//        ArrayList<Tweet> dbTweets = (ArrayList<Tweet>) Tweet.getAllTweetsFromDB();
//
//        tweets.addAll(dbTweets);
//        adapter.notifyDataSetChanged();
//    }
//
//    public void handleTweetJsonSuccess(JSONArray json) {
//        int curSize = adapter.getItemCount();
//
//        ArrayList<Tweet> newTweets = Tweet.fromJsonArray(json);
//
//        tweets.addAll(newTweets);
//        adapter.notifyItemRangeInserted(curSize, newTweets.size());
//
//    }
//
//
//    public void addTweetToTimelineStart(Tweet tweet) {
//        tweets.add(0, tweet);
//        adapter.notifyItemInserted(0);
//    }
//
//    @Override
//    public void onTweetPost(Tweet tweet) {
//        addTweetToTimelineStart(tweet);
//
//        rvTweets.smoothScrollToPosition(0);
//
//        Toast.makeText(this, "Tweet: " + tweet.getBody(), Toast.LENGTH_SHORT).show();
//    }
}
