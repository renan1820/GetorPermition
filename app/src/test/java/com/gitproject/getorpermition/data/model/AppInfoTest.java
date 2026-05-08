package com.gitproject.getorpermition.data.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Validates AppInfo model: counts, dominant risk, score setter, and serialization.
 */
public class AppInfoTest {

    // ── Fixtures ────────────────────────────────────────────────────────────

    private static PermissionInfo perm(PermissionInfo.RiskLevel level) {
        return new PermissionInfo("android.permission." + level.name() + "_FAKE",
                level.name(), "Explanation", level, "GROUP");
    }

    private static AppInfo app(PermissionInfo... perms) {
        return new AppInfo("Test App", "com.test.app", Arrays.asList(perms));
    }

    private static AppInfo appEmpty() {
        return new AppInfo("Empty App", "com.test.empty", Collections.emptyList());
    }

    // ── countHighRisk ────────────────────────────────────────────────────────

    @Test
    public void countHighRisk_noPermissions_returnsZero() {
        assertEquals(0, appEmpty().countHighRisk());
    }

    @Test
    public void countHighRisk_onlyLowPerms_returnsZero() {
        AppInfo a = app(perm(PermissionInfo.RiskLevel.LOW), perm(PermissionInfo.RiskLevel.LOW));
        assertEquals(0, a.countHighRisk());
    }

    @Test
    public void countHighRisk_onlyMediumPerms_returnsZero() {
        AppInfo a = app(perm(PermissionInfo.RiskLevel.MEDIUM));
        assertEquals(0, a.countHighRisk());
    }

    @Test
    public void countHighRisk_singleHighPerm_returnsOne() {
        assertEquals(1, app(perm(PermissionInfo.RiskLevel.HIGH)).countHighRisk());
    }

    @Test
    public void countHighRisk_threeHighPerms_returnsThree() {
        AppInfo a = app(
                perm(PermissionInfo.RiskLevel.HIGH),
                perm(PermissionInfo.RiskLevel.HIGH),
                perm(PermissionInfo.RiskLevel.HIGH));
        assertEquals(3, a.countHighRisk());
    }

    @Test
    public void countHighRisk_mixedPerms_countsOnlyHigh() {
        AppInfo a = app(
                perm(PermissionInfo.RiskLevel.HIGH),
                perm(PermissionInfo.RiskLevel.MEDIUM),
                perm(PermissionInfo.RiskLevel.LOW),
                perm(PermissionInfo.RiskLevel.HIGH));
        assertEquals(2, a.countHighRisk());
    }

    // ── countMediumRisk ──────────────────────────────────────────────────────

    @Test
    public void countMediumRisk_noPermissions_returnsZero() {
        assertEquals(0, appEmpty().countMediumRisk());
    }

    @Test
    public void countMediumRisk_onlyHighPerms_returnsZero() {
        assertEquals(0, app(perm(PermissionInfo.RiskLevel.HIGH)).countMediumRisk());
    }

    @Test
    public void countMediumRisk_onlyLowPerms_returnsZero() {
        assertEquals(0, app(perm(PermissionInfo.RiskLevel.LOW)).countMediumRisk());
    }

    @Test
    public void countMediumRisk_singleMediumPerm_returnsOne() {
        assertEquals(1, app(perm(PermissionInfo.RiskLevel.MEDIUM)).countMediumRisk());
    }

    @Test
    public void countMediumRisk_mixedPerms_countsOnlyMedium() {
        AppInfo a = app(
                perm(PermissionInfo.RiskLevel.HIGH),
                perm(PermissionInfo.RiskLevel.MEDIUM),
                perm(PermissionInfo.RiskLevel.MEDIUM),
                perm(PermissionInfo.RiskLevel.LOW));
        assertEquals(2, a.countMediumRisk());
    }

    // ── getDominantRisk ──────────────────────────────────────────────────────

    @Test
    public void getDominantRisk_noPermissions_returnsLow() {
        assertEquals(PermissionInfo.RiskLevel.LOW, appEmpty().getDominantRisk());
    }

    @Test
    public void getDominantRisk_onlyLow_returnsLow() {
        AppInfo a = app(perm(PermissionInfo.RiskLevel.LOW), perm(PermissionInfo.RiskLevel.LOW));
        assertEquals(PermissionInfo.RiskLevel.LOW, a.getDominantRisk());
    }

    @Test
    public void getDominantRisk_onlyMedium_returnsMedium() {
        AppInfo a = app(perm(PermissionInfo.RiskLevel.MEDIUM));
        assertEquals(PermissionInfo.RiskLevel.MEDIUM, a.getDominantRisk());
    }

    @Test
    public void getDominantRisk_onlyHigh_returnsHigh() {
        AppInfo a = app(perm(PermissionInfo.RiskLevel.HIGH));
        assertEquals(PermissionInfo.RiskLevel.HIGH, a.getDominantRisk());
    }

    @Test
    public void getDominantRisk_highPlusMedium_returnsHigh() {
        // HIGH takes priority over MEDIUM
        AppInfo a = app(perm(PermissionInfo.RiskLevel.HIGH), perm(PermissionInfo.RiskLevel.MEDIUM));
        assertEquals(PermissionInfo.RiskLevel.HIGH, a.getDominantRisk());
    }

