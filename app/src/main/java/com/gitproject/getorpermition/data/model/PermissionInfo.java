package com.gitproject.getorpermition.data.model;

import java.io.Serializable;

public class PermissionInfo implements Serializable {

    public enum RiskLevel { HIGH, MEDIUM, LOW }

    private final String permissionName;   // e.g. android.permission.READ_CONTACTS
    private final String readableName;     // e.g. "Contatos"
    private final String explanation;      // why it's risky
    private final RiskLevel riskLevel;
    private final String group;            // e.g. "LOCATION", "CONTACTS"

    public PermissionInfo(String permissionName, String readableName,
                          String explanation, RiskLevel riskLevel, String group) {
        this.permissionName = permissionName;
        this.readableName = readableName;
        this.explanation = explanation;
        this.riskLevel = riskLevel;
        this.group = group;
    }

    public String getPermissionName() { return permissionName; }
    public String getReadableName() { return readableName; }
    public String getExplanation() { return explanation; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public String getGroup() { return group; }
}
