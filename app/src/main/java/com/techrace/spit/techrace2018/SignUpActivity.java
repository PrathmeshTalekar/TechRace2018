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

    EditText nameEditText, passwordEditText, emailEditText, contactEditText;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDatabaseReference, countRef;

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
        contactEditText = findViewById(R.id.contactEditText);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
            finishAffinity();
    }

    public void logInClicked(View view) {

        final String name = nameEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();
        final String contact = contactEditText.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (email.matches(emailPattern) && email.length() > 0) {
            Toast.makeText(getApplicationContext(), "valid email address", Toast.LENGTH_SHORT).show();
            if (name.equals("") || !password.equals(AppConstants.password)) {
                Toast.makeText(this, "Please Enter Correct Details", Toast.LENGTH_SHORT).show();
            } else {
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
                                                        Log.i("UNAME", name);
                                                        User user = new User(name, password, email, contact);
                                                        FirebaseDatabase.getInstance().getReference("Users")
                                                                .child(mAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
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
        } else {
            Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
        }

    }
}
