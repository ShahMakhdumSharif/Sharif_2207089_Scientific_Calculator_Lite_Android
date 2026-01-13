// java
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

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class HistoryActivity extends AppCompatActivity {
    private DatabaseReference usersRef;
    private TableLayout table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        table = findViewById(R.id.table_history_users);
        Button btnBack = findViewById(R.id.btn_history_back);
        Button btnLogout = findViewById(R.id.btn_history_logout);

        if (table == null) {
            Toast.makeText(this, "Missing table_history_users in layout", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        FirebaseApp.initializeApp(this);
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        table.removeAllViews();
        TableRow header = new TableRow(this);
        header.addView(makeCell("Username", true));
        table.addView(header);

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot == null || !snapshot.exists() || !snapshot.hasChildren()) {
                    showEmptyRow("No users registered");
                    Toast.makeText(HistoryActivity.this, "No users in Realtime Database", Toast.LENGTH_SHORT).show();
                    return;
                }
                int count = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    String username = child.child("username").getValue(String.class);
                    if (username == null || username.isEmpty()) username = child.getKey();
                    if (username == null) username = "unknown";

                    final String u = username; // make effectively final for lambda
                    TableRow row = new TableRow(HistoryActivity.this);
                    TextView tv = makeCell(u, false);
                    row.addView(tv);

                    row.setClickable(true);
                    row.setFocusable(true);
                    row.setOnClickListener(v -> {
                        Toast.makeText(HistoryActivity.this, "Selected: " + u, Toast.LENGTH_SHORT).show();
                    });

                    table.addView(row);
                    count++;
                }
                Toast.makeText(HistoryActivity.this, "Loaded " + count + " users", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showEmptyRow("Failed to load users");
                Toast.makeText(HistoryActivity.this, "DB error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        btnBack.setOnClickListener(v -> {
            Intent i = new Intent(HistoryActivity.this, AdminActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        });

        btnLogout.setOnClickListener(v -> {
            Intent i = new Intent(HistoryActivity.this, WelcomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        });
    }

    private void showEmptyRow(String message) {
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
        if (header) tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
        return tv;
    }
}
