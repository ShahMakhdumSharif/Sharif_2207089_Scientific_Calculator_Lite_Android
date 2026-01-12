package com.example.calculator;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AllUsersActivity extends AppCompatActivity {

    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        TableLayout table = findViewById(R.id.table_users);
        Button btnBack = findViewById(R.id.btn_all_users_back);
        Button btnLogout = findViewById(R.id.btn_all_users_logout);

        if (table == null) {
            Toast.makeText(this, "Layout error: table_users not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        table.removeAllViews();
        TableRow header = new TableRow(this);
        header.addView(makeCell("Username", true));
        header.addView(makeCell("Password", true));
        table.addView(header);

        usersRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                showEmptyRow(table, "No users registered");
                Toast.makeText(this, "Failed to load users", Toast.LENGTH_SHORT).show();
                return;
            }

            DataSnapshot snapshot = task.getResult();
            if (!snapshot.exists() || snapshot.getChildrenCount() == 0) {
                showEmptyRow(table, "No users registered");
                Toast.makeText(this, "No users found", Toast.LENGTH_SHORT).show();
                return;
            }

            int count = 0;
            for (DataSnapshot child : snapshot.getChildren()) {
                String username = String.valueOf(child.child("username").getValue());
                String password = String.valueOf(child.child("password").getValue());

                TableRow row = new TableRow(this);
                row.addView(makeCell(username, false));
                row.addView(makeCell(password, false));
                table.addView(row);
                count++;
            }

            Toast.makeText(this, "Loaded " + count + " users", Toast.LENGTH_SHORT).show();
        });

        btnBack.setOnClickListener(v -> finish());

        btnLogout.setOnClickListener(v -> {
            Intent i = new Intent(AllUsersActivity.this, WelcomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        });
    }

    private void showEmptyRow(TableLayout table, String message) {
        TableRow row = new TableRow(this);
        TextView tv = makeCell(message, false);
        tv.setLayoutParams(new TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        row.addView(tv);
        table.addView(row);
    }

    private TextView makeCell(String text, boolean header) {
        TextView tv = new TextView(this);
        tv.setText(text);

        int pad = (int) (8 * getResources().getDisplayMetrics().density);
        tv.setPadding(pad, pad, pad, pad);

        tv.setLayoutParams(new TableRow.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        tv.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);

        if (header) {
            tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
        }
        return tv;
    }
}