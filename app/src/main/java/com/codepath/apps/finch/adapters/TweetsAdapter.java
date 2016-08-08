package com.codepath.apps.finch.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.finch.R;
import com.codepath.apps.finch.models.Tweet;
import com.codepath.apps.finch.util.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by nathansass on 8/2/16.
 */
public class TweetsAdapter extends
        RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView ivProfileImage;
        public TextView tvUserName;
        public TextView tvBody;
        public TextView tvTweetAge;
        public ImageView ivMediaImage;
        public TextView tvScreenName;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvTweetAge = (TextView) itemView.findViewById(R.id.tvTweetAge);
            ivMediaImage = (ImageView) itemView.findViewById(R.id.ivMedia);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);

        }
    }


    /* Viewholder Above */
    private List<Tweet> tweets;
    private Context context;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.tweets = tweets;
        this.context = context;
    }

    private Context getContext() {
        return context;
    }

    @Override
    public TweetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_tweet, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(TweetsAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Tweet tweet = tweets.get(position);

        TextView tvUserName = viewHolder.tvUserName;
        tvUserName.setText(tweet.getUser().getName());

        TextView tvScreenName = viewHolder.tvScreenName;
        tvScreenName.setText("@" + tweet.getUser().getScreenName());

        TextView tvBody = viewHolder.tvBody;
        tvBody.setText(tweet.getBody());

        TextView tvTweetAge = viewHolder.tvTweetAge;
        tvTweetAge.setText(tweet.getTweetAge());

        ImageView ivProfileImage = viewHolder.ivProfileImage;
        viewHolder.ivProfileImage.setImageResource(0);
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).transform(new CircleTransform()).into(ivProfileImage);

        ImageView ivMedia = viewHolder.ivMediaImage;
        viewHolder.ivMediaImage.setImageResource(0);
        if ( tweet.getMediaUrl() != null ) {
            Picasso.with(getContext()).load(tweet.getMediaUrl()).into(ivMedia);
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Tweet> newTweets) {
        tweets.addAll(newTweets);
        notifyDataSetChanged();
    }

}
