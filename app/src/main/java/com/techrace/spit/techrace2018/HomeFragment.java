package com.techrace.spit.techrace2018;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;


import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Build;
import android.os.Bundle;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;

import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
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
import static com.techrace.spit.techrace2018.MainActivity.points;
//import static com.techrace.spit.techrace2018.MainActivity.prefEditor;
//import static com.techrace.spit.techrace2018.MainActivity.pref;
import static com.techrace.spit.techrace2018.MainActivity.pref;
import static com.techrace.spit.techrace2018.MainActivity.prefEditor;
import static com.techrace.spit.techrace2018.MainActivity.timerOn;

import java.text.DateFormat;
import java.util.Date;
import java.util.prefs.Preferences;



public class HomeFragment extends Fragment {

    static final String TAG = "MonitoringActivity";
    static View myView;
    static TextView clueTextView, pointsTextView, hintTextView, timerTextView;

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
    TextView level1, level2, level3;
    static Button hintButton;
    int hintsLeft;
    @Override
    public void onStop() {
        super.onStop();

    }

    public void updateClue() {
        // clueLocation = new Location("");

        firebaseDatabase = FirebaseDatabase.getInstance();
        if (homeFragAuth.getCurrentUser() != null) {


            UID = homeFragAuth.getCurrentUser().getUid();
            Log.i("UID", UID);


            UserDatabaseReference = FirebaseDatabase.getInstance().getReference();
            UserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    DataSnapshot userDS = dataSnapshot.child("Users").child(UID);
                    level = userDS.child("level").getValue(Integer.class);
//                    if (level == 1) {
//                        level1.setText(AppConstants.level1 + String.valueOf(AppConstants.loc1level1) + " minutes");
//                        level2.setText(AppConstants.level2 + String.valueOf(AppConstants.loc1level2) + " minutes");
//                        level3.setText(AppConstants.level3 + String.valueOf(AppConstants.loc1level3) + " minutes");
//                    } else if (level == 2) {
//                        level1.setText(AppConstants.level1 + String.valueOf(AppConstants.loc2level1) + " minutes");
//                        level2.setText(AppConstants.level2 + String.valueOf(AppConstants.loc2level2) + " minutes");
//                        level3.setText(AppConstants.level3 + String.valueOf(AppConstants.loc2level3) + " minutes");
//                    } else if (level == 3) {
//                        level1.setText(AppConstants.level1 + String.valueOf(AppConstants.loc3level1) + " minutes");
//                        level2.setText(AppConstants.level2 + String.valueOf(AppConstants.loc3level2) + " minutes");
//                        level3.setText(AppConstants.level3 + String.valueOf(AppConstants.loc3level3) + " minutes");
//                    } else if (level == 4) {
//                        level1.setText(AppConstants.level1 + String.valueOf(AppConstants.loc4level1) + " minutes");
//                        level2.setText(AppConstants.level2 + String.valueOf(AppConstants.loc4level2) + " minutes");
//                        level3.setText(AppConstants.level3 + String.valueOf(AppConstants.loc4level3) + " minutes");
//                    } else if (level == 5) {
//                        level1.setText(AppConstants.level1 + String.valueOf(AppConstants.loc5level1) + " minutes");
//                        level2.setText(AppConstants.level2 + String.valueOf(AppConstants.loc5level2) + " minutes");
//                        level3.setText(AppConstants.level3 + String.valueOf(AppConstants.loc5level3) + " minutes");
//                    } else if (level == 6) {
//                        level1.setText(AppConstants.level1 + String.valueOf(AppConstants.loc3level1) + " minutes");
//                        level2.setText(AppConstants.level2 + String.valueOf(AppConstants.loc3level2) + " minutes");
//                        level3.setText(AppConstants.level3 + String.valueOf(AppConstants.loc3level3) + " minutes");
//                    } else if (level == 7) {
//                        level1.setText(AppConstants.level1 + String.valueOf(AppConstants.loc4level1) + " minutes");
//                        level2.setText(AppConstants.level2 + String.valueOf(AppConstants.loc4level2) + " minutes");
//                        level3.setText(AppConstants.level3 + String.valueOf(AppConstants.loc4level3) + " minutes");
//                    }
                    Log.i("LEVELL", String.valueOf(level));
                    prefEditor = pref.edit();
                    prefEditor.putInt(AppConstants.levelPref, level);
                    points = userDS.child("points").getValue(Integer.class);
                    prefEditor.putInt("Points", points);
                    pointsTextView.setText(String.valueOf(MainActivity.points));
                    name = (String) userDS.child("name").getValue();
                    if (level > 1) {
                        locName = dataSnapshot.child("Route 2").child("Location " + String.valueOf(level - 1)).child("Name").getValue(String.class);
                        // Log.i("loca firebase",locName);
                        prefEditor = pref.edit();
                        prefEditor.putString(AppConstants.locationLevelPref + (level - 1), locName).apply();
                    }
                    DataSnapshot locationDS = dataSnapshot.child("Route 2").child("Location " + String.valueOf(level));
                    levelString = locationDS.child("Clue").getValue(String.class);
                    prefEditor.putString(AppConstants.cluePref, levelString).apply();
                    //  pref = getActivity().getSharedPreferences("com.techrace.spit.techrace2018", Context.MODE_PRIVATE);


                    NSID = locationDS.child("NSID").getValue(String.class);
                    Log.i("NSID", NSID);
                    clueLocation.setLatitude(Double.parseDouble(locationDS.child("Latitude").getValue(String.class)));
                    clueLocation.setLongitude(Double.parseDouble(locationDS.child("Longitude").getValue(String.class)));
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
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        clueTextView = myView.findViewById(R.id.clue_text);
        pointsTextView = myView.findViewById(R.id.pointsTextView);
        level1 = myView.findViewById(R.id.level1);
        level2 = myView.findViewById(R.id.level2);
        level3 = myView.findViewById(R.id.level3);
        timerTextView = myView.findViewById(R.id.timerTextView);
        clueRelativeLayout = myView.findViewById(R.id.clueLayout);
        hintButton = myView.findViewById(R.id.hintButton);
        hintTextView = myView.findViewById(R.id.hintTextView);
        clueTextView.setText(pref.getString(AppConstants.cluePref, "Connect To Internet"));
        pointsTextView.setText(String.valueOf(pref.getInt("Points", 0)));

        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference hintRef = FirebaseDatabase.getInstance().getReference().child("Users").child(UID).child("hintsLeft");
                hintRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        hintsLeft = dataSnapshot.getValue(Integer.class);
                        if (hintsLeft <= 0) {
                            Toast.makeText(getActivity(), "No Hints Left", Toast.LENGTH_SHORT).show();
                        } else if (hintsLeft == 3) {
                            if (points > AppConstants.hint1Price) {
                                hintTextView.setVisibility(View.VISIBLE);
                                DatabaseReference hintReference = FirebaseDatabase.getInstance().getReference().child("Route 2").child("Location " + String.valueOf(level)).child("Hint");
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
                            } else {
                                Toast.makeText(getActivity(), "Not Enough Points", Toast.LENGTH_SHORT).show();
                            }
                        } else if (hintsLeft == 2) {
                            if (points > AppConstants.hint2Price) {
                                hintTextView.setVisibility(View.VISIBLE);
                                DatabaseReference hintReference = FirebaseDatabase.getInstance().getReference().child("Route 2").child("Location " + String.valueOf(level)).child("Hint");
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
                            } else {
                                Toast.makeText(getActivity(), "Not Enough Points", Toast.LENGTH_SHORT).show();
                            }
                        } else if (hintsLeft == 1) {
                            if (points > AppConstants.hint3Price) {
                                hintTextView.setVisibility(View.VISIBLE);
                                DatabaseReference hintReference = FirebaseDatabase.getInstance().getReference().child("Route 2").child("Location " + String.valueOf(level)).child("Hint");
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
                            } else {
                                Toast.makeText(getActivity(), "Not Enough Points", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.home_layout, container, false);

        return myView;
    }


//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
//        toolbar.inflateMenu(R.menu.main);
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                onOptionsItemSelected(item);
//                return true;
//            }
//        });
//
//    }

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

//    void checkManualPassword(final String manualPassword) {
//
//        DatabaseReference pass = FirebaseDatabase.getInstance().getReference().child("Route 2").child("Location " + String.valueOf(level)).child("passwords");
//        pass.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String serverPass = dataSnapshot.getValue(String.class);
//                Log.i("server pass", serverPass);
//                if (manualPassword.equals(serverPass)) {
//                    Toast.makeText(getActivity(), "Updating...", Toast.LENGTH_LONG).show();
//                    if (cooldown == 0) {
//                        timerOn = false;
//                        MainActivity.beacon = true;
//                        UserDatabaseReference = FirebaseDatabase.getInstance().getReference();
//                        UserDatabaseReference.child("Users").child(UID).child("level").setValue(level + 1);
//                        UserDatabaseReference.child("Users").child(UID).child("points").setValue(points + 5);
//                        long l = new Date().getTime();
//                        UserDatabaseReference.child("Users").child(UID).child("Time" + String.valueOf(level)).setValue(l);
//                        UserDatabaseReference.child("Leaderboard").child(UID).setValue(new LeaderBoardOBject(HomeFragment.name, level, points, l, cooldown, UID));
//                        prefEditor = pref.edit();
//                        prefEditor.putString(AppConstants.clueLevelPref+ level, levelString).apply();
//                        new HomeFragment().updateClue();
//                        MainActivity.beacon = true;
//                        event = false;
//                        //break;
//                    } else {
//
//                        Log.i("IN ELSE 1", "yes");
//                        if (!timerOn) {
//                            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
//                            Log.i("cool", "" + cooldown);
//                            timerTextView.setText("Timer of " + cooldown + " mins is set on " + currentDateTimeString);
//                            Log.i("IN timer on false", "yes");
//                            timerOn = true;
//                            Intent intent = new Intent(getActivity(), NotificationReceiver.class);
//                            PendingIntent pendingIntentforAlarm = PendingIntent.getBroadcast(
//                                    getActivity(), 9999, intent, 0);
//
//                            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
//
//                            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
//                                    + (cooldown * 60000), pendingIntentforAlarm);
//
//
//                            NotificationCompat.Builder builderalarm =
//                                    new NotificationCompat.Builder(getActivity())
//                                            .setSmallIcon(R.drawable.ic_launcher_foreground)
//                                            .setContentTitle("Please Wait")
//                                            .setContentText("Timer of " + cooldown + " mins is set on " + currentDateTimeString)
//                                            .setOngoing(true)
//                                            .setAutoCancel(false).setChannelId("Timer");
//                            NotificationChannel mChannel;
//                            NotificationManager notificationManagerforAlarm =
//                                    (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                mChannel = new NotificationChannel("Timer", "Timer", NotificationManager.IMPORTANCE_DEFAULT);
//                                notificationManagerforAlarm.createNotificationChannel(mChannel);
//                            }
//
//
//                            notificationManagerforAlarm.notify(1, builderalarm.build());
//                        }
//
//                    }
//
//
//                } else {
//                    Toast.makeText(H, "Wrong Password!", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

}
