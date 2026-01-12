// app/src/main/java/com/example/calculator/AdminLoginActivity.java
package com.example.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminLoginActivity extends AppCompatActivity {

    private static final String ADMIN_PASSWORD = "admin123";

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
            if (ADMIN_PASSWORD.equals(pass)) {
                Toast.makeText(this, "Admin login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AdminLoginActivity.this, AdminActivity.class);
                startActivity(intent);
                finish();
            } else {
                etAdminPassword.setError("Incorrect password");
                Toast.makeText(this, "Invalid admin password", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }
}
