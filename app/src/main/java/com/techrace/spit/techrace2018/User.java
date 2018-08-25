package com.techrace.spit.techrace2018;

public class User {
    public String name, password, email, contact;
    public int level, cooldown, points, hintsLeft;
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String password, String email, String contact) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.level = 1;
        this.cooldown = 0;
        this.points = 0;
        this.contact = contact;
        this.hintsLeft = 3;
    }

}
