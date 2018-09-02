package com.techrace.spit.techrace2018;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

public class FeedActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Feed> feedItems;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;
    backgroungFeed ob;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= 21)
            setupWindowAnimations();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.feed_swiperefresh);
        feedItems = new ArrayList<Feed>();
        recyclerView = (RecyclerView) findViewById(R.id.feed_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ob = new backgroungFeed();
        ob.execute();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                feedItems.clear();
                //leaderboardItems.add(new LeaderboardItem("Name","Clues solved"));
                new backgroungFeed().execute();
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
    public void onBackPressed() {
        super.onBackPressed();

        finish();
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

    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.dismiss();
    }

    class backgroungFeed extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(FeedActivity.this);
            progressDialog.setMessage("Refreshing...");
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    ob.cancel(true);
                    finish();
                }
            });
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            DatabaseReference dbFeed = FirebaseDatabase.getInstance().getReference().child("Feed");
            dbFeed.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        String title = d.child("Title").getValue(String.class);
                        Log.i("fee title", title);

                        String info = d.child("Info").getValue(String.class);
                        Log.i("feed info", info);
                        feedItems.add(new Feed(title, info));
                    }
                    if (progressDialog.isShowing()) {
                        progressDialog.hide();

                    }
                    recyclerView.setAdapter(new FeedAdapter(feedItems, FeedActivity.this));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
