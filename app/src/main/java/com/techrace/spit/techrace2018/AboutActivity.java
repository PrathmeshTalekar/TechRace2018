package com.techrace.spit.techrace2018;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    Button button_people;
    Button button_sponsors;
    TextView uidTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        button_sponsors = (Button) findViewById(R.id.sponsors);
        button_people = (Button) findViewById(R.id.credits);
        uidTextView = findViewById(R.id.uidTextView);
        if (Build.VERSION.SDK_INT >= 21)
            setupWindowAnimations();
        button_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(AboutActivity.this);
                startActivity(new Intent(AboutActivity.this, CreditsActivity.class), options.toBundle());
            }
        });
        button_sponsors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(AboutActivity.this);
                startActivity(new Intent(AboutActivity.this, SponsorsActivity.class), options.toBundle());
            }
        });

        uidTextView.setText("UID: " + HomeFragment.UID);
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupWindowAnimations() {
        Slide slide = new Slide();
        slide.setDuration(300);
        getWindow().setEnterTransition(slide);
        getWindow().setReenterTransition(slide);
    }

    public void onClick(View v) {
        Uri uri = Uri.parse((String) v.getTag());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
