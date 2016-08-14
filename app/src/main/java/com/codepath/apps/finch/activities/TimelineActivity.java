package com.codepath.apps.finch.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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

public class TimelineActivity extends AppCompatActivity implements TweetListFragment.OnItemSelectedListener, ComposeTweetFragment.TimelineCommunicator{ //  implements ComposeTweetFragment.TimelineCommunicator

    private final int REQUEST_CODE = 99;

    private TwitterClient client;

    private Context context;

    @BindView(R.id.viewpager) ViewPager vpPager;
    @BindView(R.id.tabs) PagerSlidingTabStrip tabStrip;

    MenuItem miActionProgressItem;


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
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        // Extract the action-view from the menu item
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_timeline, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

//        int searchImgId = android.support.v7.appcompat.R.id.search_button;
//        ImageView v = (ImageView) searchView.findViewById(searchImgId);
//        v.setImageResource(R.drawable.search_btn);
        // Customize searchview text and hint colors
        int searchEditId = android.support.v7.appcompat.R.id.search_src_text;
        EditText et = (EditText) searchView.findViewById(searchEditId);
        et.setTextColor(ContextCompat.getColor(context, R.color.lightText));
        et.setHintTextColor(ContextCompat.getColor(context, R.color.twitSecondary));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
//                Toast.makeText(getApplicationContext(), "query: " + query, Toast.LENGTH_SHORT).show();
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599

                Intent i = new Intent(TimelineActivity.this, SearchActivity.class);
                i.putExtra("query", query);
                startActivity(i);
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);

    }


    @Override
    public void onTweetPost(Tweet tweet) {
        //TODO: make this auto scroll
        if (vpPager.getCurrentItem() == 0) {
            Toast.makeText(this, "In tweets timeline", Toast.LENGTH_SHORT).show();

        } else if ( vpPager.getCurrentItem() == 1) {
            Toast.makeText(this, "In mentions fragment", Toast.LENGTH_SHORT).show();
        }
    }
}
