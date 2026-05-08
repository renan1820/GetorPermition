package com.gitproject.getorpermition.data.model;

import android.graphics.drawable.Drawable;
import java.io.Serializable;
import java.util.List;

public class AppInfo implements Serializable {

    private final String appName;
    private final String packageName;
    private final List<PermissionInfo> permissions;
    private int riskScore; // 0–100, higher = safer
    private boolean scoreSet = false; // true only after setRiskScore() is called
    private int grantedRiskScore; // score computed using only granted permissions
    private boolean grantedScoreSet = false;

    // Drawable is not Serializable; passed separately when navigating to detail
    private transient Drawable icon;

    public AppInfo(String appName, String packageName, List<PermissionInfo> permissions) {
        this.appName     = appName;
        this.packageName = packageName;
        this.permissions = permissions;
    }

    // --- getters ---

    public String getAppName()              { return appName; }
    public String getPackageName()          { return packageName; }
    public List<PermissionInfo> getPermissions() { return permissions; }
    public int getRiskScore()               { return riskScore; }
    public int getGrantedRiskScore()        { return grantedRiskScore; }
    public Drawable getIcon()               { return icon; }

    // --- setters ---

    public void setRiskScore(int riskScore) {
        this.riskScore = riskScore;
        this.scoreSet  = true;
    }

    public void setGrantedRiskScore(int score) {
        this.grantedRiskScore = score;
        this.grantedScoreSet  = true;
    }

    public void setIcon(Drawable icon) { this.icon = icon; }

    // --- risk helpers ---

    public int countHighRisk() {
        int count = 0;
        for (PermissionInfo p : permissions)
            if (p.getRiskLevel() == PermissionInfo.RiskLevel.HIGH) count++;
        return count;
    }

    public int countMediumRisk() {
        int count = 0;
        for (PermissionInfo p : permissions)
            if (p.getRiskLevel() == PermissionInfo.RiskLevel.MEDIUM) count++;
        return count;
    }

    /**
     * Derives the dominant risk level from the permission list alone (HIGH > MEDIUM > LOW).
     * Does NOT consider the computed score — use getAppCategory() for score-aware classification.
     */
    public PermissionInfo.RiskLevel getDominantRisk() {
        if (countHighRisk() > 0)   return PermissionInfo.RiskLevel.HIGH;
        if (countMediumRisk() > 0) return PermissionInfo.RiskLevel.MEDIUM;
        return PermissionInfo.RiskLevel.LOW;
    }

    /**
     * Returns the effective risk category for UI display and global-score weighting.
     * Returns EXTREME when the computed score reaches 0; otherwise delegates to getDominantRisk().
     * EXTREME is only reported after setRiskScore() has been called to avoid
     * misclassifying freshly constructed (unscored) AppInfo objects.
     */
    public PermissionInfo.RiskLevel getAppCategory() {
        if (scoreSet && riskScore == 0) return PermissionInfo.RiskLevel.EXTREME;
        return getDominantRisk();
    }

    /** Dominant risk considering only granted permissions. */
    public PermissionInfo.RiskLevel getGrantedDominantRisk() {
        boolean hasHigh = false, hasMedium = false;
        for (PermissionInfo p : permissions) {
            if (!p.isGranted()) continue;
            if (p.getRiskLevel() == PermissionInfo.RiskLevel.HIGH)   hasHigh   = true;
            if (p.getRiskLevel() == PermissionInfo.RiskLevel.MEDIUM) hasMedium = true;
        }
        if (hasHigh)   return PermissionInfo.RiskLevel.HIGH;
        if (hasMedium) return PermissionInfo.RiskLevel.MEDIUM;
        return PermissionInfo.RiskLevel.LOW;
    }

    /** Risk category based solely on granted permissions and granted score. */
    public PermissionInfo.RiskLevel getGrantedAppCategory() {
        if (grantedScoreSet && grantedRiskScore == 0) return PermissionInfo.RiskLevel.EXTREME;
        return getGrantedDominantRisk();
    }
}
