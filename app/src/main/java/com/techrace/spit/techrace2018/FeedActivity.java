package com.techrace.spit.techrace2018;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class FeedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        ArrayList<Feed> feedList = new ArrayList<>();
        feedList.add(new Feed("Abc", "desp"));
        feedList.add(new Feed("Abc", "desp"));
        feedList.add(new Feed("Abc", "desp"));
        feedList.add(new Feed("Abc", "desp"));
        feedList.add(new Feed("Abc", "desp"));
        FeedAdapter adapter = new FeedAdapter(this, feedList);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
    }
}
