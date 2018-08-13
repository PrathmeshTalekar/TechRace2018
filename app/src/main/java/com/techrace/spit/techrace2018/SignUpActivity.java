package com.techrace.spit.techrace2018;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.techrace.spit.techrace2018.AppConstants.PREFS_UNLOCKED;
import static com.techrace.spit.techrace2018.AppConstants.password;

public class SignUpActivity extends AppCompatActivity {

    EditText nameEditText;
    EditText passwordEditText;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDatabaseReference, myRef;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDatabaseReference = firebaseDatabase.getReference().child("Users");
        myRef = firebaseDatabase.getReference().child("Count");
        nameEditText = findViewById(R.id.nameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();
        //myRef.removeEventListener();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    public void logInClicked(View view) {


        if (nameEditText.getText().toString().equals("") || !passwordEditText.getText().toString().equals(password)) {
            Toast.makeText(this, "Please Enter Correct Details", Toast.LENGTH_SHORT).show();
        } else {
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    count = (dataSnapshot.getValue(Integer.class));
                    Log.i("CNT", String.valueOf(count));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            count++;
            userDatabaseReference.child(String.valueOf(count) + " " + nameEditText.getText().toString()).setValue(String.valueOf(count) + " " + nameEditText.getText().toString());
            // userDatabaseReference.push().child("Password").setValue(passwordEditText.getText().toString());
            myRef.setValue(count);

            SharedPreferences pref = getSharedPreferences(AppConstants.PREFS, MODE_PRIVATE);
            pref.edit().putBoolean(PREFS_UNLOCKED, true).apply();
            finish();
        }
    }

}
