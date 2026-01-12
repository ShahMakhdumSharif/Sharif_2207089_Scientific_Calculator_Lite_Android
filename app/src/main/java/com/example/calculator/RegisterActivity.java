package com.example.calculator;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // initialize Firebase (safe even if auto-init already happened)
        FirebaseApp.initializeApp(this);
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        EditText etUser = findViewById(R.id.et_reg_username);
        EditText etPass = findViewById(R.id.et_reg_password);
        EditText etConfirm = findViewById(R.id.et_reg_confirm);
        Button btnRegister = findViewById(R.id.btn_register_user);
        Button btnBack = findViewById(R.id.btn_register_back);

        btnRegister.setOnClickListener(v -> {
            String user = etUser.getText().toString().trim();
            String pass = etPass.getText().toString();
            String confirm = etConfirm.getText().toString();

            if (TextUtils.isEmpty(user)) {
                etUser.setError("Required");
                return;
            }
            if (TextUtils.isEmpty(pass)) {
                etPass.setError("Required");
                return;
            }
            if (!pass.equals(confirm)) {
                etConfirm.setError("Does not match");
                return;
            }

            // check if username exists
            usersRef.child(user).get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(this, "Database error", Toast.LENGTH_SHORT).show();
                    return;
                }
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    etUser.setError("Username taken");
                    Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
                } else {
                    // store password (example: plain text - replace with hashing in production)
                    usersRef.child(user).child("password").setValue(pass)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Register failed", Toast.LENGTH_SHORT).show());
                }
            });
        });

        btnBack.setOnClickListener(v -> finish());
    }
}
