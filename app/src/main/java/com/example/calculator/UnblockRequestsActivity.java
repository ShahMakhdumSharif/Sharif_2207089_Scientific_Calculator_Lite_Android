package com.example.calculator;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UnblockRequestsActivity extends AppCompatActivity {

    private static final String TAG = "UnblockRequests";
    private RecyclerView rv;
    private UnblockRequestAdapter adapter;
    private List<UserRecord> requests = new ArrayList<>();

    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unblock_requests);

        rv = findViewById(R.id.rv_unblock_requests);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UnblockRequestAdapter(requests, this);
        rv.setAdapter(adapter);

        Button back = findViewById(R.id.btn_back_unblock);
        back.setOnClickListener(v -> finish());

        usersRef = FirebaseDatabase.getInstance().getReference("users");
        loadRequests();
    }

    private void loadRequests() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requests.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Boolean req = child.child("unblock_request").getValue(Boolean.class);
                    Boolean blocked = child.child("blocked").getValue(Boolean.class);
                    if (req != null && req && blocked != null && blocked) {
                        String username = child.getKey();
                        String name = child.child("name").getValue(String.class);
                        UserRecord u = new UserRecord(username == null ? "" : username, name == null ? "" : name);
                        requests.add(u);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load requests: " + error.getMessage());
                Toast.makeText(UnblockRequestsActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void approveRequest(String username) {
        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Invalid username", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Approve Unblock")
                .setMessage("Approve unblock request for '" + username + "'? This will make the user able to login again.")
                .setPositiveButton("Yes", (dlg, which) -> {
                    try {
                        usersRef.child(username).child("blocked").setValue(false);
                        usersRef.child(username).child("unblock_request").removeValue();
                        Toast.makeText(this, "Approved " + username, Toast.LENGTH_SHORT).show();
                        loadRequests();
                    } catch (Exception e) {
                        Log.e(TAG, "approveRequest failed: " + e.getMessage(), e);
                        Toast.makeText(this, "Failed to approve: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("No", (dlg, which) -> {
                    // nothing
                })
                .show();
    }

    public void rejectRequest(String username) {
        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Invalid username", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Reject Unblock")
                .setMessage("Reject unblock request for '" + username + "'? This will keep the user blocked.")
                .setPositiveButton("Yes", (dlg, which) -> {
                    try {
                        usersRef.child(username).child("unblock_request").removeValue();
                        Toast.makeText(this, "Rejected " + username, Toast.LENGTH_SHORT).show();
                        loadRequests();
                    } catch (Exception e) {
                        Log.e(TAG, "rejectRequest failed: " + e.getMessage(), e);
                        Toast.makeText(this, "Failed to reject: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("No", (dlg, which) -> {
                    // nothing
                })
                .show();
    }

    static class UserRecord {
        String username;
        String name;

        UserRecord(String username, String name) {
            this.username = username;
            this.name = name;
        }
    }
}
