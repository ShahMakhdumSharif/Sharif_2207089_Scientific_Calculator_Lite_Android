package com.example.calculator;
import android.util.Log;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserHistoryActivity extends AppCompatActivity {

    private TableLayout table;
    private TextView tvHeader;
    private Button btnBack, btnLogout;
    private DatabaseReference historyRef;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_history);

        // get username from intent
        username = getIntent().getStringExtra("username");
        if (username == null) username = "unknown";

        tvHeader = findViewById(R.id.tv_user_name_header);
        tvHeader.setText("History for: " + username);

        table = findViewById(R.id.table_user_history);
        btnBack = findViewById(R.id.btn_back);
        btnLogout = findViewById(R.id.btn_logout);

        FirebaseApp.initializeApp(this);
        historyRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(username)
                .child("history");

        loadHistory();

        btnBack.setOnClickListener(v -> finish());

        btnLogout.setOnClickListener(v -> {
            Intent i = new Intent(UserHistoryActivity.this, WelcomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        });
    }

    private void loadHistory() {
        table.removeAllViews();

        // Add header row
        TableRow header = new TableRow(this);
        header.addView(makeCell("Expression", true));
        header.addView(makeCell("Result", true));
        table.addView(header);

        // Load history from Firebase
        historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    showEmptyRow("No calculation history found");
                    return;
                }

                for (DataSnapshot child : snapshot.getChildren()) {
                    String expression = child.child("expression").getValue(String.class);
                    String result = child.child("result").getValue(String.class);

                    if (expression == null) expression = "-";
                    if (result == null) result = "-";

                    TableRow row = new TableRow(UserHistoryActivity.this);
                    row.addView(makeCell(expression, false));
                    row.addView(makeCell(result, false));

                    table.addView(row);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showEmptyRow("Failed to load history: " + error.getMessage());
            }
        });
    }

    private void showEmptyRow(String message) {
        TableRow row = new TableRow(this);
        TextView tv = makeCell(message, false);
        tv.setLayoutParams(new TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        row.addView(tv);
        table.addView(row);
    }

    private TextView makeCell(String text, boolean header) {
        TextView tv = new TextView(this);
        tv.setText(text);
        int pad = (int) (8 * getResources().getDisplayMetrics().density);
        tv.setPadding(pad, pad, pad, pad);
        tv.setLayoutParams(new TableRow.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
        ));
        tv.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        if (header) tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
        return tv;
    }
}