package com.techrace.spit.techrace2018;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LocationsList {


    /**
     * An array of sample (dummy) items.
     */
    public static final List<String> ITEMS = new ArrayList<>();
    private static SharedPreferences pref;

    static {

        ITEMS.add("SPIT Quad");
        ITEMS.add("Andheri Recreation Centre");
        ITEMS.add("Versova Beach");
        ITEMS.add("JW Mariott Hotel");
        ITEMS.add("Pali Hill");
        ITEMS.add("Guru Nanak Park");
        ITEMS.add("Mumbai University");
        ITEMS.add("Sion Fort");
        ITEMS.add("Deonar Dumping Ground");
        ITEMS.add("IIT Powai");
        ITEMS.add("Powai Lake");
        ITEMS.add("Seven Hills Hospital");
        ITEMS.add("Mahakali caves");
        ITEMS.add("Azad Nagar Metro");
        ITEMS.add("Bhavans College");
    }

    public static class Location {
        public final int id;
        public final String name;
        public final String details;
        public int image;

        public String latitude;
        public String longitude;

        public Location(int id, String name, String details, int imageRes, String latitude, String longitude) {
            this.id = id;
            this.name = name;
            this.details = details;
            this.image = imageRes;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public String toString() {
            return name;
        }
    }

//        public static String getUsernameHashedKey(Context context)
//        {
//            pref = context.getSharedPreferences(AppConstants.PREFS, IntroActivity.MODE_PRIVATE);
//            final String name = pref.getString(AppConstants.PREFS_USERNAME,"Testing 123");
//            return name;
//        }


}
