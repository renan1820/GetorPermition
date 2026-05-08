package com.gitproject.getorpermition.utils;

import com.gitproject.getorpermition.data.model.PermissionInfo;
import com.gitproject.getorpermition.data.model.PermissionInfo.RiskLevel;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Validates the full PermissionClassifier catalog:
 *  - Every cataloged permission resolves to the correct risk level and group
 *  - Unknown permissions fall back to LOW with a derived readable name
 *  - Edge cases for name derivation
 */
public class PermissionClassifierTest {

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static void assertLevel(String permission, RiskLevel expected) {
        assertEquals("Risk level for " + permission,
                expected, PermissionClassifier.classify(permission).getRiskLevel());
    }

    private static void assertGroup(String permission, String expected) {
        assertEquals("Group for " + permission,
                expected, PermissionClassifier.classify(permission).getGroup());
    }

    private static void assertKnown(String permission, RiskLevel level, String group) {
        PermissionInfo info = PermissionClassifier.classify(permission);
        assertNotNull(info);
        assertEquals(permission, info.getPermissionName());
        assertEquals(level, info.getRiskLevel());
        assertEquals(group, info.getGroup());
        assertFalse("Readable name must not be empty", info.getReadableName().isEmpty());
        assertFalse("Explanation must not be empty", info.getExplanation().isEmpty());
    }

    // ═══════════════════════════════════════════════════════════════
    // HIGH RISK — all 16 permissions
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void classify_readContacts_isHigh() {
        assertKnown("android.permission.READ_CONTACTS", RiskLevel.HIGH, "CONTACTS");
    }

    @Test
    public void classify_writeContacts_isHigh() {
        assertKnown("android.permission.WRITE_CONTACTS", RiskLevel.HIGH, "CONTACTS");
    }

    @Test
    public void classify_accessFineLocation_isHigh() {
        assertKnown("android.permission.ACCESS_FINE_LOCATION", RiskLevel.HIGH, "LOCATION");
    }

    @Test
    public void classify_accessBackgroundLocation_isHigh() {
        assertKnown("android.permission.ACCESS_BACKGROUND_LOCATION", RiskLevel.HIGH, "LOCATION");
    }

    @Test
    public void classify_readCallLog_isHigh() {
        assertKnown("android.permission.READ_CALL_LOG", RiskLevel.HIGH, "PHONE");
    }

    @Test
    public void classify_processOutgoingCalls_isHigh() {
        assertKnown("android.permission.PROCESS_OUTGOING_CALLS", RiskLevel.HIGH, "PHONE");
    }

    @Test
    public void classify_readSms_isHigh() {
        assertKnown("android.permission.READ_SMS", RiskLevel.HIGH, "SMS");
    }

    @Test
    public void classify_sendSms_isHigh() {
        assertKnown("android.permission.SEND_SMS", RiskLevel.HIGH, "SMS");
    }

    @Test
    public void classify_receiveSms_isHigh() {
        assertKnown("android.permission.RECEIVE_SMS", RiskLevel.HIGH, "SMS");
    }

    @Test
    public void classify_recordAudio_isHigh() {
        assertKnown("android.permission.RECORD_AUDIO", RiskLevel.HIGH, "MICROPHONE");
    }

    @Test
    public void classify_bindAccessibilityService_isHigh() {
        assertKnown("android.permission.BIND_ACCESSIBILITY_SERVICE", RiskLevel.HIGH, "ACCESSIBILITY");
    }

    @Test
    public void classify_packageUsageStats_isHigh() {
        assertKnown("android.permission.PACKAGE_USAGE_STATS", RiskLevel.HIGH, "USAGE");
    }

    @Test
    public void classify_readExternalStorage_isHigh() {
        assertKnown("android.permission.READ_EXTERNAL_STORAGE", RiskLevel.HIGH, "STORAGE");
    }

    @Test
    public void classify_writeExternalStorage_isHigh() {
        assertKnown("android.permission.WRITE_EXTERNAL_STORAGE", RiskLevel.HIGH, "STORAGE");
    }

    @Test
    public void classify_writeCallLog_isHigh() {
        assertKnown("android.permission.WRITE_CALL_LOG", RiskLevel.HIGH, "PHONE");
    }

    @Test
    public void classify_systemAlertWindow_isHigh() {
        assertKnown("android.permission.SYSTEM_ALERT_WINDOW", RiskLevel.HIGH, "SYSTEM");
    }

    // ═══════════════════════════════════════════════════════════════
    // MEDIUM RISK — all 13 permissions
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void classify_accessCoarseLocation_isMedium() {
        assertKnown("android.permission.ACCESS_COARSE_LOCATION", RiskLevel.MEDIUM, "LOCATION");
    }

