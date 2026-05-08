package com.gitproject.getorpermition.ui.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.gitproject.getorpermition.R;
import com.gitproject.getorpermition.data.model.PermissionInfo;

import java.util.ArrayList;
import java.util.List;

public class PermissionAdapter extends RecyclerView.Adapter<PermissionAdapter.PermissionViewHolder> {

    private List<PermissionInfo> permissions = new ArrayList<>();

    public void setPermissions(List<PermissionInfo> permissions) {
        this.permissions = permissions != null ? permissions : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PermissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_permission, parent, false);
        return new PermissionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PermissionViewHolder holder, int position) {
        holder.bind(permissions.get(position));
    }

    @Override
    public int getItemCount() { return permissions.size(); }

    static class PermissionViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvExplanation;
        TextView tvBadge;

        PermissionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_permission_name);
            tvExplanation = itemView.findViewById(R.id.tv_permission_explanation);
            tvBadge = itemView.findViewById(R.id.tv_permission_badge);
        }

        void bind(PermissionInfo p) {
            Context ctx = itemView.getContext();
            tvName.setText(p.getReadableName());
            tvExplanation.setText(p.getExplanation());

            switch (p.getRiskLevel()) {
                case HIGH:
                    tvBadge.setText(ctx.getString(R.string.risk_high));
                    tvBadge.setBackgroundColor(ContextCompat.getColor(ctx, R.color.risk_high));
                    break;
                case MEDIUM:
                    tvBadge.setText(ctx.getString(R.string.risk_medium));
                    tvBadge.setBackgroundColor(ContextCompat.getColor(ctx, R.color.risk_medium));
                    break;
                default:
                    tvBadge.setText(ctx.getString(R.string.risk_low));
                    tvBadge.setBackgroundColor(ContextCompat.getColor(ctx, R.color.risk_low));
                    break;
            }
        }
    }
}
