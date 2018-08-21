package com.techrace.spit.techrace2018;

public class LeaderBoardOBject {
    public String Name;
    public int Level, Points;
    public long Time;


    public LeaderBoardOBject(String name, int level, int points, long timeinMillis) {
        this.Level = level;
        this.Name = name;
        this.Points = points;
        this.Time = timeinMillis;

    }
}
