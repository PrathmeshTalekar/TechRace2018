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
import android.widget.ListView;

import com.techrace.spit.techrace2018.CluesAdapter;
import com.techrace.spit.techrace2018.R;

import java.util.ArrayList;

public class CluesFragment extends Fragment {
    View myView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.clues_layout, container, false);
        myView = rootView;

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.techrace.spit.techrace2018", Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor=sharedPreferences.edit();
        ArrayList<String> clueList = new ArrayList<>();
        int i;
        for (i = 1; i <= 16; i++) {
            Log.i("clueList", sharedPreferences.getString("Clue " + i, "Not Found"));
            clueList.add(sharedPreferences.getString("Clue " + i, ""));

        }
//        String clue12=sharedPreferences.getString("Clue 12","abc");
//        if (i<12 && !clue12.equals("abc")){
//            clueList.add(clue12);
//        }

        CluesAdapter adapter = new CluesAdapter(getActivity(), clueList);
        ListView listView = (ListView) myView.findViewById(R.id.list);
        listView.setAdapter(adapter);
    }
}