    @Test
    public void classify_readPhoneState_isMedium() {
        assertKnown("android.permission.READ_PHONE_STATE", RiskLevel.MEDIUM, "PHONE");
    }

    @Test
    public void classify_getAccounts_isMedium() {
        assertKnown("android.permission.GET_ACCOUNTS", RiskLevel.MEDIUM, "ACCOUNTS");
    }

    @Test
    public void classify_camera_isMedium() {
        assertKnown("android.permission.CAMERA", RiskLevel.MEDIUM, "CAMERA");
    }

    @Test
    public void classify_useBiometric_isMedium() {
        assertKnown("android.permission.USE_BIOMETRIC", RiskLevel.MEDIUM, "BIOMETRIC");
    }

    @Test
    public void classify_useFingerprint_isMedium() {
        assertKnown("android.permission.USE_FINGERPRINT", RiskLevel.MEDIUM, "BIOMETRIC");
    }

    @Test
    public void classify_bluetooth_isMedium() {
        assertKnown("android.permission.BLUETOOTH", RiskLevel.MEDIUM, "BLUETOOTH");
    }

    @Test
    public void classify_bluetoothScan_isMedium() {
        assertKnown("android.permission.BLUETOOTH_SCAN", RiskLevel.MEDIUM, "BLUETOOTH");
    }

    @Test
    public void classify_readMediaImages_isMedium() {
        assertKnown("android.permission.READ_MEDIA_IMAGES", RiskLevel.MEDIUM, "MEDIA");
    }

    @Test
    public void classify_readMediaVideo_isMedium() {
        assertKnown("android.permission.READ_MEDIA_VIDEO", RiskLevel.MEDIUM, "MEDIA");
    }

    @Test
    public void classify_readMediaAudio_isMedium() {
        assertKnown("android.permission.READ_MEDIA_AUDIO", RiskLevel.MEDIUM, "MEDIA");
    }

    @Test
    public void classify_callPhone_isMedium() {
        assertKnown("android.permission.CALL_PHONE", RiskLevel.MEDIUM, "PHONE");
    }

    @Test
    public void classify_manageExternalStorage_isMedium() {
        assertKnown("android.permission.MANAGE_EXTERNAL_STORAGE", RiskLevel.MEDIUM, "STORAGE");
    }

    // ═══════════════════════════════════════════════════════════════
    // LOW RISK — all 12 permissions
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void classify_internet_isLow() {
        assertKnown("android.permission.INTERNET", RiskLevel.LOW, "NETWORK");
    }

    @Test
    public void classify_accessNetworkState_isLow() {
        assertKnown("android.permission.ACCESS_NETWORK_STATE", RiskLevel.LOW, "NETWORK");
    }

    @Test
    public void classify_accessWifiState_isLow() {
        assertKnown("android.permission.ACCESS_WIFI_STATE", RiskLevel.LOW, "NETWORK");
    }

    @Test
    public void classify_changeWifiState_isLow() {
        assertKnown("android.permission.CHANGE_WIFI_STATE", RiskLevel.LOW, "NETWORK");
    }

    @Test
    public void classify_vibrate_isLow() {
        assertKnown("android.permission.VIBRATE", RiskLevel.LOW, "HARDWARE");
    }

    @Test
    public void classify_receiveBootCompleted_isLow() {
        assertKnown("android.permission.RECEIVE_BOOT_COMPLETED", RiskLevel.LOW, "SYSTEM");
    }

    @Test
    public void classify_foregroundService_isLow() {
        assertKnown("android.permission.FOREGROUND_SERVICE", RiskLevel.LOW, "SYSTEM");
    }

    @Test
    public void classify_wakeLock_isLow() {
        assertKnown("android.permission.WAKE_LOCK", RiskLevel.LOW, "SYSTEM");
    }

    @Test
    public void classify_requestInstallPackages_isLow() {
        assertKnown("android.permission.REQUEST_INSTALL_PACKAGES", RiskLevel.LOW, "SYSTEM");
    }

    @Test
    public void classify_postNotifications_isLow() {
        assertKnown("android.permission.POST_NOTIFICATIONS", RiskLevel.LOW, "NOTIFICATIONS");
    }

    @Test
    public void classify_scheduleExactAlarm_isLow() {
        assertKnown("android.permission.SCHEDULE_EXACT_ALARM", RiskLevel.LOW, "ALARM");
    }

    @Test
    public void classify_flashlight_isLow() {
        assertKnown("android.permission.FLASHLIGHT", RiskLevel.LOW, "HARDWARE");
    }

    // ═══════════════════════════════════════════════════════════════
    // Unknown permissions — fallback behavior
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void classify_unknownPermission_returnsLow() {
        assertLevel("com.unknown.permission.SOME_FEATURE", RiskLevel.LOW);
    }

