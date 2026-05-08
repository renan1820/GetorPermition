package com.gitproject.getorpermition.data.model;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Validates AppInfo Java serialization round-trip.
 *
 * AppInfo is passed between fragments (ScanFragment → ResultFragment → DetailFragment)
 * via Bundle.putSerializable(). This test ensures no data is lost in transit.
 * The Drawable icon field is transient and must NOT be serialized.
 */
public class AppInfoSerializationTest {

    private static PermissionInfo perm(PermissionInfo.RiskLevel level) {
        return new PermissionInfo("android.permission." + level, "Readable", "Explanation", level, "GROUP");
    }

    private static AppInfo roundTrip(AppInfo original) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(original);
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        try (ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (AppInfo) ois.readObject();
        }
    }

    @Test
    public void serialization_preservesAppName() throws Exception {
        AppInfo original = new AppInfo("WhatsApp", "com.whatsapp", Collections.emptyList());
        assertEquals("WhatsApp", roundTrip(original).getAppName());
    }

    @Test
    public void serialization_preservesPackageName() throws Exception {
        AppInfo original = new AppInfo("App", "com.example.app", Collections.emptyList());
        assertEquals("com.example.app", roundTrip(original).getPackageName());
    }

    @Test
    public void serialization_preservesRiskScore() throws Exception {
        AppInfo original = new AppInfo("App", "com.test", Collections.emptyList());
        original.setRiskScore(73);
        assertEquals(73, roundTrip(original).getRiskScore());
    }

    @Test
    public void serialization_preservesPermissionCount() throws Exception {
        AppInfo original = new AppInfo("App", "com.test", Arrays.asList(
                perm(PermissionInfo.RiskLevel.HIGH),
                perm(PermissionInfo.RiskLevel.MEDIUM),
                perm(PermissionInfo.RiskLevel.LOW)));
        assertEquals(3, roundTrip(original).getPermissions().size());
    }

    @Test
    public void serialization_preservesPermissionRiskLevels() throws Exception {
        AppInfo original = new AppInfo("App", "com.test", Arrays.asList(
                perm(PermissionInfo.RiskLevel.HIGH),
                perm(PermissionInfo.RiskLevel.MEDIUM)));
        AppInfo restored = roundTrip(original);
        assertEquals(PermissionInfo.RiskLevel.HIGH, restored.getPermissions().get(0).getRiskLevel());
        assertEquals(PermissionInfo.RiskLevel.MEDIUM, restored.getPermissions().get(1).getRiskLevel());
    }

    @Test
    public void serialization_iconIsNullAfterRoundTrip_transientField() throws Exception {
        // The Drawable icon is transient — it must not be serialized
        AppInfo original = new AppInfo("App", "com.test", Collections.emptyList());
        // We can't set a real Drawable in a unit test, but we can verify it's null after round-trip
        assertNull("Transient icon must be null after deserialization", roundTrip(original).getIcon());
    }

    @Test
    public void serialization_permissionInfo_preservesAllFields() throws Exception {
        PermissionInfo perm = new PermissionInfo(
                "android.permission.CAMERA", "Câmera", "Acessa câmera.", PermissionInfo.RiskLevel.MEDIUM, "CAMERA");
        AppInfo original = new AppInfo("CameraApp", "com.camera", Collections.singletonList(perm));
        AppInfo restored = roundTrip(original);
        PermissionInfo restoredPerm = restored.getPermissions().get(0);

        assertEquals("android.permission.CAMERA", restoredPerm.getPermissionName());
        assertEquals("Câmera", restoredPerm.getReadableName());
        assertEquals("Acessa câmera.", restoredPerm.getExplanation());
        assertEquals(PermissionInfo.RiskLevel.MEDIUM, restoredPerm.getRiskLevel());
        assertEquals("CAMERA", restoredPerm.getGroup());
    }

    @Test
    public void serialization_emptyPermissions_survivesRoundTrip() throws Exception {
        AppInfo original = new AppInfo("SafeApp", "com.safe", Collections.emptyList());
        assertTrue(roundTrip(original).getPermissions().isEmpty());
    }

    @Test
    public void serialization_dominantRiskPreservedAfterRoundTrip() throws Exception {
        AppInfo original = new AppInfo("App", "com.test", Arrays.asList(
                perm(PermissionInfo.RiskLevel.HIGH)));
        assertEquals(PermissionInfo.RiskLevel.HIGH, roundTrip(original).getDominantRisk());
    }
}
