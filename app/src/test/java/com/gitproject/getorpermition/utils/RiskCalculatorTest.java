package com.gitproject.getorpermition.utils;

import com.gitproject.getorpermition.data.model.AppInfo;
import com.gitproject.getorpermition.data.model.PermissionInfo;
import com.gitproject.getorpermition.data.model.PermissionInfo.RiskLevel;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Validates all scoring rules:
 *  - Per-app score formula with caps and floor
 *  - Global score weighted average (HIGH apps weight ×3)
 */
public class RiskCalculatorTest {

    // ── Fixtures ─────────────────────────────────────────────────────────────

    private static PermissionInfo perm(RiskLevel level) {
        return new PermissionInfo("perm." + level, level.name(), "Explanation", level, "GROUP");
    }

    /** Builds an AppInfo with the given permissions and pre-computes its score. */
    private static AppInfo scoredApp(RiskLevel... levels) {
        List<PermissionInfo> perms = new ArrayList<>();
        for (RiskLevel l : levels) perms.add(perm(l));
        AppInfo app = new AppInfo("App", "com.test", perms);
        app.setRiskScore(RiskCalculator.calculateAppScore(app));
        return app;
    }

    /** Creates N identical permissions of the given level. */
    private static AppInfo appWithN(int n, RiskLevel level) {
        RiskLevel[] levels = new RiskLevel[n];
        Arrays.fill(levels, level);
        return scoredApp(levels);
    }

    // ═══════════════════════════════════════════════════════════════
    // calculateAppScore — No permissions
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void appScore_noPermissions_returns100() {
        AppInfo app = new AppInfo("App", "com.test", Collections.emptyList());
        assertEquals(100, RiskCalculator.calculateAppScore(app));
    }

    // ═══════════════════════════════════════════════════════════════
    // calculateAppScore — HIGH risk only (penalty 20 each, cap 60)
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void appScore_oneHigh_returns80() {
        assertEquals(80, RiskCalculator.calculateAppScore(appWithN(1, RiskLevel.HIGH)));
    }

    @Test
    public void appScore_twoHigh_returns60() {
        assertEquals(60, RiskCalculator.calculateAppScore(appWithN(2, RiskLevel.HIGH)));
    }

    @Test
    public void appScore_threeHigh_returns40_capReachedExactly() {
        // 3 × 20 = 60 — exactly at the cap
        assertEquals(40, RiskCalculator.calculateAppScore(appWithN(3, RiskLevel.HIGH)));
    }

    @Test
    public void appScore_fourHigh_returns40_capEnforced() {
        // 4 × 20 = 80, capped at 60 → still 40
        assertEquals(40, RiskCalculator.calculateAppScore(appWithN(4, RiskLevel.HIGH)));
    }

    @Test
    public void appScore_tenHigh_returns40_capEnforced() {
        // Any number ≥ 3 HIGH permissions is capped at 60 penalty
        assertEquals(40, RiskCalculator.calculateAppScore(appWithN(10, RiskLevel.HIGH)));
    }

    // ═══════════════════════════════════════════════════════════════
    // calculateAppScore — MEDIUM risk only (penalty 8 each, cap 24)
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void appScore_oneMedium_returns92() {
        assertEquals(92, RiskCalculator.calculateAppScore(appWithN(1, RiskLevel.MEDIUM)));
    }

    @Test
    public void appScore_twoMedium_returns84() {
        assertEquals(84, RiskCalculator.calculateAppScore(appWithN(2, RiskLevel.MEDIUM)));
    }

    @Test
    public void appScore_threeMedium_returns76_capReachedExactly() {
        // 3 × 8 = 24 — exactly at the cap
        assertEquals(76, RiskCalculator.calculateAppScore(appWithN(3, RiskLevel.MEDIUM)));
    }

    @Test
    public void appScore_fourMedium_returns76_capEnforced() {
        // 4 × 8 = 32, capped at 24 → still 76
        assertEquals(76, RiskCalculator.calculateAppScore(appWithN(4, RiskLevel.MEDIUM)));
    }

    @Test
    public void appScore_tenMedium_returns76_capEnforced() {
        assertEquals(76, RiskCalculator.calculateAppScore(appWithN(10, RiskLevel.MEDIUM)));
    }

    // ═══════════════════════════════════════════════════════════════
    // calculateAppScore — LOW risk only (penalty 1 each, no cap)
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void appScore_oneLow_returns99() {
        assertEquals(99, RiskCalculator.calculateAppScore(appWithN(1, RiskLevel.LOW)));
    }

