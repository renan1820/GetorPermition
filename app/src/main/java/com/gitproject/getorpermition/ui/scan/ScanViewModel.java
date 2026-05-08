package com.gitproject.getorpermition.ui.scan;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gitproject.getorpermition.data.model.AppInfo;
import com.gitproject.getorpermition.data.repository.PermissionRepository;

import java.util.List;

public class ScanViewModel extends AndroidViewModel {

    public enum ScanState { IDLE, SCANNING, DONE, ERROR }

    private final MutableLiveData<ScanState> scanState = new MutableLiveData<>(ScanState.IDLE);
    private final MutableLiveData<String> currentAppName = new MutableLiveData<>("");
    private final MutableLiveData<Float> scanProgress = new MutableLiveData<>(0f);
    private final MutableLiveData<List<AppInfo>> apps = new MutableLiveData<>();
    private final MutableLiveData<Integer> globalScore = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final PermissionRepository repository;

    public ScanViewModel(@NonNull Application application) {
        super(application);
        repository = new PermissionRepository(application);
    }

    public void startScan() {
        if (scanState.getValue() == ScanState.SCANNING) return;
        scanState.setValue(ScanState.SCANNING);
        scanProgress.setValue(0f);

        repository.scanInstalledApps(new PermissionRepository.ScanCallback() {
            @Override
            public void onProgress(String appName, int scanned, int total) {
                // This is called from a background thread; post to main thread via LiveData
                currentAppName.postValue(appName);
                scanProgress.postValue(total > 0 ? (float) scanned / total : 0f);
            }

            @Override
            public void onComplete(List<AppInfo> result, int score) {
                apps.setValue(result);
                globalScore.setValue(score);
                scanState.setValue(ScanState.DONE);
            }

            @Override
            public void onError(Exception e) {
                errorMessage.setValue(e.getMessage());
                scanState.setValue(ScanState.ERROR);
            }
        });
    }

    // --- Observables ---
    public LiveData<ScanState> getScanState() { return scanState; }
    public LiveData<String> getCurrentAppName() { return currentAppName; }
    public LiveData<Float> getScanProgress() { return scanProgress; }
    public LiveData<List<AppInfo>> getApps() { return apps; }
    public LiveData<Integer> getGlobalScore() { return globalScore; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
}
