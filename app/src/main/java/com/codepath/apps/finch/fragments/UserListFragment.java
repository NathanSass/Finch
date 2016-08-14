package com.codepath.apps.finch.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.finch.R;
import com.codepath.apps.finch.adapters.UserAdapter;
import com.codepath.apps.finch.models.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by nathansass on 8/13/16.
 */
public class UserListFragment extends Fragment {

    private UserAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    @BindView(R.id.rvUsers)
    RecyclerView rvUsers;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<User> users;

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_users_list, parent, false );
        // BUGBUG: May need to change this
        unbinder = ButterKnife.bind(this, v);

        setUpRecyclerView();

//        initEndlessScrolling();

//        setTweetItemClickListener();

//        setupSwipeRefreshListener();

//        populateTimelineFromDB();

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setUpRecyclerView() {
        users = new ArrayList<>();
        adapter = new UserAdapter(getActivity(), users );

        rvUsers.setAdapter(adapter);
        linearLayoutManager = new LinearLayoutManager(getActivity());

        rvUsers.setLayoutManager(linearLayoutManager);
    }


    public void addAll(List<User> users) {
        adapter.clear();
        adapter.addAll(users);
        adapter.notifyDataSetChanged();
    }

}
