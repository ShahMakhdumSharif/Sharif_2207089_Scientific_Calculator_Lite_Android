package com.example.calculator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UnblockRequestAdapter extends RecyclerView.Adapter<UnblockRequestAdapter.VH> {

    private final List<UnblockRequestsActivity.UserRecord> items;
    private final UnblockRequestsActivity activity;

    public UnblockRequestAdapter(List<UnblockRequestsActivity.UserRecord> items, UnblockRequestsActivity activity) {
        this.items = items;
        this.activity = activity;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_unblock_request, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        UnblockRequestsActivity.UserRecord u = items.get(position);
        holder.tvUsername.setText(u.username);
        holder.tvName.setText(u.name);

        holder.btnApprove.setOnClickListener(v -> {
            if (activity != null) {
                try { activity.approveRequest(u.username); } catch (Exception e) { android.util.Log.e("UnblockAdapter", "approve click error", e); }
            }
        });
        holder.btnReject.setOnClickListener(v -> {
            if (activity != null) {
                try { activity.rejectRequest(u.username); } catch (Exception e) { android.util.Log.e("UnblockAdapter", "reject click error", e); }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvUsername, tvName;
        Button btnApprove, btnReject;

        VH(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username_unblock);
            tvName = itemView.findViewById(R.id.tv_name_unblock);
            btnApprove = itemView.findViewById(R.id.btn_approve);
            btnReject = itemView.findViewById(R.id.btn_reject);
        }
    }
}

