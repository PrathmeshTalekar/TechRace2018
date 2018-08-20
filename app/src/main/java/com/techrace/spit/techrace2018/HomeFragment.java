package com.techrace.spit.techrace2018;


import android.content.Context;


import android.content.SharedPreferences;

import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import static com.techrace.spit.techrace2018.MainActivity.prefEditor;
import static com.techrace.spit.techrace2018.MainActivity.pref;
import static com.techrace.spit.techrace2018.MainActivity.timerOn;

import java.util.prefs.Preferences;

import br.com.safety.locationlistenerhelper.core.CurrentLocationListener;
import br.com.safety.locationlistenerhelper.core.CurrentLocationReceiver;
import br.com.safety.locationlistenerhelper.core.LocationTracker;


public class HomeFragment extends Fragment {

    static final String TAG = "MonitoringActivity";
    static View myView;
    static TextView clueTextView, t2, t3, pointsTextView, timerTextView;
    static DatabaseReference UserDatabaseReference;
    static FirebaseDatabase firebaseDatabase;
    static FirebaseAuth homeFragAuth = MainActivity.mAuth;
    static String UID;
    static int level = 1, points, cooldown;
    static String levelString;
    static String beaconID, NSID;
    static Beacon firstBeacon;
    NetworkInfo.State wifi, mobile;
    static Location clueLocation;
    static String volunteerPassword;
    static boolean beacon = true;
    static long l;
    static String name;
    // private BeaconManager MainActivity.beaconManager;

    static LocationTracker locationTracker;

    public void updateClue() {
        clueLocation = new Location("");
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
                    Log.i("LEVELL", String.valueOf(level));
                    points = userDS.child("points").getValue(Integer.class);

                    pointsTextView.setText(String.valueOf(points));
                    name = (String) userDS.child("name").getValue();

                    DataSnapshot locationDS = dataSnapshot.child("Route 2").child("Location " + String.valueOf(level));
                    levelString = locationDS.child("Clue").getValue(String.class);
                    //  pref = getActivity().getSharedPreferences("com.techrace.spit.techrace2018", Context.MODE_PRIVATE);

                    Log.i("LEVEL STNG", String.valueOf(level) + "  " + levelString);
                    NSID = locationDS.child("NSID").getValue(String.class);
                    Log.i("NSID", NSID);
                    clueLocation.setLatitude(Double.parseDouble(locationDS.child("Latitude").getValue(String.class)));
                    clueLocation.setLongitude(Double.parseDouble(locationDS.child("Longitude").getValue(String.class)));
                    Log.i("LOC LAT", String.valueOf(clueLocation.getLatitude()));
                    clueTextView.setText(levelString);
                    clueTextView.setBackgroundColor(MainActivity.resources.getColor(R.color.coldBlue));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        prefEditor = pref.edit().putInt("Level", level).putInt("Points", points).putString("Clue", levelString);
        prefEditor.apply();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ConnectivityManager conMan = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();

        //wifi
        wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        clueTextView = myView.findViewById(R.id.clue_text);
        t2 = myView.findViewById(R.id.textView3);
        t3 = myView.findViewById(R.id.textView2);
        pointsTextView = myView.findViewById(R.id.pointsTextView);
        timerTextView = myView.findViewById(R.id.timerTextView);


        clueTextView.setText(pref.getString("Clue", "Connect To Internet"));
        pointsTextView.setText(String.valueOf(pref.getInt("Points", 0)));
//        MainActivity.pref=getActivity().getSharedPreferences("OFFLINE",Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.home_layout, container, false);

        return myView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("DEST", "DESTROYYYEDDD");
        if (MainActivity.beaconManager != null) {
            if (MainActivity.beaconManager.isBound((MainActivity) HomeFragment.this.getActivity())) {
                MainActivity.beaconManager.unbind((MainActivity) HomeFragment.this.getActivity());
            }
        }
//        if(locationTracker!=null) {
//            locationTracker.stopLocationService(getActivity());
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (MainActivity.beaconManager != null) {
            if (MainActivity.beaconManager.isBound((MainActivity) HomeFragment.this.getActivity()))
                MainActivity.beaconManager.setBackgroundMode(false);
        }
//        if(locationTracker!=null) {
//            locationTracker.stopLocationService(getActivity());
//        }
//
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }


    @Override
    public void onResume() {
        super.onResume();

        updateClue();


        try {
            locationTracker = new LocationTracker("my.action")
                    .setInterval(15000)
                    .setGps(true)
                    .setNetWork(true)
                    .setNetWork(false);
            locationTracker.currentLocation(new CurrentLocationReceiver(new CurrentLocationListener() {
                @Override
                public void onCurrentLocation(Location location) {
                    //Toast.makeText(myView.getContext(), "Currently:" + location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                    double distanceinmetres = clueLocation.distanceTo(location);

                    Toast.makeText(myView.getContext(), "Distance: " + distanceinmetres, Toast.LENGTH_SHORT).show();

                    if (mobile == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTED) {
                        if (!timerOn) {
                                if (distanceinmetres <= 250) {
                                    clueTextView.setBackgroundColor(MainActivity.resources.getColor(R.color.hotRed));
                                    if (beacon) {
                                        MainActivity.beaconManager = BeaconManager.getInstanceForApplication((MainActivity) HomeFragment.this.getActivity());
                                        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
                                        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
                                        MainActivity.beaconManager.getBeaconParsers().add(new BeaconParser().
                                                setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
                                        MainActivity.beaconManager.bind((MainActivity) HomeFragment.this.getActivity());
                                    } else {
                                        MainActivity.beaconManager.unbind((MainActivity) HomeFragment.this.getActivity());
                                        MainActivity.beaconManager.disableForegroundServiceScanning();
                                        MainActivity.beaconManager.removeAllMonitorNotifiers();
                                        MainActivity.beaconManager.applySettings();
                                    }

                                } else {
                                    clueTextView.setBackgroundColor(MainActivity.resources.getColor(R.color.coldBlue));
                                }
                        }
                    } else {
                        Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_LONG).show();
                        // clueTextView.setBackgroundColor(MainActivity.resources.getColor(R.color.coldBlue));

                    }

                }

                @Override
                public void onPermissionDiened() {

                    // hotcoldtext.setText("Location OFF");
                }
            })).start(getActivity(), (AppCompatActivity) getActivity());
        } catch (Exception e) {
            Log.v("EROOR", "" + e);

        }

        //    if (MainActivity.beaconManager.isBound(this)) MainActivity.beaconManager.setBackgroundMode(false);
    }

//    @Override
//    public Context getApplicationContext() {
//        return null;
//    }
//
//    @Override
//    public void unbindService(ServiceConnection serviceConnection) {
//
//    }
//
//    @Override
//    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
//        return false;
//    }
}
