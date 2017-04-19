package com.hmmelton.appsheet.helpers;

import com.hmmelton.appsheet.interfaces.AppSheetService;
import com.hmmelton.appsheet.interfaces.GetUserIdsCallback;
import com.hmmelton.appsheet.interfaces.GetUsersCallback;
import com.hmmelton.appsheet.models.User;
import com.hmmelton.appsheet.models.UserList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
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
        getUserIds(new ArrayList<>(), null, userIds -> {
            if (userIds != null) {
                // Data was returned
                getUsers(userIds, callback);
            } else {
                // Response was null
                callback.onComplete(null);
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

    /**
     * This method fetches users from the AppSheet sample web service.
     * @param ids List of ID's of users to fetch
     * @param callback Callback used to return data
     */
    private void getUsers(List<Integer> ids, final GetUsersCallback callback) {
        // Create tree map to automatically sort users by age
        final Map<Integer, User> sortedAgeUsers = new TreeMap<>();
        // Create list of Observables to run concurrently
        List<Observable<User>> observables = new ArrayList<>();
        for (int id : ids) {
            observables.add(mService.getUserDetail(id)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()));
        }
        // Combine Observables and run concurrently
        Observable.concat(observables)
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(User value) {
                        // One of the threads has finished -- add value to list
                        if (validatePhoneNumber(value.getNumber())) {
                            // Phone number is valid, so add user to list
                            sortedAgeUsers.put(value.getAge(), value);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        // All threads have finished running
                        // Sort top 5 users by age and return to calling method
                        callback.onComplete(handleGetUsersResponse(sortedAgeUsers));
                    }
                });
    }

    /**
     * This method takes the top 5 users and sorts them by name.
     * @param sortedAgeUsers List of Users, sorted by age
     * @return List of top 5 Users from input list, sorted by name
     */
    private List<User> handleGetUsersResponse(Map<Integer, User> sortedAgeUsers) {
        List<User> result = new ArrayList<>();
        // New map to sort users by name
        int index = 5;
        for (Map.Entry<Integer, User> entry : sortedAgeUsers.entrySet()) {
            if (index <= 0) {
                // All desired Users have been added to list
                break;
            }
            // Add element to result list
            result.add(entry.getValue());
            index--;
        }
        // Sort and return values
        Collections.sort(result, (o1, o2) -> o1.getName().compareTo(o2.getName()));
        return result;
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
