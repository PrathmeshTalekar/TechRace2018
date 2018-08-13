package com.techrace.spit.techrace2018;

public class User {
    public int level;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(int level) {
        this.level=level;
    }

}
