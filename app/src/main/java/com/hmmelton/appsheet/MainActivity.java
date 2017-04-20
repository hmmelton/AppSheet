package com.hmmelton.appsheet;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hmmelton.appsheet.adapters.UserInfoAdapter;
import com.hmmelton.appsheet.helpers.AppSheetServiceHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    // RecyclerView of users
    @BindView(R.id.rv_main_activity)
    RecyclerView mRecyclerView;
    // Progress bar to show while loading users
    @BindView(R.id.pb_main_activity)
    ProgressBar mProgressBar;

    // Adapter for Activity's RecyclerView
    private UserInfoAdapter mAdapter;
    // Root SwipeRefreshLayout
    private SwipeRefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setUpRecyclerView();
        setUpSwipeRefreshLayout();
        // Display progress bar
        mProgressBar.setVisibility(View.VISIBLE);
        getUsers();
    }

    /**
     * This method fetches the users needed to populate the layout's RecyclerView.
     */
    private void getUsers() {
        AppSheetServiceHelper serviceHelper = new AppSheetServiceHelper();
        serviceHelper.getYoungestPhoneUsers(users -> {
            if (mProgressBar.getVisibility() == View.VISIBLE) {
                // Hide progress bar, if visible
                mProgressBar.setVisibility(View.GONE);
            }
            if (mRefreshLayout.isRefreshing()) {
                // Remove refresh icon
                mRefreshLayout.setRefreshing(false);
            }
            if (users == null) {
                // Let user know there was no data
                Toast.makeText(MainActivity.this, R.string.error_pulling_user_data,
                        Toast.LENGTH_LONG).show();
            } else {
                // Add items to RecyclerView's adapter
                mAdapter.addAll(users);
            }
        });
    }

    /**
     * This method sets up the Activity's RecyclerView.
     */
    private void setUpRecyclerView() {
        // Set RecyclerView's LinearLayoutManager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Set RecyclerView's adapter, passing it an empty List on initialization
        mAdapter = new UserInfoAdapter(new ArrayList<>());
        mRecyclerView.setAdapter(mAdapter);
        // Add divider separator
        RecyclerView.ItemDecoration decoration =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(decoration);
    }

    /**
     * This method sets up the Activity's SwipeRefreshLayout.
     */
    private void setUpSwipeRefreshLayout() {
        mRefreshLayout = (SwipeRefreshLayout)
                ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        mRefreshLayout.setOnRefreshListener(() -> {
            mAdapter.clear();
            getUsers();
        });
    }
}
