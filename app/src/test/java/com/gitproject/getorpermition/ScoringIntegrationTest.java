package com.gitproject.getorpermition;

import com.gitproject.getorpermition.data.model.AppInfo;
import com.gitproject.getorpermition.data.model.PermissionInfo;
import com.gitproject.getorpermition.utils.PermissionClassifier;
import com.gitproject.getorpermition.utils.RiskCalculator;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration tests: full pipeline PermissionClassifier → AppInfo → RiskCalculator.
 *
 * These tests validate that the three core components work together correctly
 * using real Android permission strings, matching the production flow in
 * PermissionRepository.buildPermissionList().
 */
public class ScoringIntegrationTest {

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static AppInfo buildApp(String name, String... permNames) {
        List<PermissionInfo> perms = new ArrayList<>();
        for (String p : permNames) perms.add(PermissionClassifier.classify(p));
        AppInfo app = new AppInfo(name, "com.test." + name, perms);
        app.setRiskScore(RiskCalculator.calculateAppScore(app));
        return app;
    }

    // ═══════════════════════════════════════════════════════════════
    // Typical app profiles — real-world permission combinations
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void safeApp_onlyNetwork_scores99() {
        // A basic app that only uses internet (1 LOW penalty)
        AppInfo app = buildApp("safe",
                "android.permission.INTERNET",
                "android.permission.ACCESS_NETWORK_STATE");
        // 100 - 1 - 1 = 98
        assertEquals(98, app.getRiskScore());
        assertEquals(PermissionInfo.RiskLevel.LOW, app.getDominantRisk());
    }

    @Test
    public void messagingApp_smsTriple_scoreLimited() {
        // READ_SMS + SEND_SMS + RECEIVE_SMS: 3 HIGH permissions → penalty capped at 60
        AppInfo app = buildApp("messenger",
                "android.permission.READ_SMS",
                "android.permission.SEND_SMS",
                "android.permission.RECEIVE_SMS",
                "android.permission.INTERNET");
        // 100 - min(3×20, 60) - 1(LOW) = 100 - 60 - 1 = 39
        assertEquals(39, app.getRiskScore());
        assertEquals(PermissionInfo.RiskLevel.HIGH, app.getDominantRisk());
    }

    @Test
    public void locationApp_coarse_scoresMedium() {
        // App using coarse location (MEDIUM) + network (LOW)
        AppInfo app = buildApp("maps_light",
                "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.INTERNET");
        // 100 - 8 - 1 = 91
        assertEquals(91, app.getRiskScore());
        assertEquals(PermissionInfo.RiskLevel.MEDIUM, app.getDominantRisk());
    }

