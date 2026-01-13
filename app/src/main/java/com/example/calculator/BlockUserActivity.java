package com.example.calculator;

import android.os.Bundle;
import android.widget.Button;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;

import java.util.ArrayList;

public class BlockUserActivity extends AppCompatActivity {

    private static final String TAG = "BlockUserActivity";

    ArrayList<String> users = new ArrayList<>();
    RecyclerView rv;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_user);

        rv = findViewById(R.id.rv_blocked_users);
        btnBack = findViewById(R.id.btn_back);

        rv.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                users.clear();

                for (DataSnapshot user : snapshot.getChildren()) {
                    String key = user.getKey();
                    Boolean blocked = user.child("blocked").getValue(Boolean.class);

                    // Only show unblocked users to block
                    if (blocked == null || !blocked) {
                        users.add(key);
                        Log.d(TAG, "User available to block: " + key);
                    }
                }

                // Adapter shows block button for unblocked users
                rv.setAdapter(new UserBlockAdapter(users, true));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Firebase error: " + error.getMessage());
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }
}