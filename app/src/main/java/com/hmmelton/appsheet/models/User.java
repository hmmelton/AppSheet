package com.hmmelton.appsheet.models;

/**
 * Created by harrison on 4/18/17.
 * This model holds the data of an individual AppSheet user.
 */

public class User {

    private int mId, mAge;
    private String mName, mNumber;

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
