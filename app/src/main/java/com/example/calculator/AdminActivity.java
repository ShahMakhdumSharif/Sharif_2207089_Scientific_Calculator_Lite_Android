// File: `app/src/main/java/com/example/calculator/AdminActivity.java`
package com.example.calculator;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {
    private static final String TAG = "AdminActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        TextView tvWelcome = findViewById(R.id.tv_welcome);
        Button btnAllUsers = findViewById(R.id.btn_all_users);
        Button btnHistory = findViewById(R.id.btn_history);
        Button btnBlockList = findViewById(R.id.btn_block_list);
        Button btnBlockUser = findViewById(R.id.btn_block_user);
        Button btnLimitOp = findViewById(R.id.btn_limit_operation);
        Button btnBack = findViewById(R.id.btn_admin_back);

        if (tvWelcome == null || btnAllUsers == null || btnHistory == null ||
                btnBlockList == null || btnBlockUser == null || btnLimitOp == null || btnBack == null) {
            Log.e(TAG, "One or more views not found. Check activity_admin layout and setContentView.");
            return;
        }
        tvWelcome.setText("Welcome Shah Makhdum Sharif");

        btnAllUsers.setOnClickListener(v -> showPlaceholder("All Users"));
        btnHistory.setOnClickListener(v -> showPlaceholder("History"));
        btnBlockList.setOnClickListener(v -> showPlaceholder("Block List"));
        btnBlockUser.setOnClickListener(v -> showPlaceholder("Block User"));
        btnLimitOp.setOnClickListener(v -> showPlaceholder("Limit Operation for User"));

        btnBack.setOnClickListener(v -> finish());
    }

    private void showPlaceholder(String name) {
        Toast.makeText(this, name + " clicked (implement action)", Toast.LENGTH_SHORT).show();
    }
}
