package com.example.calculator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LimitOperationActivity extends AppCompatActivity {

    private RecyclerView rvUsers;
    private Button btnBack, btnLogout;
    private ArrayList<String> userList;
    private LimitUserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limit_user_list);

        rvUsers = findViewById(R.id.rv_users);
        btnBack = findViewById(R.id.btn_back);
        btnLogout = findViewById(R.id.btn_logout);

        rvUsers.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        adapter = new LimitUserAdapter(this, userList);
        rvUsers.setAdapter(adapter);

        // Load users from Firebase
        loadUsersFromFirebase();

        btnBack.setOnClickListener(v -> finish());
        btnLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void loadUsersFromFirebase() {
        FirebaseDatabase.getInstance().getReference("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        userList.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnap : snapshot.getChildren()) {
                                String username = userSnap.child("username").getValue(String.class);
                                if (username != null) {
                                    userList.add(username);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(LimitOperationActivity.this, "No users found in Firebase", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(LimitOperationActivity.this, "Firebase error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}