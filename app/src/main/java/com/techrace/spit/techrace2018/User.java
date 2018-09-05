package com.techrace.spit.techrace2018;

public class User {
    public String name, password, email, contact;
    public int level, cooldown, points, hintsLeft, waited, route;
    public boolean jackpot;
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String password, String email, String contact, int route) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.level = 1;
        this.cooldown = 0;
        this.points = 0;
        this.contact = contact;
        this.hintsLeft = 3;
        this.waited = 0;
        this.jackpot = false;
        this.route = route;
    }

}
