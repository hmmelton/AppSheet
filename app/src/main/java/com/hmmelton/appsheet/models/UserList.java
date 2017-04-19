package com.hmmelton.appsheet.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by harrison on 4/18/17.
 * This model represents a list of users.
 */

public class UserList {

    // List of user ID's
    @SerializedName("result")
    private List<Integer> mList;
    // Token used to retrieve more user ID's, if any more exist
    @SerializedName("token")
    private String mToken;

    /**
     * Generic constructor
     */
    public UserList() {}

    /* Getter methods */

    public List<Integer> getList() {
        return mList;
    }

    public String getToken() {
        return mToken;
    }
}