    @Test
    public void classify_unknownPermission_returnsOtherGroup() {
        assertGroup("com.unknown.permission.SOME_FEATURE", "OTHER");
    }

    @Test
    public void classify_unknownPermission_returnsNotCatalogedExplanation() {
        PermissionInfo info = PermissionClassifier.classify("com.vendor.permission.CUSTOM");
        assertEquals("Permissão não catalogada.", info.getExplanation());
    }

    @Test
    public void classify_unknownPermission_derivesReadableNameFromLastSegment() {
        // "com.vendor.permission.SOME_FEATURE" → last segment "SOME_FEATURE"
        // → lowercase + replace underscores → "some feature"
        PermissionInfo info = PermissionClassifier.classify("com.vendor.permission.SOME_FEATURE");
        assertEquals("some feature", info.getReadableName());
    }

    @Test
    public void classify_unknownPermission_stripsAllDotSegments() {
        // Only last segment is used, not any parent segment
        PermissionInfo info = PermissionClassifier.classify("a.b.c.d.MY_PERM");
        assertEquals("my perm", info.getReadableName());
    }

    @Test
    public void classify_unknownPermission_noDots_returnsFullNameAsReadable() {
        // A permission with no dots → the full string is used as readable name
        PermissionInfo info = PermissionClassifier.classify("PLAIN_PERMISSION");
        assertEquals("PLAIN_PERMISSION", info.getReadableName());
    }

    @Test
    public void classify_unknownPermission_preservesOriginalPermissionName() {
        String raw = "com.foo.bar.WEIRD_PERMISSION";
        PermissionInfo info = PermissionClassifier.classify(raw);
        assertEquals(raw, info.getPermissionName());
    }

    @Test
    public void classify_alwaysReturnsNonNull() {
        assertNotNull(PermissionClassifier.classify("any.random.permission.WHATEVER"));
    }

    // ═══════════════════════════════════════════════════════════════
    // Catalog completeness — HIGH must not be misclassified as MEDIUM/LOW
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void classify_highRiskPermissions_neverReturnMediumOrLow() {
        String[] highPerms = {
                "android.permission.READ_CONTACTS",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.READ_SMS",
                "android.permission.RECORD_AUDIO",
                "android.permission.BIND_ACCESSIBILITY_SERVICE",
                "android.permission.SYSTEM_ALERT_WINDOW"
        };
        for (String perm : highPerms) {
            assertLevel(perm, RiskLevel.HIGH);
        }
    }

    @Test
    public void classify_mediumRiskPermissions_neverReturnHighOrLow() {
        String[] medPerms = {
                "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.CAMERA",
                "android.permission.GET_ACCOUNTS",
                "android.permission.READ_PHONE_STATE"
        };
        for (String perm : medPerms) {
            assertLevel(perm, RiskLevel.MEDIUM);
        }
    }

    @Test
    public void classify_lowRiskPermissions_neverReturnHighOrMedium() {
        String[] lowPerms = {
                "android.permission.INTERNET",
                "android.permission.VIBRATE",
                "android.permission.FOREGROUND_SERVICE",
                "android.permission.POST_NOTIFICATIONS"
        };
        for (String perm : lowPerms) {
            assertLevel(perm, RiskLevel.LOW);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Security invariants
    // ═══════════════════════════════════════════════════════════════

    @Test
    public void classify_accessFineLocation_notDowngradedToMedium() {
        // ACCESS_FINE_LOCATION (GPS) must be HIGH, not confused with COARSE (MEDIUM)
        assertLevel("android.permission.ACCESS_FINE_LOCATION", RiskLevel.HIGH);
        assertLevel("android.permission.ACCESS_COARSE_LOCATION", RiskLevel.MEDIUM);
    }

    @Test
    public void classify_smsTriplet_allHigh() {
        // All three SMS permissions are HIGH risk (2FA interception risk)
        assertLevel("android.permission.READ_SMS", RiskLevel.HIGH);
        assertLevel("android.permission.SEND_SMS", RiskLevel.HIGH);
        assertLevel("android.permission.RECEIVE_SMS", RiskLevel.HIGH);
    }

    @Test
    public void classify_contactsPermissions_bothHigh() {
        assertLevel("android.permission.READ_CONTACTS", RiskLevel.HIGH);
        assertLevel("android.permission.WRITE_CONTACTS", RiskLevel.HIGH);
    }

    @Test
    public void classify_callLogVsCallPhone_differentRisks() {
        // READ_CALL_LOG (history) is HIGH; CALL_PHONE (action) is MEDIUM
        assertLevel("android.permission.READ_CALL_LOG", RiskLevel.HIGH);
        assertLevel("android.permission.CALL_PHONE", RiskLevel.MEDIUM);
    }
}