    @Test
    public void getDominantRisk_highPlusLow_returnsHigh() {
        AppInfo a = app(perm(PermissionInfo.RiskLevel.HIGH), perm(PermissionInfo.RiskLevel.LOW));
        assertEquals(PermissionInfo.RiskLevel.HIGH, a.getDominantRisk());
    }

    @Test
    public void getDominantRisk_mediumPlusLow_returnsMedium() {
        AppInfo a = app(perm(PermissionInfo.RiskLevel.MEDIUM), perm(PermissionInfo.RiskLevel.LOW));
        assertEquals(PermissionInfo.RiskLevel.MEDIUM, a.getDominantRisk());
    }

    @Test
    public void getDominantRisk_allThreeLevels_returnsHigh() {
        AppInfo a = app(
                perm(PermissionInfo.RiskLevel.LOW),
                perm(PermissionInfo.RiskLevel.MEDIUM),
                perm(PermissionInfo.RiskLevel.HIGH));
        assertEquals(PermissionInfo.RiskLevel.HIGH, a.getDominantRisk());
    }

    // ── riskScore setter / getter ────────────────────────────────────────────

    @Test
    public void riskScore_defaultsToZero() {
        assertEquals(0, appEmpty().getRiskScore());
    }

    @Test
    public void riskScore_setterUpdatesGetter() {
        AppInfo a = appEmpty();
        a.setRiskScore(75);
        assertEquals(75, a.getRiskScore());
    }

    @Test
    public void riskScore_setterAcceptsZero() {
        AppInfo a = appEmpty();
        a.setRiskScore(0);
        assertEquals(0, a.getRiskScore());
    }

    @Test
    public void riskScore_setterAccepts100() {
        AppInfo a = appEmpty();
        a.setRiskScore(100);
        assertEquals(100, a.getRiskScore());
    }

    // ── getAppCategory ───────────────────────────────────────────────────────

    @Test
    public void getAppCategory_scoreNotSet_returnsDominantRisk() {
        // scoreSet = false → getAppCategory() delegates to getDominantRisk()
        AppInfo a = app(perm(PermissionInfo.RiskLevel.HIGH));
        assertEquals(PermissionInfo.RiskLevel.HIGH, a.getAppCategory());
    }

    @Test
    public void getAppCategory_scoreSetToNonZero_returnsDominantRisk() {
        AppInfo a = app(perm(PermissionInfo.RiskLevel.HIGH));
        a.setRiskScore(80);
        assertEquals(PermissionInfo.RiskLevel.HIGH, a.getAppCategory());
    }

    @Test
    public void getAppCategory_scoreSetToZero_returnsExtreme() {
        AppInfo a = app(perm(PermissionInfo.RiskLevel.HIGH));
        a.setRiskScore(0);
        assertEquals(PermissionInfo.RiskLevel.EXTREME, a.getAppCategory());
    }

    @Test
    public void getAppCategory_scoreSetToZero_withHighPerms_extremeTakesPrecedence() {
        // Even though getDominantRisk() = HIGH, getAppCategory() returns EXTREME when score = 0
        AppInfo a = app(
                perm(PermissionInfo.RiskLevel.HIGH),
                perm(PermissionInfo.RiskLevel.HIGH));
        a.setRiskScore(0);
        assertEquals(PermissionInfo.RiskLevel.EXTREME, a.getAppCategory());
    }

    @Test
    public void getAppCategory_scoreSetToZero_withOnlyLowPerms_returnsExtreme() {
        // 100+ LOW permissions can also drive score to 0 → still EXTREME
        AppInfo a = appEmpty();
        a.setRiskScore(0);
        assertEquals(PermissionInfo.RiskLevel.EXTREME, a.getAppCategory());
    }

    @Test
    public void getAppCategory_freshApp_defaultScoreZeroNotExtreme() {
        // riskScore defaults to 0 BUT scoreSet = false → NOT classified as EXTREME
        AppInfo a = appEmpty();
        assertNotEquals(PermissionInfo.RiskLevel.EXTREME, a.getAppCategory());
    }

    @Test
    public void getAppCategory_score100_returnsLow() {
        AppInfo a = appEmpty();
        a.setRiskScore(100);
        assertEquals(PermissionInfo.RiskLevel.LOW, a.getAppCategory());
    }

    // ── icon (transient, not serialized) ────────────────────────────────────

    @Test
    public void icon_defaultsToNull() {
        assertNull(appEmpty().getIcon());
    }

    // ── basic getters ────────────────────────────────────────────────────────

    @Test
    public void getAppName_returnsConstructorValue() {
        AppInfo a = new AppInfo("WhatsApp", "com.whatsapp", Collections.emptyList());
        assertEquals("WhatsApp", a.getAppName());
    }

    @Test
    public void getPackageName_returnsConstructorValue() {
        AppInfo a = new AppInfo("App", "com.example.app", Collections.emptyList());
        assertEquals("com.example.app", a.getPackageName());
    }

    @Test
    public void getPermissions_returnsConstructorList() {
        List<PermissionInfo> perms = Arrays.asList(perm(PermissionInfo.RiskLevel.LOW));
        AppInfo a = new AppInfo("App", "com.example", perms);
        assertEquals(perms, a.getPermissions());
    }

    // ── Serializable ────────────────────────────────────────────────────────

    @Test
    public void appInfo_implementsSerializable() {
        assertTrue(appEmpty() instanceof java.io.Serializable);
    }
}
