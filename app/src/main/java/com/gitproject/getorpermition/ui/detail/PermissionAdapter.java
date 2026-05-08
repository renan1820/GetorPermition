package com.gitproject.getorpermition.ui.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.gitproject.getorpermition.R;
import com.gitproject.getorpermition.data.model.PermissionInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PermissionAdapter extends RecyclerView.Adapter<PermissionAdapter.PermissionViewHolder> {

    private List<PermissionInfo> permissions = new ArrayList<>();
    private final Set<Integer> expandedPositions = new HashSet<>();

    public void setPermissions(List<PermissionInfo> permissions) {
        this.permissions = permissions != null ? permissions : new ArrayList<>();
        expandedPositions.clear();
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
        PermissionInfo p = permissions.get(position);
        boolean expanded = expandedPositions.contains(position);
        holder.bind(p, expanded);

        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_ID) return;
            if (expandedPositions.contains(pos)) expandedPositions.remove(pos);
            else expandedPositions.add(pos);
            notifyItemChanged(pos);
        });
    }

    @Override
    public int getItemCount() { return permissions.size(); }

    static class PermissionViewHolder extends RecyclerView.ViewHolder {
        FrameLayout flIconContainer;
        TextView tvName;
        TextView tvBadge;
        TextView tvGrantStatus;
        TextView tvChevron;
        LinearLayout layoutRiskDetail;
        TextView tvExplanation;
        TextView tvMaliciousUse;

        PermissionViewHolder(@NonNull View itemView) {
            super(itemView);
            flIconContainer  = itemView.findViewById(R.id.fl_icon_container);
            tvName           = itemView.findViewById(R.id.tv_permission_name);
            tvBadge          = itemView.findViewById(R.id.tv_permission_badge);
            tvGrantStatus    = itemView.findViewById(R.id.tv_grant_status);
            tvChevron        = itemView.findViewById(R.id.tv_chevron);
            layoutRiskDetail = itemView.findViewById(R.id.layout_risk_detail);
            tvExplanation    = itemView.findViewById(R.id.tv_permission_explanation);
            tvMaliciousUse   = itemView.findViewById(R.id.tv_permission_malicious_use);
        }

        void bind(PermissionInfo p, boolean expanded) {
            Context ctx = itemView.getContext();

            tvName.setText(p.getReadableName());
            tvExplanation.setText(p.getExplanation());
            tvMaliciousUse.setText(p.getMaliciousUse());
            tvChevron.setText(expanded ? "▲" : "▼");
            layoutRiskDetail.setVisibility(expanded ? View.VISIBLE : View.GONE);

            int badgeDrawable, textColor, iconBg;
            String label;

            switch (p.getRiskLevel()) {
                case HIGH:
                    badgeDrawable = R.drawable.bg_risk_badge_high;
                    textColor     = ContextCompat.getColor(ctx, R.color.risk_high_2);
                    iconBg        = ContextCompat.getColor(ctx, R.color.risk_high_bg);
                    label         = ctx.getString(R.string.risk_high);
                    break;
                case MEDIUM:
                    badgeDrawable = R.drawable.bg_risk_badge_medium;
                    textColor     = ContextCompat.getColor(ctx, R.color.risk_medium_2);
                    iconBg        = ContextCompat.getColor(ctx, R.color.risk_medium_bg);
                    label         = ctx.getString(R.string.risk_medium);
                    break;
                default:
                    badgeDrawable = R.drawable.bg_risk_badge_low;
                    textColor     = ContextCompat.getColor(ctx, R.color.risk_low_2);
                    iconBg        = ContextCompat.getColor(ctx, R.color.risk_low_bg);
                    label         = ctx.getString(R.string.risk_low);
                    break;
            }

            tvBadge.setBackgroundResource(badgeDrawable);
            tvBadge.setTextColor(textColor);
            tvBadge.setText(label);
            flIconContainer.setBackgroundColor(iconBg);

            if (p.isGranted()) {
                tvGrantStatus.setText(ctx.getString(R.string.permission_granted));
                tvGrantStatus.setTextColor(textColor);
                tvGrantStatus.setBackgroundResource(badgeDrawable);
                tvGrantStatus.setVisibility(View.VISIBLE);
            } else {
                tvGrantStatus.setText(ctx.getString(R.string.permission_denied));
                tvGrantStatus.setTextColor(ContextCompat.getColor(ctx, R.color.text_muted));
                tvGrantStatus.setBackgroundResource(0);
                tvGrantStatus.setVisibility(View.VISIBLE);
            }
        }
    }
}
