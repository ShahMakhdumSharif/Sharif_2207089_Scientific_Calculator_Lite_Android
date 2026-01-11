package com.example.calculator;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
            Toast.makeText(this, "Register clicked (implement saving user)", Toast.LENGTH_SHORT).show();
        });

        btnBack.setOnClickListener(v -> finish());
    }
}
