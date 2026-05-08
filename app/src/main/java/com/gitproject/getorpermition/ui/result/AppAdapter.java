package com.gitproject.getorpermition.ui.result;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.gitproject.getorpermition.R;
import com.gitproject.getorpermition.data.model.AppInfo;
import com.gitproject.getorpermition.data.model.PermissionInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> {

    public interface OnDetailClickListener {
        void onDetailClick(AppInfo app);
    }

    private List<AppInfo> apps = new ArrayList<>();
    // Track which positions are expanded
    private final Set<Integer> expandedPositions = new HashSet<>();
    private OnDetailClickListener detailListener;

    public void setApps(List<AppInfo> apps) {
        this.apps = apps != null ? apps : new ArrayList<>();
        expandedPositions.clear();
        notifyDataSetChanged();
    }

    public void setOnDetailClickListener(OnDetailClickListener listener) {
        this.detailListener = listener;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        AppInfo app = apps.get(position);
        boolean expanded = expandedPositions.contains(position);
        holder.bind(app, expanded);

        holder.cardView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_ID) return;
            if (expandedPositions.contains(pos)) {
                expandedPositions.remove(pos);
            } else {
                expandedPositions.add(pos);
            }
            notifyItemChanged(pos);
        });

        holder.btnDetail.setOnClickListener(v -> {
            if (detailListener != null) detailListener.onDetailClick(app);
        });
    }

    @Override
    public int getItemCount() { return apps.size(); }

    static class AppViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivIcon;
        TextView tvAppName;
        TextView tvRiskBadge;
        TextView tvScore;
        LinearLayout expandedLayout;
        LinearLayout permissionsContainer;
        Button btnDetail;

        AppViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_app);
            ivIcon = itemView.findViewById(R.id.iv_app_icon);
            tvAppName = itemView.findViewById(R.id.tv_app_name);
            tvRiskBadge = itemView.findViewById(R.id.tv_risk_badge);
            tvScore = itemView.findViewById(R.id.tv_score);
            expandedLayout = itemView.findViewById(R.id.layout_expanded);
            permissionsContainer = itemView.findViewById(R.id.container_permissions);
            btnDetail = itemView.findViewById(R.id.btn_detail);
        }

        void bind(AppInfo app, boolean expanded) {
            Context ctx = itemView.getContext();

            tvAppName.setText(app.getAppName());
            tvScore.setText(String.valueOf(app.getRiskScore()));

            if (app.getIcon() != null) {
                ivIcon.setImageDrawable(app.getIcon());
            } else {
                ivIcon.setImageResource(android.R.drawable.sym_def_app_icon);
            }

            // Risk badge
            PermissionInfo.RiskLevel dominant = app.getDominantRisk();
            switch (dominant) {
                case HIGH:
                    tvRiskBadge.setText(ctx.getString(R.string.risk_high));
                    tvRiskBadge.setBackgroundColor(ContextCompat.getColor(ctx, R.color.risk_high));
                    tvScore.setTextColor(ContextCompat.getColor(ctx, R.color.risk_high));
                    break;
                case MEDIUM:
                    tvRiskBadge.setText(ctx.getString(R.string.risk_medium));
                    tvRiskBadge.setBackgroundColor(ContextCompat.getColor(ctx, R.color.risk_medium));
                    tvScore.setTextColor(ContextCompat.getColor(ctx, R.color.risk_medium));
                    break;
                default:
                    tvRiskBadge.setText(ctx.getString(R.string.risk_low));
                    tvRiskBadge.setBackgroundColor(ContextCompat.getColor(ctx, R.color.risk_low));
                    tvScore.setTextColor(ContextCompat.getColor(ctx, R.color.risk_low));
                    break;
            }

            // Expanded permissions preview (top 5)
            expandedLayout.setVisibility(expanded ? View.VISIBLE : View.GONE);
            if (expanded) {
                permissionsContainer.removeAllViews();
                List<PermissionInfo> perms = app.getPermissions();
                int limit = Math.min(perms.size(), 5);
                for (int i = 0; i < limit; i++) {
                    PermissionInfo p = perms.get(i);
                    TextView tv = new TextView(ctx);
                    tv.setText("• " + p.getReadableName());
                    tv.setTextSize(13f);
                    switch (p.getRiskLevel()) {
                        case HIGH:
                            tv.setTextColor(ContextCompat.getColor(ctx, R.color.risk_high)); break;
                        case MEDIUM:
                            tv.setTextColor(ContextCompat.getColor(ctx, R.color.risk_medium)); break;
                        default:
                            tv.setTextColor(ContextCompat.getColor(ctx, R.color.text_muted)); break;
                    }
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 4, 0, 0);
                    tv.setLayoutParams(lp);
                    permissionsContainer.addView(tv);
                }
                if (perms.size() > 5) {
                    TextView more = new TextView(ctx);
                    more.setText("+" + (perms.size() - 5) + " mais permissões");
                    more.setTextSize(12f);
                    more.setTextColor(ContextCompat.getColor(ctx, R.color.text_muted));
                    permissionsContainer.addView(more);
                }
            }
        }
    }
}
