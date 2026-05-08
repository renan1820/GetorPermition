package com.gitproject.getorpermition.ui.result;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gitproject.getorpermition.R;
import com.gitproject.getorpermition.data.model.AppInfo;
import com.gitproject.getorpermition.ui.scan.ScanViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class ResultFragment extends Fragment {

    private ScanViewModel scanViewModel;
    private ResultViewModel resultViewModel;
    private AppAdapter adapter;

    private TextView tvGlobalScore;
    private TextView tvScoreStatus;
    private TextView tvAppsCount;
    private RecyclerView recyclerView;
    private ChipGroup chipGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvGlobalScore = view.findViewById(R.id.tv_global_score);
        tvScoreStatus = view.findViewById(R.id.tv_score_status);
        tvAppsCount = view.findViewById(R.id.tv_apps_count);
        recyclerView = view.findViewById(R.id.rv_apps);
        chipGroup = view.findViewById(R.id.chip_group_filter);

        // Shared ViewModel from Activity: same instance as ScanFragment's
        scanViewModel = new ViewModelProvider(requireActivity()).get(ScanViewModel.class);
        resultViewModel = new ViewModelProvider(this).get(ResultViewModel.class);

        setupRecyclerView();
        setupChips();
        observeViewModels();
    }

    private void setupRecyclerView() {
        adapter = new AppAdapter();
        adapter.setOnDetailClickListener(app -> {
            Bundle args = new Bundle();
            args.putSerializable("app_info", app);
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_result_to_detail, args);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupChips() {
        Chip chipAll = chipGroup.findViewById(R.id.chip_all);
        Chip chipHigh = chipGroup.findViewById(R.id.chip_high);
        Chip chipMedium = chipGroup.findViewById(R.id.chip_medium);
        Chip chipSafe = chipGroup.findViewById(R.id.chip_safe);

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chip_all) resultViewModel.setFilter(ResultViewModel.Filter.ALL);
            else if (id == R.id.chip_high) resultViewModel.setFilter(ResultViewModel.Filter.HIGH);
            else if (id == R.id.chip_medium) resultViewModel.setFilter(ResultViewModel.Filter.MEDIUM);
            else if (id == R.id.chip_safe) resultViewModel.setFilter(ResultViewModel.Filter.LOW);
        });
    }

    private void observeViewModels() {
        scanViewModel.getApps().observe(getViewLifecycleOwner(), apps -> {
            if (apps != null) {
                resultViewModel.setApps(apps);
                tvAppsCount.setText(apps.size() + " " + getString(R.string.apps_analyzed));
            }
        });

        scanViewModel.getGlobalScore().observe(getViewLifecycleOwner(), score -> {
            if (score != null) updateScoreCard(score);
        });

        resultViewModel.getFilteredApps().observe(getViewLifecycleOwner(), apps -> {
            adapter.setApps(apps);
        });
    }

    private void updateScoreCard(int score) {
        tvGlobalScore.setText(String.valueOf(score));
        if (score < 40) {
            tvScoreStatus.setText(getString(R.string.score_vulnerable));
            tvGlobalScore.setTextColor(ContextCompat.getColor(requireContext(), R.color.score_vulnerable));
            tvScoreStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.score_vulnerable));
        } else if (score < 70) {
            tvScoreStatus.setText(getString(R.string.score_moderate));
            tvGlobalScore.setTextColor(ContextCompat.getColor(requireContext(), R.color.score_moderate));
            tvScoreStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.score_moderate));
        } else {
            tvScoreStatus.setText(getString(R.string.score_safe));
            tvGlobalScore.setTextColor(ContextCompat.getColor(requireContext(), R.color.score_safe));
            tvScoreStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.score_safe));
        }
    }
}
