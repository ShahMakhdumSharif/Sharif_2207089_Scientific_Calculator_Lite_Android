package com.example.calculator;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminLoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        EditText etAdminPassword = findViewById(R.id.et_admin_password);
        Button btnLogin = findViewById(R.id.btn_admin_login);
        Button btnBack = findViewById(R.id.btn_admin_back);

        btnLogin.setOnClickListener(v -> {
            String pass = etAdminPassword.getText().toString();
            if (TextUtils.isEmpty(pass)) {
                etAdminPassword.setError("Required");
                return;
            }
            Toast.makeText(this, "Admin login clicked (implement auth)", Toast.LENGTH_SHORT).show();
        });

        btnBack.setOnClickListener(v -> finish());
    }
}
