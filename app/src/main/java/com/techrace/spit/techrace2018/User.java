package com.techrace.spit.techrace2018;

public class User {
    public String name, password, email, contact;
    public int level, power1, power2, power3, power4, cooldown, points;
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String password, String email, String contact) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.level = 1;
        this.power1 = 0;
        this.power2 = 0;
        this.power3 = 0;
        this.power4 = 0;
        this.cooldown = 0;
        this.points = 0;
        this.contact = contact;
    }

}
