package com.techrace.spit.techrace2018;


import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class LBUpdate implements Comparator<LBUpdate> {

    public String name;
    public int points;
    public int level;
    public long timeInMil;
    ArrayList<LBUpdate> items = new ArrayList<>();
    ArrayList<LBUpdate> finalList = new ArrayList<>();

    public LBUpdate(String name, int points, int level, long timeInMil) {
        this.name = name;
        this.points = points;
        this.level = level;
        this.timeInMil = timeInMil;
    }

    public LBUpdate() {

    }

    ArrayList<LBUpdate> leaderUpdate() {
        items.clear();
        finalList.clear();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Leaderboard");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Log.i("DDDDDD", d.toString());
                    String name = (String) d.child("Name").getValue();
                    int level = d.child("Level").getValue(Integer.class);
                    int points = d.child("Points").getValue(Integer.class);
                    long timeInMil = d.child("Time").getValue(Long.class);
                    items.add(new LBUpdate(name, points, level, timeInMil));
                }

                int maxLevel = 0;
                for (int i = 0; i < items.size(); i++) {
                    int current = items.get(i).level;
                    if (current > maxLevel) {
                        maxLevel = current;
                    }
                }
                Log.i("MAXLEVEL", String.valueOf(maxLevel));
                while (maxLevel > 0) {
                    List<LBUpdate> sameLevelList = new ArrayList<>();
                    for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).level == maxLevel) {
                            sameLevelList.add(items.get(i));
                        }
                    }
                    Log.i("SAMELEVELLIST", sameLevelList.toString());
                    Collections.sort(sameLevelList, new LBUpdate.TimeComparator());
                    Collections.reverse(sameLevelList);
                    finalList.addAll(sameLevelList);
                    // sameLevelList.clear();
                    for (int j = 0; j < finalList.size(); j++) {
                        Log.i("Final LIST LOG", finalList.toString());
                    }
                    maxLevel--;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return finalList;
    }

    @Override
    public int compare(LBUpdate o1, LBUpdate o2) {
        return 0;
    }

    public static class TimeComparator implements Comparator<LBUpdate> {

        @Override
        public int compare(LBUpdate o1, LBUpdate o2) {
            long time1 = o1.timeInMil;
            long time2 = o2.timeInMil;

            if (time1 == time2)
                return 0;
            else if (time1 < time2)
                return 1;
            else
                return -1;
        }
    }
}

