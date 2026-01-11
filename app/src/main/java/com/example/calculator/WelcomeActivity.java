// app/src/main/java/com/example/calculator/WelcomeActivity.java
package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button userLogin = findViewById(R.id.btn_user_login);
        Button adminLogin = findViewById(R.id.btn_admin_login);
        Button register = findViewById(R.id.btn_register);

        userLogin.setOnClickListener(v ->
                Toast.makeText(this, "User Login clicked (implement navigation)", Toast.LENGTH_SHORT).show()
        );

        adminLogin.setOnClickListener(v ->
                Toast.makeText(this, "Admin Login clicked (implement navigation)", Toast.LENGTH_SHORT).show()
        );

        register.setOnClickListener(v ->
                Toast.makeText(this, "Register New User clicked (implement navigation)", Toast.LENGTH_SHORT).show()
        );
    }
}
