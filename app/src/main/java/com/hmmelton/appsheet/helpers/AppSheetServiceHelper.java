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
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by harrison on 4/18/17.
 * This is a helper class for using AppSheetService to query the AppSheet sample web service.
 */

public class AppSheetServiceHelper {

    // Service used to send requests to AppSheet web service
    private AppSheetService mService;

    /**
     * Constructor
     */
    public AppSheetServiceHelper() {
        mService = setUpRetrofit();
    }

    /**
     * This method sets up Retrofit and the AppSheet web service.
     */
    private AppSheetService setUpRetrofit() {
        // Create Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://appsheettest1.azurewebsites.net/sample/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        // Use Retrofit instance to create instance of AppSheetService
        return retrofit.create(AppSheetService.class);
    }

    /**
     * This method finds the 10 youngest users with valid US phone numbers.
     * @param callback callback used to return data
     */
    public void getYoungestPhoneUsers(final GetUsersCallback callback) {
        getUserIds(new ArrayList<>(), null, userIds -> {
            if (userIds != null) {
                // Data was returned
                getUsers(userIds, users -> callback.onComplete(handleGetUsersResponse(users)));
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
                        callback.onComplete(new ArrayList<>(sortedAgeUsers.values()));
                    }
                });
    }

    /**
     * This method takes the top 5 users and sorts them by name.
     * @param sortedAgeUsers List of Users, sorted by age
     * @return List of top 5 Users from input list, sorted by name
     */
    private List<User> handleGetUsersResponse(List<User> sortedAgeUsers) {
        // Create list from first 5 values or age-sorted list
        List<User> result = sortedAgeUsers.subList(0, 5);
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
        return number.matches("^(\\([0-9]{3}\\) ?|[0-9]{3}[- ]?)?[0-9]{3}[- ]?[0-9]{4}$");
    }
}
