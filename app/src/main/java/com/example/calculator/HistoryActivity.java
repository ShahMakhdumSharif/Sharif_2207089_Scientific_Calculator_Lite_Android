package com.example.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView rvUsers;
    Button btnBack, btnLogout;
    ArrayList<String> userList = new ArrayList<>();
    UserAdapter adapter;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        rvUsers = findViewById(R.id.rv_users);
        btnBack = findViewById(R.id.btn_history_back);
        btnLogout = findViewById(R.id.btn_history_logout);

        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter();
        rvUsers.setAdapter(adapter);

        FirebaseApp.initializeApp(this);
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        loadUsers();

        btnBack.setOnClickListener(v -> finish());

        btnLogout.setOnClickListener(v -> {
            Intent i = new Intent(this, WelcomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        });
    }

    private void loadUsers() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String username = child.child("username").getValue(String.class);
                    if (username == null) username = child.getKey();
                    if (username != null)
                        userList.add(username);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // ================= ADAPTER =================

    class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserVH> {

        @NonNull
        @Override
        public UserVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_history_item_user, parent, false);
            return new UserVH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull UserVH holder, int position) {
            String username = userList.get(position);
            holder.tvUsername.setText(username);

            holder.itemView.setOnClickListener(v -> {
                Intent i = new Intent(HistoryActivity.this, UserHistoryActivity.class);
                i.putExtra("username", username);
                startActivity(i);
            });
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }

        class UserVH extends RecyclerView.ViewHolder {
            TextView tvUsername;

            UserVH(@NonNull View itemView) {
                super(itemView);
                tvUsername = itemView.findViewById(R.id.tv_username);
            }
        }
    }
}