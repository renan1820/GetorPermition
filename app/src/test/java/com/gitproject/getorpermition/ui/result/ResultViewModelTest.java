package com.gitproject.getorpermition.ui.result;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.gitproject.getorpermition.data.model.AppInfo;
import com.gitproject.getorpermition.data.model.PermissionInfo;
import com.gitproject.getorpermition.data.model.PermissionInfo.RiskLevel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Validates ResultViewModel filter logic:
 *  - Default state
 *  - ALL / HIGH / MEDIUM / LOW filters applied to getDominantRisk()
 *  - Filter applied when setApps is called after filter is set
 *  - Edge cases: null / empty input
 */
public class ResultViewModelTest {

    /**
     * Makes LiveData dispatch values synchronously on the calling thread.
     * Required because MediatorLiveData uses the main-thread Looper internally.
     */
    @Rule
    public InstantTaskExecutorRule instantTaskRule = new InstantTaskExecutorRule();

    private ResultViewModel viewModel;

    // ── App fixtures ──────────────────────────────────────────────────────────

    /** App whose dominant risk is HIGH (has at least 1 HIGH permission). */
    private static AppInfo highApp(String name) {
        List<PermissionInfo> perms = Arrays.asList(
                perm(RiskLevel.HIGH), perm(RiskLevel.LOW));
        AppInfo app = new AppInfo(name, "com.test." + name, perms);
        app.setRiskScore(40);
        return app;
    }

    /** App whose dominant risk is MEDIUM (no HIGH, at least 1 MEDIUM). */
    private static AppInfo mediumApp(String name) {
        List<PermissionInfo> perms = Arrays.asList(
                perm(RiskLevel.MEDIUM), perm(RiskLevel.LOW));
        AppInfo app = new AppInfo(name, "com.test." + name, perms);
        app.setRiskScore(75);
        return app;
    }

    /** App whose dominant risk is LOW (only LOW permissions). */
    private static AppInfo lowApp(String name) {
        List<PermissionInfo> perms = Arrays.asList(
                perm(RiskLevel.LOW), perm(RiskLevel.LOW));
        AppInfo app = new AppInfo(name, "com.test." + name, perms);
        app.setRiskScore(98);
        return app;
    }

    /** App with no permissions at all — dominant risk is LOW. */
    private static AppInfo emptyApp(String name) {
        AppInfo app = new AppInfo(name, "com.test." + name, Collections.emptyList());
        app.setRiskScore(100);
        return app;
    }

    private static PermissionInfo perm(RiskLevel level) {
        return new PermissionInfo("p." + level, level.name(), "Desc", level, "GRP");
    }

    /** Returns current filteredApps value (requires at least one observer). */
    private List<AppInfo> currentFiltered() {
        List<AppInfo> value = viewModel.getFilteredApps().getValue();
        return value != null ? value : Collections.emptyList();
    }

    // ── Setup ─────────────────────────────────────────────────────────────────

    @Before
    public void setup() {
        viewModel = new ResultViewModel();
        // Activate the MediatorLiveData so its internal sources fire
        viewModel.getFilteredApps().observeForever(apps -> {});
    }

    // ═══════════════════════════════════════════════════════════════
    // Initial / default state
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void initialFilter_isAll() {
        assertEquals(ResultViewModel.Filter.ALL, viewModel.getActiveFilter().getValue());
    }

    @Test
    public void initialFilteredApps_isEmpty() {
        assertTrue(currentFiltered().isEmpty());
    }

    // ═══════════════════════════════════════════════════════════════
    // setApps — edge cases
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void setApps_null_resultIsEmpty() {
        viewModel.setApps(null);
        assertTrue(currentFiltered().isEmpty());
    }

    @Test
    public void setApps_emptyList_resultIsEmpty() {
        viewModel.setApps(Collections.emptyList());
        assertTrue(currentFiltered().isEmpty());
    }

    @Test
    public void setApps_populatesList_withDefaultFilter() {
        List<AppInfo> apps = Arrays.asList(highApp("h"), mediumApp("m"), lowApp("l"));
        viewModel.setApps(apps);
        assertEquals(3, currentFiltered().size());
    }

    // ═══════════════════════════════════════════════════════════════
    // Filter.ALL
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void filterAll_returnsAllApps() {
        List<AppInfo> apps = Arrays.asList(highApp("h"), mediumApp("m"), lowApp("l"), emptyApp("e"));
        viewModel.setApps(apps);
        viewModel.setFilter(ResultViewModel.Filter.ALL);
        assertEquals(4, currentFiltered().size());
    }

