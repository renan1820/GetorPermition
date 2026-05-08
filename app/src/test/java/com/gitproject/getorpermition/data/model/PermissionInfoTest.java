package com.gitproject.getorpermition.data.model;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Validates PermissionInfo model construction, getters, and RiskLevel enum.
 */
public class PermissionInfoTest {

    // ── Fixtures ────────────────────────────────────────────────────────────

    private static PermissionInfo make(PermissionInfo.RiskLevel level) {
        return new PermissionInfo(
                "android.permission.FOO",
                "Foo Permission",
                "Allows access to foo.",
                level,
                "FOO_GROUP"
        );
    }

    // ── Constructor / Getters (5-arg, backward-compatible) ──────────────────

    @Test
    public void constructor_storesPermissionName() {
        PermissionInfo p = new PermissionInfo(
                "android.permission.CAMERA", "Câmera", "Desc", PermissionInfo.RiskLevel.MEDIUM, "CAMERA");
        assertEquals("android.permission.CAMERA", p.getPermissionName());
    }

    @Test
    public void constructor_storesReadableName() {
        PermissionInfo p = new PermissionInfo(
                "android.permission.CAMERA", "Câmera", "Desc", PermissionInfo.RiskLevel.MEDIUM, "CAMERA");
        assertEquals("Câmera", p.getReadableName());
    }

    @Test
    public void constructor_storesExplanation() {
        PermissionInfo p = new PermissionInfo(
                "android.permission.CAMERA", "Câmera", "Desc explicativa", PermissionInfo.RiskLevel.MEDIUM, "CAMERA");
        assertEquals("Desc explicativa", p.getExplanation());
    }

    @Test
    public void constructor5arg_maliciousUseDefaultsToEmpty() {
        PermissionInfo p = new PermissionInfo(
                "android.permission.CAMERA", "Câmera", "Desc", PermissionInfo.RiskLevel.MEDIUM, "CAMERA");
        assertEquals("", p.getMaliciousUse());
    }

    @Test
    public void constructor6arg_storesMaliciousUse() {
        PermissionInfo p = new PermissionInfo(
                "android.permission.CAMERA", "Câmera", "Desc", "Malicious use example.",
                PermissionInfo.RiskLevel.MEDIUM, "CAMERA");
        assertEquals("Malicious use example.", p.getMaliciousUse());
    }

    @Test
    public void constructor_storesRiskLevel() {
        assertEquals(PermissionInfo.RiskLevel.EXTREME, make(PermissionInfo.RiskLevel.EXTREME).getRiskLevel());
        assertEquals(PermissionInfo.RiskLevel.HIGH,    make(PermissionInfo.RiskLevel.HIGH).getRiskLevel());
        assertEquals(PermissionInfo.RiskLevel.MEDIUM,  make(PermissionInfo.RiskLevel.MEDIUM).getRiskLevel());
        assertEquals(PermissionInfo.RiskLevel.LOW,     make(PermissionInfo.RiskLevel.LOW).getRiskLevel());
    }

    @Test
    public void constructor_storesGroup() {
        PermissionInfo p = new PermissionInfo(
                "android.permission.ACCESS_FINE_LOCATION", "GPS", "Desc", PermissionInfo.RiskLevel.HIGH, "LOCATION");
        assertEquals("LOCATION", p.getGroup());
    }

    // ── RiskLevel enum ───────────────────────────────────────────────────────

    @Test
    public void riskLevel_hasExactlyFourValues() {
        assertEquals(4, PermissionInfo.RiskLevel.values().length);
    }

    @Test
    public void riskLevel_enumOrdinalOrder_extremeHighMediumLow() {
        assertTrue(PermissionInfo.RiskLevel.EXTREME.ordinal() < PermissionInfo.RiskLevel.HIGH.ordinal());
        assertTrue(PermissionInfo.RiskLevel.HIGH.ordinal()    < PermissionInfo.RiskLevel.MEDIUM.ordinal());
        assertTrue(PermissionInfo.RiskLevel.MEDIUM.ordinal()  < PermissionInfo.RiskLevel.LOW.ordinal());
    }

    @Test
    public void riskLevel_extremeHasLowestOrdinal() {
        assertEquals(0, PermissionInfo.RiskLevel.EXTREME.ordinal());
    }

    @Test
    public void permissionInfo_implementsSerializable() {
        PermissionInfo p = make(PermissionInfo.RiskLevel.HIGH);
        assertTrue(p instanceof java.io.Serializable);
    }
}
