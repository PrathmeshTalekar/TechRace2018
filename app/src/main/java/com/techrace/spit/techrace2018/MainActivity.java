package com.techrace.spit.techrace2018;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.Date;

import static com.techrace.spit.techrace2018.HomeFragment.NSID;
import static com.techrace.spit.techrace2018.HomeFragment.UID;
import static com.techrace.spit.techrace2018.HomeFragment.beaconID;
import static com.techrace.spit.techrace2018.HomeFragment.clueLocation;
import static com.techrace.spit.techrace2018.HomeFragment.clueTextView;
import static com.techrace.spit.techrace2018.HomeFragment.firstBeacon;
import static com.techrace.spit.techrace2018.HomeFragment.UserDatabaseReference;
import static com.techrace.spit.techrace2018.HomeFragment.l;
import static com.techrace.spit.techrace2018.HomeFragment.level;
import static com.techrace.spit.techrace2018.HomeFragment.t2;
import static com.techrace.spit.techrace2018.HomeFragment.updateClue;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BeaconConsumer {
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    //static BeaconManager beaconManager;
    static FirebaseAuth mAuth;
    static BeaconManager beaconManager;
    public static Resources resources;
    Date d = new Date();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(isMockSettingsON(MainActivity.this))
//        {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                Toast.makeText(this,"Turn off mock location",Toast.LENGTH_SHORT).show();
//                this.finishAffinity();
//            }
//        }
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons in the background.");
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
        }


//        SharedPreferences pref = getSharedPreferences(AppConstants.PREFS, MODE_PRIVATE);
//        boolean locked = !pref.getBoolean(AppConstants.PREFS_UNLOCKED, false);
//        if (locked) {
//            //  Launch app intro
//            Intent i = new Intent(MainActivity.this, SignUpActivity.class);
//            startActivity(i);
//        }

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

        resources = getResources();
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                            }
                        });
                    }
                    builder.show();
                }

            }
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        verifyBluetooth();

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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            //                        finish();
                            //                        System.exit(0);
                        }
                    });
                }
                builder.show();
            }
        }
        catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        System.exit(0);
                    }

                });
            }
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
            case R.id.achievements:
                fragment = new AchievementsFragment();
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
                fragment = new LeaderBoardFragment();
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
        super.onDestroy();

    }


    @Override
    public void onBeaconServiceConnect() {
        MainActivity.beaconManager.addRangeNotifier(new RangeNotifier() {
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
                                                                    runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            t2.setText(String.valueOf(dist));
                                                                        }
                                                                    });

                                                                    Log.i("FOUNDD", "The first beacon " + firstBeacon.toString() + " is about " + firstBeacon.getDistance() + " meters away.");
                                                                    if (beaconID.equals(s) && dist <= 0.45) {

                                                                        //  locationTracker.stopLocationService(getActivity());
                                                                        MainActivity.beaconManager.unbind(MainActivity.this);
                                                                        //   MainActivity.beaconManager.disableForegroundServiceScanning();
                                                                        MainActivity.beaconManager.removeAllRangeNotifiers();
                                                                        MainActivity.beaconManager.applySettings();

                                                                        //MainActivity.beaconManager.setForegroundBetweenScanPeriod(30000);
                                                                        if (level == 3) {
                                                                            HomeFragment.abc = false;
                                                                            clueTextView.setBackgroundColor(MainActivity.resources.getColor(R.color.confirmGreen));
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
                                                                                                UserDatabaseReference.child("Users").child(UID).child("points").setValue(HomeFragment.points + 5);
                                                                                                l = d.getTime();
                                                                                                UserDatabaseReference.child("Users").child(UID).child("Time" + String.valueOf(level)).setValue(l);
                                                                                                UserDatabaseReference.child("Leaderboard").child(UID).setValue(new LeaderBoardOBject(HomeFragment.name, level, HomeFragment.points, l));
                                                                                                updateClue();
                                                                                                HomeFragment.abc = true;
                                                                                            } else {
                                                                                                Toast.makeText(MainActivity.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        }
                                                                                    });
                                                                            alertDialog.show();
                                                                            break;

                                                                        } else {
                                                                            runOnUiThread(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    clueTextView.setBackgroundColor(MainActivity.resources.getColor(R.color.confirmGreen));
                                                                                }
                                                                            });

                                                                            HomeFragment.abc = true;
                                                                            UserDatabaseReference = FirebaseDatabase.getInstance().getReference();
                                                                            UserDatabaseReference.child("Users").child(UID).child("level").setValue(level + 1);
                                                                            UserDatabaseReference.child("Users").child(UID).child("points").setValue(HomeFragment.points + 5);
                                                                            l = d.getTime();
                                                                            UserDatabaseReference.child("Users").child(UID).child("Time" + String.valueOf(level)).setValue(l);
                                                                            UserDatabaseReference.child("Leaderboard").child(UID).setValue(new LeaderBoardOBject(HomeFragment.name, level, HomeFragment.points, l));
                                                                            updateClue();
                                                                            break;
                                                                        }


                                                                    }

                                                                    beacons.remove(firstBeacon);
                                                                }
                                                                beacons.clear();


                                                            }
                                                        }
                                                    }
        );
        MainActivity.beaconManager.addMonitorNotifier(new MonitorNotifier() {
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
            MainActivity.beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
        try {
            MainActivity.beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
    }
}