    @Test
    public void filterAll_afterHighFilter_restoresFullList() {
        List<AppInfo> apps = Arrays.asList(highApp("h"), mediumApp("m"), lowApp("l"));
        viewModel.setApps(apps);
        viewModel.setFilter(ResultViewModel.Filter.HIGH);
        viewModel.setFilter(ResultViewModel.Filter.ALL);
        assertEquals(3, currentFiltered().size());
    }

    @Test
    public void filterAll_preservesOriginalOrder() {
        AppInfo h = highApp("h");
        AppInfo m = mediumApp("m");
        AppInfo l = lowApp("l");
        viewModel.setApps(Arrays.asList(h, m, l));
        viewModel.setFilter(ResultViewModel.Filter.ALL);
        List<AppInfo> result = currentFiltered();
        assertSame(h, result.get(0));
        assertSame(m, result.get(1));
        assertSame(l, result.get(2));
    }

    // ═══════════════════════════════════════════════════════════════
    // Filter.HIGH
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void filterHigh_returnsOnlyHighDominantApps() {
        viewModel.setApps(Arrays.asList(highApp("h1"), highApp("h2"), mediumApp("m"), lowApp("l")));
        viewModel.setFilter(ResultViewModel.Filter.HIGH);
        assertEquals(2, currentFiltered().size());
    }

    @Test
    public void filterHigh_excludesMediumApps() {
        viewModel.setApps(Arrays.asList(mediumApp("m1"), mediumApp("m2")));
        viewModel.setFilter(ResultViewModel.Filter.HIGH);
        assertTrue(currentFiltered().isEmpty());
    }

    @Test
    public void filterHigh_excludesLowApps() {
        viewModel.setApps(Arrays.asList(lowApp("l1"), emptyApp("e1")));
        viewModel.setFilter(ResultViewModel.Filter.HIGH);
        assertTrue(currentFiltered().isEmpty());
    }

    @Test
    public void filterHigh_appWithHighAndMediumPerms_isIncluded() {
        // getDominantRisk() = HIGH if any HIGH perm exists, regardless of MEDIUM count
        AppInfo mixed = highApp("mixed"); // already has HIGH + LOW
        viewModel.setApps(Collections.singletonList(mixed));
        viewModel.setFilter(ResultViewModel.Filter.HIGH);
        assertEquals(1, currentFiltered().size());
    }

    @Test
    public void filterHigh_noHighApps_returnsEmpty() {
        viewModel.setApps(Arrays.asList(mediumApp("m"), lowApp("l")));
        viewModel.setFilter(ResultViewModel.Filter.HIGH);
        assertTrue(currentFiltered().isEmpty());
    }

    @Test
    public void filterHigh_allHighApps_returnsAll() {
        viewModel.setApps(Arrays.asList(highApp("h1"), highApp("h2"), highApp("h3")));
        viewModel.setFilter(ResultViewModel.Filter.HIGH);
        assertEquals(3, currentFiltered().size());
    }

    // ═══════════════════════════════════════════════════════════════
    // Filter.MEDIUM
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void filterMedium_returnsOnlyMediumDominantApps() {
        viewModel.setApps(Arrays.asList(highApp("h"), mediumApp("m1"), mediumApp("m2"), lowApp("l")));
        viewModel.setFilter(ResultViewModel.Filter.MEDIUM);
        assertEquals(2, currentFiltered().size());
    }

    @Test
    public void filterMedium_excludesHighApps() {
        viewModel.setApps(Arrays.asList(highApp("h")));
        viewModel.setFilter(ResultViewModel.Filter.MEDIUM);
        assertTrue(currentFiltered().isEmpty());
    }

    @Test
    public void filterMedium_excludesLowApps() {
        viewModel.setApps(Arrays.asList(lowApp("l"), emptyApp("e")));
        viewModel.setFilter(ResultViewModel.Filter.MEDIUM);
        assertTrue(currentFiltered().isEmpty());
    }

    @Test
    public void filterMedium_appWithMediumAndLowPerms_isIncluded() {
        // getDominantRisk() = MEDIUM if no HIGH and at least 1 MEDIUM
        AppInfo app = mediumApp("m"); // MEDIUM + LOW perms
        viewModel.setApps(Collections.singletonList(app));
        viewModel.setFilter(ResultViewModel.Filter.MEDIUM);
        assertEquals(1, currentFiltered().size());
    }

    @Test
    public void filterMedium_appWithHighPerm_isExcluded() {
        // Even if app has MEDIUM perms, if it has HIGH it's dominated by HIGH
        AppInfo app = highApp("h"); // HIGH + LOW perms → dominant = HIGH
        viewModel.setApps(Collections.singletonList(app));
        viewModel.setFilter(ResultViewModel.Filter.MEDIUM);
        assertTrue(currentFiltered().isEmpty());
    }

