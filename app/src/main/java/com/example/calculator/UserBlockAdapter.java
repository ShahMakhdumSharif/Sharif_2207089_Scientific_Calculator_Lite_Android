package com.example.calculator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class UserBlockAdapter extends RecyclerView.Adapter<UserBlockAdapter.ViewHolder> {

    private final List<String> users;
    private final boolean blockAction; // true = block, false = unblock

    public UserBlockAdapter(List<String> users, boolean blockAction) {
        this.users = users;
        this.blockAction = blockAction;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_block, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String username = users.get(position);
        holder.tvUsername.setText(username);
        holder.btnAction.setText(blockAction ? "Block" : "Unblock");

        holder.btnAction.setOnClickListener(v -> {
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(username)
                    .child("blocked");

            ref.setValue(blockAction);
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername;
        Button btnAction;

        ViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username);
            btnAction = itemView.findViewById(R.id.btn_action);
        }
    }
}