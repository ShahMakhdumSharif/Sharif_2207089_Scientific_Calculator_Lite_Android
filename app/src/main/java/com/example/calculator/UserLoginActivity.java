package com.example.calculator;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UserLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

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
            Toast.makeText(this, "User login clicked (implement auth)", Toast.LENGTH_SHORT).show();
        });

        btnBack.setOnClickListener(v -> finish());
    }
}