    // ═══════════════════════════════════════════════════════════════
    // Filter.LOW (shown as "Seguros" in UI)
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void filterLow_returnsOnlyLowDominantApps() {
        viewModel.setApps(Arrays.asList(highApp("h"), mediumApp("m"), lowApp("l1"), emptyApp("e")));
        viewModel.setFilter(ResultViewModel.Filter.LOW);
        assertEquals(2, currentFiltered().size());
    }

    @Test
    public void filterLow_excludesHighApps() {
        viewModel.setApps(Arrays.asList(highApp("h")));
        viewModel.setFilter(ResultViewModel.Filter.LOW);
        assertTrue(currentFiltered().isEmpty());
    }

    @Test
    public void filterLow_excludesMediumApps() {
        viewModel.setApps(Arrays.asList(mediumApp("m")));
        viewModel.setFilter(ResultViewModel.Filter.LOW);
        assertTrue(currentFiltered().isEmpty());
    }

    @Test
    public void filterLow_appWithNoPermissions_isIncluded() {
        // An app with no permissions is LOW dominant
        viewModel.setApps(Collections.singletonList(emptyApp("e")));
        viewModel.setFilter(ResultViewModel.Filter.LOW);
        assertEquals(1, currentFiltered().size());
    }

    @Test
    public void filterLow_onlyLowPerms_isIncluded() {
        viewModel.setApps(Collections.singletonList(lowApp("l")));
        viewModel.setFilter(ResultViewModel.Filter.LOW);
        assertEquals(1, currentFiltered().size());
    }

    // ═══════════════════════════════════════════════════════════════
    // Filter changes after setApps
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void filterSwitch_highToMedium_updatesCorrectly() {
        viewModel.setApps(Arrays.asList(highApp("h"), mediumApp("m"), lowApp("l")));
        viewModel.setFilter(ResultViewModel.Filter.HIGH);
        assertEquals(1, currentFiltered().size());

        viewModel.setFilter(ResultViewModel.Filter.MEDIUM);
        assertEquals(1, currentFiltered().size());
        assertEquals("com.test.m", currentFiltered().get(0).getPackageName());
    }

    @Test
    public void filterSwitch_multipleChanges_finalFilterApplied() {
        viewModel.setApps(Arrays.asList(highApp("h"), mediumApp("m"), lowApp("l")));
        viewModel.setFilter(ResultViewModel.Filter.HIGH);
        viewModel.setFilter(ResultViewModel.Filter.MEDIUM);
        viewModel.setFilter(ResultViewModel.Filter.LOW);
        viewModel.setFilter(ResultViewModel.Filter.ALL);
        assertEquals(3, currentFiltered().size());
    }

    // ═══════════════════════════════════════════════════════════════
    // Filter applied when setApps is called after filter is set
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void setApps_withActiveHighFilter_filtersImmediately() {
        viewModel.setFilter(ResultViewModel.Filter.HIGH);
        viewModel.setApps(Arrays.asList(highApp("h"), mediumApp("m"), lowApp("l")));
        // Filter was already HIGH — setApps must re-apply it
        assertEquals(1, currentFiltered().size());
        assertEquals("com.test.h", currentFiltered().get(0).getPackageName());
    }

    @Test
    public void setApps_withActiveMediumFilter_filtersImmediately() {
        viewModel.setFilter(ResultViewModel.Filter.MEDIUM);
        viewModel.setApps(Arrays.asList(highApp("h"), mediumApp("m1"), mediumApp("m2")));
        assertEquals(2, currentFiltered().size());
    }

    @Test
    public void setApps_withActiveLowFilter_filtersImmediately() {
        viewModel.setFilter(ResultViewModel.Filter.LOW);
        viewModel.setApps(Arrays.asList(highApp("h"), mediumApp("m"), lowApp("l"), emptyApp("e")));
        assertEquals(2, currentFiltered().size());
    }

    @Test
    public void setApps_replacesOldListAndRefilters() {
        viewModel.setApps(Arrays.asList(highApp("h"), mediumApp("m")));
        viewModel.setFilter(ResultViewModel.Filter.HIGH);
        assertEquals(1, currentFiltered().size());

        // Replace with a new list containing no HIGH apps
        viewModel.setApps(Arrays.asList(mediumApp("m2"), lowApp("l")));
        assertEquals(0, currentFiltered().size()); // filter still HIGH
    }

    // ═══════════════════════════════════════════════════════════════
    // activeFilter LiveData
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void setFilter_updatesActiveFilterLiveData() {
        viewModel.setFilter(ResultViewModel.Filter.HIGH);
        assertEquals(ResultViewModel.Filter.HIGH, viewModel.getActiveFilter().getValue());

        viewModel.setFilter(ResultViewModel.Filter.LOW);
        assertEquals(ResultViewModel.Filter.LOW, viewModel.getActiveFilter().getValue());
    }
}
