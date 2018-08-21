package com.techrace.spit.techrace2018;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import static com.techrace.spit.techrace2018.HomeFragment.points;


public class PowerCardsFragment extends Fragment {
    View myView;
    Button plusTwo, plusFour;

    public PowerCardsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_power_cards, container, false);
        return myView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        plusTwo = myView.findViewById(R.id.plusTwo);
        plusFour = myView.findViewById(R.id.plusFour);
        plusTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (points >= 20) {
                    Intent i = new Intent(getActivity(), LeaderboardActivity.class);
                    i.putExtra("SELECT USER", "TRUE");
                    startActivityForResult(i, 1);
                    //   onActivityResult(1,,);
                } else {
                    Toast.makeText(getActivity(), "Not Enough Points", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
