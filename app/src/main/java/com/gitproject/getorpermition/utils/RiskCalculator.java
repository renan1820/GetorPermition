package com.gitproject.getorpermition.utils;

import com.gitproject.getorpermition.data.model.AppInfo;
import com.gitproject.getorpermition.data.model.PermissionInfo;
import com.gitproject.getorpermition.data.model.PermissionInfo.RiskLevel;

import java.util.List;

/**
 * Calculates security scores for individual apps and the global device score.
 *
 * Per-app score (0–100, higher = safer):
 *   Start at 100.
 *   Each HIGH permission  → -20 (capped at -60 total from HIGH)
 *   Each MEDIUM permission → -8 (capped at -24 total from MEDIUM)
 *   Each LOW permission   → -1
 *
 * Global device score:
 *   Weighted average where apps with HIGH-risk dominant weight × 3.
 */
public class RiskCalculator {

    private static final int PENALTY_HIGH = 20;
    private static final int PENALTY_MEDIUM = 8;
    private static final int PENALTY_LOW = 1;
    private static final int CAP_HIGH = 60;
    private static final int CAP_MEDIUM = 24;

    public static int calculateAppScore(AppInfo app) {
        int penaltyHigh = 0;
        int penaltyMedium = 0;
        int penaltyLow = 0;

        for (PermissionInfo p : app.getPermissions()) {
            if (p.getRiskLevel() == RiskLevel.HIGH) penaltyHigh += PENALTY_HIGH;
            else if (p.getRiskLevel() == RiskLevel.MEDIUM) penaltyMedium += PENALTY_MEDIUM;
            else penaltyLow += PENALTY_LOW;
        }

        // Apply caps per risk category
        penaltyHigh = Math.min(penaltyHigh, CAP_HIGH);
        penaltyMedium = Math.min(penaltyMedium, CAP_MEDIUM);

        int score = 100 - penaltyHigh - penaltyMedium - penaltyLow;
        return Math.max(0, score);
    }

    public static int calculateGlobalScore(List<AppInfo> apps) {
        if (apps == null || apps.isEmpty()) return 100;

        long weightedSum = 0;
        long totalWeight = 0;

        for (AppInfo app : apps) {
            // Apps with high-risk permissions get weight 3x in the global average
            int weight = (app.getDominantRisk() == RiskLevel.HIGH) ? 3 : 1;
            weightedSum += (long) app.getRiskScore() * weight;
            totalWeight += weight;
        }

        return (int) (weightedSum / totalWeight);
    }
}
