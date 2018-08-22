package com.techrace.spit.techrace2018;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CluesFragment extends Fragment {
    View myView;
    private DatabaseReference mDatabase;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.clues_layout, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final ArrayList<Clue> clue = new ArrayList<Clue>();
        clue.add(new Clue("a"));
        clue.add(new Clue("b"));
        clue.add(new Clue("c"));
        clue.add(new Clue("d"));
        clue.add(new Clue("e"));
        clue.add(new Clue("f"));
        clue.add(new Clue("g"));
        clue.add(new Clue("h"));
        clue.add(new Clue("i"));


        CluesAdapter adapter = new CluesAdapter(getActivity(), clue);
        ListView listView = (ListView) rootView.findViewById(R.id.list);
        listView.setAdapter(adapter);
        return rootView;
    }
}
