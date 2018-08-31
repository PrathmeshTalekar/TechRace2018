package com.techrace.spit.techrace2018;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Build;
import android.os.Bundle;


import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;

import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.altbeacon.beacon.Beacon;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;

import static android.content.Context.ALARM_SERVICE;
import static com.techrace.spit.techrace2018.MainActivity.beacon;
import static com.techrace.spit.techrace2018.MainActivity.cooldown;
import static com.techrace.spit.techrace2018.MainActivity.event;
import static com.techrace.spit.techrace2018.MainActivity.globalMenu;
import static com.techrace.spit.techrace2018.MainActivity.points;
//import static com.techrace.spit.techrace2018.MainActivity.prefEditor;
//import static com.techrace.spit.techrace2018.MainActivity.pref;
import static com.techrace.spit.techrace2018.MainActivity.pref;
import static com.techrace.spit.techrace2018.MainActivity.prefEditor;
import static com.techrace.spit.techrace2018.MainActivity.routeNo;
import static com.techrace.spit.techrace2018.MainActivity.timerOn;

import java.text.DateFormat;
import java.util.Date;
import java.util.prefs.Preferences;



public class HomeFragment extends Fragment {

    static final String TAG = "MonitoringActivity";
    static View myView;
    static TextView clueTextView, hintTextView, timerTextView;
    MenuItem myItem;
    static DatabaseReference UserDatabaseReference;
    static FirebaseDatabase firebaseDatabase;
    static FirebaseAuth homeFragAuth = MainActivity.mAuth;
    static String UID;
    static int level = 1;
    static String levelString;
    static String NSID;
    static Location clueLocation;
    static String volunteerPassword;
    static String name;
    static RelativeLayout clueRelativeLayout;
    static String locName;
    static Button hintButton;
    int hintsLeft;
    long lastClickTime;
    ;
    static CardView hintView, noteView;
    @Override
    public void onStop() {
        super.onStop();

    }

