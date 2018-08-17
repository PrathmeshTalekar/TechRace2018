package com.techrace.spit.techrace2018;


import android.content.Context;

import com.techrace.spit.techrace2018.MainActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;

import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;


import java.util.Collection;
import java.util.Date;

import br.com.safety.locationlistenerhelper.core.CurrentLocationListener;
import br.com.safety.locationlistenerhelper.core.CurrentLocationReceiver;
import br.com.safety.locationlistenerhelper.core.LocationTracker;


public class HomeFragment extends Fragment {

    static final String TAG = "MonitoringActivity";
    static View myView;
    static TextView clueTextView, t2, t3, pointsTextView;
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
    static boolean abc = true;
    static long l;
    static String name;
    // private BeaconManager MainActivity.beaconManager;
    private LocationTracker locationTracker;

    public static void updateClue() {
        clueLocation = new Location("");
        firebaseDatabase = FirebaseDatabase.getInstance();
        if (homeFragAuth.getCurrentUser() != null) {


            UID = homeFragAuth.getCurrentUser().getUid();
            Log.i("UID", UID);
        }

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
                Log.i("LEVEL STNG", String.valueOf(level) + "  " + levelString);
                NSID = locationDS.child("NSID").getValue(String.class);
                Log.i("NSID", NSID);
                clueLocation.setLatitude(Double.parseDouble(locationDS.child("Latitude").getValue(String.class)));
                clueLocation.setLongitude(Double.parseDouble(locationDS.child("Longitude").getValue(String.class)));
                Log.i("LOC LAT", String.valueOf(clueLocation.getLatitude()));
                clueTextView.setText(levelString);
                clueTextView.setBackgroundColor(Color.BLUE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        if (MainActivity.beaconManager != null) {
            if (MainActivity.beaconManager.isBound((MainActivity) HomeFragment.this.getActivity())) {
                MainActivity.beaconManager.unbind((MainActivity) HomeFragment.this.getActivity());
            }
        }
        locationTracker.stopLocationService(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        if (MainActivity.beaconManager != null) {
            if (MainActivity.beaconManager.isBound((MainActivity) HomeFragment.this.getActivity()))
                MainActivity.beaconManager.setBackgroundMode(true);
        }
        locationTracker.stopLocationService(getActivity());
//
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


//    @Override
//    public void onBeaconServiceConnect() {
////        MainActivity.beaconManager.addRangeNotifier(new RangeNotifier() {
////                                           @Override
////                                           public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
////                                               if (beacons.size() > 0) {
////                                                   //MainActivity.beaconManager.setForegroundBetweenScanPeriod(2000);
////                                                   Log.i(TAG, "didRangeBeaconsInRegion called with beacon count:  " + beacons.size());
////                                                   while (beacons.iterator().hasNext()) {
////                                                       Beacon b = beacons.iterator().next();
////                                                       firstBeacon = b;
////
////                                                       beaconID = firstBeacon.toString();
////                                                       Log.i("SIZE", String.valueOf(beacons.size()));
////                                                       Log.i("BID", beaconID);
////                                                       Log.i("BEACON blue address", firstBeacon.getBluetoothAddress());
////                                                       Log.i("BEACON Id1", firstBeacon.getId1().toString());
////                                                       Log.i("BEACON Manufacture", String.valueOf(firstBeacon.getManufacturer()));
////                                                       //Log.i("NSIDDD",MainActivity.NSID);
////                                                       //Beacon firstBeacon = beacons.iterator().next();
////                                                       String s = "id1: " + NSID + " id2: 0x000000000000";
////                                                       Log.i("AAAA", s);
////                                                       double dist = firstBeacon.getDistance();
////                                                       t2.setText(String.valueOf(dist));
////                                                       Log.i("FOUNDD", "The first beacon " + firstBeacon.toString() + " is about " + firstBeacon.getDistance() + " meters away.");
////                                                       if (beaconID.equals(s) && dist <= 0.45) {
////
////                                                           //  locationTracker.stopLocationService(getActivity());
////                                                           MainActivity.beaconManager.unbind((MainActivity)HomeFragment.this.getActivity());
////                                                           //   MainActivity.beaconManager.disableForegroundServiceScanning();
////                                                           MainActivity.beaconManager.removeAllRangeNotifiers();
////                                                           MainActivity.beaconManager.applySettings();
////
////                                                           //MainActivity.beaconManager.setForegroundBetweenScanPeriod(30000);
////                                                           if (level == 3) {
////                                                               abc = false;
////                                                               clueTextView.setBackgroundColor(Color.GREEN);
////                                                               final AlertDialog.Builder alertDialog = new AlertDialog.Builder(myView.getContext());
////                                                               alertDialog.setTitle("MEET THE VOLUNTEER");
////                                                               alertDialog.setMessage("Enter Password");
////                                                               alertDialog.setCancelable(false);
////
////                                                               final EditText passwordEditText = new EditText(myView.getContext());
////                                                               LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
////                                                                       LinearLayout.LayoutParams.MATCH_PARENT,
////                                                                       LinearLayout.LayoutParams.MATCH_PARENT);
////                                                               passwordEditText.setLayoutParams(lp);
////                                                               alertDialog.setView(passwordEditText);
////
////
////                                                               alertDialog.setPositiveButton("GO!",
////                                                                       new DialogInterface.OnClickListener() {
////                                                                           public void onClick(DialogInterface dialog, int which) {
////                                                                               volunteerPassword = passwordEditText.getText().toString();
////                                                                               if (volunteerPassword.equals("hello")) {
////
////                                                                                   Toast.makeText(getActivity(),
////                                                                                           "Password Matched", Toast.LENGTH_SHORT).show();
////                                                                                   UserDatabaseReference = FirebaseDatabase.getInstance().getReference();
////                                                                                   UserDatabaseReference.child("Users")
////                                                                                           .child(UID)
////                                                                                           .child("level")
////                                                                                           .setValue(level + 1);
////                                                                                   UserDatabaseReference.child("Users")
////                                                                                           .child(UID)
////                                                                                           .child("points")
////                                                                                           .setValue(points + 5);
////                                                                                   l = d.getTime();
////                                                                                   UserDatabaseReference.child("Users")
////                                                                                           .child(UID)
////                                                                                           .child("Time" + String.valueOf(level))
////                                                                                           .setValue(l);
////                                                                                   UserDatabaseReference.child("Leaderboard")
////                                                                                           .child(UID)
////                                                                                           .setValue(new LeaderBoardOBject(name, level, points, l));
////                                                                                   updateClue();
////                                                                                   abc = true;
////                                                                               } else {
////                                                                                   Toast.makeText(getActivity(),
////                                                                                           "Wrong Password!", Toast.LENGTH_SHORT).show();
////                                                                               }
////
////                                                                           }
////                                                                       });
////                                                               alertDialog.show();
//////
////                                                               break;
////
////                                                           } else {
////
////                                                               clueTextView.setBackgroundColor(Color.GREEN);
////                                                               abc = true;
////                                                               UserDatabaseReference = FirebaseDatabase.getInstance().getReference();
////                                                               UserDatabaseReference.child("Users").child(UID).child("level").setValue(level + 1);
////                                                               UserDatabaseReference.child("Users").child(UID).child("points").setValue(points + 5);
////                                                               l = d.getTime();
////                                                               UserDatabaseReference.child("Users").child(UID).child("Time" + String.valueOf(level)).setValue(l);
////                                                               UserDatabaseReference.child("Leaderboard").child(UID).setValue(new LeaderBoardOBject(name, level, points, l));
////                                                               updateClue();
////                                                               break;
////                                                           }
////
////
////                                                       }
////
////                                                       beacons.remove(firstBeacon);
////                                                   }
////                                                   beacons.clear();
////
////
////                                               }
////                                           }
////                                       }
////        );
////        MainActivity.beaconManager.addMonitorNotifier(new
////
////                                                 MonitorNotifier() {
////                                                     @Override
////                                                     public void didEnterRegion(Region region) {
////
////                                                         Log.i("YESSS", "I just saw a beacon for the first time!");
////                                                     }
////
////                                                     @Override
////                                                     public void didExitRegion(Region region) {
////                                                         Log.i("NOOO", "I no longer see a beacon");
////                                                     }
////
////                                                     @Override
////                                                     public void didDetermineStateForRegion(int state, Region region) {
////                                                         Log.i("OOOO", "I have just switched from seeing/not seeing beacons: " + state);
////                                                     }
////                                                 });
////
////        try
////
////        {
////
////            MainActivity.beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
////
////        } catch (
////                RemoteException e)
////
////        {
////        }
////        try
////
////        {
////            MainActivity.beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
////        } catch (
////                RemoteException e)
////
////        {
////        }
//
//    }

    @Override
    public void onResume() {
        super.onResume();

        updateClue();


        try {

            locationTracker = new LocationTracker("my.action")
                    .setInterval(30000)
                    .setGps(true)
                    .setNetWork(false)
                    .currentLocation(new CurrentLocationReceiver(new CurrentLocationListener() {
                        @Override
                        public void onCurrentLocation(Location location) {
                            //Toast.makeText(myView.getContext(), "Currently:" + location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                            double distanceinmetres = clueLocation.distanceTo(location);

                            Toast.makeText(myView.getContext(), "Distance: " + distanceinmetres, Toast.LENGTH_SHORT).show();

                            if (mobile == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTED) {
                                if (distanceinmetres <= 250) {
                                    clueTextView.setBackgroundColor(Color.RED);
                                    if (abc) {
                                        MainActivity.beaconManager = BeaconManager.getInstanceForApplication(getActivity());

                                        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
                                        // type.  Do a web search for "setBeaconLayout" to get the proper expression.

                                        MainActivity.beaconManager.getBeaconParsers().add(new BeaconParser().
                                                setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
                                        if (HomeFragment.this == null) {
                                            Log.i("CONNULL", "NULL");
                                        }
                                        MainActivity.beaconManager.bind((MainActivity) HomeFragment.this.getActivity());
                                    } else {
                                        MainActivity.beaconManager.unbind((MainActivity) HomeFragment.this.getActivity());
                                        MainActivity.beaconManager.disableForegroundServiceScanning();
                                        MainActivity.beaconManager.removeAllMonitorNotifiers();
                                        MainActivity.beaconManager.applySettings();
                                    }
                                } else {
                                    clueTextView.setBackgroundColor(Color.BLUE);
                                }
                            } else {
                                clueTextView.setBackgroundColor(Color.BLUE);
                            }
                        }

                        @Override
                        public void onPermissionDiened() {

                            // hotcoldtext.setText("Location OFF");
                        }
                    }))
                    .start(getActivity(), (AppCompatActivity) getActivity());
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
