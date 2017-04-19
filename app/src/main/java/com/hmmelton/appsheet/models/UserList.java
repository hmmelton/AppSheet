package com.hmmelton.appsheet.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by harrison on 4/18/17.
 * This model represents a list of users.
 */

public class UserList {

    @SerializedName("result")
    private List<Integer> mList; // List of user ID's
    @SerializedName("token")
    private String mToken; // Token used to retrieve more user ID's, if any more exist

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