    public void updateClue() {
        // clueLocation = new Location("");

        firebaseDatabase = FirebaseDatabase.getInstance();
        routeNo = pref.getInt("Route", 1);
        if (homeFragAuth.getCurrentUser() != null) {


            UID = homeFragAuth.getCurrentUser().getUid();
            Log.i("UID", UID);


            UserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);
            UserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    level = dataSnapshot.child("level").getValue(Integer.class);
                    Log.i("LEVELL", String.valueOf(level));
                    prefEditor = pref.edit();
                    prefEditor.putInt(AppConstants.levelPref, level);
                    points = dataSnapshot.child("points").getValue(Integer.class);
                    prefEditor.putInt("Points", points);
                    if (globalMenu != null) {
                        myItem = globalMenu.findItem(R.id.pointsBox);
                        myItem.setTitle("" + points);
                    }
                    name = (String) dataSnapshot.child("name").getValue();
                    DatabaseReference UserDatabaseReference1 = FirebaseDatabase.getInstance().getReference().child("Route " + MainActivity.routeNo);
                    UserDatabaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            levelString = dataSnapshot.child("Location " + level).child("Clue").getValue(String.class);
                            Log.i("lvlstrnh", levelString);
                            prefEditor = pref.edit();
                            prefEditor.putString(AppConstants.cluePref, levelString).apply();
                            String locName = dataSnapshot.child("Location " + (level - 1)).child("Name").getValue(String.class);
                            prefEditor = pref.edit();
                            prefEditor.putString("Location " + (level - 1), locName).apply();

                            NSID = dataSnapshot.child("Location " + level).child("NSID").getValue(String.class);
                            Log.i("NSID", NSID);
                            clueLocation.setLatitude(Double.parseDouble(dataSnapshot.child("Location " + String.valueOf(level)).child("Latitude").getValue(String.class)));
                            clueLocation.setLongitude(Double.parseDouble(dataSnapshot.child("Location " + level).child("Longitude").getValue(String.class)));
                            Log.i("LOC LAT", String.valueOf(clueLocation.getLatitude()));
                            clueTextView.setText(levelString);
                            clueRelativeLayout.setBackgroundColor(MainActivity.resources.getColor(R.color.coldBlue));
                            if (pref.getString(AppConstants.hintPref, "").equals("")) {
                                hintTextView.setVisibility(View.INVISIBLE);
                            } else {
                                hintTextView.setVisibility(View.VISIBLE);
                                hintTextView.setText(pref.getString(AppConstants.hintPref, ""));
                            }
                            beacon = true;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        clueTextView = myView.findViewById(R.id.clue_text);
        //pointsTextView = myView.findViewById(R.id.pointsTextView);
        timerTextView = myView.findViewById(R.id.timerTextView);
        clueRelativeLayout = myView.findViewById(R.id.clueLayout);
        hintButton = myView.findViewById(R.id.hintButton);
        hintTextView = myView.findViewById(R.id.hintTextView);
        hintView = myView.findViewById(R.id.hint_view);
        noteView = myView.findViewById(R.id.noteView);
        if (!pref.getString("Hint", "").equals("")) {
            hintTextView.setText(pref.getString("Hint", ""));
        } else {
            hintView.setVisibility(View.INVISIBLE);
        }
        clueTextView.setText(pref.getString(AppConstants.cluePref, "Connect To Internet"));
        if (globalMenu != null) {
            myItem = globalMenu.findItem(R.id.pointsBox);
            myItem.setTitle(String.valueOf(pref.getInt("Points", 0)));
            // pointsTextView.setText(String.valueOf(pref.getInt("Points", 0)));
        }

        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();
                if (pref.getString(AppConstants.hintPref, "abc").equals("")) {
                    final DatabaseReference hintRef = FirebaseDatabase.getInstance().getReference().child("Users").child(UID).child("hintsLeft");
                    hintRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            hintsLeft = dataSnapshot.getValue(Integer.class);
                            if (hintsLeft <= 0) {
                                Toast.makeText(getActivity(), "No Hints Left", Toast.LENGTH_SHORT).show();
                            } else if (hintsLeft == 3) {
                                if (points >= AppConstants.hint1Price) {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                    alertDialogBuilder.setCancelable(false)
                                            .setTitle("Are you sure?")
                                            .setMessage("Do you want to buy a HINT for " + AppConstants.hint1Price + " points?")
                                            .setNegativeButton("No", null)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    hintView.setVisibility(View.VISIBLE);
                                                    hintTextView.setVisibility(View.VISIBLE);
                                                    DatabaseReference hintReference = FirebaseDatabase.getInstance().getReference().child("Route " + MainActivity.routeNo).child("Location " + String.valueOf(level)).child("Hint");
                                                    hintReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            hintButton.setEnabled(false);
                                                            DatabaseReference powerReference1 = FirebaseDatabase.getInstance().getReference();
                                                            Log.i("point hint", "" + MainActivity.points);
                                                            powerReference1.child("Users").child(UID).child("points")
                                                                    .setValue(MainActivity.points - AppConstants.hint1Price);
                                                            String hint = dataSnapshot.getValue(String.class);
                                                            powerReference1.child("Users").child(UID).child("hintsLeft").setValue(hintsLeft - 1);
                                                            prefEditor = pref.edit();
                                                            prefEditor.putString(AppConstants.hintPref, hint).apply();
                                                            hintTextView.setText(hint);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
                                            }).show();

                                } else {
                                    Toast.makeText(getActivity(), "Not Enough Points", Toast.LENGTH_SHORT).show();
                                }
                            } else if (hintsLeft == 2) {
                                if (points >= AppConstants.hint2Price) {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                    alertDialogBuilder.setCancelable(false)
                                            .setTitle("Are you sure?")
                                            .setMessage("Do you want to buy a HINT for " + AppConstants.hint2Price + " points?")
                                            .setNegativeButton("No", null)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    hintView.setVisibility(View.VISIBLE);
                                                    hintTextView.setVisibility(View.VISIBLE);
                                                    DatabaseReference hintReference = FirebaseDatabase.getInstance().getReference().child("Route " + MainActivity.routeNo).child("Location " + String.valueOf(level)).child("Hint");
                                                    hintReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            hintButton.setEnabled(false);
                                                            DatabaseReference powerReference1 = FirebaseDatabase.getInstance().getReference();
                                                            Log.i("point hint", "" + MainActivity.points);
                                                            powerReference1.child("Users").child(UID).child("points")
                                                                    .setValue(MainActivity.points - AppConstants.hint2Price);
                                                            String hint = dataSnapshot.getValue(String.class);
                                                            powerReference1.child("Users").child(UID).child("hintsLeft").setValue(hintsLeft - 1);
                                                            prefEditor = pref.edit();
                                                            prefEditor.putString(AppConstants.hintPref, hint).apply();
                                                            hintTextView.setText(hint);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
                                            }).show();

                                } else {
                                    Toast.makeText(getActivity(), "Not Enough Points", Toast.LENGTH_SHORT).show();
                                }
                            } else if (hintsLeft == 1) {
                                if (points >= AppConstants.hint3Price) {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                    alertDialogBuilder.setCancelable(false)
                                            .setTitle("Are you sure?")
                                            .setMessage("Do you want to buy a HINT for " + AppConstants.hint3Price + " points?")
                                            .setNegativeButton("No", null)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    hintView.setVisibility(View.VISIBLE);
                                                    hintTextView.setVisibility(View.VISIBLE);
                                                    DatabaseReference hintReference = FirebaseDatabase.getInstance().getReference().child("Route " + MainActivity.routeNo).child("Location " + String.valueOf(level)).child("Hint");
                                                    hintReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            hintButton.setEnabled(false);
                                                            DatabaseReference powerReference1 = FirebaseDatabase.getInstance().getReference();
                                                            Log.i("point hint", "" + MainActivity.points);
                                                            powerReference1.child("Users").child(UID).child("points")
                                                                    .setValue(MainActivity.points - AppConstants.hint3Price);
                                                            String hint = dataSnapshot.getValue(String.class);
                                                            powerReference1.child("Users").child(UID).child("hintsLeft").setValue(hintsLeft - 1);
                                                            prefEditor = pref.edit();
                                                            prefEditor.putString(AppConstants.hintPref, hint).apply();
                                                            hintTextView.setText(hint);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
                                            }).show();
                                } else {
                                    Toast.makeText(getActivity(), "Not Enough Points", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Already Used", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.home_layout, container, false);

        return myView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // sharedPreferences=getActivity().getSharedPreferences("com.techrace.spit.techrace2018",Context.MODE_PRIVATE);
        if (pref.getInt(AppConstants.levelPref, -1) != level) {
            updateClue();
        }
        hintTextView.setText(pref.getString("Hint", ""));
        //else{
        //    pointsTextView.setText(String.valueOf(MainActivity.points));
        //}
        //    if (MainActivity.beaconManager.isBound(this)) MainActivity.beaconManager.setBackgroundMode(false);
    }


}
