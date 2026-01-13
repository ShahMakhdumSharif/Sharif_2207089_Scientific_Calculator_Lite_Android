package com.example.calculator;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LimitUserAdapter extends RecyclerView.Adapter<LimitUserAdapter.UserViewHolder> {

    private List<String> users;
    private Context context;

    public LimitUserAdapter(Context context, List<String> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.activity_limit_item_user, parent, false); // Use your XML file here
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        String username = users.get(position);
        holder.textView.setText(username);

        holder.textView.setOnClickListener(v -> {
            Intent intent = new Intent(context, LimitOperationUserActivity.class);
            intent.putExtra("username", username); // Pass username to next activity
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_username);
        }
    }
}