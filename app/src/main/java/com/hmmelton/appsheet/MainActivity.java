package com.hmmelton.appsheet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hmmelton.appsheet.adapters.UserInfoAdapter;
import com.hmmelton.appsheet.helpers.AppSheetServiceHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    // RecyclerView of users
    @BindView(R.id.rv_main_activity)
    RecyclerView mRecyclerView;
    // Progress bar to show while loading users
    @BindView(R.id.pb_main_activity)
    ProgressBar mProgressBar;

    // Adapter for Activity's RecyclerView
    private UserInfoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setUpRecyclerView();
        // Display progress bar
        mProgressBar.setVisibility(View.VISIBLE);
        getUsers();
    }

    /**
     * This method fetches the users needed to populate the layout's RecyclerView.
     */
    private void getUsers() {
        Log.e(TAG, "getUsers");
        AppSheetServiceHelper serviceHelper = new AppSheetServiceHelper();
        Log.e(TAG, "service helper created");
        serviceHelper.getYoungestPhoneUsers(users -> {
            mProgressBar.setVisibility(View.GONE);
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
     * This method sets up the Activity's RecyclerView
     */
    private void setUpRecyclerView() {
        // Set RecyclerView's LinearLayoutManager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Set RecyclerView's adapter, passing it an empty List on initialization
        mAdapter = new UserInfoAdapter(new ArrayList<>());
        mRecyclerView.setAdapter(mAdapter);
    }
}
