package com.techrace.spit.techrace2018;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
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

import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import br.com.safety.locationlistenerhelper.core.CurrentLocationListener;
import br.com.safety.locationlistenerhelper.core.CurrentLocationReceiver;
import br.com.safety.locationlistenerhelper.core.LocationTracker;

import static android.content.Context.ALARM_SERVICE;
import static com.techrace.spit.techrace2018.MainActivity.NSID;

public class HomeFragment extends Fragment implements BeaconConsumer {
    View myView;
    protected static final String TAG = "MonitoringActivity";
    private BeaconManager beaconManager;
    TextView t,t2,t3;
    static DatabaseReference databaseReference,UserDatabaseReference;
    static FirebaseDatabase firebaseDatabase;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.home_layout, container, false);

        return myView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        beaconManager = BeaconManager.getInstanceForApplication(myView.getContext());
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        beaconManager.bind(this);
        t = myView.findViewById(R.id.clue_text);
        t2=myView.findViewById(R.id.textView3);
        t3=myView.findViewById(R.id.textView2);
        HomeFragment.databaseReference=HomeFragment.firebaseDatabase.getReference().child("Locations").child("Location "+MainActivity.level);
        databaseReference.child("Clue").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value=dataSnapshot.getValue(String.class);
                t.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        super.onViewCreated(view, savedInstanceState);
        HomeFragment.firebaseDatabase= FirebaseDatabase.getInstance();
        HomeFragment.UserDatabaseReference=HomeFragment.firebaseDatabase.getReference().child("Users").child("001 Ram Shyam");
        HomeFragment.UserDatabaseReference.child("Level").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String s=dataSnapshot.getValue(String.class);
                MainActivity.level=Integer.parseInt(s);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        HomeFragment.databaseReference=HomeFragment.firebaseDatabase.getReference().child("Locations");
        //if(level==1){
        HomeFragment.databaseReference.child("Location "+MainActivity.level).child("NSID").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                NSID=dataSnapshot.getValue(String.class);
                t3.setText(MainActivity.NSID);
                Log.i("NSID",NSID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateClue();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
    }
    String beaconID;
    Beacon firstBeacon;
    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Log.i(TAG, "didRangeBeaconsInRegion called with beacon count:  " + beacons.size());
                    for(Beacon b:beacons) {
                        firstBeacon = b;
                        beaconID = b.toString();
                        Log.i("BID",beaconID);
                        Log.i("BEACON blue address",firstBeacon.getBluetoothAddress());
//                        Log.i("BEACON blue name",firstBeacon.getBluetoothName());
                        Log.i("BEACON Id1",firstBeacon.getId1().toString());
//                        Log.i("BEACON parse identi",firstBeacon.getParserIdentifier());
                        Log.i("BEACON Manufacture",String.valueOf(firstBeacon.getManufacturer()));
                        //Log.i("NSIDDD",MainActivity.NSID);
                        //Beacon firstBeacon = beacons.iterator().next();
                        String s="id1: " + NSID + " id2: 0x000000000000";
                        Log.i("AAAA",s);
                        if (beaconID.equals(s) && firstBeacon.getDistance() <= 1.0) {
                            MainActivity.level++;
                            updateClue();
                        }
                        t2.setText(String.valueOf(firstBeacon.getDistance()));
                        Log.i("FOUNDD", "The first beacon " + firstBeacon.toString() + " is about " + firstBeacon.getDistance() + " meters away.");
//                        beacons.remove(firstBeacon);

                    }
                    beacons.clear();


                }
            }

        });
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {

                Log.i("YESSS", "I just saw an beacon for the first time!");
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i("NOOO", "I no longer see an beacon");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i("OOOO", "I have just switched from seeing/not seeing beacons: " + state);
            }
        });

        try {
            HomeFragment.databaseReference=HomeFragment.firebaseDatabase.getReference().child("Locations");
            //if(level==1){
            HomeFragment.databaseReference.child("Location "+MainActivity.level).child("NSID").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    NSID=dataSnapshot.getValue(String.class);
                    t3.setText(MainActivity.NSID);
                    Log.i("NSID",NSID);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
    }
    public void updateClue(){
        HomeFragment.UserDatabaseReference=HomeFragment.firebaseDatabase.getReference().child("Users").child("001 Ram Shyam").child("Level");
        HomeFragment.UserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().setValue(String.valueOf(MainActivity.level));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        HomeFragment.UserDatabaseReference=HomeFragment.firebaseDatabase.getReference().child("Users").child("001 Ram Shyam").child("Level");
        HomeFragment.UserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MainActivity.level=Integer.parseInt(dataSnapshot.getValue(String.class));
                Log.i("LEVEL UPDATED",String.valueOf(MainActivity.level));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        HomeFragment.databaseReference=HomeFragment.firebaseDatabase.getReference().child("Locations");
        //if(level==1){
        HomeFragment.databaseReference.child("Location "+MainActivity.level).child("NSID").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                NSID=dataSnapshot.getValue(String.class);
                t3.setText(MainActivity.NSID);
//                Log.i("NSID",NSID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        HomeFragment.databaseReference.child("Locations").child("Location "+MainActivity.level).child("NSID").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                NSID=dataSnapshot.getValue(String.class);
//               Log.i("NSID UPDATE", NSID);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

//        User userPoints=new User(5);
//        UserDatabaseReference.child("Points").setValue(userPoints);
        databaseReference=firebaseDatabase.getReference().child("Locations").child("Location "+MainActivity.level);
        databaseReference.child("Clue").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value=dataSnapshot.getValue(String.class);
                t.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    @Override
    public Context getApplicationContext() {
        return null;
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {

    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return false;
    }
}
