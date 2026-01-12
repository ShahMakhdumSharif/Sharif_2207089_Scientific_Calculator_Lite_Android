package com.example.calculator;

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

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseApp.initializeApp(this);
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        EditText etUser = findViewById(R.id.et_reg_username);
        EditText etPass = findViewById(R.id.et_reg_password);
        EditText etConfirm = findViewById(R.id.et_reg_confirm);
        Button btnRegister = findViewById(R.id.btn_register_user);
        Button btnBack = findViewById(R.id.btn_register_back);

        if (etUser == null || etPass == null || etConfirm == null || btnRegister == null || btnBack == null) {
            Log.e(TAG, "One or more views not found. Check activity_register layout and setContentView.");
            return;
        }

        btnRegister.setOnClickListener(v -> {
            String user = etUser.getText().toString().trim();
            String pass = etPass.getText().toString();
            String confirm = etConfirm.getText().toString();

            if (TextUtils.isEmpty(user)) {
                etUser.setError("Required");
                return;
            }
            if (!user.matches("^[A-Za-z0-9_\\-]{3,30}$")) {
                etUser.setError("Only letters, digits, _ or - allowed (3-30 chars)");
                return;
            }
            if (TextUtils.isEmpty(pass)) {
                etPass.setError("Required");
                return;
            }
            if (pass.length() < 6) {
                etPass.setError("Password too short");
                return;
            }
            if (!pass.equals(confirm)) {
                etConfirm.setError("Does not match");
                return;
            }

            // disable UI while registering
            btnRegister.setEnabled(false);
            btnBack.setEnabled(false);

            // normalize username to use as Firebase key (must match login normalization)
            String key = encodeKey(user);

            // check if username already exists
            usersRef.child(key).get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e(TAG, "Firebase read failed", task.getException());
                    Toast.makeText(this, "Database error", Toast.LENGTH_SHORT).show();
                    btnRegister.setEnabled(true);
                    btnBack.setEnabled(true);
                    return;
                }

                DataSnapshot snapshot = task.getResult();
                if (snapshot != null && snapshot.exists()) {
                    etUser.setError("User already exists");
                    Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show();
                    btnRegister.setEnabled(true);
                    btnBack.setEnabled(true);
                    return;
                }

                // store under the normalized username key
                UserRecord record = new UserRecord(user, pass /* replace with hash in production */);
                usersRef.child(key).setValue(record)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Register failed", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Register failure", e);
                            btnRegister.setEnabled(true);
                            btnBack.setEnabled(true);
                        });
            });
        });

        btnBack.setOnClickListener(v -> finish());
    }

    // Normalize and replace illegal Firebase key characters; lowercase to match login normalization
    private String encodeKey(String s) {
        if (s == null) return "";
        String key = s.trim().toLowerCase();
        return key.replaceAll("[\\.#\\$\\[\\]/]", "_");
    }

    // simple user POJO stored in DB (do not store plaintext passwords in production)
    public static class UserRecord {
        public String username;
        public String password;

        public UserRecord() { } // required for Firebase
        public UserRecord(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }
}
