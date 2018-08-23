package com.techrace.spit.techrace2018;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class LocationsFragment extends Fragment {
    private DatabaseReference mDatabase;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.locations_layout, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final ArrayList<Location> location = new ArrayList<Location>();
        int i;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.techrace.spit.techrace2018", Context.MODE_PRIVATE);
        for (i = 1; i <= 16; i++) {
            Log.i("locationList", sharedPreferences.getString("Location " + i, "not found"));
            location.add(new Location(sharedPreferences.getString("Location " + i, "")));

        }

        LocationsAdapter adapter = new LocationsAdapter(getActivity(), location);
        ListView listView = (ListView) rootView.findViewById(R.id.list);
        listView.setAdapter(adapter);
        return rootView;
    }

}
