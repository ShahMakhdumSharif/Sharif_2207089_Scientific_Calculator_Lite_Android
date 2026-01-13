package com.example.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserLoginActivity extends AppCompatActivity {

    private static final String TAG = "UserLoginActivity";
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        FirebaseApp.initializeApp(this);
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        EditText etUsername = findViewById(R.id.et_username);
        EditText etPassword = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login);
        Button btnBack = findViewById(R.id.btn_back);

        btnLogin.setOnClickListener(v -> {
            String userInput = etUsername.getText().toString().trim();
            String passInput = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(userInput)) {
                etUsername.setError("Required");
                return;
            }
            if (TextUtils.isEmpty(passInput)) {
                etPassword.setError("Required");
                return;
            }

            String key = encodeKey(userInput);

            usersRef.child(key).get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e(TAG, "Firebase read failed", task.getException());
                    Toast.makeText(this, "Database error", Toast.LENGTH_SHORT).show();
                    return;
                }

                DataSnapshot snapshot = task.getResult();
                if (snapshot == null || !snapshot.exists()) {
                    etUsername.setError("User not found");
                    Toast.makeText(this, "No such user", Toast.LENGTH_SHORT).show();
                    return;
                }

                String stored = snapshot.child("password").getValue(String.class);
                if (stored == null) {
                    etPassword.setError("Incorrect");
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (passInput.equals(stored)) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

                    // Start UserInterfaceActivity
                    Intent intent = new Intent(UserLoginActivity.this, UserInterfaceActivity.class);
                    intent.putExtra("USERNAME", userInput); // pass username
                    startActivity(intent);
                    finish(); // optional
                } else {
                    etPassword.setError("Incorrect");
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private String encodeKey(String username) {
        if (username == null) return "";
        String key = username.trim().toLowerCase();
        return key.replace(".", "_")
                .replace("#", "_")
                .replace("$", "_")
                .replace("[", "_")
                .replace("]", "_");
    }
}