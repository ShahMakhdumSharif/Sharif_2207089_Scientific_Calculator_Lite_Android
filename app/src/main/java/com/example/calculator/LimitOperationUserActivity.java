package com.example.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LimitOperationUserActivity extends AppCompatActivity {

    private String username;
    private DatabaseReference dbRef;

    private Button btnMat, btnPoly, btnLin, btnAdd, btnSubtract, btnMultiply, btnDivide, btnSqrt, btnXPow, btnClear;
    private Button btnBack, btnLogout;
    private TextView tvLimitMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limit_operation_user);

        dbRef = FirebaseDatabase.getInstance().getReference("users");
        username = getIntent().getStringExtra("username");

        tvLimitMessage = findViewById(R.id.tv_limit_message);
        tvLimitMessage.setText("Limit operation for: " + username);

        // Bind operation buttons
        btnMat = findViewById(R.id.btn_mat);
        btnPoly = findViewById(R.id.btn_poly);
        btnLin = findViewById(R.id.btn_lin);
        btnAdd = findViewById(R.id.btn_add);
        btnSubtract = findViewById(R.id.btn_subtract);
        btnMultiply = findViewById(R.id.btn_multiply);
        btnDivide = findViewById(R.id.btn_divide);
        btnSqrt = findViewById(R.id.btn_sqrt);
        btnXPow = findViewById(R.id.btn_x_pow);
        btnClear = findViewById(R.id.btn_clear);

        // Bind Back & Logout
        btnBack = findViewById(R.id.btn_back);
        btnLogout = findViewById(R.id.btn_logout);

        // Operation clicks
        btnMat.setOnClickListener(v -> assignOperation("MATRIX"));
        btnPoly.setOnClickListener(v -> assignOperation("POLYNOMIAL"));
        btnLin.setOnClickListener(v -> assignOperation("LINEAR"));
        btnAdd.setOnClickListener(v -> assignOperation("+"));
        btnSubtract.setOnClickListener(v -> assignOperation("-"));
        btnMultiply.setOnClickListener(v -> assignOperation("*"));
        btnDivide.setOnClickListener(v -> assignOperation("/"));
        btnSqrt.setOnClickListener(v -> assignOperation("√"));
        btnXPow.setOnClickListener(v -> assignOperation("x^"));
        btnClear.setOnClickListener(v -> removeOperation("CLEAR"));

        // Back button
        btnBack.setOnClickListener(v -> finish()); // closes this activity

        // Logout button
        btnLogout.setOnClickListener(v -> {
            // go back to login activity (replace LoginActivity.class with your login screen)
            Intent intent = new Intent(LimitOperationUserActivity.this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clear back stack
            startActivity(intent);
        });
    }

    private void assignOperation(String operation) {
        if (username == null) return;

        dbRef.child(username).child("limited_operation").child(operation).setValue(true)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Operation allowed: " + operation, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update operation", Toast.LENGTH_SHORT).show());
    }

    private void removeOperation(String operation) {
        if (username == null) return;

        dbRef.child(username).child("limited_operation").child(operation).removeValue()
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Operation removed: " + operation, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to remove operation", Toast.LENGTH_SHORT).show());
    }
}