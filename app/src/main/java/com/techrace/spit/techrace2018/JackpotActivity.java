package com.techrace.spit.techrace2018;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.techrace.spit.techrace2018.HomeFragment.UID;
import static com.techrace.spit.techrace2018.MainActivity.jackpotRunning;
import static com.techrace.spit.techrace2018.MainActivity.points;

public class JackpotActivity extends AppCompatActivity {

    static Activity jackpot;
    ValueEventListener jackpotListener;
    TextView questionTextView;
    Button submitButton;
    EditText answerEditText;
    AlertDialog submitDialog;
    String answer;
    boolean answered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jackpot);
        jackpot = this;
        jackpotRunning = true;
        questionTextView = findViewById(R.id.questionTextView);
        answerEditText = findViewById(R.id.answerEditText);
        submitButton = findViewById(R.id.submitButton);
        questionTextView.setText("Loading...");
        FirebaseDatabase.getInstance().getReference().child("Jackpot").child("Question").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                questionTextView.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        jackpotListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {

                    int jp = dataSnapshot.getValue(Integer.class);
                    Log.i("IN JP CHANGE", "" + jp);
                    if (jp == 1) {
                        Log.i("IN JP YES", "" + jp);

                    } else {
                        if (jackpotRunning) {
                            Log.i("IN jp running", "" + jp);

                            JackpotActivity.this.finish();
                        }

                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        jackpotRunning = true;

        FirebaseDatabase.getInstance().getReference().child("Jackpot").addValueEventListener(jackpotListener);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = answerEditText.getText().toString().trim();
                if (!answer.equals("")) {
                    AlertDialog.Builder submitBuilder = new AlertDialog.Builder(JackpotActivity.this)
                            .setCancelable(false)
                            .setMessage("Are you sure you want to submit?")
                            .setTitle("Are you sure?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    answered = true;

                                    FirebaseDatabase.getInstance().getReference().child("Users").child(UID).child("Jackpot Answer").setValue(answer);
                                    FirebaseDatabase.getInstance().getReference().child("Jackpot").child("Answer").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String serverAnswer = dataSnapshot.getValue(String.class);
                                            if (serverAnswer.equals(answer)) {
                                                FirebaseDatabase.getInstance().getReference().child("Users").child(UID).child("points").setValue(points + (2 * AppConstants.jackpotPrice));
                                                Toast.makeText(JackpotActivity.this, "Correct", Toast.LENGTH_LONG).show();
                                                finish();
                                            } else {
                                                if (points < 0) {
                                                    FirebaseDatabase.getInstance().getReference().child("Users").child(UID).child("points").setValue(0);
                                                }
                                                Toast.makeText(JackpotActivity.this, "Incorrect", Toast.LENGTH_LONG).show();
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            })
                            .setNegativeButton("No", null);
                    submitDialog = submitBuilder.create();
                    submitDialog.show();
                } else {
                    Toast.makeText(JackpotActivity.this, "Please Enter Answer", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        jackpotRunning = false;
        if (submitDialog != null && submitDialog.isShowing()) {
            submitDialog.cancel();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Can't go back. Please submit", Toast.LENGTH_LONG).show();
    }


}