    @Test
    public void appScore_fiveLow_returns95() {
        assertEquals(95, RiskCalculator.calculateAppScore(appWithN(5, RiskLevel.LOW)));
    }

    @Test
    public void appScore_hundredLow_returnsZero_clamped() {
        // 100 × 1 = 100 penalty → 100 - 100 = 0
        assertEquals(0, RiskCalculator.calculateAppScore(appWithN(100, RiskLevel.LOW)));
    }

    @Test
    public void appScore_moreThanHundredLow_returnsZero_floorEnforced() {
        // 101 × 1 = 101 penalty → would be -1, clamped to 0
        assertEquals(0, RiskCalculator.calculateAppScore(appWithN(101, RiskLevel.LOW)));
    }

    @Test
    public void appScore_neverNegative() {
        AppInfo app = appWithN(200, RiskLevel.LOW);
        assertTrue(RiskCalculator.calculateAppScore(app) >= 0);
    }

    // ═══════════════════════════════════════════════════════════════
    // calculateAppScore — Mixed permissions
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void appScore_oneHighOneMediumOneLow_returns71() {
        // 100 - 20 - 8 - 1 = 71
        AppInfo app = scoredApp(RiskLevel.HIGH, RiskLevel.MEDIUM, RiskLevel.LOW);
        assertEquals(71, RiskCalculator.calculateAppScore(app));
    }

    @Test
    public void appScore_twoHighTwoMedium_returns44() {
        // 2×20=40 (HIGH) + 2×8=16 (MEDIUM) → 100 - 40 - 16 = 44
        AppInfo app = scoredApp(RiskLevel.HIGH, RiskLevel.HIGH, RiskLevel.MEDIUM, RiskLevel.MEDIUM);
        assertEquals(44, RiskCalculator.calculateAppScore(app));
    }

    @Test
    public void appScore_capsAppliedIndependently_highAndMediumBothCapped() {
        // 4 HIGH (cap 60) + 4 MEDIUM (cap 24) + 0 LOW → 100 - 60 - 24 = 16
        AppInfo app = scoredApp(
                RiskLevel.HIGH, RiskLevel.HIGH, RiskLevel.HIGH, RiskLevel.HIGH,
                RiskLevel.MEDIUM, RiskLevel.MEDIUM, RiskLevel.MEDIUM, RiskLevel.MEDIUM);
        assertEquals(16, RiskCalculator.calculateAppScore(app));
    }

    @Test
    public void appScore_capsApplied_withLowPenalties() {
        // 4 HIGH (cap 60) + 4 MEDIUM (cap 24) + 5 LOW → 100 - 60 - 24 - 5 = 11
        AppInfo app = scoredApp(
                RiskLevel.HIGH, RiskLevel.HIGH, RiskLevel.HIGH, RiskLevel.HIGH,
                RiskLevel.MEDIUM, RiskLevel.MEDIUM, RiskLevel.MEDIUM, RiskLevel.MEDIUM,
                RiskLevel.LOW, RiskLevel.LOW, RiskLevel.LOW, RiskLevel.LOW, RiskLevel.LOW);
        assertEquals(11, RiskCalculator.calculateAppScore(app));
    }

    @Test
    public void appScore_withCapsAndManyLow_clampedToZero() {
        // 4 HIGH (cap 60) + 4 MEDIUM (cap 24) + 20 LOW → 100 - 60 - 24 - 20 = -4 → 0
        List<PermissionInfo> perms = new ArrayList<>();
        for (int i = 0; i < 4; i++) perms.add(perm(RiskLevel.HIGH));
        for (int i = 0; i < 4; i++) perms.add(perm(RiskLevel.MEDIUM));
        for (int i = 0; i < 20; i++) perms.add(perm(RiskLevel.LOW));
        AppInfo app = new AppInfo("App", "com.test", perms);
        assertEquals(0, RiskCalculator.calculateAppScore(app));
    }

    @Test
    public void appScore_scoreIsNeverAbove100() {
        // An app with no permissions gets exactly 100, never more
        AppInfo app = new AppInfo("App", "com.test", Collections.emptyList());
        assertTrue(RiskCalculator.calculateAppScore(app) <= 100);
    }

    // ═══════════════════════════════════════════════════════════════
    // calculateGlobalScore — Edge cases
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void globalScore_nullList_returns100() {
        assertEquals(100, RiskCalculator.calculateGlobalScore(null));
    }

    @Test
    public void globalScore_emptyList_returns100() {
        assertEquals(100, RiskCalculator.calculateGlobalScore(Collections.emptyList()));
    }

