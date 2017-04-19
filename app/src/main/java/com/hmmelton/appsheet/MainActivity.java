package com.hmmelton.appsheet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hmmelton.appsheet.interfaces.AppSheetService;
import com.hmmelton.appsheet.interfaces.GetUsersCallback;
import com.hmmelton.appsheet.helpers.AppSheetServiceHelper;
import com.hmmelton.appsheet.models.User;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    // Base URL for AppSheet sample web service
    @BindString(R.string.base_url)
    String mBaseUrl;

    // RecyclerView of users
    @BindView(R.id.rv_main_activity)
    RecyclerView mRecyclerView;
    // Progress bar to show while loading users
    @BindView(R.id.pb_main_activity)
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Display progress bar
        mProgressBar.setVisibility(View.VISIBLE);
        getUsers();
    }

    /**
     * This method fetches the users needed to populate the layout's RecyclerView.
     */
    private void getUsers() {
        AppSheetServiceHelper serviceHelper = new AppSheetServiceHelper(setUpRetrofit());
        serviceHelper.getYoungestPhoneUsers(new GetUsersCallback() {
            @Override
            public void onComplete(List<User> users) {
                if (users == null) {
                    // Let user know there was no data
                    Toast.makeText(MainActivity.this, R.string.error_pulling_user_data,
                            Toast.LENGTH_LONG).show();
                } else {
                    // TODO: add to RecyclerView
                }
            }
        });
    }

    /**
     * This method sets up Retrofit and the AppSheet web service.
     */
    private AppSheetService setUpRetrofit() {
        // Create Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // Use Retrofit instance to create instance of AppSheetService
        return retrofit.create(AppSheetService.class);
    }
}
