package com.codepath.apps.finch.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.finch.R;
import com.codepath.apps.finch.models.User;
import com.codepath.apps.finch.util.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nathansass on 8/12/16.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

        // Provide a direct reference to each of the views within a data item
        // Used to cache the views within the item layout for fast access
        public static class ViewHolder extends RecyclerView.ViewHolder {
            // Your holder should contain a member variable
            // for any view that will be set as you render a row
            @BindView(R.id.ivProfileImage)
            ImageView ivProfileImage;
            @BindView(R.id.tvName)
            TextView tvName;
            @BindView(R.id.tvTagline)
            TextView tvTagline;

            // We also create a constructor that accepts the entire item row
            // and does the view lookups to find each subview
            public ViewHolder(View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }


    /* Viewholder Above */
    private List<User> users;
    private Context context;

    public UserAdapter(Context context, List<User> users) {
        this.users = users;
        this.context = context;
    }

    private Context getContext() {
        return context;
    }

    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_user, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(UserAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        final User user = users.get(position);
        UserAdapter.ViewHolder vhViewHolder = viewHolder;

        /* UI MANIPULATION HERE */
        TextView tvName = vhViewHolder.tvName;
        TextView tvTagline = viewHolder.tvTagline;
        ImageView ivProfileImage = viewHolder.ivProfileImage;

        tvName.setText(user.getName());
        tvTagline.setText(user.getTagline());
        viewHolder.ivProfileImage.setImageResource(0);
        Picasso.with(getContext()).load(user.getProfileImageUrl()).transform(new CircleTransform()).into(ivProfileImage);

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return users.size();
    }

    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<User> newUsers) {
        users.addAll(newUsers);
        notifyDataSetChanged();
    }

}
