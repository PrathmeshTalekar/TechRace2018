package com.techrace.spit.techrace2018;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.techrace.spit.techrace2018.HomeFragment.UID;

import static com.techrace.spit.techrace2018.MainActivity.points;


public class PowerCardsFragment extends Fragment {
    View myView;
    static int twoORfour = 0;
    LinearLayout plusTwo, plusFour, unlockClue;
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
        unlockClue = myView.findViewById(R.id.unlockClue);
        plusTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (points >= AppConstants.plusTwoPrice) {
                    twoORfour = 2;
                    LeaderboardActivity.selectUser = true;
                    Intent i1 = new Intent(getActivity(), LeaderboardActivity.class);
                    i1.putExtra("SELECT USER", "TRUE");
                    startActivity(i1);


                } else {
                    Toast.makeText(getActivity(), "Not Enough Points", Toast.LENGTH_SHORT).show();
                }
            }
        });
        plusFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (points >= AppConstants.plusFourPrice) {
                    twoORfour = 4;
                    LeaderboardActivity.selectUser = true;
                    Intent i1 = new Intent(getActivity(), LeaderboardActivity.class);
                    i1.putExtra("SELECT USER", "TRUE");
                    startActivity(i1);


                } else {
                    Toast.makeText(getActivity(), "Not Enough Points", Toast.LENGTH_SHORT).show();
                }
            }

        });
        unlockClue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.points >= AppConstants.unlockACluePrice) {

                }
            }
        });
    }
}
