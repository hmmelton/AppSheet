package com.hmmelton.appsheet.interfaces;

import com.hmmelton.appsheet.models.User;
import com.hmmelton.appsheet.models.UserList;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by harrison on 4/18/17.
 * This interface describes available interactions with the AppSheet sample web service.
 */

public interface AppSheetService {

    /**
     * This method fetches a list of user ID's.
     * @param token token used to specify set of ID's, if more than one set exists
     * @return Retrofit Call that returns UserList object
     */
    @GET("list")
    Call<UserList> getList(@Query("token") String token);

    /**
     * This method fetches the details of an individual user.
     * @param userId ID of user whose information to fetch
     * @return Retrofit Call that returns User object
     */
    @GET("detail/{user_id}")
    Observable<User> getUserDetail(@Path("user_id") int userId);
}
