package com.gitproject.getorpermition.data.model;

import android.graphics.drawable.Drawable;
import java.io.Serializable;
import java.util.List;

public class AppInfo implements Serializable {

    private final String appName;
    private final String packageName;
    private final List<PermissionInfo> permissions;
    private int riskScore; // 0–100, higher = safer

    // Drawable is not Serializable; passed separately when navigating to detail
    private transient Drawable icon;

    public AppInfo(String appName, String packageName, List<PermissionInfo> permissions) {
        this.appName = appName;
        this.packageName = packageName;
        this.permissions = permissions;
    }

    // --- getters ---

    public String getAppName() { return appName; }
    public String getPackageName() { return packageName; }
    public List<PermissionInfo> getPermissions() { return permissions; }
    public int getRiskScore() { return riskScore; }
    public Drawable getIcon() { return icon; }

    // --- setters ---

    public void setRiskScore(int riskScore) { this.riskScore = riskScore; }
    public void setIcon(Drawable icon) { this.icon = icon; }

    // Convenience: count by risk level
    public int countHighRisk() {
        int count = 0;
        for (PermissionInfo p : permissions) {
            if (p.getRiskLevel() == PermissionInfo.RiskLevel.HIGH) count++;
        }
        return count;
    }

    public int countMediumRisk() {
        int count = 0;
        for (PermissionInfo p : permissions) {
            if (p.getRiskLevel() == PermissionInfo.RiskLevel.MEDIUM) count++;
        }
        return count;
    }

    // Dominant risk level for badge display
    public PermissionInfo.RiskLevel getDominantRisk() {
        if (countHighRisk() > 0) return PermissionInfo.RiskLevel.HIGH;
        if (countMediumRisk() > 0) return PermissionInfo.RiskLevel.MEDIUM;
        return PermissionInfo.RiskLevel.LOW;
    }
}
