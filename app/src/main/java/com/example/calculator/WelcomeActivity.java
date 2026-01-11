package com.example.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button userLogin = findViewById(R.id.btn_user_login);
        Button adminLogin = findViewById(R.id.btn_admin_login);
        Button register = findViewById(R.id.btn_register);

        userLogin.setOnClickListener(v -> startActivity(new Intent(this, UserLoginActivity.class)));
        adminLogin.setOnClickListener(v -> startActivity(new Intent(this, AdminLoginActivity.class)));
        register.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }
}
