package com.example.calculator;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserLoginActivity extends AppCompatActivity {

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
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString();
            if (TextUtils.isEmpty(user)) {
                etUsername.setError("Required");
                return;
            }
            if (TextUtils.isEmpty(pass)) {
                etPassword.setError("Required");
                return;
            }

            usersRef.child(user).get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(this, "Database error", Toast.LENGTH_SHORT).show();
                    return;
                }
                DataSnapshot snapshot = task.getResult();
                if (!snapshot.exists()) {
                    etUsername.setError("User not found");
                    Toast.makeText(this, "No such user", Toast.LENGTH_SHORT).show();
                    return;
                }
                String stored = "";
                if (snapshot.child("password").getValue() != null) {
                    stored = snapshot.child("password").getValue(String.class);
                }
                if (pass.equals(stored)) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                    // proceed to next activity
                } else {
                    etPassword.setError("Incorrect");
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnBack.setOnClickListener(v -> finish());
    }
}
