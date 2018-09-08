package com.techrace.spit.techrace2018;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;

import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.techrace.spit.techrace2018.service.Constants;
import com.techrace.spit.techrace2018.service.TimerService;

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

import static com.techrace.spit.techrace2018.HomeFragment.UserDatabaseReference;

import static com.techrace.spit.techrace2018.HomeFragment.hintButton;
import static com.techrace.spit.techrace2018.HomeFragment.level;
import static com.techrace.spit.techrace2018.HomeFragment.levelString;
import static com.techrace.spit.techrace2018.HomeFragment.timerTextView;
import static com.techrace.spit.techrace2018.HomeFragment.clockView;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BeaconConsumer, LocationAssistant.Listener {
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    static FirebaseAuth mAuth;
    public static int cooldown;
    static int maxWait, routeNo;
    public static Resources resources;
    static SharedPreferences pref;
    static SharedPreferences.Editor prefEditor;

    static boolean beacon = true, manualPass = false;
    static int points;
    String beaconID;
    Intent timerService;
    long currentTime, duration;

    static boolean timerOn = false, event = false;
    Beacon firstBeacon;
    static String selectUID = null;
    int lvlManual, addPointsManual;
    BeaconManager beaconManager;
    String appliedBy = null;
    LocationTracker locationTracker;
    NetworkInfo.State wifi, mobile;
    static boolean jackpotRunning = false;
    static Menu globalMenu;
    int lvl;
    ValueEventListener levelListener, pointsListener, cooldownListener, jackpotListener;
    AlertDialog reverseDialog, jpAlert;
    LocationAssistant assistant;
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        globalMenu = menu;
        return true;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!updateUI(intent)) {
                if (!updateUI(timerService)) {
                    timerService.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
                    startService(timerService);
                    showTimerCompleteNotification();
                }
            }
        }
    };
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_help) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

            TextView head = new TextView(this);
            head.setText(R.string.action_help);
            head.setTextSize(24);
            head.setTextColor(Color.WHITE);
            head.setPadding(0, 0, 0, 16);

            TextView textView = new TextView(this);
            textView.setTextColor(Color.WHITE);
            textView.setText(R.string.scan_help);
            textView.setTextSize(18);
            textView.setPadding(0, 0, 0, 16);

            Button button = new Button(this);
            button.setBackgroundColor(Color.parseColor("#333333"));
            button.setText(R.string.scan_manual);
            button.setTextColor(getResources().getColor(R.color.colorAccent));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetDialog.dismiss();

                    final BottomSheetDialog bottomSheet = new BottomSheetDialog(MainActivity.this);

                    TextView textView = new TextView(MainActivity.this);
                    textView.setText(R.string.manual_desc);
                    textView.setTextSize(20);
                    textView.setTextColor(Color.WHITE);

                    final EditText codeText = new EditText(MainActivity.this);
                    codeText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    Button button = new Button(MainActivity.this);
                    button.setBackgroundColor(Color.parseColor("#333333"));
                    button.setText(R.string.action_confirm);
                    button.setTextColor(getResources().getColor(R.color.colorAccent));
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String input = codeText.getText().toString();
                            checkManualPassword(input);
                            codeText.setText("");
                            bottomSheet.dismiss();
                            bottomSheet.cancel();
                        }
                    });

                    LinearLayout linearLayout = new LinearLayout(MainActivity.this);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    linearLayout.setBackgroundColor(Color.DKGRAY);
                    linearLayout.setPadding(48, 64, 48, 64);
                    linearLayout.addView(textView);
                    linearLayout.addView(codeText);
                    linearLayout.addView(button);

                    bottomSheetDialog.setTitle(R.string.action_help);
                    bottomSheetDialog.setContentView(linearLayout);
                    bottomSheetDialog.setCanceledOnTouchOutside(true);
                    bottomSheetDialog.show();
                }
            });

            LinearLayout linearLayout = new LinearLayout(MainActivity.this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setBackgroundColor(Color.DKGRAY);
            linearLayout.setPadding(48, 48, 48, 64);
            linearLayout.addView(head);
            linearLayout.addView(textView);
            linearLayout.addView(button);

            bottomSheetDialog.setTitle(R.string.action_help);
            bottomSheetDialog.setContentView(linearLayout);
            bottomSheetDialog.setCanceledOnTouchOutside(true);
            bottomSheetDialog.show();
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onStart() {
        super.onStart();
        timerService = new Intent(this, TimerService.class);
        //Register broadcast if service is already running
        if (isMyServiceRunning(TimerService.class)) {
            registerReceiver(broadcastReceiver, new IntentFilter(Constants.ACTION.BROADCAST_ACTION));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
//        ActionBar actionBar;
//        actionBar=getActionBar();
//        ColorDrawable colorDrawable=new ColorDrawable(Color.parseColor("#88000000"));
//        actionBar.setBackgroundDrawable(colorDrawable);
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
        pref = MainActivity.this.getSharedPreferences(AppConstants.techRacePref, MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        routeNo = pref.getInt("Route", 1);
        ConnectivityManager conMan = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        assistant = new LocationAssistant(this, this, LocationAssistant.Accuracy.HIGH, 5000, false);
        //wifi
//        IntentFilter intentFilter=new IntentFilter(Constants.ACTION.BROADCAST_ACTION);
//        this.registerReceiver(broadcastReceiver,intentFilter);
        wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        levelListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                level = dataSnapshot.getValue(Integer.class);
                Log.i("LEVEL", "" + level);
                if (level != pref.getInt("Level", -1)) {
                    HomeFragment.hintView.setVisibility(View.INVISIBLE);
                    prefEditor = pref.edit();
                    prefEditor.putString(AppConstants.hintPref, "").apply();
                    prefEditor = pref.edit();
                    prefEditor.putInt(AppConstants.levelPref, level).apply();

                }


                if (level != 13) {

                    HomeFragment.imgViewHome.setVisibility(View.GONE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        pointsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                points = dataSnapshot.getValue(Integer.class);
                if (level > 1) {
                    DatabaseReference leadercooldown = FirebaseDatabase.getInstance().getReference().child("Leaderboard");
                    leadercooldown.child(UID).child("Points").setValue(points);
                }
                Log.i("Points updated", "" + points);
                prefEditor = pref.edit();
                prefEditor.putInt(AppConstants.pointsPref, points).apply();
                MenuItem myItem = globalMenu.findItem(R.id.pointsBox);
                myItem.setTitle("" + points);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        cooldownListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cooldown = dataSnapshot.getValue(Integer.class);
                int localCool = pref.getInt(AppConstants.cooldownPref, -1);
                Log.i("local cool", "" + localCool);
                if (localCool == -1) {
                    prefEditor = pref.edit();
                    prefEditor.putInt(AppConstants.cooldownPref, cooldown).apply();
                    localCool = cooldown;
                }
                if (level > 1) {
                    DatabaseReference leadercooldown = FirebaseDatabase.getInstance().getReference().child("Leaderboard");
                    leadercooldown.child(UID).child("Cooldown").setValue(cooldown);
                }
                Log.i("COOLDOWN FOUND", "" + cooldown);
                if (cooldown - localCool > 0) {
                    Log.i("inside1", "ues" + points);
                    if (points >= AppConstants.reversePrice) {
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            v.vibrate(500);
                        }
                        timerOn = false;
                        Log.i("inside", "ues");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder reverseAlertDialog = new AlertDialog.Builder(MainActivity.this);
                                reverseAlertDialog.setTitle("Timer Of " + cooldown + " Minutes Applied").setMessage("Buy Right Back at Ya Card? for 35 points")
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
                                                        reverseReference1.child("Users").child(UID).child("cooldown").setValue(0);
                                                        prefEditor = pref.edit();
                                                        prefEditor.putInt(AppConstants.cooldownPref, 0).apply();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                            }
                                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final DatabaseReference waited = FirebaseDatabase.getInstance().getReference().child("Users").child(UID).child("waited");
                                        waited.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                int w = dataSnapshot.getValue(Integer.class);
                                                waited.setValue(w + 1);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                        prefEditor = pref.edit();
                                        prefEditor.putInt(AppConstants.cooldownPref, cooldown).apply();

                                    }

                                }).setCancelable(false);
//
                                if (reverseDialog != null) {
                                    if (!reverseDialog.isShowing()) {
                                        if (!((Activity) MainActivity.this).isFinishing()) {
                                            reverseDialog.show();
                                        }
                                    }
                                } else {
                                    reverseDialog = reverseAlertDialog.create();
                                    if (!((Activity) MainActivity.this).isFinishing()) {
                                        reverseDialog.show();

                                    }
                                }
                            }
                        });
                    } else {
                        final DatabaseReference waited = FirebaseDatabase.getInstance().getReference().child("Users").child(UID).child("waited");
                        waited.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int w = dataSnapshot.getValue(Integer.class);
                                waited.setValue(w + 1);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        prefEditor = pref.edit();
                        prefEditor.putInt("Cooldown", cooldown).apply();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        jackpotListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {

                    int jp = dataSnapshot.getValue(Integer.class);
                    Log.i("IN JP CHANGE", "" + jp);
                    if (jp == 1) {
                        FirebaseDatabase.getInstance().getReference().child("Users").child(UID).child("jackpot").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                boolean b = dataSnapshot.getValue(Boolean.class);
                                if (!b) {
                                    AlertDialog.Builder jackpotAlertDialog = new AlertDialog.Builder(MainActivity.this)
                                            .setCancelable(false)
                                            .setMessage("Do you want to play the JACKPOT question?")
                                            .setTitle("Jackpot Available")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    FirebaseDatabase.getInstance().getReference().child("Users").child(UID).child("points").setValue(points - AppConstants.jackpotPrice);

                                                    Intent intent = new Intent(MainActivity.this, JackpotActivity.class);
                                                    intent.putExtra("EXTRA", 56);
                                                    startActivity(intent);
                                                    FirebaseDatabase.getInstance().getReference().child("Users").child(UID).child("jackpot").setValue(true);
                                                }
                                            })
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    FirebaseDatabase.getInstance().getReference().child("Users").child(UID).child("jackpot").setValue(true);
                                                }
                                            });
                                    if (jpAlert != null) {
                                        if (!jpAlert.isShowing()) {
                                            if (!((Activity) MainActivity.this).isFinishing()) {
                                                jpAlert.show();
                                            }
                                        }
                                    } else {
                                        jpAlert = jackpotAlertDialog.create();
                                        if (!((Activity) MainActivity.this).isFinishing()) {
                                            jpAlert.show();

                                        }
                                    }