    // ═══════════════════════════════════════════════════════════════
    // calculateGlobalScore — Single-app cases
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void globalScore_singleLowApp_returnsItsScore() {
        AppInfo app = appWithN(5, RiskLevel.LOW); // score = 95
        assertEquals(95, RiskCalculator.calculateGlobalScore(Collections.singletonList(app)));
    }

    @Test
    public void globalScore_singleMediumApp_returnsItsScore() {
        AppInfo app = appWithN(1, RiskLevel.MEDIUM); // score = 92
        assertEquals(92, RiskCalculator.calculateGlobalScore(Collections.singletonList(app)));
    }

    @Test
    public void globalScore_singleHighApp_returnsItsScore() {
        AppInfo app = appWithN(1, RiskLevel.HIGH); // score = 80
        assertEquals(80, RiskCalculator.calculateGlobalScore(Collections.singletonList(app)));
    }

    // ═══════════════════════════════════════════════════════════════
    // calculateGlobalScore — Weighting rules
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void globalScore_twoLowApps_simpleAverage() {
        // No HIGH apps → all weight = 1
        // (70 + 90) / 2 = 80
        AppInfo a1 = scoredApp(RiskLevel.LOW, RiskLevel.LOW, RiskLevel.LOW); // 97
        a1.setRiskScore(70);
        AppInfo a2 = scoredApp(RiskLevel.LOW);
        a2.setRiskScore(90);
        assertEquals(80, RiskCalculator.calculateGlobalScore(Arrays.asList(a1, a2)));
    }

    @Test
    public void globalScore_highAppWeighsThreeTimes() {
        // HIGH app (score=40, weight=3) + LOW app (score=100, weight=1)
        // weighted sum = 40*3 + 100*1 = 120+100 = 220
        // total weight = 3+1 = 4
        // 220/4 = 55
        AppInfo highApp = scoredApp(RiskLevel.HIGH); // dominant=HIGH
        highApp.setRiskScore(40);
        AppInfo lowApp = new AppInfo("Low", "com.low", Collections.emptyList());
        lowApp.setRiskScore(100);
        assertEquals(55, RiskCalculator.calculateGlobalScore(Arrays.asList(highApp, lowApp)));
    }

    @Test
    public void globalScore_mediumAppWeighsOnce() {
        // MEDIUM app (score=60, weight=1) + LOW app (score=80, weight=1)
        // (60 + 80) / 2 = 70
        AppInfo medApp = scoredApp(RiskLevel.MEDIUM);
        medApp.setRiskScore(60);
        AppInfo lowApp = new AppInfo("Low", "com.low", Collections.emptyList());
        lowApp.setRiskScore(80);
        assertEquals(70, RiskCalculator.calculateGlobalScore(Arrays.asList(medApp, lowApp)));
    }

    @Test
    public void globalScore_twoHighApps_weightedEquallyBetweenThem() {
        // Both HIGH: weight=3 each → simple average (3+3)
        // (30*3 + 50*3) / (3+3) = (90+150)/6 = 240/6 = 40
        AppInfo h1 = scoredApp(RiskLevel.HIGH);
        h1.setRiskScore(30);
        AppInfo h2 = scoredApp(RiskLevel.HIGH);
        h2.setRiskScore(50);
        assertEquals(40, RiskCalculator.calculateGlobalScore(Arrays.asList(h1, h2)));
    }

    @Test
    public void globalScore_integerTruncation_noRounding() {
        // HIGH (score=11, weight=3) + LOW (score=10, weight=1)
        // (11*3 + 10*1) / (3+1) = (33+10)/4 = 43/4 = 10 (integer division, not 10.75)
        AppInfo highApp = scoredApp(RiskLevel.HIGH);
        highApp.setRiskScore(11);
        AppInfo lowApp = new AppInfo("Low", "com.low", Collections.emptyList());
        lowApp.setRiskScore(10);
        assertEquals(10, RiskCalculator.calculateGlobalScore(Arrays.asList(highApp, lowApp)));
    }

    @Test
    public void globalScore_threeApps_highDominatesWeighting() {
        // HIGH (score=20, w=3) + MEDIUM (score=80, w=1) + LOW (score=100, w=1)
        // (20*3 + 80 + 100) / (3+1+1) = (60+80+100)/5 = 240/5 = 48
        AppInfo highApp = scoredApp(RiskLevel.HIGH);
        highApp.setRiskScore(20);
        AppInfo medApp = scoredApp(RiskLevel.MEDIUM);
        medApp.setRiskScore(80);
        AppInfo lowApp = new AppInfo("Low", "com.low", Collections.emptyList());
        lowApp.setRiskScore(100);
        assertEquals(48, RiskCalculator.calculateGlobalScore(Arrays.asList(highApp, medApp, lowApp)));
    }

