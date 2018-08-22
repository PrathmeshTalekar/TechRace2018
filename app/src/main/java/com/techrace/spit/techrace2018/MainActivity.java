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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;

import br.com.safety.locationlistenerhelper.core.CurrentLocationListener;
import br.com.safety.locationlistenerhelper.core.CurrentLocationReceiver;
import br.com.safety.locationlistenerhelper.core.LocationTracker;

import static com.techrace.spit.techrace2018.HomeFragment.NSID;
import static com.techrace.spit.techrace2018.HomeFragment.UID;


import static com.techrace.spit.techrace2018.HomeFragment.clueLocation;
import static com.techrace.spit.techrace2018.HomeFragment.clueRelativeLayout;
import static com.techrace.spit.techrace2018.HomeFragment.clueTextView;

import static com.techrace.spit.techrace2018.HomeFragment.UserDatabaseReference;

import static com.techrace.spit.techrace2018.HomeFragment.level;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BeaconConsumer {
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    //static BeaconManager beaconManager;
    static FirebaseAuth mAuth;
    static int cooldown;
    public static Resources resources;
    static SharedPreferences pref;
    static SharedPreferences.Editor prefEditor;
    Date d = new Date();
    static boolean beacon = true;
    static int points;
    String beaconID;
    static boolean timerOn = false;
    Beacon firstBeacon;
    static String selectUID = null;
    long l;
    BeaconManager beaconManager;
    String appliedBy = null;
    LocationTracker locationTracker;
    NetworkInfo.State wifi, mobile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @TargetApi(23)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_REQUEST_COARSE_LOCATION);
                    }

                });
                builder.show();
            }
        }
        mAuth = FirebaseAuth.getInstance();
        // Log.i("MAUTH",mAuth.getCurrentUser().getDisplayName());
        if (mAuth.getCurrentUser() == null) {
            Intent i = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(i);
        } else {
            UserDatabaseReference = FirebaseDatabase.getInstance().getReference();
            UserDatabaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("cooldown").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    cooldown = dataSnapshot.getValue(Integer.class);
                    DatabaseReference leadercooldown = FirebaseDatabase.getInstance().getReference().child("Leaderboard");
                    leadercooldown.child(UID).child("Cooldown").setValue(cooldown);
                    Log.i("COOLDOWN FOUND", "" + cooldown);
                    if (cooldown > 0) {
                        if (points >= AppConstants.reversePrice) {
                            final android.support.v7.app.AlertDialog.Builder reverseAlertDialog = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                            reverseAlertDialog.setTitle("TIMER APPLIED").setMessage("Buy Right Back at Ya Card? for 60 points")
                                    .setPositiveButton("BUY", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            DatabaseReference reverseReference = FirebaseDatabase.getInstance().getReference();
                                            reverseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("Applied By").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    appliedBy = dataSnapshot.getValue(String.class);
                                                    Log.i("applied", appliedBy);
                                                    DatabaseReference reverseReference1 = FirebaseDatabase.getInstance().getReference();
                                                    reverseReference1.child("Users").child(appliedBy).child("cooldown").setValue(cooldown);
                                                    reverseReference1.child("Users").child(appliedBy).child("Applied By").setValue(UID);
                                                    reverseReference1.child("Users").child(UID).child("points")
                                                            .setValue(MainActivity.points - AppConstants.reversePrice);
                                                    reverseReference1.child(mAuth.getCurrentUser().getUid()).child("cooldown").setValue(0);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setCancelable(false).show();
                        }
                    }
                    timerOn = false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            UserDatabaseReference = FirebaseDatabase.getInstance().getReference();
            UserDatabaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("points").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    points = dataSnapshot.getValue(Integer.class);
                    DatabaseReference leadercooldown = FirebaseDatabase.getInstance().getReference().child("Leaderboard");
                    leadercooldown.child(UID).child("Points").setValue(points);
                    Log.i("POints updated", "" + points);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // displaySelectedScreen(R.id.home);
        pref = getSharedPreferences("com.techrace.spit.techrace2018", MODE_PRIVATE);

        resources = getResources();
        displaySelectedScreen(R.id.home);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSION", "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);

                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                            }
                        });

                    builder.show();
                }

            }
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        ConnectivityManager conMan = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();

        //wifi
        wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        verifyBluetooth();
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

                    // Toast.makeText(myView.getContext(), "Distance: " + distanceinmetres, Toast.LENGTH_SHORT).show();

                    if (mobile == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTED) {
                        if (!timerOn) {
                            if (distanceinmetres <= 250) {
                                clueRelativeLayout.setBackgroundColor(MainActivity.resources.getColor(R.color.hotRed));
                                if (beacon) {
                                    beaconManager = BeaconManager.getInstanceForApplication(MainActivity.this);
                                    // To detect proprietary beacons, you must add a line like below corresponding to your beacon
                                    // type.  Do a web search for "setBeaconLayout" to get the proper expression.
                                    beaconManager.getBeaconParsers().add(new BeaconParser().
                                            setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
                                    beaconManager.bind(MainActivity.this);
                                } else {
                                    beaconManager.unbind(MainActivity.this);
                                    beaconManager.disableForegroundServiceScanning();
                                    beaconManager.removeAllMonitorNotifiers();
                                    beaconManager.applySettings();
                                }

                            } else {
                                clueRelativeLayout.setBackgroundColor(MainActivity.resources.getColor(R.color.coldBlue));
                            }
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "No Internet", Toast.LENGTH_LONG).show();
                        // clueTextView.setBackgroundColor(MainActivity.resources.getColor(R.color.coldBlue));

                    }

                }

                @Override
                public void onPermissionDiened() {

                    // hotcoldtext.setText("Location OFF");
                }
            })).start(MainActivity.this, MainActivity.this);
        } catch (Exception e) {
            Log.v("EROOR", "" + e);

        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void verifyBluetooth() {

        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth not enabled");
                builder.setMessage("Please enable bluetooth in settings and restart this application.");
                builder.setPositiveButton(android.R.string.ok, null);

                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            //                        finish();
                            //                        System.exit(0);
                        }
                    });

                builder.show();
            }
        }
        catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);

            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        System.exit(0);
                    }

                });

            builder.show();

        }

    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        displaySelectedScreen(item.getItemId());
        return true;
    }
    private void displaySelectedScreen(int id) {

        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (id) {
            case R.id.powerCards:
                fragment = new PowerCardsFragment();
                break;
            case R.id.clues:
                fragment = new CluesFragment();
                break;
            case R.id.credits:
                fragment = new CreditsFragment();
                break;
            case R.id.feed:
                fragment = new FeedFragment();
                break;
            case R.id.home:
                fragment = new HomeFragment();
                break;
            case R.id.help:
                fragment = new HelpFragment();
                break;
            case R.id.leaderboard:
                startActivity(new Intent(MainActivity.this, LeaderboardActivity.class));
                break;
            case R.id.locations:
                fragment = new LocationsFragment();
                break;
            case R.id.rules:
                fragment = new RulesFragment();
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void onDestroy() {
        locationTracker.stopLocationService(getBaseContext());
        super.onDestroy();
        if (beaconManager != null) {
            if (beaconManager.isBound(MainActivity.this)) {
                beaconManager.setBackgroundMode(false);
                beaconManager.unbind(MainActivity.this);
            }
        }
        locationTracker.stopLocationService(getBaseContext());


    }


    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
                                                        @Override
                                                        public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                                                            if (beacons.size() > 0) {
                                                                //MainActivity.beaconManager.setForegroundBetweenScanPeriod(2000);
                                                                Log.i(HomeFragment.TAG, "didRangeBeaconsInRegion called with beacon count:  " + beacons.size());
                                                                while (beacons.iterator().hasNext()) {
                                                                    Beacon b = beacons.iterator().next();
                                                                    firstBeacon = b;

                                                                    beaconID = firstBeacon.toString();
                                                                    Log.i("SIZE", String.valueOf(beacons.size()));
                                                                    Log.i("BID", beaconID);
                                                                    Log.i("BEACON blue address", firstBeacon.getBluetoothAddress());
                                                                    Log.i("BEACON Id1", firstBeacon.getId1().toString());
                                                                    Log.i("BEACON Manufacture", String.valueOf(firstBeacon.getManufacturer()));
                                                                    //Log.i("NSIDDD",MainActivity.NSID);
                                                                    //Beacon firstBeacon = beacons.iterator().next();
                                                                    String s = "id1: " + NSID + " id2: 0x000000000000";
                                                                    Log.i("AAAA", s);
                                                                    final double dist = firstBeacon.getDistance();
//                                                                    runOnUiThread(new Runnable() {
//                                                                        @Override
//                                                                        public void run() {
//                                                                            t2.setText(String.valueOf(dist));
//                                                                        }
//                                                                    });

                                                                    Log.i("FOUNDD", "The first beacon " + firstBeacon.toString() + " is about " + firstBeacon.getDistance() + " meters away.");
                                                                    if (beaconID.equals(s) && dist <= 0.45) {


                                                                        // locationTracker.stopLocationService(getBaseContext());
                                                                        beaconManager.unbind(MainActivity.this);
                                                                        beaconManager.removeAllRangeNotifiers();
                                                                        beaconManager.disableForegroundServiceScanning();
                                                                        beaconManager.applySettings();
                                                                        beacon = false;
                                                                        runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                clueRelativeLayout.setBackgroundColor(MainActivity.resources.getColor(R.color.confirmGreen));
                                                                            }
                                                                        });
                                                                        //MainActivity.beaconManager.setForegroundBetweenScanPeriod(30000);
                                                                        if (level == 3) {


                                                                            final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                                                                            alertDialog.setTitle("MEET THE VOLUNTEER");
                                                                            alertDialog.setMessage("Enter Password");
                                                                            alertDialog.setCancelable(false);

                                                                            final EditText passwordEditText = new EditText(HomeFragment.myView.getContext());
                                                                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                                                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                                                                    LinearLayout.LayoutParams.MATCH_PARENT);
                                                                            passwordEditText.setLayoutParams(lp);
                                                                            alertDialog.setView(passwordEditText);
                                                                            alertDialog.setPositiveButton("GO!",
                                                                                    new DialogInterface.OnClickListener() {
                                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                                            HomeFragment.volunteerPassword = passwordEditText.getText().toString();
                                                                                            if (HomeFragment.volunteerPassword.equals("hello")) {

                                                                                                Toast.makeText(MainActivity.this,
                                                                                                        "Password Matched", Toast.LENGTH_SHORT).show();
                                                                                                UserDatabaseReference = FirebaseDatabase.getInstance().getReference();
                                                                                                UserDatabaseReference.child("Users").child(UID).child("level").setValue(level + 1);
                                                                                                UserDatabaseReference.child("Users").child(UID).child("points").setValue(points + 5);
                                                                                                l = d.getTime();
                                                                                                UserDatabaseReference.child("Users").child(UID).child("Time" + String.valueOf(level)).setValue(l);
                                                                                                UserDatabaseReference.child("Leaderboard").child(UID).setValue(new LeaderBoardOBject(HomeFragment.name, level, points, l, cooldown, UID));
                                                                                                new HomeFragment().updateClue();
                                                                                                beacon = true;

                                                                                            } else {
                                                                                                Toast.makeText(MainActivity.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        }
                                                                                    });
                                                                            alertDialog.show();
                                                                            break;

                                                                        } else {


                                                                            if (cooldown == 0) {
                                                                                timerOn = false;
                                                                                beacon = true;
                                                                                UserDatabaseReference = FirebaseDatabase.getInstance().getReference();
                                                                                UserDatabaseReference.child("Users").child(UID).child("level").setValue(level + 1);
                                                                                UserDatabaseReference.child("Users").child(UID).child("points").setValue(points + 5);
                                                                                l = d.getTime();
                                                                                UserDatabaseReference.child("Users").child(UID).child("Time" + String.valueOf(level)).setValue(l);
                                                                                UserDatabaseReference.child("Leaderboard").child(UID).setValue(new LeaderBoardOBject(HomeFragment.name, level, points, l, cooldown, UID));
                                                                                new HomeFragment().onResume();
                                                                                beacon = true;
                                                                                break;
                                                                            } else {

                                                                                Log.i("IN ELSE 1", "yes");
                                                                                if (!timerOn) {
                                                                                    Log.i("IN timer on false", "yes");
                                                                                    timerOn = true;
                                                                                    Intent intent = new Intent(MainActivity.this, NotificationReceiver.class);
                                                                                    PendingIntent pendingIntentforAlarm = PendingIntent.getBroadcast(
                                                                                            MainActivity.this, 9999, intent, 0);

                                                                                    AlarmManager alarmManager = (AlarmManager) MainActivity.this.getSystemService(ALARM_SERVICE);

                                                                                    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                                                                                            + (cooldown * 60000), pendingIntentforAlarm);

                                                                                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                                                                    Log.i("cool", "" + cooldown);


                                                                                    NotificationCompat.Builder builderalarm =
                                                                                            new NotificationCompat.Builder(MainActivity.this)
                                                                                                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                                                                                                    .setContentTitle("Please Wait")
                                                                                                    .setContentText("Timer of " + cooldown + " mins is set on " + currentDateTimeString)
                                                                                                    .setOngoing(true)
                                                                                                    .setAutoCancel(false);

                                                                                    NotificationManager notificationManagerforAlarm =
                                                                                            (NotificationManager) MainActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                                                                    notificationManagerforAlarm.notify(1, builderalarm.build());

//                                        countDownTimer=new CountDownTimer(cooldown * 60000, 1000) {
//                                            @Override
//                                            public void onTick(long millisUntilFinished) {
//
//                                                Log.i("TIME LEFT",""+millisUntilFinished/1000);
////                                                                                            runOnUiThread(new Runnable() {
////                                                                                               @Override
////                                                                                               public void run() {
////                                                                                                   timerTextView.setText(""+millisUntilFinished/1000);
////                                                                                               }
////                                                                                            });
//                                            }
//
//                                            @Override
//                                            public void onFinish() {
//
//                                                UserDatabaseReference = FirebaseDatabase.getInstance().getReference();
//                                                UserDatabaseReference.child("Users").child(UID).child("cooldown").setValue(0);
//                                                UserDatabaseReference.child("Users").child(UID).child("level").setValue(level + 1);
//                                                UserDatabaseReference.child("Users").child(UID).child("points").setValue(HomeFragment.points + 5);
//                                                l = d.getTime();
//                                                UserDatabaseReference.child("Users").child(UID).child("Time" + String.valueOf(level)).setValue(l);
//                                                UserDatabaseReference.child("Leaderboard").child(UID).setValue(new LeaderBoardOBject(HomeFragment.name, level, HomeFragment.points, l));
//                                                new HomeFragment().onResume();
//                                                HomeFragment.beacon = true;
//                                                timerOn=false;
//
//                                            }
//                                        }.start();

                                                                                }

                                                                            }
                                                                        }


                                                                    }

                                                                    beacons.remove(firstBeacon);
                                                                }
                                                                beacons.clear();


                                                            }
                                                        }
                                                    }
        );
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {

                Log.i("YESSS", "I just saw a beacon for the first time!");
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i("NOOO", "I no longer see a beacon");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i("OOOO", "I have just switched from seeing/not seeing beacons: " + state);
            }
        });
        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
    }

}