//                                    jpAlert=jackpotAlertDialog.create();
//                                    jpAlert.show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    } else {
                        if (jpAlert != null && jpAlert.isShowing()) {
                            jpAlert.cancel();
                        }
                        if (jackpotRunning) {
                            Log.i("IN jp running", "" + jp);

                            JackpotActivity.jackpot.finish();
                        }


                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        // Log.i("MAUTH",mAuth.getCurrentUser().getDisplayName());
        if (mAuth.getCurrentUser() == null) {

            Intent i = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(i);
        } else {
            points = pref.getInt(AppConstants.pointsPref, 0);
            UID = mAuth.getCurrentUser().getUid();
            pref = MainActivity.this.getSharedPreferences(AppConstants.techRacePref, MODE_PRIVATE);

            UserDatabaseReference = FirebaseDatabase.getInstance().getReference();

//            UserDatabaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("points").addValueEventListener(pointsListener);

        }
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onOptionsItemSelected(item);
                return true;
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // displaySelectedScreen(R.id.home);
        if (level <= 11) {
            maxWait = 2;
        } else {
            maxWait = 1;
        }
        resources = getResources();
        displaySelectedScreen(R.id.home);
        clueLocation = new Location("");

        new HomeFragment().updateClue();


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

    void checkManualPassword(final String manualPassword) {

        DatabaseReference pass = FirebaseDatabase.getInstance().getReference().child("Route " + routeNo).child("Location " + String.valueOf(level)).child("passwords");
        pass.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String skipPass, highPass, mediumPass, lowPass;
                skipPass = dataSnapshot.child("Skip").getValue(String.class);
                highPass = dataSnapshot.child("High").getValue(String.class);
                mediumPass = dataSnapshot.child("Medium").getValue(String.class);
                lowPass = dataSnapshot.child("Low").getValue(String.class);

                if (manualPassword.equals(skipPass)) {

                    Toast.makeText(MainActivity.this, "Updating...", Toast.LENGTH_LONG).show();
                    // if (cooldown == 0) {

                    timerOn = false;
                    MainActivity.beacon = true;
                    UserDatabaseReference = FirebaseDatabase.getInstance().getReference();
                    final int levelLocal = level;
                    FirebaseDatabase.getInstance().getReference().child("Route " + routeNo).child("Location " + levelLocal).child("Crossed").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int c = dataSnapshot.getValue(Integer.class);
                            FirebaseDatabase.getInstance().getReference().child("Route " + routeNo).child("Location " + levelLocal).child("Crossed").setValue(c + 1);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    lvlManual = level;
                    prefEditor = pref.edit();
                    prefEditor.putString(AppConstants.clueLevelPref + lvlManual, levelString).apply();
                    UserDatabaseReference.child("Users").child(UID).child("points").setValue(points + 5);
                    UserDatabaseReference.child("Users").child(UID).child("Time " + String.valueOf(lvlManual)).setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseDatabase.getInstance().getReference().child("Users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    long ts = dataSnapshot.child("Time " + String.valueOf(lvlManual)).getValue(Long.class);
                                    Log.i("ts", String.valueOf(ts));
                                    DatabaseReference UserDatabaseReference5 = FirebaseDatabase.getInstance().getReference();
                                    UserDatabaseReference5.child("Leaderboard").child(UID).setValue(new LeaderBoardOBject(HomeFragment.name, lvlManual, points, ts, cooldown, UID));


                                    UserDatabaseReference5.child("Users").child(UID).child("level").setValue(level + 1);
                                    prefEditor = pref.edit();
                                    prefEditor.putString("Note", "").apply();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            timerTextView.setText("");
                                        }
                                    });

                                    beacon = false;
                                    if (beaconManager != null) {
                                        beaconManager.unbind(MainActivity.this);
                                        beaconManager.removeAllRangeNotifiers();
                                        beaconManager.applySettings();
                                    }
                                    hintButton.setEnabled(true);
                                    prefEditor = pref.edit();
                                    prefEditor.putString(AppConstants.hintPref, "");
                                    new HomeFragment().updateClue();
                                    event = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });


                } else if (manualPassword.equals(highPass)) {
                    Toast.makeText(MainActivity.this, "Updating...", Toast.LENGTH_LONG).show();
                    // if (cooldown == 0) {
                    timerOn = false;
                    MainActivity.beacon = true;
                    UserDatabaseReference = FirebaseDatabase.getInstance().getReference();
                    final int levelLocal = level;
                    FirebaseDatabase.getInstance().getReference().child("Route " + routeNo).child("Location " + levelLocal).child("Crossed").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int c = dataSnapshot.getValue(Integer.class);
                            FirebaseDatabase.getInstance().getReference().child("Route " + routeNo).child("Location " + levelLocal).child("Crossed").setValue(c + 1);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    prefEditor = pref.edit();
                    prefEditor.putString(AppConstants.clueLevelPref + level, levelString).apply();
                    lvlManual = level;
                    if (lvlManual == 4 || lvlManual == 13) {
                        addPointsManual = 5;
                    } else if (lvlManual == 9) {
                        addPointsManual = 4;
                    } else {
                        addPointsManual = 0;
                    }

                    UserDatabaseReference.child("Users").child(UID).child("points").setValue(points + 5 + addPointsManual);
                    UserDatabaseReference.child("Users").child(UID).child("Time " + String.valueOf(lvlManual)).setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseDatabase.getInstance().getReference().child("Users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    long ts = dataSnapshot.child("Time " + String.valueOf(lvlManual)).getValue(Long.class);
                                    Log.i("ts", String.valueOf(ts));
                                    DatabaseReference UserDatabaseReference5 = FirebaseDatabase.getInstance().getReference();
                                    UserDatabaseReference5.child("Leaderboard").child(UID).setValue(new LeaderBoardOBject(HomeFragment.name, lvlManual, points, ts, cooldown, UID));


                                    UserDatabaseReference5.child("Users").child(UID).child("level").setValue(level + 1);
                                    prefEditor = pref.edit();
                                    prefEditor.putString("Note", "").apply();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            timerTextView.setText("");
                                        }
                                    });

                                    beacon = false;
                                    if (beaconManager != null) {
                                        beaconManager.unbind(MainActivity.this);
                                        beaconManager.removeAllRangeNotifiers();
                                        beaconManager.applySettings();
                                    }
                                    hintButton.setEnabled(true);
                                    prefEditor = pref.edit();
                                    prefEditor.putString(AppConstants.hintPref, "");
                                    new HomeFragment().updateClue();
                                    event = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });


                } else if (manualPassword.equals(mediumPass)) {
                    Toast.makeText(MainActivity.this, "Updating...", Toast.LENGTH_LONG).show();
                    // if (cooldown == 0) {
                    timerOn = false;
                    MainActivity.beacon = true;
                    UserDatabaseReference = FirebaseDatabase.getInstance().getReference();
                    final int levelLocal = level;
                    FirebaseDatabase.getInstance().getReference().child("Route " + routeNo).child("Location " + levelLocal).child("Crossed").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int c = dataSnapshot.getValue(Integer.class);
                            FirebaseDatabase.getInstance().getReference().child("Route " + routeNo).child("Location " + levelLocal).child("Crossed").setValue(c + 1);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    prefEditor = pref.edit();
                    prefEditor.putString(AppConstants.clueLevelPref + level, levelString).apply();
                    lvlManual = level;
                    if (lvlManual == 4 || lvlManual == 13) {
                        addPointsManual = 5;
                    } else if (lvlManual == 9) {
                        addPointsManual = 2;
                    } else {
                        addPointsManual = 0;
                    }

                    UserDatabaseReference.child("Users").child(UID).child("points").setValue(points + 5 + addPointsManual);
                    UserDatabaseReference.child("Users").child(UID).child("Time " + String.valueOf(lvlManual)).setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseDatabase.getInstance().getReference().child("Users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    long ts = dataSnapshot.child("Time " + String.valueOf(lvlManual)).getValue(Long.class);
                                    Log.i("ts", String.valueOf(ts));
                                    DatabaseReference UserDatabaseReference5 = FirebaseDatabase.getInstance().getReference();
                                    UserDatabaseReference5.child("Leaderboard").child(UID).setValue(new LeaderBoardOBject(HomeFragment.name, lvlManual, points, ts, cooldown, UID));


                                    UserDatabaseReference5.child("Users").child(UID).child("level").setValue(level + 1);
                                    prefEditor = pref.edit();
                                    prefEditor.putString("Note", "").apply();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            timerTextView.setText("");
                                        }
                                    });

                                    beacon = false;
                                    if (beaconManager != null) {
                                        beaconManager.unbind(MainActivity.this);
                                        beaconManager.removeAllRangeNotifiers();
                                        beaconManager.applySettings();
                                    }
                                    hintButton.setEnabled(true);
                                    prefEditor = pref.edit();
                                    prefEditor.putString(AppConstants.hintPref, "");
                                    new HomeFragment().updateClue();
                                    event = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });


                } else if (manualPassword.equals(lowPass)) {
                    Toast.makeText(MainActivity.this, "Updating...", Toast.LENGTH_LONG).show();
                    // if (cooldown == 0) {
                    timerOn = false;
                    MainActivity.beacon = true;
                    UserDatabaseReference = FirebaseDatabase.getInstance().getReference();
                    final int levelLocal = level;
                    FirebaseDatabase.getInstance().getReference().child("Route " + routeNo).child("Location " + levelLocal).child("Crossed").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int c = dataSnapshot.getValue(Integer.class);
                            FirebaseDatabase.getInstance().getReference().child("Route " + routeNo).child("Location " + levelLocal).child("Crossed").setValue(c + 1);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    prefEditor = pref.edit();
                    prefEditor.putString(AppConstants.clueLevelPref + level, levelString).apply();
                    lvlManual = level;
                    if (lvlManual == 4) {
                        addPointsManual = -5;
                    } else if (lvlManual == 9) {
                        addPointsManual = -4;
                    } else if (lvlManual == 13) {
                        addPointsManual = -5;
                    } else {
                        addPointsManual = 0;
                    }

                    UserDatabaseReference.child("Users").child(UID).child("points").setValue(points + 5 + addPointsManual);
                    UserDatabaseReference.child("Users").child(UID).child("Time " + String.valueOf(lvlManual)).setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseDatabase.getInstance().getReference().child("Users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    long ts = dataSnapshot.child("Time " + String.valueOf(lvlManual)).getValue(Long.class);
                                    Log.i("ts", String.valueOf(ts));
                                    DatabaseReference UserDatabaseReference5 = FirebaseDatabase.getInstance().getReference();
                                    UserDatabaseReference5.child("Leaderboard").child(UID).setValue(new LeaderBoardOBject(HomeFragment.name, lvlManual, points, ts, cooldown, UID));


                                    UserDatabaseReference5.child("Users").child(UID).child("level").setValue(level + 1);
                                    prefEditor = pref.edit();
                                    prefEditor.putString("Note", "").apply();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            timerTextView.setText("");
                                        }
                                    });
                                    beacon = false;
                                    if (beaconManager != null) {
                                        beaconManager.unbind(MainActivity.this);
                                        beaconManager.removeAllRangeNotifiers();
                                        beaconManager.applySettings();
                                    }
                                    hintButton.setEnabled(true);
                                    prefEditor = pref.edit();
                                    prefEditor.putString(AppConstants.hintPref, "");
                                    new HomeFragment().updateClue();
                                    event = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });

                } else {
                    Toast.makeText(MainActivity.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        assistant.stop();
        locationTracker.stopLocationService(getBaseContext());
        //  UserDatabaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("level").removeEventListener(levelListener);
        // UserDatabaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("points").removeEventListener(pointsListener);
        if (UserDatabaseReference != null && mAuth.getCurrentUser() != null) {
            if (pref.getBoolean("CoolAttached", true) == true) {
                UserDatabaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("cooldown").removeEventListener(cooldownListener);
                prefEditor = pref.edit();
                prefEditor.putBoolean("CoolAttached", false).commit();
            }

        }
        if (reverseDialog != null && reverseDialog.isShowing()) {
            reverseDialog.cancel();
        }
        if (jpAlert != null && jpAlert.isShowing()) {
            jpAlert.cancel();
        }
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

    //    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//    private void verifyBluetooth() {
//
//        try {
//            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
//                Toast.makeText(MainActivity.this, "Turn On Bluetooth In Hot Region", Toast.LENGTH_LONG).show();
//            }
//        }
//        catch (RuntimeException e) {
//            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("Bluetooth LE not available");
//            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
//            builder.setPositiveButton(android.R.string.ok, null);
//
//            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//
//                @Override
//                public void onDismiss(DialogInterface dialog) {
//                    finish();
//                    System.exit(0);
//                }
//
//            });
//
//            builder.show();
//
//        }
//
//    }
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
            case R.id.about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            case R.id.feed:
                startActivity(new Intent(MainActivity.this, FeedActivity.class));
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
        //locationTracker.stopLocationService(getBaseContext());
        super.onDestroy();
        if (beaconManager != null) {
            if (beaconManager.isBound(MainActivity.this)) {
                beaconManager.setBackgroundMode(false);
                beaconManager.unbind(MainActivity.this);
            }
        }



    }

    @Override
    protected void onStop() {
        super.onStop();
        if (pref.getBoolean("JacpotAttached", false)) {
            UserDatabaseReference.child("Jackpot").removeEventListener(jackpotListener);
            prefEditor = pref.edit();
            prefEditor.putBoolean("JacpotAttached", false).apply();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        assistant.start();
        Log.i("Resume", "RESUMED");
        if (mAuth != null) {
            if (mAuth.getCurrentUser() != null) {

                UID = mAuth.getCurrentUser().getUid();
                pref = MainActivity.this.getSharedPreferences(AppConstants.techRacePref, MODE_PRIVATE);
                points = pref.getInt(AppConstants.pointsPref, 0);
                UserDatabaseReference = FirebaseDatabase.getInstance().getReference();
                Log.i("coolatt111", "" + pref.getBoolean("CoolAttached", false));
                if (pref.getBoolean("CoolAttached", false) == false) {
                    Log.i("coolatt", "" + pref.getBoolean("CoolAttached", false));
                    prefEditor = pref.edit();
                    prefEditor.putBoolean("CoolAttached", true).commit();
                    UserDatabaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("cooldown").addValueEventListener(cooldownListener);
                    prefEditor = pref.edit();
                }
                cooldown = pref.getInt(AppConstants.cooldownPref, 0);
                UserDatabaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("level").addValueEventListener(levelListener);
                UserDatabaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("points").addValueEventListener(pointsListener);
                UserDatabaseReference.child("Jackpot").child("Start").addValueEventListener(jackpotListener);
                prefEditor = pref.edit();
                prefEditor.putBoolean("JacpotAttached", true).apply();
                if (pref.getInt(AppConstants.levelPref, -1) == 13) {
                    if (pref.getInt("Route", routeNo) == 1) {
                        HomeFragment.imgViewHome.setImageResource(R.drawable.untitled_1crop);
                    } else {
                        HomeFragment.imgViewHome.setImageResource(R.drawable.untitled_2crop);
                    }
                }
            }


            try {
                LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                if (!gpsEnabled || !networkEnabled) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setMessage("Turn on Location").setCancelable(false);
                    dialog.setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                            //get gps
                        }
                    });
                    dialog.show();
                }
                locationTracker = new LocationTracker("my.action")
                        .setInterval(10000)
                        .setGps(true)
                        .setNetWork(false);
                locationTracker.currentLocation(new CurrentLocationReceiver(new CurrentLocationListener() {
                    @Override
                    public void onCurrentLocation(Location location) {
                        //Toast.makeText(myView.getContext(), "Currently:" + location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();

                        double distanceinmetres = clueLocation.distanceTo(location);

                        // Toast.makeText(myView.getContext(), "Distance: " + distanceinmetres, Toast.LENGTH_SHORT).show();

                        //     if (mobile == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTED) {
                        if (!timerOn && !event) {
                            if (distanceinmetres <= 250) {
                                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
                                    Toast.makeText(MainActivity.this, "Turn On Bluetooth", Toast.LENGTH_SHORT).show();
                                }
                                if (beacon) {
                                    clueRelativeLayout.setBackgroundColor(MainActivity.resources.getColor(R.color.hotRed));
                                    beaconManager = BeaconManager.getInstanceForApplication(MainActivity.this);
                                    // To detect proprietary beacons, you must add a line like below corresponding to your beacon
                                    // type.  Do a web search for "setBeaconLayout" to get the proper expression.
                                    beaconManager.getBeaconParsers().add(new BeaconParser().
                                            setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
                                    beaconManager.setForegroundBetweenScanPeriod(1000);
                                    beaconManager.bind(MainActivity.this);
                                } else {
                                    beaconManager.removeAllMonitorNotifiers();
                                    beaconManager.applySettings();
                                    beaconManager.unbind(MainActivity.this);
                                }

                            } else {
                                clueRelativeLayout.setBackgroundColor(MainActivity.resources.getColor(R.color.coldBlue));

                            }
                        }
                        //  }

                    }

                    @Override
                    public void onPermissionDiened() {
                    }
                })).start(getBaseContext(), MainActivity.this);
            } catch (Exception e) {
                Log.i("EROOR", "" + e);


            }
        }
    }

    @Override
    public void onNeedLocationPermission() {

    }

    @Override
    public void onExplainLocationPermission() {

    }

    @Override
    public void onLocationPermissionPermanentlyDeclined(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {

    }

    @Override
    public void onNeedLocationSettingsChange() {

    }

    @Override
    public void onFallBackToSystemSettings(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {

    }

    @Override
    public void onNewLocationAvailable(Location location) {

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
                                                       firstBeacon = beacons.iterator().next();


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

                                                       Log.i("FOUNDD", "The first beacon " + firstBeacon.toString() + " is about " + firstBeacon.getDistance() + " meters away.");
                                                       if (beaconID.equals(s) && dist <= 0.40) {


                                                           // locationTracker.stopLocationService(getBaseContext());

                                                           runOnUiThread(new Runnable() {
                                                               @Override
                                                               public void run() {
                                                                   clueRelativeLayout.setBackgroundColor(MainActivity.resources.getColor(R.color.confirmGreen));
                                                               }
                                                           });
                                                           if (level <= 7) {
                                                               DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Route " + routeNo).child("Location 7").child("Count");
                                                               databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                   @Override
                                                                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                       int cnt = dataSnapshot.getValue(Integer.class);
                                                                       if (cnt >= 75) {
                                                                           DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(UID).child("points");
                                                                           db.setValue(0);
                                                                           Toast.makeText(MainActivity.this, "You Have Been Eliminated. Reset Points to 0.", Toast.LENGTH_LONG).show();
                                                                           if (level == 7) {
                                                                               db = FirebaseDatabase.getInstance().getReference().child("Users").child(UID).child("level");
                                                                               db.setValue(1);
                                                                           }
                                                                       }
                                                                   }

                                                                   @Override
                                                                   public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                   }
                                                               });
                                                           } else if (level > 7 && level <= 11) {
                                                               DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Route " + routeNo).child("Location 11").child("Count");
                                                               databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                   @Override
                                                                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                       int cnt = dataSnapshot.getValue(Integer.class);
                                                                       if (cnt >= 25) {
                                                                           DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(UID).child("points");
                                                                           db.setValue(0);
                                                                           Toast.makeText(MainActivity.this, "You Have Been Eliminated. Reset Points to 0.", Toast.LENGTH_LONG).show();
                                                                           if (level == 11) {
                                                                               db = FirebaseDatabase.getInstance().getReference().child("Users").child(UID).child("level");
                                                                               db.setValue(1);
                                                                           }
                                                                       }
                                                                   }

                                                                   @Override
                                                                   public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                   }
                                                               });
                                                           }
                                                           if (level == 4 || level == 9 || level == 13) {
                                                               event = true;
                                                               if (cooldown == 0) {
                                                                   runOnUiThread(new Runnable() {
                                                                       @Override
                                                                       public void run() {
                                                                           timerTextView.setText("Meet The Volunteer To Continue");
                                                                       }
                                                                   });

                                                                   break;
                                                               } else {
                                                                   if (!timerOn) {
                                                                       timerOn = true;
//                                                                       Intent intent = new Intent(MainActivity.this, NotificationReceiver.class);
//                                                                       PendingIntent pendingIntentforAlarm = PendingIntent.getBroadcast(
//                                                                               MainActivity.this, 9999, intent, 0);
//
//                                                                       AlarmManager alarmManager = (AlarmManager) MainActivity.this.getSystemService(ALARM_SERVICE);
//
//
//                                                                       alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
//                                                                               + (cooldown * 60000), pendingIntentforAlarm);
//
//                                                                       Log.i("cool", "" + cooldown);
//
//                                                                       final String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
//                                                                       runOnUiThread(new Runnable() {
//                                                                           @Override
//                                                                           public void run() {
//                                                                               HomeFragment.timerTextView.setText("Timer of " + cooldown + " mins is set on " + currentDateTimeString);
//                                                                           }
//                                                                       });
//                                                                       prefEditor = pref.edit();
//                                                                       prefEditor.putString("Note", "Timer of " + cooldown + " mins is set on " + currentDateTimeString).apply();
//
//                                                                       NotificationCompat.Builder builderalarm =
//                                                                               new NotificationCompat.Builder(MainActivity.this)
//                                                                                       .setSmallIcon(R.mipmap.ic_launcher)
//                                                                                       .setContentTitle("Please Wait")
//                                                                                       .setContentText("Timer of " + cooldown + " mins is set on " + currentDateTimeString)
//                                                                                       .setOngoing(true)
//
//                                                                                       .setAutoCancel(false)
//
//                                                                                       .setTimeoutAfter(cooldown * 60000).setChannelId("Timer");
//                                                                       NotificationChannel mChannel;
//                                                                       NotificationManager notificationManagerforAlarm =
//                                                                               (NotificationManager) MainActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
//                                                                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                                                           mChannel = new NotificationChannel("Timer", "Timer", NotificationManager.IMPORTANCE_DEFAULT);
//                                                                           notificationManagerforAlarm.createNotificationChannel(mChannel);
//                                                                       }
//
//
//                                                                       notificationManagerforAlarm.notify(1, builderalarm.build());

                                                                       runOnUiThread(new Runnable() {
                                                                           @Override
                                                                           public void run() {
                                                                               HomeFragment.timerTextView.setText("Timer of " + cooldown + " minutes is set.");
                                                                           }
                                                                       });
                                                                       if (!isMyServiceRunning(TimerService.class)) {
                                                                           timerService.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                                                                           duration = cooldown * 60000;
                                                                           prefEditor = pref.edit();
                                                                           prefEditor.putLong("Duration", duration).apply();
                                                                           timerService.putExtra(Constants.TIMER.DURATION, duration);
                                                                           startService(timerService);
                                                                           MainActivity.this.registerReceiver(broadcastReceiver, new IntentFilter(Constants.ACTION.BROADCAST_ACTION));
                                                                           Log.i("here", "in");
                                                                       }

                                                                   }
                                                               }
                                                           } else {
                                                               if (cooldown == 0) {


                                                                   if (level == 7) {

                                                                       DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Route " + routeNo).child("Location 7").child("Count");
                                                                       db.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                           @Override
                                                                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                               int cnt = dataSnapshot.getValue(Integer.class);
                                                                               FirebaseDatabase.getInstance().getReference().child("Route " + routeNo).child("Location 7").child("Count").setValue(cnt + 1);
                                                                               if (routeNo == 1) {
                                                                                   FirebaseDatabase.getInstance().getReference().child("Route 2").child("Location 7").child("Count").setValue(cnt + 1);
                                                                               } else {
                                                                                   FirebaseDatabase.getInstance().getReference().child("Route 1").child("Location 7").child("Count").setValue(cnt + 1);
                                                                               }
                                                                           }

                                                                           @Override
                                                                           public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                           }
                                                                       });
                                                                   }
                                                                   if (level == 11) {
                                                                       Log.i("In", "11");
                                                                       DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("waited");
                                                                       databaseReference.setValue(0);
                                                                       DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Route " + routeNo).child("Location 11").child("Count");
                                                                       db.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                           @Override
                                                                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                               int cnt = dataSnapshot.getValue(Integer.class);
                                                                               FirebaseDatabase.getInstance().getReference().child("Route " + routeNo).child("Location 11").child("Count").setValue(cnt + 1);
                                                                               if (routeNo == 1) {
                                                                                   FirebaseDatabase.getInstance().getReference().child("Route 2").child("Location 11").child("Count").setValue(cnt + 1);
                                                                               } else {
                                                                                   FirebaseDatabase.getInstance().getReference().child("Route 1").child("Location 11").child("Count").setValue(cnt + 1);
                                                                               }
                                                                               if (cnt % 2 == 0) {
                                                                                   routeNo = 2;
                                                                                   prefEditor = pref.edit();
                                                                                   prefEditor.putInt("Route", 2).apply();

                                                                               } else {
                                                                                   routeNo = 1;
                                                                                   prefEditor = pref.edit();
                                                                                   prefEditor.putInt("Route", 1).apply();
                                                                               }
                                                                               FirebaseDatabase.getInstance().getReference().child("Users").child(UID).child("new route").setValue(routeNo);
                                                                               Log.i("New route", "" + pref.getInt("Route", 1));
                                                                           }

                                                                           @Override
                                                                           public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                           }
                                                                       });
                                                                   }
                                                                   prefEditor = pref.edit();
                                                                   prefEditor.putString(AppConstants.clueLevelPref + level, levelString).apply();
                                                                   UserDatabaseReference = FirebaseDatabase.getInstance().getReference();
                                                                   lvl = level;


                                                                   UserDatabaseReference.child("Users").child(UID).child("Time " + String.valueOf(level)).setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                       @Override
                                                                       public void onComplete(@NonNull Task<Void> task) {
                                                                           if (level > 1) {

                                                                               FirebaseDatabase.getInstance().getReference().child("Users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                   @Override
                                                                                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                       Log.i("loclvl", "" + level);
                                                                                       beaconManager.unbind(MainActivity.this);
                                                                                       beaconManager.removeAllRangeNotifiers();
                                                                                       //beaconManager.disableForegroundServiceScanning();
                                                                                       beaconManager.applySettings();
                                                                                       final int levelLocal = level;
                                                                                       FirebaseDatabase.getInstance().getReference().child("Route " + routeNo).child("Location " + levelLocal).child("Crossed").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                           @Override
                                                                                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                               int c = dataSnapshot.getValue(Integer.class);
                                                                                               FirebaseDatabase.getInstance().getReference().child("Route " + routeNo).child("Location " + levelLocal).child("Crossed").setValue(c + 1);
                                                                                           }

                                                                                           @Override
                                                                                           public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                           }
                                                                                       });
                                                                                       long prevTime = dataSnapshot.child("Time " + (lvl - 1)).getValue(Long.class);
                                                                                       Log.i("prevTime", "" + prevTime);
                                                                                       long currentTime = dataSnapshot.child("Time " + lvl).getValue(Long.class);
                                                                                       Log.i("currentTime", "" + currentTime);
                                                                                       Log.i("System currentTime", "" + System.currentTimeMillis());
                                                                                       long diff = currentTime - prevTime;
                                                                                       Log.i("dddiffff", "" + diff);

                                                                                       DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                                                                       ref.child("Leaderboard").child(UID).setValue(new LeaderBoardOBject(HomeFragment.name, lvl, points, currentTime, cooldown, UID));
                                                                                       int l1 = pref.getInt("Level 1", 0);
                                                                                       l1 = l1 * 60000;
                                                                                       int l2 = pref.getInt("Level 2", 0);
                                                                                       l2 = l2 * 60000;

                                                                                       if (diff <= l1) {
                                                                                           ref.child("Users").child(UID).child("points").setValue(points + 7);
                                                                                       } else if (diff > l1 && diff <= l2) {
                                                                                           ref.child("Users").child(UID).child("points").setValue(points + 5);
                                                                                       } else if (diff > l2) {
                                                                                           ref.child("Users").child(UID).child("points").setValue(points + 3);
                                                                                       }
                                                                                       ref.child("Users").child(UID).child("level").setValue(level + 1);
                                                                                       beacon = false;
                                                                                       timerOn = false;
                                                                                       hintButton.setEnabled(true);
                                                                                       prefEditor = pref.edit();
                                                                                       prefEditor.putString(AppConstants.hintPref, "");
                                                                                       prefEditor.putString("Note", "").apply();
                                                                                       new HomeFragment().updateClue();

                                                                                   }

                                                                                   @Override
                                                                                   public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                   }
                                                                               });
                                                                           } else {
                                                                               FirebaseDatabase.getInstance().getReference().child("Users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                   @Override
                                                                                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                                       beaconManager.unbind(MainActivity.this);
                                                                                       beaconManager.removeAllRangeNotifiers();
                                                                                       //beaconManager.disableForegroundServiceScanning();
                                                                                       beaconManager.applySettings();
                                                                                       final int levelLocal = level;
                                                                                       FirebaseDatabase.getInstance().getReference().child("Route " + routeNo).child("Location " + levelLocal).child("Crossed").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                           @Override
                                                                                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                               int c = dataSnapshot.getValue(Integer.class);
                                                                                               FirebaseDatabase.getInstance().getReference().child("Route " + routeNo).child("Location " + levelLocal).child("Crossed").setValue(c + 1);
                                                                                           }

                                                                                           @Override
                                                                                           public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                           }
                                                                                       });
                                                                                       long currentTime = dataSnapshot.child("Time " + lvl).getValue(Long.class);
                                                                                       Log.i("currentTime", "" + currentTime);
                                                                                       DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                                                                       ref.child("Leaderboard").child(UID).setValue(new LeaderBoardOBject(HomeFragment.name, lvl, points, currentTime, cooldown, UID));
                                                                                       ref.child("Users").child(UID).child("points").setValue(points + 7);

                                                                                       ref.child("Users").child(UID).child("level").setValue(level + 1);

                                                                                       beacon = false;
                                                                                       timerOn = false;
                                                                                       hintButton.setEnabled(true);
                                                                                       prefEditor = pref.edit();
                                                                                       prefEditor.putString(AppConstants.hintPref, "");
                                                                                       prefEditor.putString("Note", "").apply();
                                                                                       new HomeFragment().updateClue();
                                                                                   }

                                                                                   @Override
                                                                                   public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                   }
                                                                               });
                                                                           }

                                                                       }
                                                                   });

                                                                   break;


                                                               } else {


                                                                   if (!timerOn) {
                                                                       timerOn = true;
//                                                                       Intent intent = new Intent(MainActivity.this, NotificationReceiver.class);
//                                                                       PendingIntent pendingIntentforAlarm = PendingIntent.getBroadcast(
//                                                                               MainActivity.this, 9999, intent, 0);
//
//                                                                       AlarmManager alarmManager = (AlarmManager) MainActivity.this.getSystemService(ALARM_SERVICE);
//
//                                                                       alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
//                                                                               + (cooldown * 59000), pendingIntentforAlarm);
//
//                                                                       Log.i("cool", "" + cooldown);
//
//                                                                       final String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
//                                                                       runOnUiThread(new Runnable() {
//                                                                           @Override
//                                                                           public void run() {
//                                                                               HomeFragment.timerTextView.setText("Timer of " + cooldown + " mins is set on " + currentDateTimeString);
//                                                                           }
//                                                                       });
//                                                                       prefEditor = pref.edit();
//                                                                       prefEditor.putString("Note", "Timer of " + cooldown + " mins is set on " + currentDateTimeString).apply();
//
//                                                                       NotificationCompat.Builder builderalarm =
//                                                                               new NotificationCompat.Builder(MainActivity.this)
//                                                                                       .setSmallIcon(R.mipmap.ic_launcher)
//                                                                                       .setContentTitle("Please Wait")
//                                                                                       .setContentText("Timer of " + cooldown + " mins is set on " + currentDateTimeString)
//                                                                                       .setOngoing(true)
//                                                                                       .setAutoCancel(false)
//                                                                                       .setTimeoutAfter(cooldown * 60000).setChannelId("Timer");
//                                                                       NotificationChannel mChannel;
//                                                                       NotificationManager notificationManagerforAlarm =
//                                                                               (NotificationManager) MainActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
//                                                                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                                                           mChannel = new NotificationChannel("Timer", "Timer", NotificationManager.IMPORTANCE_DEFAULT);
//                                                                           notificationManagerforAlarm.createNotificationChannel(mChannel);
//                                                                       }
//
//
//                                                                       notificationManagerforAlarm.notify(1, builderalarm.build());
                                                                       runOnUiThread(new Runnable() {
                                                                           @Override
                                                                           public void run() {
                                                                               HomeFragment.timerTextView.setText("Timer of " + cooldown + " minutes is set.");
                                                                           }
                                                                       });
                                                                       Log.i("service run", "" + isMyServiceRunning(TimerService.class));
                                                                       if (!isMyServiceRunning(TimerService.class)) {
                                                                           timerService.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                                                                           duration = cooldown * 60000;
                                                                           prefEditor = pref.edit();
                                                                           prefEditor.putLong("Duration", duration).apply();
                                                                           timerService.putExtra(Constants.TIMER.DURATION, duration);
                                                                           startService(timerService);
                                                                           MainActivity.this.registerReceiver(broadcastReceiver, new IntentFilter(Constants.ACTION.BROADCAST_ACTION));
                                                                           Log.i("here", "in");
                                                                       }

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
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId1", null, null, null));
        } catch (RemoteException e) {
        }
    }

    @Override
    public void onError(LocationAssistant.ErrorType type, String message) {

    }

    @Override
    public void onMockLocationsDetected(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {
        //Toast.makeText(MainActivity.this, "Stop Mocking Location", Toast.LENGTH_LONG).show();
        //finishAffinity();
//        NotificationCompat.Builder fakeGPSNotifi=new NotificationCompat.Builder(MainActivity.this);
//        fakeGPSNotifi.setContentTitle("DO NOT USE FAKE GPS")
//                .setContentText("Cheaters will be eliminate");
//        NotificationChannel mChannel;
//        NotificationManager notificationManagerforFake =
//               (NotificationManager) MainActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//           mChannel = new NotificationChannel("Fake", "Faked", NotificationManager.IMPORTANCE_DEFAULT);
//           notificationManagerforFake.createNotificationChannel(mChannel);
//        }
//        if (notificationManagerforFake != null) {
//            notificationManagerforFake.notify(123,fakeGPSNotifi.build());
//        }
    }

    //Receives the extra to update current timer and then updates the textView.
    public boolean updateUI(Intent intent) {
        if (!intent.hasExtra(Constants.TIMER.CURRENT_TIME)) return false;

        this.currentTime = intent.getLongExtra(Constants.TIMER.CURRENT_TIME, 0L);
        Log.i("durationbefore pref", "" + duration);
        duration = pref.getLong("Duration", 120000);
        Log.i("currentT", "" + this.currentTime);
        Log.i("durationT", "" + duration);
        if (this.currentTime >= duration) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    clockView.setText("");
                }
            });


            prefEditor = pref.edit();
            prefEditor.putLong("Duration", 0).apply();
            Toast.makeText(MainActivity.this, "Timer done", Toast.LENGTH_SHORT).show();
            UserDatabaseReference = FirebaseDatabase.getInstance().getReference();
            UserDatabaseReference.child("Users").child(UID).child("cooldown").setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    timerOn = false;
                    event = false;
                }
            });
            MainActivity.prefEditor = pref.edit().putInt("Cooldown", 0);
            MainActivity.prefEditor.putString("Note", "").apply();
            MainActivity.beacon = true;

            return false;
        }

        final int secs = (int) (currentTime / 1000);
        final int minutes = secs / 60;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clockView.setText(Integer.toString(minutes) + ":" + String.format("%02d", secs % 60));
            }
        });

        return true;
    }
    /******************************************************************************************/


    /************* Helper Methods ****************************/
    private void showTimerCompleteNotification() {
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Timer Done!")
                        .setContentText("Congrats")
                        .setContentIntent(resultPendingIntent)
                        .setColor(Color.BLACK)
                        .setLights(Color.BLUE, 500, 500)
                        .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                        .setDefaults(NotificationCompat.DEFAULT_SOUND)
                        .setStyle(new NotificationCompat.InboxStyle());

        // Gets an instance of the NotificationManager service
        final NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, mBuilder.build());

        //Cancel the notification after a little while
        Handler h = new Handler();
        long delayInMilliseconds = 5000;

        h.postDelayed(new Runnable() {
            public void run() {
                mNotifyMgr.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);
            }
        }, delayInMilliseconds);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}


