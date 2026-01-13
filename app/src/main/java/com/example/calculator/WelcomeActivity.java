package com.example.calculator;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;

public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = "WelcomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase first
        FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_welcome);

        Button btnRegister = findViewById(R.id.btn_register);
        if (btnRegister == null) {
            Log.e(TAG, "btn_register not found - check activity_welcome.xml id and setContentView");
        } else {
            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Register button clicked");
                    startActivity(new Intent(WelcomeActivity.this, RegisterActivity.class));
                }
            });
        }

        Button btnUserLogin = findViewById(R.id.btn_user_login);
        if (btnUserLogin == null) {
            Log.e(TAG, "btn_user_login not found - check activity_welcome.xml id and setContentView");
        } else {
            btnUserLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "User Login button clicked");
                    startActivity(new Intent(WelcomeActivity.this, UserLoginActivity.class));
                }
            });
        }

        Button btnAdminLogin = findViewById(R.id.btn_admin_login);
        if (btnAdminLogin == null) {
            Log.e(TAG, "btn_admin_login not found - check activity_welcome.xml id and setContentView");
        } else {
            btnAdminLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Admin Login button clicked");
                    startActivity(new Intent(WelcomeActivity.this, AdminLoginActivity.class));
                }
            });
        }
    }
}