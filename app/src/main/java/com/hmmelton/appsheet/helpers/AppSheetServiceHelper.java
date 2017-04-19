package com.hmmelton.appsheet.models;

import com.hmmelton.appsheet.interfaces.AppSheetService;
import com.hmmelton.appsheet.interfaces.GetUserIdsCallback;
import com.hmmelton.appsheet.interfaces.GetUsersCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by harrison on 4/18/17.
 * This is a helper class for using AppSheetService to query the AppSheet sample web service.
 */

public class AppSheetServiceHelper {

    // Service used to send requests to AppSheet web service
    private AppSheetService mService;

    /**
     * Constructor
     * @param service instance of AppSheetService used to make calls to AppSheet web service.
     */
    public AppSheetServiceHelper(AppSheetService service) {
        mService = service;
    }

    /**
     * This method finds the 10 youngest users with valid US phone numbers.
     * @param callback callback used to return data
     */
    public void getYoungestPhoneUsers(final GetUsersCallback callback) {
        getUserIds(new ArrayList<Integer>(), null, new GetUserIdsCallback() {
            @Override
            public void onComplete(List<Integer> userIds) {
                if (userIds != null) {
                    // Data was returned
                    getUsers(userIds, callback);
                } else {
                    // Response was null
                    callback.onComplete(null);
                }
            }
        });
    }

    /**
     * This method fetches all user ID's from the AppSheet sample web service.
     * @param list Current list of integer ID's
     * @param token token used to fetch subsequent sets of ID's
     * @param callback callback used to return data
     */
    private void getUserIds(final List<Integer> list, String token,
                            final GetUserIdsCallback callback) {
        Call<UserList> request = mService.getList(token);
        // Make call to AppSheet web service
        request.enqueue(new Callback<UserList>() {
            @Override
            public void onResponse(Call<UserList> call, Response<UserList> response) {
                if (response != null) {
                    // Response was returned
                    UserList users = response.body();
                    list.addAll(users.getList()); // Add all users to current list
                    if (users.getToken() == null) {
                        // All user ID's have been retrieved
                        callback.onComplete(list);
                    } else {
                        // There are more user ID's to fetch
                        getUserIds(list, users.getToken(), callback);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserList> call, Throwable t) {
                // Call failed, return null
                callback.onComplete(null);
            }
        });
    }

    private void getUsers(List<Integer> ids, GetUsersCallback callback) {
        for (int id : ids) {
            // Fetch info on all users with ID's in list
            Call<User> request = mService.getUserDetail(id);
            request.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response != null && response.body() != null) {

                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });
        }
    }
}
