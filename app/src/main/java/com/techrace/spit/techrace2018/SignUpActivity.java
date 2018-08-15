package com.techrace.spit.techrace2018;

import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ThreadPoolExecutor;

import static com.techrace.spit.techrace2018.AppConstants.PREFS_UNLOCKED;
import static com.techrace.spit.techrace2018.AppConstants.password;
import static com.techrace.spit.techrace2018.MainActivity.mAuth;

public class SignUpActivity extends AppCompatActivity {

    EditText nameEditText, passwordEditText, emailEditText;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDatabaseReference, countRef;
    ValueEventListener listener;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDatabaseReference = firebaseDatabase.getReference().child("Users");
        countRef = firebaseDatabase.getReference().child("Count");
        nameEditText = findViewById(R.id.nameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        emailEditText = findViewById(R.id.emailEditText);
        mAuth = FirebaseAuth.getInstance();
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }
    }

    public void logInClicked(View view) {


//        if (nameEditText.getText().toString().equals("") || !passwordEditText.getText().toString().equals(password)) {
//            Toast.makeText(this, "Please Enter Correct Details", Toast.LENGTH_SHORT).show();
//        } else {
//            myRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    count = (dataSnapshot.getValue(Integer.class));
//                    Log.i("CNT", String.valueOf(count));
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//            count++;
//            userDatabaseReference.child(String.valueOf(count) + " " + nameEditText.getText().toString()).setValue(String.valueOf(count) + " " + nameEditText.getText().toString());
//            // userDatabaseReference.push().child("Password").setValue(passwordEditText.getText().toString());
//            myRef.setValue(count);
//
//            SharedPreferences pref = getSharedPreferences(AppConstants.PREFS, MODE_PRIVATE);
//            pref.edit().putBoolean(PREFS_UNLOCKED, true).apply();
//            finish();
//        }
        registerUser();
    }

    public void registerUser() {
        final String name = nameEditText.getText().toString();
        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        if (name.equals("") || !password.equals(AppConstants.password)) {
            Toast.makeText(this, "Please Enter Correct Details", Toast.LENGTH_SHORT).show();
        } else {
            //final ProgressBar progressBar=findViewById(R.id.progressBar);
            //   progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            } else {
                                mAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {


//                                                    listener=countRef.addValueEventListener(new ValueEventListener() {
//                                                        @Override
//                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                            count=dataSnapshot.getValue(Integer.class);
//                                                            Log.i("COUNT",String.valueOf(count));
//                                                            //dataSnapshot.getRef().setValue(count+1);
//                                                        }
//
//                                                        @Override
//                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                        }
//                                                    });
//                                                    countRef.removeEventListener(listener);
                                                    //String uName=String.format("%03d",count)+" "+name;
                                                    String uName = String.valueOf(count) + " " + name;
                                                    //String uName=name;
                                                    Log.i("UNAME", uName);
                                                    User user = new User(uName, password, email);
                                                    // countRef.setValue(count++);
                                                    FirebaseDatabase.getInstance().getReference("Users")
                                                            .child(mAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
//                                                                countRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                                    @Override
//                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                                        dataSnapshot.getRef().setValue(count+1);
//                                                                    }
//
//                                                                    @Override
//                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                                    }
//                                                                });
                                                                //    progressBar.setVisibility(View.GONE);

                                                                Toast.makeText(SignUpActivity.this, "Signed Up Successfully", Toast.LENGTH_LONG).show();
                                                            } else {
                                                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                            }
                        }
                    });

            finish();
        }
    }
}