    @Test
    public void globalScore_multipleHighApps_amplifiesImpact() {
        // 3 HIGH apps (score=10 each) vs 1 LOW app (score=100)
        // (10*3 + 10*3 + 10*3 + 100*1) / (3+3+3+1) = (30+30+30+100)/10 = 190/10 = 19
        AppInfo h1 = scoredApp(RiskLevel.HIGH); h1.setRiskScore(10);
        AppInfo h2 = scoredApp(RiskLevel.HIGH); h2.setRiskScore(10);
        AppInfo h3 = scoredApp(RiskLevel.HIGH); h3.setRiskScore(10);
        AppInfo lowApp = new AppInfo("L", "com.l", Collections.emptyList()); lowApp.setRiskScore(100);
        assertEquals(19, RiskCalculator.calculateGlobalScore(Arrays.asList(h1, h2, h3, lowApp)));
    }

    // ═══════════════════════════════════════════════════════════════
    // calculateGlobalScore — EXTREME weighting (weight = 4)
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void globalScore_extremeAppWeighsFourTimes() {
        // EXTREME app (score=0, w=4) + LOW app (score=100, w=1)
        // (0×4 + 100×1) / (4+1) = 100/5 = 20
        AppInfo extreme = new AppInfo("Extreme", "com.extreme", Collections.emptyList());
        extreme.setRiskScore(0);
        AppInfo low = new AppInfo("Low", "com.low", Collections.emptyList());
        low.setRiskScore(100);
        assertEquals(20, RiskCalculator.calculateGlobalScore(Arrays.asList(extreme, low)));
    }

    @Test
    public void globalScore_extremeWeighsMoreThanHigh() {
        // EXTREME (score=0, w=4) + LOW (score=100, w=1) = 100/5 = 20
        // HIGH    (score=1, w=3) + LOW (score=100, w=1) = 103/4 = 25
        // EXTREME drags the global score lower than a HIGH app does
        AppInfo extreme = new AppInfo("Extreme", "com.extreme", Collections.emptyList());
        extreme.setRiskScore(0); // scoreSet=true, score=0 → EXTREME category, weight=4
        AppInfo lowForExtreme = new AppInfo("Low1", "com.low1", Collections.emptyList());
        lowForExtreme.setRiskScore(100);
        int scoreWithExtreme = RiskCalculator.calculateGlobalScore(Arrays.asList(extreme, lowForExtreme));

        AppInfo high = scoredApp(RiskLevel.HIGH); // dominant=HIGH, weight=3
        high.setRiskScore(1); // non-zero keeps category as HIGH
        AppInfo lowForHigh = new AppInfo("Low2", "com.low2", Collections.emptyList());
        lowForHigh.setRiskScore(100);
        int scoreWithHigh = RiskCalculator.calculateGlobalScore(Arrays.asList(high, lowForHigh));

        assertEquals(20, scoreWithExtreme);
        assertEquals(25, scoreWithHigh);
        assertTrue(scoreWithExtreme < scoreWithHigh);
    }

    @Test
    public void globalScore_twoExtremeApps_bothWeighFour() {
        // 2× EXTREME (score=0, w=4 each)
        // (0×4 + 0×4) / (4+4) = 0/8 = 0
        AppInfo e1 = new AppInfo("E1", "com.e1", Collections.emptyList());
        e1.setRiskScore(0);
        AppInfo e2 = new AppInfo("E2", "com.e2", Collections.emptyList());
        e2.setRiskScore(0);
        assertEquals(0, RiskCalculator.calculateGlobalScore(Arrays.asList(e1, e2)));
    }

    @Test
    public void globalScore_extremeMixedWithHigh_correctWeights() {
        // EXTREME (score=0, w=4) + HIGH (score=40, w=3)
        // (0×4 + 40×3) / (4+3) = 120/7 = 17 (integer division)
        AppInfo extreme = new AppInfo("Extreme", "com.extreme", Collections.emptyList());
        extreme.setRiskScore(0);
        AppInfo high = scoredApp(RiskLevel.HIGH);
        high.setRiskScore(40);
        assertEquals(17, RiskCalculator.calculateGlobalScore(Arrays.asList(extreme, high)));
    }
}
