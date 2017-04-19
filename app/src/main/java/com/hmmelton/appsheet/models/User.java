package com.hmmelton.appsheet.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by harrison on 4/18/17.
 * This model holds the data of an individual AppSheet user.
 */

public class User {

    @SerializedName("id")
    int mId;
    @SerializedName("age")
    int mAge;
    @SerializedName("name")
    String mName;
    @SerializedName("number")
    String mNumber;

    /**
     * Generic constructor
     */
    public User() {}

    /* Getter methods */

    public int getId() {
        return mId;
    }

    public int getAge() {
        return mAge;
    }

    public String getName() {
        return mName;
    }

    public String getNumber() {
        return mNumber;
    }
}
