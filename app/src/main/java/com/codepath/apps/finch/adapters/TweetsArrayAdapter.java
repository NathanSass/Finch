package com.codepath.apps.finch.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.codepath.apps.finch.models.Tweet;

import java.util.List;

/**
 * Created by nathansass on 8/2/16.
 */
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {
    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1,tweets);
    }
}
