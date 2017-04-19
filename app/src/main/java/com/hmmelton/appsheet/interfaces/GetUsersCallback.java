package com.hmmelton.appsheet.interfaces;

import com.hmmelton.appsheet.models.User;

import java.util.List;

/**
 * Created by harrison on 4/18/17.
 * This interface is a callback used while fetching users from the AppSheet web service.
 */

public interface GetUsersCallback {
    void onComplete(List<User> users);
}
