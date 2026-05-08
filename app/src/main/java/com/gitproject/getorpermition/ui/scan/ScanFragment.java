package com.gitproject.getorpermition.ui.scan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.gitproject.getorpermition.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ScanFragment extends Fragment {

    private ScanViewModel viewModel;
    private Button btnScan;
    private ImageButton ibLanguage;
    private ProgressBar progressBar;
    private TextView tvCurrentApp;
    private TextView tvStatus;
    private RadarView radarView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnScan    = view.findViewById(R.id.btn_start_scan);
        ibLanguage = view.findViewById(R.id.ib_language);
        progressBar  = view.findViewById(R.id.progress_scan);
        tvCurrentApp = view.findViewById(R.id.tv_current_app);
        tvStatus     = view.findViewById(R.id.tv_status);
        radarView    = view.findViewById(R.id.radar_view);

        // Scoped to Activity so ResultFragment reads the same data
        viewModel = new ViewModelProvider(requireActivity()).get(ScanViewModel.class);

        observeViewModel();
        btnScan.setOnClickListener(v -> viewModel.startScan());
        ibLanguage.setOnClickListener(v -> showLanguageDialog());
    }

    private void observeViewModel() {
        viewModel.getScanState().observe(getViewLifecycleOwner(), state -> {
            switch (state) {
                case IDLE:
                    showIdleUi();
                    break;
                case SCANNING:
                    showScanningUi();
                    break;
                case DONE:
                    // Guard: only navigate if we're still on this fragment
                    androidx.navigation.NavController nav =
                            Navigation.findNavController(requireView());
                    if (nav.getCurrentDestination() != null
                            && nav.getCurrentDestination().getId() == R.id.scanFragment) {
                        nav.navigate(R.id.action_scan_to_result);
                    }
                    break;
                case ERROR:
                    showIdleUi();
                    tvStatus.setText(getString(R.string.scan_error));
                    break;
            }
        });

        viewModel.getCurrentAppName().observe(getViewLifecycleOwner(), name -> {
            if (name != null && !name.isEmpty()) tvCurrentApp.setText(name);
        });

        viewModel.getScanProgress().observe(getViewLifecycleOwner(), progress ->
                progressBar.setProgress((int) (progress * 100)));
    }

    private void showIdleUi() {
        btnScan.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        tvCurrentApp.setVisibility(View.GONE);
        tvStatus.setText("");
        radarView.stopAnimation();
    }

    private void showScanningUi() {
        btnScan.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        tvCurrentApp.setVisibility(View.VISIBLE);
        tvStatus.setText(getString(R.string.scanning_label));
        radarView.startAnimation();
    }

    private void showLanguageDialog() {
        String[] options = {
                getString(R.string.language_pt_br),
                getString(R.string.language_en)
        };
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.language_select_title)
                .setItems(options, (dialog, which) -> {
                    String tag = which == 0 ? "pt-BR" : "en";
                    AppCompatDelegate.setApplicationLocales(
                            LocaleListCompat.forLanguageTags(tag));
                })
                .show();
    }
}
