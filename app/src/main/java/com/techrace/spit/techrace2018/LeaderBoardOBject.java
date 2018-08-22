package com.techrace.spit.techrace2018;

public class LeaderBoardOBject {
    public String Name;
    public int Level, Points;
    public long Time;
    public int Cooldown;
    public String Uid;

    public LeaderBoardOBject(String name, int level, int points, long timeinMillis, int cool, String uid) {
        this.Level = level;
        this.Name = name;
        this.Points = points;
        this.Time = timeinMillis;
        this.Cooldown = cool;
        this.Uid = uid;

    }
}
