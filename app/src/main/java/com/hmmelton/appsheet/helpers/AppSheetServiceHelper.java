package com.hmmelton.appsheet.helpers;

import android.util.Log;

import com.hmmelton.appsheet.interfaces.AppSheetService;
import com.hmmelton.appsheet.interfaces.GetUserIdsCallback;
import com.hmmelton.appsheet.interfaces.GetUsersCallback;
import com.hmmelton.appsheet.models.User;
import com.hmmelton.appsheet.models.UserList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

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

    private void getUsers(List<Integer> ids, final GetUsersCallback callback) {
        final Map<Integer, User> sortedAgeUsers = new TreeMap<>();
        // Create CountDownLatch to wait for all async threads to finish
        final CountDownLatch latch = new CountDownLatch(ids.size());
        for (int id : ids) {
            // Fetch info on all users with ID's in list
            Call<User> request = mService.getUserDetail(id);
            request.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response != null && response.body() != null) {
                        // Data was returned
                        User user = response.body();
                        if (validatePhoneNumber(user.getNumber())) {
                            // Phone number is valid, so add to sorted map
                            sortedAgeUsers.put(user.getAge(), user);
                            // Notify latch that this thread has completed
                            latch.countDown();
                        }
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    // Error fetching user data
                    latch.countDown();
                }
            });
        }
        // Notify latch to wait until all threads are complete to execute next section of code
        try {
            latch.await();
        } catch (InterruptedException e) {
            Log.e("APSHelper", e.toString());
        }
        callback.onComplete(handleGetUsersResponse(sortedAgeUsers));
    }

    private List<User> handleGetUsersResponse(Map<Integer, User> sortedUsers) {

    }

    /**
     * This method checks whether or not a phone number is valid according to US standards.
     * @param number phone number to be checked
     * @return boolean representing whether or not number is valid
     */
    private boolean validatePhoneNumber(String number) {
        return number.matches("^(\\([0-9]{3}\\) ?|[0-9]{3}[- ]?)?[0-9]{3}-?[0-9]{4}$");
    }
}
