package com.gitproject.getorpermition.data.model;

import java.io.Serializable;

public class PermissionInfo implements Serializable {

    // EXTREME must come first so ordinal-based sorting places it above HIGH
    public enum RiskLevel { EXTREME, HIGH, MEDIUM, LOW }

    private final String permissionName;   // e.g. android.permission.READ_CONTACTS
    private final String readableName;     // e.g. "Contatos"
    private final String explanation;      // what the permission accesses
    private final String maliciousUse;     // 1 example of what a malicious app could do
    private final RiskLevel riskLevel;
    private final String group;            // e.g. "LOCATION", "CONTACTS"

    /** Full constructor used by PermissionClassifier. */
    public PermissionInfo(String permissionName, String readableName,
                          String explanation, String maliciousUse,
                          RiskLevel riskLevel, String group) {
        this.permissionName = permissionName;
        this.readableName   = readableName;
        this.explanation    = explanation;
        this.maliciousUse   = maliciousUse;
        this.riskLevel      = riskLevel;
        this.group          = group;
    }

    /** Backward-compatible constructor (maliciousUse defaults to empty). */
    public PermissionInfo(String permissionName, String readableName,
                          String explanation, RiskLevel riskLevel, String group) {
        this(permissionName, readableName, explanation, "", riskLevel, group);
    }

    public String getPermissionName() { return permissionName; }
    public String getReadableName()   { return readableName; }
    public String getExplanation()    { return explanation; }
    public String getMaliciousUse()   { return maliciousUse; }
    public RiskLevel getRiskLevel()   { return riskLevel; }
    public String getGroup()          { return group; }
}
