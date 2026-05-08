package com.gitproject.getorpermition.ui.result;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gitproject.getorpermition.data.model.AppInfo;
import com.gitproject.getorpermition.data.model.PermissionInfo;
import com.gitproject.getorpermition.utils.RiskCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResultViewModel extends ViewModel {

    public enum Filter { ALL, HIGH, MEDIUM, LOW }

    private List<AppInfo> allApps = new ArrayList<>();
    private final MutableLiveData<Filter>  activeFilter    = new MutableLiveData<>(Filter.ALL);
    private final MutableLiveData<Boolean> grantedOnlyMode = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> displayScore    = new MutableLiveData<>(0);
    private final MediatorLiveData<List<AppInfo>> filteredApps = new MediatorLiveData<>();

    public ResultViewModel() {
        filteredApps.addSource(activeFilter,    f -> applyFilter());
        filteredApps.addSource(grantedOnlyMode, m -> applyFilter());
    }

    public void setApps(List<AppInfo> apps) {
        this.allApps = apps != null ? apps : new ArrayList<>();
        updateDisplayScore();
        applyFilter();
    }

    public void setFilter(Filter filter) {
        activeFilter.setValue(filter);
    }

    public void setGrantedOnlyMode(boolean enabled) {
        grantedOnlyMode.setValue(enabled);
        updateDisplayScore();
    }

    private void updateDisplayScore() {
        Boolean granted = grantedOnlyMode.getValue();
        if (Boolean.TRUE.equals(granted)) {
            displayScore.setValue(RiskCalculator.calculateGlobalScoreGrantedOnly(allApps));
        } else {
            displayScore.setValue(RiskCalculator.calculateGlobalScore(allApps));
        }
    }

    private void applyFilter() {
        Filter filter  = activeFilter.getValue();
        Boolean granted = grantedOnlyMode.getValue();
        boolean grantedMode = Boolean.TRUE.equals(granted);

        List<AppInfo> base = new ArrayList<>();

        if (filter == null || filter == Filter.ALL) {
            base.addAll(allApps);
        } else {
            for (AppInfo app : allApps) {
                PermissionInfo.RiskLevel category = grantedMode
                        ? app.getGrantedAppCategory()
                        : app.getAppCategory();
                if (filter == Filter.HIGH
                        && (category == PermissionInfo.RiskLevel.EXTREME
                            || category == PermissionInfo.RiskLevel.HIGH)) {
                    base.add(app);
                } else if (filter == Filter.MEDIUM
                        && category == PermissionInfo.RiskLevel.MEDIUM) {
                    base.add(app);
                } else if (filter == Filter.LOW
                        && category == PermissionInfo.RiskLevel.LOW) {
                    base.add(app);
                }
            }
        }

        if (grantedMode) {
            // Re-rank by granted score: worst (lowest) first
            Collections.sort(base,
                    (a, b) -> Integer.compare(a.getGrantedRiskScore(), b.getGrantedRiskScore()));
        }

        filteredApps.setValue(base);
    }

    public LiveData<List<AppInfo>>  getFilteredApps()   { return filteredApps; }
    public LiveData<Filter>         getActiveFilter()    { return activeFilter; }
    public LiveData<Boolean>        getGrantedOnlyMode() { return grantedOnlyMode; }
    public LiveData<Integer>        getDisplayScore()    { return displayScore; }
}
