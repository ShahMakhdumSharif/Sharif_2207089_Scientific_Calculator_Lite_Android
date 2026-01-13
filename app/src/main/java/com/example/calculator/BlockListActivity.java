package com.example.calculator;

import android.os.Bundle;
import android.widget.Button;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;

import java.util.ArrayList;

public class BlockListActivity extends AppCompatActivity {

    private static final String TAG = "BlockListActivity";

    ArrayList<String> blockedUsers = new ArrayList<>();
    RecyclerView rv;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_list);

        rv = findViewById(R.id.rv_blocked_users);
        btnBack = findViewById(R.id.btn_back);

        rv.setLayoutManager(new LinearLayoutManager(this));

        // Firebase reference
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                blockedUsers.clear();

                for (DataSnapshot user : snapshot.getChildren()) {
                    String key = user.getKey();
                    Boolean blocked = user.child("blocked").getValue(Boolean.class);

                    if (blocked != null && blocked) {
                        blockedUsers.add(key);
                        Log.d(TAG, "Blocked User: " + key);
                    }
                }

                // Adapter shows unblock button for blocked users
                rv.setAdapter(new UserBlockAdapter(blockedUsers, false));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Firebase error: " + error.getMessage());
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }
}