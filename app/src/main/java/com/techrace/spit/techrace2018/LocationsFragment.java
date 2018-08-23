package com.techrace.spit.techrace2018;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
        location.add(new Location("location 1"));
        location.add(new Location("location 2"));
        location.add(new Location("location 3"));
        location.add(new Location("location 4"));
        location.add(new Location("location 5"));
        location.add(new Location("location 6"));
        location.add(new Location("location 7"));
        location.add(new Location("location 8"));
        location.add(new Location("location 9"));

        LocationsAdapter adapter = new LocationsAdapter(getActivity(), location);
        ListView listView = (ListView) rootView.findViewById(R.id.list);
        listView.setAdapter(adapter);
        return rootView;
    }

}
