package com.codepath.apps.finch.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.finch.R;
import com.codepath.apps.finch.models.Tweet;
import com.codepath.apps.finch.util.CircleTransform;
import com.codepath.apps.finch.util.PatternEditableBuilder;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.regex.Pattern;

import butterknife.ButterKnife;

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

        public ImageView ivLikeIcon;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            ButterKnife.bind(this, itemView);

            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvTweetAge = (TextView) itemView.findViewById(R.id.tvTweetAge);
            ivMediaImage = (ImageView) itemView.findViewById(R.id.ivMedia);
            ivLikeIcon = (ImageView) itemView.findViewById(R.id.ivLikeIcon);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);

        }
    }


    /* Viewholder Above */
    private List<Tweet> tweets;
    private Context context;
    private final Communicator communicator;

    public TweetsAdapter(Context context, List<Tweet> tweets, Communicator communicator) {
        this.tweets = tweets;
        this.context = context;
        this.communicator = communicator;
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
        final Tweet tweet = tweets.get(position);
        final TweetsAdapter.ViewHolder vhViewHolder = viewHolder;
        TextView tvUserName = viewHolder.tvUserName;
        tvUserName.setText(tweet.getUser().getName());

        TextView tvScreenName = viewHolder.tvScreenName;
        tvScreenName.setText("@" + tweet.getUser().getScreenName());

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
        /* Making text clickable */
        TextView tvBody = viewHolder.tvBody;
        tvBody.setText(tweet.getBody());

        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"), R.color.twitBlue,
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String screenName) {

                                communicator.onProfileLinkClickListener( screenName.substring(1, screenName.length()) );

                            }
                        }).into(tvBody);

        /* Tweet Favoriteing */
        Boolean isFavorite = tweet.isFavorited();
        ImageView ivLikeIcon = viewHolder.ivLikeIcon;
        if (isFavorite) {
            ivLikeIcon.setImageResource(R.drawable.ic_favorite_true);
        } else {
            ivLikeIcon.setImageResource(R.drawable.ic_favorite_false);
        }

        ivLikeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isFavorite = tweet.isFavorited();

                ImageView ivLikeIcon = vhViewHolder.ivLikeIcon;
                tweet.setFavorited(!isFavorite);

                if ( tweet.isFavorited() ) {
                    ivLikeIcon.setImageResource(R.drawable.ic_favorite_true);
                } else {
                    ivLikeIcon.setImageResource(R.drawable.ic_favorite_false);
                }

                Toast.makeText(getContext(), "LIke Icon click", Toast.LENGTH_SHORT).show();
            }
        });
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

    public interface Communicator {
        void onProfileLinkClickListener(String screenName);
    }

}
