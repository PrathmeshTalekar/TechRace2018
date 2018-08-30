package com.techrace.spit.techrace2018;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techrace.spit.techrace2018.LeaderboardActivity;

import static com.techrace.spit.techrace2018.HomeFragment.UID;

import static com.techrace.spit.techrace2018.MainActivity.points;


public class PowerCardsFragment extends Fragment {
    View myView;
    static int twoORfour = 0;

    TextView plusTwo, plusFour, unlockClue;
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
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.techrace.spit.techrace2018", Context.MODE_PRIVATE);
        Log.i("clue12", sharedPreferences.getString("Clue 12", "abc"));
        if (sharedPreferences.getString("Clue 12", "abc").equals("abc")) {
            unlockClue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MainActivity.points >= AppConstants.unlockACluePrice) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity())
                                .setCancelable(false)
                                .setMessage("Do you wamt to unlock a clue?")
                                .setTitle("Are you sure?")
                                .setNegativeButton("No", null)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DatabaseReference unlockClueRef = FirebaseDatabase.getInstance().getReference().child("Route " + MainActivity.routeNo).child("Location 12").child("Clue");
                                        unlockClueRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                String s = dataSnapshot.getValue(String.class);
                                                DatabaseReference powerReference1 = FirebaseDatabase.getInstance().getReference();
                                                powerReference1.child("Users").child(UID).child("points")
                                                        .setValue(MainActivity.points - AppConstants.unlockACluePrice);
                                                SharedPreferences share = getActivity().getSharedPreferences("com.techrace.spit.techrace2018", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = share.edit();
                                                edit.putString("Clue 12", s).apply();
                                                unlockClue.setClickable(false);
                                                Log.i("Clue 12", s);
                                                Toast.makeText(getActivity(), "Unlocked", Toast.LENGTH_LONG).show();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });
                        alert.show();


                    } else {
                        Toast.makeText(getActivity(), "Not Enough Points", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), "Already Unlocked", Toast.LENGTH_SHORT).show();
        }
    }
}
