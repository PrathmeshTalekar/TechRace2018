package com.techrace.spit.techrace2018;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class LeaderboardActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<LBUpdate> leaderboardItems;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= 21)
            setupWindowAnimations();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.leaderboard_swiperefresh);
        leaderboardItems = new ArrayList<LBUpdate>();
        recyclerView = (RecyclerView) findViewById(R.id.leaderboard_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //leaderboardItems.add(new LeaderboardItem("Name","Clues solved"));
        new back().execute();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                leaderboardItems.clear();
                //leaderboardItems.add(new LeaderboardItem("Name","Clues solved"));
                new back().execute();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupWindowAnimations() {
        Slide slide = new Slide();
        slide.setDuration(300);
        getWindow().setEnterTransition(slide);
        getWindow().setReenterTransition(slide);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    class back extends AsyncTask<Void, Void, Void> {
        ArrayList<LBUpdate> items = new ArrayList<>();
        ArrayList<LBUpdate> finalList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            Log.i("LOADING", "True");
            progressDialog = new ProgressDialog(LeaderboardActivity.this);
            progressDialog.setMessage("Populating Leaderboard...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            items.clear();
            finalList.clear();
            DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Leaderboard");
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        Log.i("DDDDDD", d.toString());
                        String name = (String) d.child("Name").getValue();
                        int level = d.child("Level").getValue(Integer.class);
                        int points = d.child("Points").getValue(Integer.class);
                        long timeInMil = d.child("Time").getValue(Long.class);
                        items.add(new LBUpdate(name, points, level, timeInMil));
                    }

                    int maxLevel = 0;
                    for (int i = 0; i < items.size(); i++) {
                        int current = items.get(i).level;
                        if (current > maxLevel) {
                            maxLevel = current;
                        }
                    }
                    Log.i("MAXLEVEL", String.valueOf(maxLevel));
                    while (maxLevel > 0) {
                        List<LBUpdate> sameLevelList = new ArrayList<>();
                        for (int i = 0; i < items.size(); i++) {
                            if (items.get(i).level == maxLevel) {
                                sameLevelList.add(items.get(i));
                            }
                        }
                        Log.i("SAMELEVELLIST", sameLevelList.toString());
                        Collections.sort(sameLevelList, new LBUpdate.TimeComparator());
                        Collections.reverse(sameLevelList);
                        finalList.addAll(sameLevelList);
                        // sameLevelList.clear();
                        for (int j = 0; j < finalList.size(); j++) {
                            Log.i("Final LIST LOG", finalList.toString());
                        }
                        maxLevel--;
                    }
                    if (progressDialog.isShowing()) {
                        progressDialog.hide();
                    }
                    recyclerView.setAdapter(new LeaderboardAdapter(finalList, LeaderboardActivity.this));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i("LBLIST", finalList.toString());
//            LeaderboardAdapter adapter = new LeaderboardAdapter(finalList,LeaderboardActivity.this);
//            ListView listView = (ListView) LeaderBoardFragment.myView1.findViewById(R.id.list);
//            listView.setAdapter(adapter);
//            if (progressDialog.isShowing())
//            {
//                progressDialog.hide();
//            }
//            recyclerView.setAdapter(new LeaderboardAdapter(finalList,LeaderboardActivity.this));

        }
    }
}
