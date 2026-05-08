package com.gitproject.getorpermition.ui.result;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gitproject.getorpermition.R;
import com.gitproject.getorpermition.ui.scan.ScanViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ResultFragment extends Fragment {

    private ScanViewModel scanViewModel;
    private ResultViewModel resultViewModel;
    private AppAdapter adapter;

    private TextView tvGlobalScore;
    private TextView tvScoreStatus;
    private TextView tvAppsCount;

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
        tvAppsCount   = view.findViewById(R.id.tv_apps_count);
        RecyclerView recyclerView     = view.findViewById(R.id.rv_apps);
        ChipGroup chipGroup           = view.findViewById(R.id.chip_group_filter);
        Chip chipGrantedOnly          = view.findViewById(R.id.chip_granted_only);

        scanViewModel   = new ViewModelProvider(requireActivity()).get(ScanViewModel.class);
        resultViewModel = new ViewModelProvider(this).get(ResultViewModel.class);

        setupRecyclerView(recyclerView);
        setupChips(chipGroup);
        setupGrantedOnlyToggle(chipGrantedOnly);
        observeViewModels();
        setupBackPressDialog();
    }

    private void setupBackPressDialog() {
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle(R.string.dialog_rescan_title)
                                .setMessage(R.string.dialog_rescan_message)
                                .setPositiveButton(R.string.dialog_rescan_yes, (d, w) -> {
                                    scanViewModel.reset();
                                    Navigation.findNavController(requireView()).popBackStack();
                                })
                                .setNegativeButton(R.string.dialog_rescan_no, null)
                                .show();
                    }
                });
    }

    private void setupRecyclerView(RecyclerView rv) {
        adapter = new AppAdapter();
        adapter.setOnDetailClickListener(app -> {
            Bundle args = new Bundle();
            args.putSerializable("app_info", app);
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_result_to_detail, args);
        });
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);
    }

    private void setupChips(ChipGroup chipGroup) {
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chip_all)         resultViewModel.setFilter(ResultViewModel.Filter.ALL);
            else if (id == R.id.chip_high)   resultViewModel.setFilter(ResultViewModel.Filter.HIGH);
            else if (id == R.id.chip_medium) resultViewModel.setFilter(ResultViewModel.Filter.MEDIUM);
            else if (id == R.id.chip_safe)   resultViewModel.setFilter(ResultViewModel.Filter.LOW);
        });
    }

    private void setupGrantedOnlyToggle(Chip chip) {
        chip.setOnCheckedChangeListener((btn, isChecked) -> {
            resultViewModel.setGrantedOnlyMode(isChecked);
            adapter.setGrantedOnlyMode(isChecked);
        });
    }

    private void observeViewModels() {
        scanViewModel.getApps().observe(getViewLifecycleOwner(), apps -> {
            if (apps != null) {
                resultViewModel.setApps(apps);
                tvAppsCount.setText(apps.size() + " " + getString(R.string.apps_analyzed));
            }
        });

        resultViewModel.getDisplayScore().observe(getViewLifecycleOwner(), score -> {
            if (score != null) updateScoreCard(score);
        });

        resultViewModel.getFilteredApps().observe(getViewLifecycleOwner(), apps ->
                adapter.setApps(apps));
    }

    private void updateScoreCard(int score) {
        tvGlobalScore.setText(String.valueOf(score));
        int textColor;
        String label;
        if (score < 40) {
            textColor = ContextCompat.getColor(requireContext(), R.color.risk_high);
            label = getString(R.string.score_vulnerable);
        } else if (score < 70) {
            textColor = ContextCompat.getColor(requireContext(), R.color.risk_medium);
            label = getString(R.string.score_moderate);
        } else {
            textColor = ContextCompat.getColor(requireContext(), R.color.risk_low);
            label = getString(R.string.score_safe);
        }
        tvGlobalScore.setTextColor(textColor);
        tvScoreStatus.setTextColor(textColor);
        tvScoreStatus.setText(label);
    }
}
