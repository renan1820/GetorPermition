package com.gitproject.getorpermition.ui.result;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.gitproject.getorpermition.R;
import com.gitproject.getorpermition.data.model.AppInfo;
import com.gitproject.getorpermition.data.model.PermissionInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_NORMAL   = 0;
    private static final int TYPE_CRITICAL = 1;

    public interface OnDetailClickListener {
        void onDetailClick(AppInfo app);
    }

    private List<AppInfo> apps = new ArrayList<>();
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

    @Override
    public int getItemViewType(int position) {
        return apps.get(position).getAppCategory() == PermissionInfo.RiskLevel.EXTREME
                ? TYPE_CRITICAL : TYPE_NORMAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_CRITICAL) {
            View view = inflater.inflate(R.layout.item_app_critical, parent, false);
            return new CriticalViewHolder(view);
        }
        View view = inflater.inflate(R.layout.item_app, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AppInfo app = apps.get(position);
        boolean expanded = expandedPositions.contains(position);

        View cardRoot;
        Button btnDetail;

        if (holder instanceof CriticalViewHolder) {
            CriticalViewHolder vh = (CriticalViewHolder) holder;
            vh.bind(app, expanded);
            cardRoot  = vh.cardView;
            btnDetail = vh.btnDetail;
        } else {
            AppViewHolder vh = (AppViewHolder) holder;
            vh.bind(app, expanded);
            cardRoot  = vh.cardView;
            btnDetail = vh.btnDetail;
        }

        cardRoot.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_ID) return;
            if (expandedPositions.contains(pos)) expandedPositions.remove(pos);
            else expandedPositions.add(pos);
            notifyItemChanged(pos);
        });

        btnDetail.setOnClickListener(v -> {
            if (detailListener != null) detailListener.onDetailClick(app);
        });
    }

    @Override
    public int getItemCount() { return apps.size(); }

    // ─────────────────────────────────────────────────────────────
    // Normal ViewHolder
    // ─────────────────────────────────────────────────────────────

    static class AppViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivIcon;
        TextView tvAppName;
        TextView tvRiskBadge;
        TextView tvScore;
        ProgressBar pbScoreBar;
        LinearLayout expandedLayout;
        LinearLayout permissionsContainer;
        Button btnDetail;

        AppViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView             = itemView.findViewById(R.id.card_app);
            ivIcon               = itemView.findViewById(R.id.iv_app_icon);
            tvAppName            = itemView.findViewById(R.id.tv_app_name);
            tvRiskBadge          = itemView.findViewById(R.id.tv_risk_badge);
            tvScore              = itemView.findViewById(R.id.tv_score);
            pbScoreBar           = itemView.findViewById(R.id.pb_score_bar);
            expandedLayout       = itemView.findViewById(R.id.layout_expanded);
            permissionsContainer = itemView.findViewById(R.id.container_permissions);
            btnDetail            = itemView.findViewById(R.id.btn_detail);
        }

        void bind(AppInfo app, boolean expanded) {
            Context ctx = itemView.getContext();

            tvAppName.setText(app.getAppName());
            tvScore.setText(String.valueOf(app.getRiskScore()));
            pbScoreBar.setProgress(app.getRiskScore());

            if (app.getIcon() != null) ivIcon.setImageDrawable(app.getIcon());
            else ivIcon.setImageResource(android.R.drawable.sym_def_app_icon);

            applyRiskStyle(ctx, app.getDominantRisk());

            expandedLayout.setVisibility(expanded ? View.VISIBLE : View.GONE);
            if (expanded) {
                permissionsContainer.removeAllViews();
                List<PermissionInfo> perms = app.getPermissions();
                int limit = Math.min(perms.size(), 5);
                for (int i = 0; i < limit; i++) addPermissionLine(ctx, perms.get(i));
                if (perms.size() > 5) {
                    permissionsContainer.addView(makeTextView(ctx,
                            ctx.getString(R.string.more_permissions, perms.size() - 5),
                            ContextCompat.getColor(ctx, R.color.text_muted), 12f, 0));
                }
            }
        }

        private void applyRiskStyle(Context ctx, PermissionInfo.RiskLevel level) {
            int color, bgDrawable, scoreColor;
            String label;
            switch (level) {
                case HIGH:
                    color      = ContextCompat.getColor(ctx, R.color.risk_high_2);
                    bgDrawable = R.drawable.bg_risk_badge_high;
                    scoreColor = ContextCompat.getColor(ctx, R.color.risk_high);
                    label      = ctx.getString(R.string.risk_high);
                    pbScoreBar.setProgressTintList(android.content.res.ColorStateList.valueOf(
                            ContextCompat.getColor(ctx, R.color.risk_high)));
                    break;
                case MEDIUM:
                    color      = ContextCompat.getColor(ctx, R.color.risk_medium_2);
                    bgDrawable = R.drawable.bg_risk_badge_medium;
                    scoreColor = ContextCompat.getColor(ctx, R.color.risk_medium);
                    label      = ctx.getString(R.string.risk_medium);
                    pbScoreBar.setProgressTintList(android.content.res.ColorStateList.valueOf(
                            ContextCompat.getColor(ctx, R.color.risk_medium)));
                    break;
                default:
                    color      = ContextCompat.getColor(ctx, R.color.risk_low_2);
                    bgDrawable = R.drawable.bg_risk_badge_low;
                    scoreColor = ContextCompat.getColor(ctx, R.color.risk_low);
                    label      = ctx.getString(R.string.risk_low);
                    pbScoreBar.setProgressTintList(android.content.res.ColorStateList.valueOf(
                            ContextCompat.getColor(ctx, R.color.risk_low)));
                    break;
            }
            tvRiskBadge.setText(label);
            tvRiskBadge.setTextColor(color);
            tvRiskBadge.setBackgroundResource(bgDrawable);
            tvScore.setTextColor(scoreColor);
        }

        void addPermissionLine(Context ctx, PermissionInfo p) {
            int nameColor;
            String prefix;
            if (p.isGranted()) {
                prefix = "● ";
                switch (p.getRiskLevel()) {
                    case HIGH:   nameColor = ContextCompat.getColor(ctx, R.color.risk_high_2);   break;
                    case MEDIUM: nameColor = ContextCompat.getColor(ctx, R.color.risk_medium_2); break;
                    default:     nameColor = ContextCompat.getColor(ctx, R.color.text_muted);    break;
                }
            } else {
                prefix = "○ ";
                nameColor = ContextCompat.getColor(ctx, R.color.text_muted);
            }
            permissionsContainer.addView(
                    makeTextView(ctx, prefix + p.getReadableName(), nameColor, 13f, 4));

            // Explanation only for dangerous permissions that are actually granted
            if (p.isGranted() && p.getRiskLevel() != PermissionInfo.RiskLevel.LOW
                    && p.getExplanation() != null && !p.getExplanation().isEmpty()) {
                permissionsContainer.addView(
                        makeTextView(ctx, "  " + p.getExplanation(),
                                ContextCompat.getColor(ctx, R.color.text_muted), 11f, 2));
            }
        }

        TextView makeTextView(Context ctx, String text, int color, float sizeSp, int topMarginDp) {
            TextView tv = new TextView(ctx);
            tv.setText(text);
            tv.setTextColor(color);
            tv.setTextSize(sizeSp);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            float density = ctx.getResources().getDisplayMetrics().density;
            lp.setMargins(0, (int) (topMarginDp * density), 0, 0);
            tv.setLayoutParams(lp);
            return tv;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Critical ViewHolder (score = 0)
    // ─────────────────────────────────────────────────────────────

    static class CriticalViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivIcon;
        TextView tvAppName;
        LinearLayout expandedLayout;
        LinearLayout permissionsContainer;
        Button btnDetail;

        CriticalViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView             = itemView.findViewById(R.id.card_app);
            ivIcon               = itemView.findViewById(R.id.iv_app_icon);
            tvAppName            = itemView.findViewById(R.id.tv_app_name);
            expandedLayout       = itemView.findViewById(R.id.layout_expanded);
            permissionsContainer = itemView.findViewById(R.id.container_permissions);
            btnDetail            = itemView.findViewById(R.id.btn_detail);
        }

        void bind(AppInfo app, boolean expanded) {
            Context ctx = itemView.getContext();

            tvAppName.setText(app.getAppName());

            if (app.getIcon() != null) ivIcon.setImageDrawable(app.getIcon());
            else ivIcon.setImageResource(android.R.drawable.sym_def_app_icon);

            expandedLayout.setVisibility(expanded ? View.VISIBLE : View.GONE);
            if (expanded) {
                permissionsContainer.removeAllViews();
                List<PermissionInfo> perms = app.getPermissions();
                int limit = Math.min(perms.size(), 5);
                for (int i = 0; i < limit; i++) {
                    PermissionInfo p = perms.get(i);
                    int nameColor;
                    String prefix;
                    if (p.isGranted()) {
                        prefix = "● ";
                        switch (p.getRiskLevel()) {
                            case HIGH:   nameColor = ContextCompat.getColor(ctx, R.color.risk_critical_2); break;
                            case MEDIUM: nameColor = ContextCompat.getColor(ctx, R.color.risk_medium_2);   break;
                            default:     nameColor = ContextCompat.getColor(ctx, R.color.text_muted);      break;
                        }
                    } else {
                        prefix = "○ ";
                        nameColor = ContextCompat.getColor(ctx, R.color.text_muted);
                    }
                    permissionsContainer.addView(
                            makeTextView(ctx, prefix + p.getReadableName(), nameColor, 13f, 4));

                    // Explanation only for dangerous permissions that are actually granted
                    if (p.isGranted() && p.getRiskLevel() != PermissionInfo.RiskLevel.LOW
                            && p.getExplanation() != null && !p.getExplanation().isEmpty()) {
                        permissionsContainer.addView(
                                makeTextView(ctx, "  " + p.getExplanation(),
                                        ContextCompat.getColor(ctx, R.color.text_muted), 11f, 2));
                    }
                }
                if (perms.size() > 5) {
                    permissionsContainer.addView(makeTextView(ctx,
                            ctx.getString(R.string.more_permissions, perms.size() - 5),
                            ContextCompat.getColor(ctx, R.color.text_muted), 12f, 0));
                }
            }
        }

        private TextView makeTextView(Context ctx, String text, int color, float sizeSp, int topMarginDp) {
            TextView tv = new TextView(ctx);
            tv.setText(text);
            tv.setTextColor(color);
            tv.setTextSize(sizeSp);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            float density = ctx.getResources().getDisplayMetrics().density;
            lp.setMargins(0, (int) (topMarginDp * density), 0, 0);
            tv.setLayoutParams(lp);
            return tv;
        }
    }
}