    @Test
    public void locationApp_fine_scoresHigh() {
        // App using precise GPS (HIGH) + network (LOW)
        AppInfo app = buildApp("maps_gps",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.INTERNET");
        // 100 - 20 - 1 = 79
        assertEquals(79, app.getRiskScore());
        assertEquals(PermissionInfo.RiskLevel.HIGH, app.getDominantRisk());
    }

    @Test
    public void cameraApp_isolatedCamera_scoresMedium() {
        // Camera-only app (CAMERA = MEDIUM when isolated)
        AppInfo app = buildApp("camera",
                "android.permission.CAMERA",
                "android.permission.INTERNET");
        // 100 - 8 - 1 = 91
        assertEquals(91, app.getRiskScore());
        assertEquals(PermissionInfo.RiskLevel.MEDIUM, app.getDominantRisk());
    }

    @Test
    public void spywareProfile_highDangerPerms_veryLowScore() {
        // An app requesting all the most dangerous permissions
        AppInfo app = buildApp("malicious",
                "android.permission.READ_CONTACTS",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.READ_SMS",
                "android.permission.RECORD_AUDIO",
                "android.permission.BIND_ACCESSIBILITY_SERVICE");
        // 5 HIGH (capped at 60) → score = 40
        assertEquals(40, app.getRiskScore());
        assertEquals(PermissionInfo.RiskLevel.HIGH, app.getDominantRisk());
        assertEquals(5, app.countHighRisk());
    }

    @Test
    public void unknownPermissions_treatedAsLow() {
        // Apps from OEM manufacturers may declare custom permissions — must default to LOW
        AppInfo app = buildApp("oem_app",
                "com.samsung.permission.SMARTCLIP",
                "com.google.android.gms.permission.AD_ID");
        // 2 unknown → 2 LOW → 100 - 2 = 98
        assertEquals(98, app.getRiskScore());
        assertEquals(PermissionInfo.RiskLevel.LOW, app.getDominantRisk());
    }

    @Test
    public void appWithContactsAndLocation_bothHigh_capApplied() {
        // READ_CONTACTS (HIGH) + ACCESS_FINE_LOCATION (HIGH)
        AppInfo app = buildApp("social",
                "android.permission.READ_CONTACTS",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.INTERNET");
        // 2 HIGH (40 penalty) + 1 LOW (1 penalty) = 100 - 40 - 1 = 59
        assertEquals(59, app.getRiskScore());
    }

    // ═══════════════════════════════════════════════════════════════
    // Global score — typical device scenarios
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void globalScore_cleanDevice_staysHigh() {
        // Device with only safe apps (no dangerous permissions)
        AppInfo a1 = buildApp("browser", "android.permission.INTERNET");
        AppInfo a2 = buildApp("clock",   "android.permission.VIBRATE");
        AppInfo a3 = buildApp("notes");  // no permissions

        // All LOW dominant → weight = 1 each
        // a1: 100-1=99, a2: 100-1=99, a3: 100
        // global = (99+99+100)/3 = 298/3 = 99
        int global = RiskCalculator.calculateGlobalScore(Arrays.asList(a1, a2, a3));
        assertTrue("Clean device should score above 90", global > 90);
    }

    @Test
    public void globalScore_deviceWithSingleSpywareApp_dragsScoreDown() {
        // 3 safe apps + 1 spyware app (score≈40, HIGH dominant → weight 3)
        AppInfo safe1 = buildApp("safe1", "android.permission.INTERNET");          // 99
        AppInfo safe2 = buildApp("safe2", "android.permission.VIBRATE");           // 99
        AppInfo safe3 = buildApp("safe3");                                          // 100
        AppInfo spy   = buildApp("spy",
                "android.permission.READ_CONTACTS",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.READ_SMS");
        // spy: 3 HIGH → capped at 60 → score = 40, dominant HIGH → weight 3

        // weights: spy=3, safe1=1, safe2=1, safe3=1 → total=6
        // sum = 40*3 + 99*1 + 99*1 + 100*1 = 120+99+99+100 = 418
        // global = 418/6 = 69
        int global = RiskCalculator.calculateGlobalScore(Arrays.asList(spy, safe1, safe2, safe3));
        assertTrue("One dangerous app should drag score below 75", global < 75);
        assertTrue("Score must stay above 0", global > 0);
    }

    @Test
    public void globalScore_highAppsWeighMoreThanMediumApps() {
        // Two scenarios with same raw score: HIGH vs MEDIUM dominant
        AppInfo highDom = new AppInfo("h", "com.h", buildPerms(
                "android.permission.READ_CONTACTS")); // HIGH → score 80, weight 3
        highDom.setRiskScore(80);

        AppInfo medDom = new AppInfo("m", "com.m", buildPerms(
                "android.permission.CAMERA")); // MEDIUM → score 92, weight 1
        medDom.setRiskScore(92);

        AppInfo referenceApp = new AppInfo("ref", "com.ref", java.util.Collections.emptyList());
        referenceApp.setRiskScore(100);

        // highDom pulls score down more due to weight
        int globalWithHigh   = RiskCalculator.calculateGlobalScore(Arrays.asList(highDom,  referenceApp));
        int globalWithMedium = RiskCalculator.calculateGlobalScore(Arrays.asList(medDom, referenceApp));

        // globalWithHigh = (80*3+100)/(3+1) = (240+100)/4 = 85
        // globalWithMedium = (92+100)/(1+1) = 192/2 = 96
        assertTrue("HIGH-dominant app must drag global score lower than MEDIUM-dominant",
                globalWithHigh < globalWithMedium);
    }

    // ═══════════════════════════════════════════════════════════════
    // Permission count helpers used in AppInfo
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void countHighRisk_usingRealPermissions_matchesExpected() {
        AppInfo app = buildApp("test",
                "android.permission.READ_CONTACTS",       // HIGH
                "android.permission.CAMERA",              // MEDIUM
                "android.permission.INTERNET",            // LOW
                "android.permission.ACCESS_FINE_LOCATION" // HIGH
        );
        assertEquals(2, app.countHighRisk());
        assertEquals(1, app.countMediumRisk());
    }

    @Test
    public void classify_thenCountAndScore_endToEndConsistency() {
        // Ensures that classify → AppInfo → calculateAppScore are consistent
        List<PermissionInfo> perms = new ArrayList<>();
        perms.add(PermissionClassifier.classify("android.permission.RECORD_AUDIO")); // HIGH
        perms.add(PermissionClassifier.classify("android.permission.CAMERA"));       // MEDIUM
        perms.add(PermissionClassifier.classify("android.permission.VIBRATE"));      // LOW

        AppInfo app = new AppInfo("test", "com.test", perms);
        int score = RiskCalculator.calculateAppScore(app);

        // 100 - 20(HIGH) - 8(MEDIUM) - 1(LOW) = 71
        assertEquals(71, score);
        assertEquals(1, app.countHighRisk());
        assertEquals(1, app.countMediumRisk());
        assertEquals(PermissionInfo.RiskLevel.HIGH, app.getDominantRisk());
    }

    // ═══════════════════════════════════════════════════════════════
    // EXTREME category — end-to-end pipeline
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void extremeApp_hundredLowPerms_scoredZeroAndClassifiedExtreme() {
        // 100 LOW permissions → 100 × 1 penalty = score 0 → getAppCategory() = EXTREME
        List<PermissionInfo> perms = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            perms.add(PermissionClassifier.classify("android.permission.INTERNET"));
        }
        AppInfo app = new AppInfo("extreme", "com.extreme", perms);
        app.setRiskScore(RiskCalculator.calculateAppScore(app));

        assertEquals(0, app.getRiskScore());
        assertEquals(PermissionInfo.RiskLevel.EXTREME, app.getAppCategory());
        assertEquals(PermissionInfo.RiskLevel.LOW, app.getDominantRisk());
    }

    @Test
    public void globalScore_extremeAppWeighsFourTimes_endToEnd() {
        // EXTREME (score=0, w=4) + safe (score=100, w=1) = (0×4+100×1)/(4+1) = 20
        AppInfo extreme = new AppInfo("extreme", "com.extreme", new ArrayList<>());
        extreme.setRiskScore(0);

        AppInfo safe = buildApp("safe");
        assertEquals(20, RiskCalculator.calculateGlobalScore(Arrays.asList(extreme, safe)));
    }

    @Test
    public void globalScore_extremeOutweighsHighDominantApp() {
        // EXTREME (score=0, w=4) + clean (100, w=1) = (0×4+100)/(4+1) = 20
        // HIGH    (score=40, w=3) + clean (100, w=1) = (40×3+100)/(3+1) = 55
        // Even with a better raw score, the HIGH app contributes less harm than EXTREME.
        AppInfo extreme = new AppInfo("extreme", "com.extreme", new ArrayList<>());
        extreme.setRiskScore(0);

        AppInfo high = buildApp("high", "android.permission.READ_CONTACTS"); // HIGH dominant
        high.setRiskScore(40);

        AppInfo clean1 = new AppInfo("c1", "com.c1", new ArrayList<>()); clean1.setRiskScore(100);
        AppInfo clean2 = new AppInfo("c2", "com.c2", new ArrayList<>()); clean2.setRiskScore(100);

        int withExtreme = RiskCalculator.calculateGlobalScore(Arrays.asList(extreme, clean1));
        int withHigh    = RiskCalculator.calculateGlobalScore(Arrays.asList(high, clean2));

        assertTrue("EXTREME (w=4) must drag global score lower than HIGH (w=3)",
                withExtreme < withHigh);
        assertEquals(20, withExtreme);
        assertEquals(55, withHigh);
    }

    // ── Private helper ────────────────────────────────────────────────────────

    private static List<PermissionInfo> buildPerms(String... names) {
        List<PermissionInfo> perms = new ArrayList<>();
        for (String n : names) perms.add(PermissionClassifier.classify(n));
        return perms;
    }
}
