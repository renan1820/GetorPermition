package com.gitproject.getorpermition.ui.detail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gitproject.getorpermition.R;
import com.gitproject.getorpermition.data.model.AppInfo;

public class DetailFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppInfo app = null;
        Bundle args = getArguments();
        if (args != null) app = (AppInfo) args.getSerializable("app_info");

        ImageView ivIcon              = view.findViewById(R.id.iv_detail_icon);
        TextView tvName               = view.findViewById(R.id.tv_detail_name);
        TextView tvPackage            = view.findViewById(R.id.tv_detail_package);
        TextView tvScore              = view.findViewById(R.id.tv_detail_score);
        LinearLayout layoutCritical   = view.findViewById(R.id.layout_critical_status);
        RecyclerView rv               = view.findViewById(R.id.rv_detail_permissions);
        Button btnSettings            = view.findViewById(R.id.btn_open_settings);

        PermissionAdapter adapter = new PermissionAdapter();
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        if (app != null) {
            final AppInfo finalApp = app;

            tvName.setText(app.getAppName());
            tvPackage.setText(app.getPackageName());

            if (app.getIcon() != null) ivIcon.setImageDrawable(app.getIcon());
            else ivIcon.setImageResource(android.R.drawable.sym_def_app_icon);

            bindScoreBlock(tvScore, layoutCritical, app);

            adapter.setPermissions(app.getPermissions());

            btnSettings.setOnClickListener(v -> {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", finalApp.getPackageName(), null));
                startActivity(intent);
            });
        }
    }

    private void bindScoreBlock(TextView tvScore, LinearLayout layoutCritical, AppInfo app) {
        if (app.getAppCategory() == com.gitproject.getorpermition.data.model.PermissionInfo.RiskLevel.EXTREME) {
            tvScore.setVisibility(View.GONE);
            layoutCritical.setVisibility(View.VISIBLE);
        } else {
            tvScore.setVisibility(View.VISIBLE);
            layoutCritical.setVisibility(View.GONE);
            int score = app.getRiskScore();
            tvScore.setText(String.valueOf(score));
            int color;
            if (score < 40)      color = ContextCompat.getColor(requireContext(), R.color.risk_high);
            else if (score < 70) color = ContextCompat.getColor(requireContext(), R.color.risk_medium);
            else                 color = ContextCompat.getColor(requireContext(), R.color.risk_low);
            tvScore.setTextColor(color);
        }
    }
}
