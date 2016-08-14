package com.codepath.apps.finch.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.codepath.apps.finch.R;
import com.codepath.apps.finch.fragments.SearchFragment;
import com.codepath.apps.finch.fragments.TweetListFragment;
import com.codepath.apps.finch.models.Tweet;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity implements TweetListFragment.OnItemSelectedListener{

    @BindView(R.id.tvToolbarTitle)
    TextView tvToolbarTitle;

    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        query = getIntent().getStringExtra("query");
        tvToolbarTitle.setText( query);

        constructFragment();
    }

    public void constructFragment() {
        SearchFragment searchFragment = SearchFragment.newInstance(query);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContainer, searchFragment);

        ft.commit();
    }

    @Override
    public void onTweetClick(Tweet tweet) {

    }

    @Override
    public void showProgressSpinner() {

    }

    @Override
    public void hideProgressSpinner() {

    }
}
