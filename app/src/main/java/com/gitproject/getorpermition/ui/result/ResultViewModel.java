package com.gitproject.getorpermition.ui.result;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gitproject.getorpermition.data.model.AppInfo;
import com.gitproject.getorpermition.data.model.PermissionInfo;

import java.util.ArrayList;
import java.util.List;

public class ResultViewModel extends ViewModel {

    public enum Filter { ALL, HIGH, MEDIUM, LOW }

    private List<AppInfo> allApps = new ArrayList<>();
    private final MutableLiveData<Filter> activeFilter = new MutableLiveData<>(Filter.ALL);
    private final MediatorLiveData<List<AppInfo>> filteredApps = new MediatorLiveData<>();

    public ResultViewModel() {
        filteredApps.addSource(activeFilter, filter -> applyFilter());
    }

    public void setApps(List<AppInfo> apps) {
        this.allApps = apps != null ? apps : new ArrayList<>();
        applyFilter();
    }

    public void setFilter(Filter filter) {
        activeFilter.setValue(filter);
    }

    private void applyFilter() {
        Filter filter = activeFilter.getValue();
        if (filter == null || filter == Filter.ALL) {
            filteredApps.setValue(allApps);
            return;
        }
        List<AppInfo> result = new ArrayList<>();
        for (AppInfo app : allApps) {
            PermissionInfo.RiskLevel dominant = app.getDominantRisk();
            if (filter == Filter.HIGH && dominant == PermissionInfo.RiskLevel.HIGH) result.add(app);
            else if (filter == Filter.MEDIUM && dominant == PermissionInfo.RiskLevel.MEDIUM) result.add(app);
            else if (filter == Filter.LOW && dominant == PermissionInfo.RiskLevel.LOW) result.add(app);
        }
        filteredApps.setValue(result);
    }

    public LiveData<List<AppInfo>> getFilteredApps() { return filteredApps; }
    public LiveData<Filter> getActiveFilter() { return activeFilter; }
}
